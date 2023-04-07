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
package org.eclipse.dirigible.components.listeners.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.components.listeners.service.ListenerService;
import org.eclipse.dirigible.components.listeners.service.ListenersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ListenerSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(60)
public class ListenerSynchronizer<A extends Artefact> implements Synchronizer<Listener> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(ListenerSynchronizer.class);

    /** The Constant FILE_EXTENSION_LISTENER. */
    private static final String FILE_EXTENSION_LISTENER = ".listener";

    /** The callback. */
    private SynchronizerCallback callback;

    /** The listener service. */
    @Autowired
    private ListenerService listenerService;
    
    /** The Scheduler manager. */
    @Autowired
    private ListenersManager listenersManager;

    /**
     * Checks if is accepted.
     *
     * @param file the file
     * @param attrs the attrs
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(Path file, BasicFileAttributes attrs) {
        return file.toString().endsWith(FILE_EXTENSION_LISTENER);
    }

    /**
     * Checks if is accepted.
     *
     * @param type the type
     * @return true, if is accepted
     */
    @Override
    public boolean isAccepted(String type) {
        return Listener.ARTEFACT_TYPE.equals(type);
    }

    /**
     * Load.
     *
     * @param location the location
     * @param content the content
     * @return the list
     */
    @Override
    public List<Listener> load(String location, byte[] content) {
        Listener listener = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Listener.class);
        Configuration.configureObject(listener);
        listener.setLocation(location);
        listener.setType(Listener.ARTEFACT_TYPE);
        listener.updateKey();
        try {
        	Listener maybe = getService().findByKey(listener.getKey());
			if (maybe != null) {
				listener.setId(maybe.getId());
			}
            getService().save(listener);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            if (logger.isErrorEnabled()) {logger.error("listener: {}", listener);}
            if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
        }
        return List.of(listener);
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
    public ArtefactService<Listener> getService() {
        return listenerService;
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
    	Listener listener = null;
        if (wrapper.getArtefact() instanceof Listener){
            listener = (Listener) wrapper.getArtefact();
            ArtefactLifecycle flag = ArtefactLifecycle.valueOf(flow);
            switch (flag){
                case CREATED:
					try {
						listenersManager.startListener(listener);
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE, "");
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			            callback.addError(e.getMessage());
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE, e.getMessage());
					}
                    break;
                case UPDATED:
                	try {
                		listenersManager.stopListener(listener);
						listenersManager.startListener(listener);
						callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED.toString(), ArtefactState.SUCCESSFUL_UPDATE, "");
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			            callback.addError(e.getMessage());
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_UPDATE, e.getMessage());
					}
                    break;
                default:
    				callback.registerState(this, wrapper, ArtefactLifecycle.FAILED.toString(), ArtefactState.FAILED, "Unknown flow: " + flow);
    				throw new UnsupportedOperationException(flow);
            }
        } else {
            String message = String.format("Trying to process %s as Listener", wrapper.getArtefact().getClass());
            callback.registerState(this, wrapper, ArtefactLifecycle.FAILED.toString(), ArtefactState.FAILED, message);
			throw new UnsupportedOperationException(message);
        }
        
        return true;
    }
    
    /**
     * Cleanup.
     *
     * @param listener the listener
     */
    @Override
    public void cleanup(Listener listener) {
        try {
        	listenersManager.stopListener(listener);
            getService().delete(listener);
            callback.registerState(this, listener, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE, "");
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, listener, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE, e.getMessage());
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
     * Gets the file extension.
     *
     * @return the file extension
     */
    @Override
	public String getFileExtension() {
		return FILE_EXTENSION_LISTENER;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Listener.ARTEFACT_TYPE;
	}
}
