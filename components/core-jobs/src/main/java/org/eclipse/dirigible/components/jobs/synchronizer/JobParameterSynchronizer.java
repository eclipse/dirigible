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
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.jobs.domain.JobParameter;
import org.eclipse.dirigible.components.jobs.service.JobParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * The Class JobParameterSynchronizer.
 *
 * @param A the generic type
 */
@Component
public class JobParameterSynchronizer<A extends Artefact> implements Synchronizer<JobParameter> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JobParameterSynchronizer.class);

    /**
     * The Constant FILE_JOB_PARAMETER_EXTENSION.
     */
    public static final String FILE_JOB_PARAMETER_EXTENSION = ".jobparameter";

    /**
     * The job parameter service.
     */
    private JobParameterService jobParameterService;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new job parameter synchronizer.
     *
     * @param jobParameterService the job parameter service
     */
    @Autowired
    public JobParameterSynchronizer(JobParameterService jobParameterService){this.jobParameterService = jobParameterService;}

    /**
     * Checks if is accepted.
     *
     * @param file the file
     * @param attrs the attrs
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString().endsWith(FILE_JOB_PARAMETER_EXTENSION);
    }

    /**
     * Checks if is accepted.
     *
     * @param type the type
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {return JobParameter.ARTEFACT_TYPE.equals(type);}

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     */
    @Override
    public List<JobParameter> load(String location, byte[] content) {
        JobParameter jobParameter = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), JobParameter.class);
        jobParameter.setLocation(location);
        jobParameter.setName("");
        jobParameter.setType(JobParameter.ARTEFACT_TYPE);
        jobParameter.updateKey();
        try {
            getService().save(jobParameter);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            if (logger.isErrorEnabled()) {logger.error("jobParameter: {}", jobParameter);}
            if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
        }
        return List.of(jobParameter);
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
    public ArtefactService<JobParameter> getService() {return jobParameterService;}

    /**
     * Cleanup.
     *
     * @param artefact the artefact
     */
    @Override
    public void cleanup(JobParameter artefact) {
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
