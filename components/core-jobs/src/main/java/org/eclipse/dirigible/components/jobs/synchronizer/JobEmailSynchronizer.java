/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.jobs.synchronizer;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.jobs.domain.JobEmail;
import org.eclipse.dirigible.components.jobs.service.JobEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * The Class JobEmailSynchronizer.
 *
 * @param A the generic type
 */
@Component
public class JobEmailSynchronizer<A extends Artefact> implements Synchronizer<JobEmail> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JobEmailSynchronizer.class);

    /**
     * The Constant FILE_JOB_EMAIL_EXTENSION.
     */
    public static final String FILE_JOB_EMAIL_EXTENSION = ".jobemail";

    /**
     * The job email service.
     */
    private JobEmailService jobEmailService;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new job email synchronizer.
     *
     * @param jobEmailService the job email service
     */
    @Autowired
    public JobEmailSynchronizer(JobEmailService jobEmailService){this.jobEmailService = jobEmailService;}

    /**
     * Checks if is accepted.
     *
     * @param file the file
     * @param attrs the attrs
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString().endsWith(FILE_JOB_EMAIL_EXTENSION);
    }

    /**
     * Checks if is accepted.
     *
     * @param type the type
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {return JobEmail.ARTEFACT_TYPE.equals(type);}

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     */
    @Override
    public List<JobEmail> load(String location, byte[] content) {
        JobEmail jobEmail = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), JobEmail.class);
        jobEmail.setLocation(location);
        jobEmail.setName("");
        jobEmail.setType(JobEmail.ARTEFACT_TYPE);
        jobEmail.updateKey();
        try {
            getService().save(jobEmail);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            if (logger.isErrorEnabled()) {logger.error("jobEmail: {}", jobEmail);}
            if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
        }
        return List.of(jobEmail);
    }

    /**
     * Prepare.
     *
     * @param wrappers the wrappers
     * @param depleter the depleter
     */
    @Override
    public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
    }

    /**
     * Process.
     *
     * @param wrappers the wrappers
     * @param depleter the depleter
     */
    @Override
    public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
        try {
            List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ArtefactLifecycle.CREATED.toString());
            callback.registerErrors(this, results, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE_UPDATE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
        }
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<JobEmail> getService() {return jobEmailService;}

    /**
     * Cleanup.
     *
     * @param artefact the artefact
     */
    @Override
    public void cleanup(JobEmail artefact) {
        try {
            getService().delete(artefact);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
        }
    }

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE);
        return true;
    }

    /**
     * Sets the callback.
     *
     * @param callback the new callback
     */
    @Override
    public void setCallback(SynchronizerCallback callback) {this.callback = callback;}
}
