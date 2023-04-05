/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.synchronizer;

import static java.text.MessageFormat.format;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.engine.bpm.flowable.domain.Bpmn;
import org.eclipse.dirigible.components.engine.bpm.flowable.provider.BpmProviderFlowable;
import org.eclipse.dirigible.components.engine.bpm.flowable.service.BpmnService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class BpmnSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(300)
public class BpmnSynchronizer<A extends Artefact> implements Synchronizer<Bpmn> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(BpmnSynchronizer.class);
	
	/** The Constant FILE_EXTENSION_BPMN. */
	public static final String FILE_EXTENSION_BPMN = ".bpmn";
	
	/** The bpmn service. */
	private BpmnService bpmnService;
	
	/** The bpmn service. */
	private BpmProviderFlowable bpmProviderFlowable;
	
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
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Bpmn> getService() {
		return bpmnService;
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
	 * @param file the file
	 * @param attrs the attrs
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(Path file, BasicFileAttributes attrs) {
		return file.toString().endsWith(getFileExtension());
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
	 */
	@Override
	public List<Bpmn> load(String location, byte[] content) {
		Bpmn bpmn = new Bpmn();
		bpmn.setLocation(location);
		bpmn.setName(Paths.get(location).getFileName().toString());
		bpmn.setType(Bpmn.ARTEFACT_TYPE);
		bpmn.updateKey();
		bpmn.setContent(content);
		try {
			Bpmn maybe = getService().findByKey(bpmn.getKey());
			if (maybe != null) {
				bpmn.setId(maybe.getId());
			}
			getService().save(bpmn);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("bpmn: {}", bpmn);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(bpmn);
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
	 * Complete.
	 *
	 * @param wrapper the wrapper
	 * @param flow the flow
	 * @return true, if successful
	 */
	@Override
	public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
		
		Bpmn bpmn = null;
		if (wrapper.getArtefact() instanceof Bpmn) {
			bpmn = (Bpmn) wrapper.getArtefact();
		} else {
			throw new UnsupportedOperationException(String.format("Trying to process %s as BPMN", wrapper.getArtefact().getClass()));
		}
		
		deployOnProcessEngine(bpmn);
		
		callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE, "");
		return true;
	}

	/**
	 * Cleanup.
	 *
	 * @param bpmn the bpmn
	 */
	@Override
	public void cleanup(Bpmn bpmn) {
		try {
			getService().delete(bpmn);
			
			removeFromProcessEngine(bpmn);
			
			callback.registerState(this, bpmn, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE, "");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, bpmn, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE, e.getMessage());
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
	
	/**
	 * Deploy on process engine.
	 *
	 * @param bpmn the bpmn
	 */
	private void deployOnProcessEngine(Bpmn bpmn) {
		try {
			ProcessEngine processEngine = (ProcessEngine) bpmProviderFlowable.getProcessEngine();
			RepositoryService repositoryService = processEngine.getRepositoryService();
			
			Deployment deployment = repositoryService.createDeployment()
				.key(bpmn.getLocation())
				.addBytes(bpmn.getLocation(), bpmn.getContent())
				.deploy();
			if (logger.isInfoEnabled()) {logger.info(format("Deployed: [{0}] with key: [{1}] on the Flowable BPMN Engine.", deployment.getId(), deployment.getKey()));}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error("Error on deploying a BPMN file from location: {}", bpmn.getLocation(), e);}
		}
	}
	
	/**
	 * Removes the from process engine.
	 *
	 * @param bpmn the bpmn
	 */
	private void removeFromProcessEngine(Bpmn bpmn) {
		
		ProcessEngine processEngine = (ProcessEngine) bpmProviderFlowable.getProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		
		List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
		for (Deployment deployment : deployments) {
			if (logger.isTraceEnabled()) {logger.trace(format("Deployment: [{0}] with key: [{1}]", deployment.getId(), deployment.getKey()));}
			if (bpmn.getLocation().equals(deployment.getKey())) {
				repositoryService.deleteDeployment(deployment.getId(), true);
				if (logger.isInfoEnabled()) {logger.info(format("Deleted deployment: [{0}] with key: [{1}] on the Flowable BPMN Engine.", deployment.getId(), deployment.getKey()));}
			}
		}

	}

}
