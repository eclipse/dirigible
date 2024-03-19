/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.synchronizer;

import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.BaseSynchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.engine.bpm.flowable.domain.Bpmn;
import org.eclipse.dirigible.components.engine.bpm.flowable.provider.BpmProviderFlowable;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.BpmnService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * The Class BpmnSynchronizer.
 */
@Component
@Order(SynchronizersOrder.BPMN)
public class BpmnSynchronizer extends BaseSynchronizer<Bpmn, Long> {

    /** The Constant FILE_EXTENSION_BPMN. */
    public static final String FILE_EXTENSION_BPMN = ".bpmn";
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(BpmnSynchronizer.class);
    /** The bpmn service. */
    private final BpmnService bpmnService;

    /** The bpmn service. */
    private final BpmProviderFlowable bpmProviderFlowable;

    /** The synchronization callback. */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new bpmn synchronizer.
     *
     * @param bpmnService the bpmn service
     * @param bpmProviderFlowable the bpm provider flowable
     */
    @Autowired
    public BpmnSynchronizer(BpmnService bpmnService, BpmProviderFlowable bpmProviderFlowable) {
        this.bpmnService = bpmnService;
        this.bpmProviderFlowable = bpmProviderFlowable;
    }

    /**
     * Gets the bpm provider flowable.
     *
     * @return the bpm provider flowable
     */
    public BpmProviderFlowable getBpmProviderFlowable() {
        return bpmProviderFlowable;
    }

    /**
     * Checks if is accepted.
     *
     * @param type the artefact
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {
        return Bpmn.ARTEFACT_TYPE.equals(type);
    }

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     * @throws ParseException the parse exception
     */
    @Override
    public List<Bpmn> parse(String location, byte[] content) throws ParseException {
        Bpmn bpmn = new Bpmn();
        bpmn.setLocation(location);
        bpmn.setName(Paths.get(location)
                          .getFileName()
                          .toString());
        bpmn.setType(Bpmn.ARTEFACT_TYPE);
        bpmn.updateKey();
        bpmn.setContent(content);
        try {
            Bpmn maybe = getService().findByKey(bpmn.getKey());
            if (maybe != null) {
                bpmn.setId(maybe.getId());
            }
            bpmn = getService().save(bpmn);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            if (logger.isErrorEnabled()) {
                logger.error("bpmn: {}", bpmn);
            }
            if (logger.isErrorEnabled()) {
                logger.error("content: {}", new String(content));
            }
            throw new ParseException(e.getMessage(), 0);
        }
        return List.of(bpmn);
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Bpmn, Long> getService() {
        return bpmnService;
    }

    /**
     * Retrieve.
     *
     * @param location the location
     * @return the list
     */
    @Override
    public List<Bpmn> retrieve(String location) {
        return getService().getAll();
    }

    /**
     * Sets the status.
     *
     * @param artefact the artefact
     * @param lifecycle the lifecycle
     * @param error the error
     */
    @Override
    public void setStatus(Bpmn artefact, ArtefactLifecycle lifecycle, String error) {
        artefact.setLifecycle(lifecycle);
        artefact.setError(error);
        getService().save(artefact);
    }

    /**
     * Complete.
     *
     * @param wrapper the wrapper
     * @param flow the flow
     * @return true, if successful
     */
    @Override
    protected boolean completeImpl(TopologyWrapper<Bpmn> wrapper, ArtefactPhase flow) {

        try {
            Bpmn bpmn = wrapper.getArtefact();

            switch (flow) {
                case CREATE:
                    if (ArtefactLifecycle.NEW.equals(bpmn.getLifecycle())) {
                        deployOnProcessEngine(bpmn);
                        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
                    }
                    break;
                case UPDATE:
                    if (ArtefactLifecycle.MODIFIED.equals(bpmn.getLifecycle())) {
                        updateOnProcessEngine(bpmn);
                        callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
                    }
                    if (ArtefactLifecycle.FAILED.equals(bpmn.getLifecycle())) {
                        return false;
                    }
                    break;
                case DELETE:
                    if (ArtefactLifecycle.CREATED.equals(bpmn.getLifecycle()) || ArtefactLifecycle.UPDATED.equals(bpmn.getLifecycle())
                            || ArtefactLifecycle.FAILED.equals(bpmn.getLifecycle())) {
                        removeFromProcessEngine(bpmn);
                        callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
                    }
                    break;
            }
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, wrapper, ArtefactLifecycle.FAILED, e.getMessage());
            return false;
        }
    }

    /**
     * Deploy on process engine.
     *
     * @param bpmn the bpmn
     */
    private void deployOnProcessEngine(Bpmn bpmn) {
        try {
            ProcessEngine processEngine = bpmProviderFlowable.getProcessEngine();
            RepositoryService repositoryService = processEngine.getRepositoryService();

            Deployment deployment = repositoryService.createDeployment()
                                                     .key(bpmn.getLocation())
                                                     .addBytes(bpmn.getLocation(), bpmn.getContent())
                                                     .deploy();
            List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                                                                          .deploymentId(deployment.getId())
                                                                          .list();
            if (processDefinitions.size() > 0) {
                ProcessDefinition processDefinition = processDefinitions.get(0);
                bpmn.setDeploymentId(processDefinition.getDeploymentId());
                bpmn.setProcessDefinitionId(processDefinition.getId());
                bpmn.setProcessDefinitionKey(processDefinition.getKey());
                bpmn.setProcessDefinitionName(processDefinition.getName());
                bpmn.setProcessDefinitionVersion(processDefinition.getVersion());
                bpmn.setProcessDefinitionTenantId(processDefinition.getTenantId());
                bpmn.setProcessDefinitionCategory(processDefinition.getCategory());
                bpmn.setProcessDefinitionDescription(processDefinition.getDescription());
            }
            if (logger.isInfoEnabled()) {
                logger.info(
                        format("Deployed: [{0}] with key: [{1}] on the Flowable BPMN Engine.", deployment.getId(), deployment.getKey()));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error on deploying a BPMN file from location: {}", bpmn.getLocation(), e);
            }
        }
    }

    /**
     * Deploy on process engine.
     *
     * @param bpmn the bpmn
     */
    private void updateOnProcessEngine(Bpmn bpmn) {
        try {
            ProcessEngine processEngine = bpmProviderFlowable.getProcessEngine();
            RepositoryService repositoryService = processEngine.getRepositoryService();

            List<Deployment> deployments = repositoryService.createDeploymentQuery()
                                                            .list();
            for (Deployment deployment : deployments) {
                if (logger.isTraceEnabled()) {
                    logger.trace(format("Deployment: [{0}] with key: [{1}]", deployment.getId(), deployment.getKey()));
                }
                if (bpmn.getLocation()
                        .equals(deployment.getKey())) {
                    List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                                                                                  .deploymentId(deployment.getId())
                                                                                  .active()
                                                                                  .list();
                    if (processDefinitions.size() > 0) {
                        ProcessDefinition processDefinition = processDefinitions.get(0);
                        // repositoryService.suspendProcessDefinitionById(processDefinition.getId());
                        repositoryService.deleteDeployment(processDefinition.getDeploymentId());
                        Deployment newDeployment = repositoryService.createDeployment()
                                                                    .key(bpmn.getLocation())
                                                                    .addBytes(bpmn.getLocation(), bpmn.getContent())
                                                                    .deploy();
                        processDefinitions = repositoryService.createProcessDefinitionQuery()
                                                              .deploymentId(newDeployment.getId())
                                                              .list();
                        if (processDefinitions.size() > 0) {
                            processDefinition = processDefinitions.get(0);
                            bpmn.setDeploymentId(processDefinition.getDeploymentId());
                            bpmn.setProcessDefinitionId(processDefinition.getId());
                            bpmn.setProcessDefinitionKey(processDefinition.getKey());
                            bpmn.setProcessDefinitionName(processDefinition.getName());
                            bpmn.setProcessDefinitionVersion(processDefinition.getVersion());
                            bpmn.setProcessDefinitionTenantId(processDefinition.getTenantId());
                            bpmn.setProcessDefinitionCategory(processDefinition.getCategory());
                            bpmn.setProcessDefinitionDescription(processDefinition.getDescription());
                            if (logger.isInfoEnabled()) {
                                logger.info(format("Updated deployment: [{0}] with key: [{1}] on the Flowable BPMN Engine.",
                                        deployment.getId(), deployment.getKey()));
                            }
                            return;
                        }
                    }
                }

                // backup if the definitions is modified, but the old version get broken and does not exist in the
                // process engine
                deployOnProcessEngine(bpmn);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Error on deploying a BPMN file from location: {}", bpmn.getLocation(), e);
            }
        }
    }

    /**
     * Removes the from process engine.
     *
     * @param bpmn the bpmn
     */
    private void removeFromProcessEngine(Bpmn bpmn) {

        ProcessEngine processEngine = bpmProviderFlowable.getProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                                                        .list();
        for (Deployment deployment : deployments) {
            if (logger.isTraceEnabled()) {
                logger.trace(format("Deployment: [{0}] with key: [{1}]", deployment.getId(), deployment.getKey()));
            }
            if (bpmn.getLocation()
                    .equals(deployment.getKey())) {
                repositoryService.deleteDeployment(deployment.getId(), true);
                if (logger.isInfoEnabled()) {
                    logger.info(format("Deleted deployment: [{0}] with key: [{1}] on the Flowable BPMN Engine.", deployment.getId(),
                            deployment.getKey()));
                }
                break;
            }
        }

    }

    /**
     * Cleanup.
     *
     * @param bpmn the bpmn
     */
    @Override
    public void cleanupImpl(Bpmn bpmn) {
        try {
            removeFromProcessEngine(bpmn);
            getService().delete(bpmn);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            callback.addError(e.getMessage());
            callback.registerState(this, bpmn, ArtefactLifecycle.DELETED, e.getMessage());
        }
    }

    /**
     * Sets the callback.
     *
     * @param callback the new callback
     */
    @Override
    public void setCallback(SynchronizerCallback callback) {
        this.callback = callback;
    }

    /**
     * Gets the file bpmn.
     *
     * @return the file bpmn
     */
    @Override
    public String getFileExtension() {
        return FILE_EXTENSION_BPMN;
    }

    /**
     * Gets the artefact type.
     *
     * @return the artefact type
     */
    @Override
    public String getArtefactType() {
        return Bpmn.ARTEFACT_TYPE;
    }

}
