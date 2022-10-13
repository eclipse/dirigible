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
package org.eclipse.dirigible.components.initializers.synchronizer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class SynchronizerHandler.
 */
public class SynchronizerHandler implements SynchronizerCallback {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SynchronizerHandler.class);
	
	/** The errors. */
	private List<String> errors = new ArrayList<>();
	
	/** The synchronization processor. */
	private SynchronizationProcessor synchronizationProcessor;
	
	/**
	 * Instantiates a new synchronizer handler.
	 *
	 * @param synchronizationProcessor the synchronization processor
	 */
	@Autowired
	public SynchronizerHandler(SynchronizationProcessor synchronizationProcessor) {
		this.synchronizationProcessor = synchronizationProcessor;
	}
	
	/**
	 * Adds the error.
	 *
	 * @param error the error
	 */
	@Override
	public void addError(String error) {
		this.errors.add(error);
	}
	
	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	@Override
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Register errors.
	 *
	 * @param synchronizer the synchronizer
	 * @param remained the remained
	 * @param lifecycle the lifecycle
	 * @param state the state
	 */
	@Override
	public void registerErrors(Synchronizer<? extends Artefact> synchronizer, List<TopologyWrapper<? extends Artefact>> remained, 
			ArtefactLifecycle lifecycle, ArtefactState state) {
		if (remained.size() > 0) {
			for (TopologyWrapper<? extends Artefact> wrapper : remained) {
				String errorMessage = String.format("Undepleted Artefact of type: [%s] with key: [%s] in phase: [%s]", 
						wrapper.getArtefact().getType(), wrapper.getId(), lifecycle.toString());
				if (logger.isErrorEnabled()) {logger.error(errorMessage);}
				errors.add(errorMessage);
				synchronizationProcessor.setDefinitionState(wrapper.getArtefact(), state, errorMessage);
			}
		}		
	}
	
	// TODO successful state

}
