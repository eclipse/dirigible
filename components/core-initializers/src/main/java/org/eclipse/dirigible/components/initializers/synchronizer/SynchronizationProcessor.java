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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalSorter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyFactory;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.initializers.definition.Definition;
import org.eclipse.dirigible.components.initializers.definition.DefinitionService;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class SynchronizationProcessor.
 */
@Component
public class SynchronizationProcessor implements SynchronizationWalkerCallback, SynchronizerCallback {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SynchronizationProcessor.class);
	
	/** The definitions. */
	private Map<Synchronizer<Artefact>, Map<String, Definition>> definitions = new HashMap<>();
	
	/** The artefacts. */
	private List<? extends Artefact> artefacts = new ArrayList<>();
	
	/** The repository. */
	private IRepository repository;
	
	/** The synchronizers. */
	private final List<Synchronizer<Artefact>> synchronizers;
	
	/** The errors. */
	private List<String> errors = new ArrayList<>();
	
	/** The definition service. */
	private DefinitionService definitionService;
	
	/**
	 * Instantiates a new synchronization processor.
	 *
	 * @param repository the repository
	 * @param synchronizers the synchronizers
	 * @param definitionService the definition service
	 */
	@Autowired
	public SynchronizationProcessor(IRepository repository, List<Synchronizer<Artefact>> synchronizers, DefinitionService definitionService) {
		this.repository = repository;
		this.synchronizers = synchronizers;
		this.definitionService = definitionService;
		this.synchronizers.forEach(s -> s.setCallback(this));
	}
	
	/**
	 * Process synchronizers.
	 */
	public void processSynchronizers() {
		
		if (logger.isDebugEnabled()) {logger.debug("Processing synchronizers started...");}
		
		prepare();
		
		// prepare map
		synchronizers.forEach(s -> definitions.put(s, new HashMap<>()));
		
		if (logger.isDebugEnabled()) {logger.debug("Collecting files...");}
		// collect definitions for processing
		collectFiles();
		
		if (logger.isDebugEnabled()) {logger.debug("Collecting files done. {} known types of definitions collected - {}.", synchronizers.size(),
				synchronizers.stream().map(Synchronizer::getArtefactType).collect(Collectors.toList()));}
		
		if (logger.isDebugEnabled()) {logger.debug("Loading definitions...");}
		// parse definitions to artefacts
		loadDefinitions();
		if (logger.isDebugEnabled()) {logger.debug("Loading definitions done. {} artefacts parsed. ", artefacts.size());}
		
		TopologicalSorter<TopologyWrapper<? extends Artefact>> sorter = new TopologicalSorter<>();
		TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter = new TopologicalDepleter<>();
		
		List<TopologyWrapper<? extends Artefact>> wrappers = TopologyFactory.wrap(artefacts, synchronizers);
		
		if (logger.isDebugEnabled()) {logger.debug("Topological sorting...");}
		// topological sorting by dependencies
		wrappers = sorter.sort(wrappers);
		
		// reverse the order
		Collections.reverse(wrappers);
		
		if (logger.isDebugEnabled()) {logger.debug("Preparing for processing...");}
		// preparing and depleting
		for (Synchronizer<? extends Artefact> synchronizer : synchronizers) {
			List<TopologyWrapper<? extends Artefact>> locals = 
					wrappers.stream().filter(w -> w.getSynchronizer().equals(synchronizer)).collect(Collectors.toList());
			synchronizer.prepare(locals, depleter);
		}
		if (logger.isDebugEnabled()) {logger.debug("Preparing for processing done.");}
		
		// return back to the sorted the order 
		Collections.reverse(wrappers);
		
		if (logger.isDebugEnabled()) {logger.debug("Processing of artefacts...");}
		// processing and depleting
		for (Synchronizer<? extends Artefact> synchronizer : synchronizers) {
			List<TopologyWrapper<? extends Artefact>> locals = 
					wrappers.stream().filter(w -> w.getSynchronizer().equals(synchronizer)).collect(Collectors.toList());
			synchronizer.process(locals, depleter);
		}
		if (logger.isDebugEnabled()) {logger.debug("Processing of artefacts done.");}
		
		if (logger.isDebugEnabled()) {logger.debug("Cleaning up removed artefacts...");}
		// cleanup
		for (Synchronizer<Artefact> synchronizer : synchronizers) {
			List<? extends Artefact> registered = synchronizer.getService().getAll();
			for (Artefact artefact : registered) {
				if (synchronizer.isAccepted(artefact.getType())) {
					if (!repository.getResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + artefact.getLocation()).exists()) {
						synchronizer.cleanup(artefact);
						break;
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {logger.debug("Cleaning up removed artefacts done.");}
		
		// report results
		getErrors().forEach(e -> {if (logger.isErrorEnabled()) {logger.error(e);}});
		
		if (logger.isDebugEnabled()) {logger.debug("Processing synchronizers done. {} artefacts processed.", artefacts.size());}
		// clear maps
		definitions.clear();
		artefacts.clear();
	}

	private void prepare() {
		errors.clear();
		definitions.clear();
		artefacts.clear();
	}

	/**
	 * Collect files.
	 *
	 * @param errorCallback the error callback
	 */
	private void collectFiles() {
		String registryFolder = getRegistryFolder();
		SynchronizationWalker synchronizationWalker = new SynchronizationWalker(this);
		try {
			synchronizationWalker.walk(registryFolder);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			addError(e.getMessage());
		}
	}
	
	/**
	 * Load definitions.
	 *
	 * @param errorCallback the error callback
	 */
	private void loadDefinitions() {
		for (Synchronizer<? extends Artefact> synchronizer : synchronizers) {
			for (Definition definition : definitions.get(synchronizer).values()) {
				try {
					if (definition.getContent() == null) {
						String error = String.format("Content of %s has not been loaded correctly", definition.getLocation());
						logger.error(error);
						addError(error);
						continue;
					}
					List loaded = synchronizer.load(definition.getLocation(), definition.getContent());
					artefacts.addAll(loaded);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					addError(e.getMessage());
				}
			}
		}
	}

	/**
	 * Gets the registry folder.
	 *
	 * @return the registry folder
	 */
	public String getRegistryFolder() {
		return ((LocalRepository) repository).getInternalResourcePath(IRepositoryStructure.PATH_REGISTRY_PUBLIC);
	}

	/**
	 * Visit file.
	 *
	 * @param file the file
	 * @param attrs the attrs
	 * @param location the location
	 */
	@Override
	public void visitFile(Path file, BasicFileAttributes attrs, String location) {
		checkFile(file, attrs, location);
	}

	/**
	 * Check file.
	 *
	 * @param file the file
	 * @param attrs the attrs
	 * @param location the location
	 */
	private void checkFile(Path file, BasicFileAttributes attrs, String location) {
		for (Synchronizer<Artefact> synchronizer : synchronizers) {
			if (synchronizer.isAccepted(file, attrs)) {
				// synchronizer knows this artefact, hence check whether to process it or not
				try {
					checkAndCollect(file, location, synchronizer);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				break;
			}
		}
	}

	/**
	 * Collect for processing, if new or modified.
	 *
	 * @param file the file
	 * @param location the location
	 * @param synchronizer the synchronizer
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException the file not found exception
	 */
	private void checkAndCollect(Path file, String location, Synchronizer<Artefact> synchronizer)
			throws IOException, FileNotFoundException {
		
		String type = "definition";
		if (location.indexOf('.') > 0) {
			// generate the type by the file extension
			type = location.substring(location.lastIndexOf('.') + 1);
		}
		// load the content to calculate the checksum
		byte[] content = Files.readAllBytes(file);
		if (content == null) {
			logger.error("Reading file {} returns null content", file.toString());
			return;
		}
		Definition definition = new Definition(location, FilenameUtils.getBaseName(file.getFileName().toString()), type, content);
		// check whether this artefact has been processed in the past already
		Definition maybe = definitionService.findByKey(definition.getKey());
		if (maybe != null) {
			// artefact has been processed in the past
			if (!maybe.getChecksum().equals(definition.getChecksum())) {
				// the content has been modified since the last processing
				maybe.setChecksum(definition.getChecksum());
				maybe.setState(ArtefactLifecycle.MODIFIED.toString());
				// update the artefact with the new checksum and status
				definitionService.save(maybe);
				// added to artefacts for processing
				definitions.get(synchronizer).put(definition.getKey(), definition);
			} else if (maybe.getState().equals(ArtefactLifecycle.CREATED.toString())
					|| maybe.getState().equals(ArtefactLifecycle.MODIFIED.toString())) {
				// pending from a previous run, add again for processing
				if (definitions.get(synchronizer).get(definition.getKey()) == null) {
					definitions.get(synchronizer).put(definition.getKey(), definition);
				}
			} else if (maybe.getState().equals(ArtefactLifecycle.FAILED.toString())) {
				// report the erronous state
				logger.warn("Definition with key: {} has been failed with reason {}", maybe.getKey(), maybe.getMessage());
			}
		} else {
			// artefact is new, hence stored for processing
			definition.setState(ArtefactLifecycle.CREATED.toString());
			definitionService.save(definition);
			definitions.get(synchronizer).put(definition.getKey(), definition);
		}
	}
	
	/**
	 * Sets the definition state.
	 *
	 * @param artefact the artefact
	 * @param state the state
	 * @param message the message
	 */
	public void setDefinitionState(Artefact artefact, ArtefactState state, String message) {
		Definition definition = definitionService.findByLocation(artefact.getLocation());
		if (definition == null) {
			logger.warn("Definition with location: {} does not exist, but should be.", artefact.getLocation());
			definition = new Definition(artefact.getLocation(), artefact.getName(), artefact.getType(), new byte[] {});
		}
		definition.setState(state.toString());
		definition.setMessage(message);
		definitionService.save(definition);
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
			String lifecycle, ArtefactState state) {
		if (remained.size() > 0) {
			for (TopologyWrapper<? extends Artefact> wrapper : remained) {
				String errorMessage = String.format("Undepleted Artefact of type: [%s] with key: [%s] in phase: [%s]", 
						wrapper.getArtefact().getType(), wrapper.getId(), lifecycle);
				if (logger.isErrorEnabled()) {logger.error(errorMessage);}
				errors.add(errorMessage);
				setDefinitionState(wrapper.getArtefact(), state, errorMessage);
			}
		}		
	}
	
	/**
	 * Register state.
	 *
	 * @param synchronizer the synchronizer
	 * @param wrapper the wrapper
	 * @param lifecycle the lifecycle
	 * @param state the state
	 */
	@Override
	public void registerState(Synchronizer<? extends Artefact> synchronizer, TopologyWrapper<? extends Artefact> wrapper, String lifecycle, ArtefactState state) {
		if (logger.isDebugEnabled()) {logger.debug("Processed artefact with key: {} for state: {}", wrapper.getArtefact().getKey(), state.toString());}
		registerState(synchronizer, wrapper.getArtefact(), lifecycle, state);
	}
	
	/**
	 * Register state.
	 *
	 * @param synchronizer the synchronizer
	 * @param artefact the artefact
	 * @param lifecycle the lifecycle
	 * @param state the state
	 */
	@Override
	public void registerState(Synchronizer<? extends Artefact> synchronizer, Artefact artefact, String lifecycle, ArtefactState state) {
		if (logger.isDebugEnabled()) {logger.debug("Processed artefact with key: {} for state: {}", artefact.getKey(), state.toString());}
		setDefinitionState(artefact, state, "");
	}

}
