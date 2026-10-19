/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.mail.GreenMailConfig;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.StoredMessage;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * A sending step's copy recipients and its recipient read off the process - dirigible #7935.
 *
 * <p>
 * The parser and generator tests prove the descriptor; this proves the generated sender compiles,
 * runs inside the process and that the SMTP server receives what the model declares: a
 * comma-separated {@code @var:} list reaches every address, {@code cc:} and {@code bcc:} reach
 * their mailboxes with the blind copy named in no delivered header, and a step whose {@code to:}
 * resolves blank still sends to its {@code bcc:}.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class IntentNotifyRecipientsIT extends IntegrationTest {

    static {
        GreenMailConfig.configureDirigibleEmailService();
    }

    private static final String WORKSPACE = "workspace";
    private static final String PROJECT = "notifyrecipients";
    private static final String PROJECT_PATH = IRepositoryStructure.PATH_USERS + "/admin/" + WORKSPACE + "/" + PROJECT;
    private static final String API = "/services/java/" + PROJECT + "/gen/" + PROJECT + "/api";
    private static final String CLOSINGS = API + "/closingrequest/ClosingRequestController";
    private static final String NOTICES = API + "/closingnotice/ClosingNoticeController";
    private static final long TIMEOUT_SECONDS = 90;
    private static final long MAIL_TIMEOUT_SECONDS = 60;

    private static final String ARCHIVE_KEY = "NOTIFY_RECIPIENTS_IT_ARCHIVE";
    private static final String ARCHIVE = "archive@notify-recipients.test";
    private static final String APPROVER_ONE = "approver.one@notify-recipients.test";
    private static final String APPROVER_TWO = "approver.two@notify-recipients.test";
    private static final String REQUESTER = "requester@notify-recipients.test";

    private static final String INTENT_YAML =
            """
                    name: notifyrecipients
                    description: a sending step mails a list a delegate computed, copies the requester and blind-copies an archive

                    entities:
                      - name: ClosingRequest
                        fields:
                          - { name: id,          type: integer, primaryKey: true, generated: true }
                          - { name: name,        type: string, required: true, length: 100 }
                          - { name: requestedBy, type: string, length: 200 }
                      - name: ClosingNotice
                        fields:
                          - { name: id,   type: integer, primaryKey: true, generated: true }
                          - { name: name, type: string, required: true, length: 100 }

                    processes:
                      - name: Closing
                        trigger: { onCreate: ClosingRequest }
                        vars:
                          - { name: approverEmails }
                        steps:
                          - { name: collectApprovers, kind: serviceTask, args: { delegate: custom.CollectApprovers, produces: [approverEmails], next: notify } }
                          - name: notify
                            kind: serviceTask
                            args:
                              notify:
                                to: "@var:approverEmails"
                                cc: requestedBy
                                bcc: "@config:%s"
                                subject: "Closing {name} is complete"
                                body: "Closing {name} is complete."
                              uses: [approverEmails]
                              next: done
                          - { name: done, kind: end }
                      # The same step shape with nobody to address: the delegate computes an empty list, so only
                      # the blind copy names a mailbox - and the message must still go out.
                      - name: Notice
                        trigger: { onCreate: ClosingNotice }
                        vars:
                          - { name: approverEmails }
                        steps:
                          - { name: collectNobody, kind: serviceTask, args: { delegate: custom.CollectNobody, produces: [approverEmails], next: notify } }
                          - name: notify
                            kind: serviceTask
                            args:
                              notify:
                                to: "@var:approverEmails"
                                bcc: "@config:%s"
                                subject: "Notice {name}"
                                body: "Notice {name}."
                              uses: [approverEmails]
                              next: done
                          - { name: done, kind: end }
                    """.formatted(
                    ARCHIVE_KEY, ARCHIVE_KEY);

    /** The list an earlier step computes - one comma-separated value, as the issue specifies. */
    private static final String COLLECT_APPROVERS_JAVA = """
            package custom;

            import org.flowable.engine.delegate.DelegateExecution;
            import org.flowable.engine.delegate.JavaDelegate;

            public class CollectApprovers implements JavaDelegate {

                @Override
                public void execute(DelegateExecution execution) {
                    execution.setVariable("approverEmails", "%s, %s");
                }
            }
            """.formatted(APPROVER_ONE, APPROVER_TWO);

    private static final String COLLECT_NOBODY_JAVA = """
            package custom;

            import org.flowable.engine.delegate.DelegateExecution;
            import org.flowable.engine.delegate.JavaDelegate;

            public class CollectNobody implements JavaDelegate {

                @Override
                public void execute(DelegateExecution execution) {
                    execution.setVariable("approverEmails", "");
                }
            }
            """;

    @Autowired
    private IRepository repository;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private GreenMail greenMail;

    @Test
    void a_sending_step_reaches_a_variable_list_its_copy_and_its_blind_copy() throws Exception {
        Configuration.set(ARCHIVE_KEY, ARCHIVE);
        generateProject();
        publishProject();
        synchronizationProcessor.forceProcessSynchronizers();

        create(CLOSINGS, "{\"Name\":\"Q3\",\"RequestedBy\":\"" + REQUESTER + "\"}");

        // Every recipient's mailbox gets its copy - the two from the comma-separated variable, the cc and
        // the bcc - and nobody else's.
        String closing = "Closing Q3 is complete";
        List<String> mailboxes = List.of(APPROVER_ONE, APPROVER_TWO, REQUESTER, ARCHIVE);
        await().atMost(MAIL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
               .until(() -> mailboxes.stream()
                                     .allMatch(mailbox -> inbox(mailbox, closing).size() == 1));
        assertThat(withSubject(closing)).hasSize(mailboxes.size());
        for (String mailbox : mailboxes) {
            MimeMessage message = inbox(mailbox, closing).get(0);
            assertThat(addresses(message.getRecipients(Message.RecipientType.TO))).containsExactlyInAnyOrder(APPROVER_ONE, APPROVER_TWO);
            assertThat(addresses(message.getRecipients(Message.RecipientType.CC))).containsExactly(REQUESTER);
            // The blind copy is delivered, but no delivered header may name it.
            assertThat(message.getHeader("Bcc")).isNull();
            assertThat(Collections.list(message.getAllHeaderLines())).noneMatch(line -> line.contains(ARCHIVE));
        }

        // A blank to: with a bcc: still sends - to the blind copy alone.
        create(NOTICES, "{\"Name\":\"N1\"}");
        String notice = "Notice N1";
        await().atMost(MAIL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
               .until(() -> inbox(ARCHIVE, notice).size() == 1);
        assertThat(withSubject(notice)).hasSize(1);
        assertThat(inbox(ARCHIVE, notice).get(0)
                                         .getRecipients(Message.RecipientType.TO)).isNull();
    }

    /**
     * The messages with this subject delivered to one mailbox. GreenMail keeps ONE copy per recipient
     * mailbox, which is the only place a blind copy is visible at all - its headers never name it.
     */
    private List<MimeMessage> inbox(String mailbox, String subject) {
        GreenMailUser user = greenMail.getUserManager()
                                      .getUserByEmail(mailbox);
        List<MimeMessage> matching = new ArrayList<>();
        if (user == null) {
            return matching;
        }
        try {
            for (StoredMessage stored : greenMail.getManagers()
                                                 .getImapHostManager()
                                                 .getInbox(user)
                                                 .getMessages()) {
                if (subject.equals(stored.getMimeMessage()
                                         .getSubject())) {
                    matching.add(stored.getMimeMessage());
                }
            }
        } catch (FolderException | MessagingException e) {
            throw new IllegalStateException("Cannot read the inbox of [" + mailbox + "]", e);
        }
        return matching;
    }

    /** Every delivered copy with this subject, across all mailboxes. */
    private List<MimeMessage> withSubject(String subject) throws MessagingException {
        List<MimeMessage> matching = new ArrayList<>();
        for (MimeMessage message : greenMail.getReceivedMessages()) {
            if (subject.equals(message.getSubject())) {
                matching.add(message);
            }
        }
        return matching;
    }

    private static List<String> addresses(Address[] addresses) {
        List<String> result = new ArrayList<>();
        if (addresses != null) {
            for (Address address : addresses) {
                result.add(address.toString());
            }
        }
        return result;
    }

    private void create(String controller, String body) {
        restAssuredExecutor.execute(() -> given().contentType("application/json")
                                                 .body(body)
                                                 .when()
                                                 .post(controller)
                                                 .then()
                                                 .statusCode(200),
                TIMEOUT_SECONDS);
    }

    private void generateProject() {
        writeProjectFile("app.intent", INTENT_YAML);
        writeProjectFile("custom/CollectApprovers.java", COLLECT_APPROVERS_JAVA);
        writeProjectFile("custom/CollectNobody.java", COLLECT_NOBODY_JAVA);
        AtomicReference<List<Map<String, Object>>> plan = new AtomicReference<>();
        restAssuredExecutor.execute(() -> plan.set(given().when()
                                                          .post("/services/ide/intent/generate?workspace=" + WORKSPACE + "&project="
                                                                  + PROJECT + "&path=app.intent")
                                                          .then()
                                                          .statusCode(200)
                                                          .extract()
                                                          .jsonPath()
                                                          .getList("codeGenerations")));
        for (Map<String, Object> codeGeneration : plan.get()) {
            assertEquals(Boolean.TRUE, codeGeneration.get("generated"),
                    "generating code from " + codeGeneration.get("path") + " failed: " + codeGeneration.get("error"));
        }
    }

    private void publishProject() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .post("/services/ide/publisher/" + WORKSPACE + "/" + PROJECT + "/")
                                                 .then()
                                                 .statusCode(200));
    }

    private void writeProjectFile(String fileName, String content) {
        String path = PROJECT_PATH + "/" + fileName;
        IResource existing = repository.getResource(path);
        if (existing.exists()) {
            existing.setContent(content.getBytes(StandardCharsets.UTF_8));
        } else {
            repository.createResource(path, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    @AfterEach
    void cleanup() {
        Configuration.remove(ARCHIVE_KEY);
        restAssuredExecutor.execute(() -> given().when()
                                                 .delete("/services/ide/publisher/" + WORKSPACE + "/" + PROJECT)
                                                 .then()
                                                 .statusCode(greaterThanOrEqualTo(200)));
        if (repository.hasCollection(PROJECT_PATH)) {
            repository.removeCollection(PROJECT_PATH);
        }
        synchronizationProcessor.forceProcessSynchronizers();
    }
}
