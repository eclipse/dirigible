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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.tenant.DirigibleTestTenant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * The selected tenant's {@code Owner} manages that tenant's own settings (#7877): its document
 * numbering, its tenant configuration and its print templates - and nothing of another tenant's,
 * nor an instance-wide key; a {@code User} manages none of them.
 *
 * <p>
 * Driven with MockMvc under {@code TOKEN_GROUPS}: a fabricated OIDC person selects a tenant the way
 * the picker does, and carries no tenant role until then - the selection grants the role its groups
 * name for that tenant, as a real login does.
 */
// Two provisioned tenants and a published series: ~2 minutes, a long pole of the api shard.
@Tag("slow")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TenantOwnerSettingsIT extends IntegrationTest {

    private static final String SELECTION = "/services/security/tenant-selection";
    private static final String NUMBERING = "/services/core/numbering";
    private static final String CONFIGURATIONS = "/services/core/configurations/tenant";
    private static final String TEMPLATES = "/services/print/OwnerSettingsDoc/templates";

    private static final String APP_ID = "ledger";
    private static final String GROUPS_CLAIM = "groups";

    private static final String SERIES = "OwnerSettingsIT";
    private static final String NUMBERS_PATH = IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/owner-settings-it/owner-settings-it.numbers";
    private static final String NUMBERS_CONTENT = "{\"series\": [{\"name\": \"" + SERIES + "\", \"prefix\": \"INV-\", \"size\": 10}]}";

    /** A tenant setting: allow-listed for tenant override. */
    private static final String COUNTRY = "DIRIGIBLE_APPLICATION_COUNTRY";
    /** An instance-wide key: stored per tenant only by staff, never injected. */
    private static final String INSTANCE_WIDE = "DIRIGIBLE_DATABASE_DEFAULT_SET_AUTO_COMMIT";

    private static final String TEMPLATE = """
            <document id="owner">
                <page>
                    <section>
                        <field label="Number">{{document.number}}</field>
                    </section>
                </page>
            </document>
            """;

    private static DirigibleTestTenant tenantA;
    private static DirigibleTestTenant tenantB;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    private final ObjectMapper json = new ObjectMapper();

    @BeforeAll
    static void selectTenantsByTokenGroups() {
        DirigibleConfig.MULTI_TENANT_MODE_ENABLED.setBooleanValue(true);
        DirigibleConfig.TENANT_RESOLUTION_STRATEGY.setStringValue("TOKEN_GROUPS");
        DirigibleConfig.APP_ID.setStringValue(APP_ID);
        DirigibleConfig.TENANT_GROUPS_CLAIM.setStringValue(GROUPS_CLAIM);
    }

    @BeforeEach
    void provisionTheTenantsAndTheSeriesOnce() {
        if (tenantA != null) {
            return;
        }
        DirigibleTestTenant a = new DirigibleTestTenant("owner-settings-a");
        DirigibleTestTenant b = new DirigibleTestTenant("owner-settings-b");
        createTenants(a, b);
        waitForTenantsProvisioning(List.of(a, b));
        tenantA = a;
        tenantB = b;

        repository.createResource(NUMBERS_PATH, NUMBERS_CONTENT.getBytes(StandardCharsets.UTF_8), false, "application/json", true);
        synchronizationProcessor.forceProcessSynchronizers();
        Awaitility.await()
                  .pollInterval(1, TimeUnit.SECONDS)
                  .atMost(60, TimeUnit.SECONDS)
                  .untilAsserted(() -> {
                      assertEquals("INV-000001", series(ownerOfA(), enter(ownerOfA(), tenantA)).get("example")
                                                                                               .asString());
                      assertEquals("INV-000001", series(ownerOfB(), enter(ownerOfB(), tenantB)).get("example")
                                                                                               .asString());
                  });
    }

    @Test
    void anOwnerChangesTheNumberingOfTheirTenantOnly() throws Exception {
        Authentication owner = ownerOfA();
        MockHttpSession session = enter(owner, tenantA);
        // The VAT Act case: a 10-digit Arabic-numeral number, so no prefix at all.
        mvc.perform(put(NUMBERING + "/shape").session(session)
                                             .with(authentication(owner))
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content("{\"series\":\"" + SERIES + "\",\"partition\":\"\",\"prefix\":\"\",\"size\":10}"))
           .andExpect(status().isNoContent());
        mvc.perform(put(NUMBERING).session(session)
                                  .with(authentication(owner))
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .content("{\"series\":\"" + SERIES + "\",\"partition\":\"\",\"next\":1000}"))
           .andExpect(status().isNoContent());
        assertEquals("0000001000", series(owner, session).get("example")
                                                         .asString());

        assertEquals("INV-000001", series(ownerOfB(), enter(ownerOfB(), tenantB)).get("example")
                                                                                 .asString(),
                "another tenant's series is untouched");
    }

    @Test
    void anOwnerChangesTheTenantSettingsOfTheirTenantButNoInstanceWideKey() throws Exception {
        Authentication owner = ownerOfA();
        MockHttpSession session = enter(owner, tenantA);
        mvc.perform(put(CONFIGURATIONS).session(session)
                                       .with(authentication(owner))
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content("{\"key\":\"" + COUNTRY + "\",\"value\":\"BG\"}"))
           .andExpect(status().isOk());
        mvc.perform(get(CONFIGURATIONS).session(session)
                                       .with(authentication(owner)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.key == '" + COUNTRY + "')].value").value(hasItem("BG")));
        mvc.perform(get(CONFIGURATIONS + "/predefined").session(session)
                                                       .with(authentication(owner)))
           .andExpect(status().isOk());

        mvc.perform(put(CONFIGURATIONS).session(session)
                                       .with(authentication(owner))
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content("{\"key\":\"" + INSTANCE_WIDE + "\",\"value\":\"false\"}"))
           .andExpect(status().isForbidden());
        mvc.perform(delete(CONFIGURATIONS).param("key", INSTANCE_WIDE)
                                          .session(session)
                                          .with(authentication(owner)))
           .andExpect(status().isForbidden());

        Authentication otherOwner = ownerOfB();
        mvc.perform(get(CONFIGURATIONS).session(enter(otherOwner, tenantB))
                                       .with(authentication(otherOwner)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[*].key").value(not(hasItem(COUNTRY))));
    }

    @Test
    void anOwnerManagesThePrintTemplatesOfTheirTenantOnly() throws Exception {
        Authentication owner = ownerOfA();
        MockHttpSession session = enter(owner, tenantA);
        mvc.perform(put(TEMPLATES + "/acme").param("lang", "en")
                                            .session(session)
                                            .with(authentication(owner))
                                            .contentType(MediaType.TEXT_PLAIN)
                                            .content(TEMPLATE))
           .andExpect(status().isNoContent());
        mvc.perform(get(TEMPLATES + "/acme").param("lang", "en")
                                            .session(session)
                                            .with(authentication(owner)))
           .andExpect(status().isOk());
        mvc.perform(post(TEMPLATES + "/acme/duplicate").param("lang", "en")
                                                       .param("as", "acme-copy")
                                                       .session(session)
                                                       .with(authentication(owner)))
           .andExpect(status().isCreated());
        mvc.perform(delete(TEMPLATES + "/acme-copy").param("lang", "en")
                                                    .session(session)
                                                    .with(authentication(owner)))
           .andExpect(status().isNoContent());

        Authentication otherOwner = ownerOfB();
        mvc.perform(get(TEMPLATES + "/acme").param("lang", "en")
                                            .session(enter(otherOwner, tenantB))
                                            .with(authentication(otherOwner)))
           .andExpect(status().isNotFound());
    }

    @Test
    void anOwnerOfOneTenantIsRefusedAnothersSettings() throws Exception {
        // A member of B, but not its owner: selecting B leaves the User role only.
        Authentication owner = ownerOfA();
        MockHttpSession inB = enter(owner, tenantB);
        assertRefusedEverySetting(owner, inB);

        // No group in B at all: B cannot even be selected.
        Authentication onlyOwnerOfA = person("only-a@example.com", group(tenantA, "Owner"));
        mvc.perform(post(SELECTION).session(new MockHttpSession())
                                   .with(authentication(onlyOwnerOfA))
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content("{\"tenantId\":\"" + tenantB.getId() + "\"}"))
           .andExpect(status().isForbidden());
    }

    @Test
    void aUserIsRefusedEverySetting() throws Exception {
        Authentication user = person("user-a@example.com", group(tenantA, "User"));
        assertRefusedEverySetting(user, enter(user, tenantA));
    }

    private void assertRefusedEverySetting(Authentication caller, MockHttpSession session) throws Exception {
        mvc.perform(get(NUMBERING).session(session)
                                  .with(authentication(caller)))
           .andExpect(status().isForbidden());
        mvc.perform(put(NUMBERING + "/shape").session(session)
                                             .with(authentication(caller))
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content("{\"series\":\"" + SERIES + "\",\"partition\":\"\",\"prefix\":\"\",\"size\":10}"))
           .andExpect(status().isForbidden());
        mvc.perform(get(CONFIGURATIONS).session(session)
                                       .with(authentication(caller)))
           .andExpect(status().isForbidden());
        mvc.perform(put(CONFIGURATIONS).session(session)
                                       .with(authentication(caller))
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content("{\"key\":\"" + COUNTRY + "\",\"value\":\"DE\"}"))
           .andExpect(status().isForbidden());
        mvc.perform(get(TEMPLATES + "/acme").param("lang", "en")
                                            .session(session)
                                            .with(authentication(caller)))
           .andExpect(status().isForbidden());
        mvc.perform(put(TEMPLATES + "/intruder").param("lang", "en")
                                                .session(session)
                                                .with(authentication(caller))
                                                .contentType(MediaType.TEXT_PLAIN)
                                                .content(TEMPLATE))
           .andExpect(status().isForbidden());
    }

    private JsonNode series(Authentication caller, MockHttpSession session) throws Exception {
        String body = mvc.perform(get(NUMBERING).session(session)
                                                .with(authentication(caller)))
                         .andExpect(status().isOk())
                         .andReturn()
                         .getResponse()
                         .getContentAsString();
        for (JsonNode row : json.readTree(body)) {
            if (SERIES.equals(row.get("series")
                                 .asString())) {
                return row;
            }
        }
        throw new AssertionError("no series [" + SERIES + "] in " + body);
    }

    private MockHttpSession enter(Authentication person, DirigibleTestTenant tenant) throws Exception {
        MockHttpSession session = new MockHttpSession();
        mvc.perform(post(SELECTION).session(session)
                                   .with(authentication(person))
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content("{\"tenantId\":\"" + tenant.getId() + "\"}"))
           .andExpect(status().isOk());
        return session;
    }

    /** The owner of A, and a plain member of B. */
    private static Authentication ownerOfA() {
        return person("owner-a@example.com", group(tenantA, "Owner"), group(tenantB, "User"));
    }

    private static Authentication ownerOfB() {
        return person("owner-b@example.com", group(tenantB, "Owner"));
    }

    private static String group(DirigibleTestTenant tenant, String role) {
        return tenant.getId() + "." + APP_ID + "." + role;
    }

    /**
     * A signed-in person with the given groups and, as after a {@code TOKEN_GROUPS} login, no tenant
     * role yet - the selection grants it.
     */
    private static Authentication person(String email, String... groups) {
        OidcIdToken idToken = new OidcIdToken("id-token", Instant.now(), Instant.now()
                                                                                .plusSeconds(300),
                Map.of("sub", email, "email", email, GROUPS_CLAIM, Arrays.asList(groups)));
        return new OAuth2AuthenticationToken(new DefaultOidcUser(List.of(new SimpleGrantedAuthority("ROLE_USER")), idToken), List.of(),
                "keycloak");
    }

}
