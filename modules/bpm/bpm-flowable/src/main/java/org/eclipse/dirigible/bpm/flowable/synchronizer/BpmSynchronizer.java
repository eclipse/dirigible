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
/*

 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.bpm.flowable.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.bpm.api.BpmException;
import org.eclipse.dirigible.bpm.flowable.BpmProviderFlowable;
import org.eclipse.dirigible.bpm.flowable.api.IBpmCoreService;
import org.eclipse.dirigible.bpm.flowable.artefacts.BpmSynchronizationArtefactType;
import org.eclipse.dirigible.bpm.flowable.definition.BpmDefinition;
import org.eclipse.dirigible.bpm.flowable.service.BpmCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class BpmnSynchronizer.
 */
public class BpmSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(BpmSynchronizer.class);

	private static final Map<String, BpmDefinition> BPMN_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, BpmDefinition>());

	private static final Map<String, BpmDefinition> BPMN_SYNCHRONIZED = Collections.synchronizedMap(new HashMap<String, BpmDefinition>());

	private BpmCoreService bpmCoreService = new BpmCoreService();
	
	private BpmProviderFlowable bpmProviderFlowable = new BpmProviderFlowable();
	
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final BpmSynchronizationArtefactType BPM_ARTEFACT = new BpmSynchronizationArtefactType();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (BpmSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing BPMN files...");
				try {
					if (isSynchronizationEnabled()) {
						if (isSynchronizerSuccessful("org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer")) {
							startSynchronization(SYNCHRONIZER_NAME);
							clearCache();
							synchronizePredelivered();
							synchronizeRegistry();
							updateProcessEngine();
							int immutableCount = BPMN_PREDELIVERED.size();
							int mutableCount = BPMN_SYNCHRONIZED.size();
							cleanup();
							clearCache();
							successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
						} else {
							failedSynchronization(SYNCHRONIZER_NAME, "Skipped due to dependency: org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer");
						}
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for BPMN files failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for BPMN files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing BPMN files.");
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		BpmSynchronizer synchronizer = new BpmSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered BPMN files.
	 *
	 * @param bpmnPath
	 *            the BPMN file path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredBpmnFiles(String bpmnPath) throws IOException {
		InputStream in = BpmSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + bpmnPath);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			BpmDefinition bpmDefinition = new BpmDefinition();
			bpmDefinition.setLocation(bpmnPath);
			bpmDefinition.setHash(DigestUtils.md5Hex(content));
			bpmDefinition.setContent(content);
			BPMN_PREDELIVERED.put(bpmnPath, bpmDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void clearCache() {
		BPMN_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered BPMN files...");
		// BPMN files
		for (BpmDefinition bpmDefinition : BPMN_PREDELIVERED.values()) {
			synchronizeBpm(bpmDefinition);
		}
		logger.trace("Done synchronizing predelivered Extension Points and Extensions.");
	}

	private void synchronizeBpm(BpmDefinition bpmDefinition) throws SynchronizationException {
		try {
			if (!bpmCoreService.existsBpm(bpmDefinition.getLocation())) {
				bpmCoreService.createBpm(bpmDefinition.getLocation(), bpmDefinition.getHash());
				deployOnProcessEngine(bpmDefinition);
				logger.info("Synchronized a new BPMN file from location: {}", bpmDefinition.getLocation());
				applyArtefactState(bpmDefinition, BPM_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				BpmDefinition existing = bpmCoreService.getBpm(bpmDefinition.getLocation());
				if (!bpmDefinition.equals(existing)) {
					bpmCoreService.updateBpm(bpmDefinition.getLocation(), bpmDefinition.getHash());
					deployOnProcessEngine(bpmDefinition);
					logger.info("Synchronized a modified BPMN file from location: {}", bpmDefinition.getLocation());
					applyArtefactState(bpmDefinition, BPM_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			BPMN_SYNCHRONIZED.put(bpmDefinition.getLocation(), bpmDefinition);
		} catch (BpmException e) {
			applyArtefactState(bpmDefinition, BPM_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing BPMN files from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing BPMN files from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IBpmCoreService.FILE_EXTENSION_BPMN)) {
			BpmDefinition bpmDefinition = new BpmDefinition();
			String path = getRegistryPath(resource);
			String content = new String(resource.getContent(), StandardCharsets.UTF_8);
			bpmDefinition.setLocation(path);
			bpmDefinition.setHash(DigestUtils.md5Hex(content));
			bpmDefinition.setContent(content);
			synchronizeBpm(bpmDefinition);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up BPMN files...");
		super.cleanup();

		try {
			List<BpmDefinition> bpmDefinitions = bpmCoreService.getBpmList();
			for (BpmDefinition bpmDefinition : bpmDefinitions) {
				if (!BPMN_SYNCHRONIZED.keySet().contains(bpmDefinition.getLocation())) {
					bpmCoreService.removeBpm(bpmDefinition.getLocation());
					logger.warn("Cleaned up BPMN file from location: {}", bpmDefinition.getLocation());
				}
			}
		} catch (BpmException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up BPMN files.");
	}
	
	private void deployOnProcessEngine(BpmDefinition bpmDefinition) {
		try {
			ProcessEngine processEngine = (ProcessEngine) bpmProviderFlowable.getProcessEngine();
			RepositoryService repositoryService = processEngine.getRepositoryService();
			
			Deployment deployment = repositoryService.createDeployment()
				.key(bpmDefinition.getLocation())
				.addBytes(bpmDefinition.getLocation(), bpmDefinition.getContent().getBytes(StandardCharsets.UTF_8))
				.deploy();
			logger.info(format("Deployed: [{0}] with key: [{1}] on the Flowable BPMN Engine.", deployment.getId(), deployment.getKey()));
		} catch (Exception e) {
			logger.error("Error on deploying a BPMN file from location: {}", bpmDefinition.getLocation(), e);
		}
	}
	
	private void updateProcessEngine() {
		if (BPMN_SYNCHRONIZED.isEmpty()) {
			logger.trace("No BPMN files to update.");
			return;
		}
		
		ProcessEngine processEngine = (ProcessEngine) bpmProviderFlowable.getProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		
		List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
		for (Deployment deployment : deployments) {
			logger.trace(format("Deployment: [{0}] with key: [{1}]", deployment.getId(), deployment.getKey()));
			if (!BPMN_SYNCHRONIZED.containsKey(deployment.getKey())) {
				repositoryService.deleteDeployment(deployment.getId(), true);
				logger.info(format("Deleted deployment: [{0}] with key: [{1}] on the Flowable BPMN Engine.", deployment.getId(), deployment.getKey()));
			}
		}

	}
}
