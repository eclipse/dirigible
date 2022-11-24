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
package org.eclipse.dirigible.components.security.synchronizer;

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
import org.eclipse.dirigible.components.base.helpers.GsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.security.domain.Access;
import org.eclipse.dirigible.components.security.domain.Constraints;
import org.eclipse.dirigible.components.security.service.AccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class AccessSynchronizer.
 *
 * @param <A> the generic type
 */

@Component
@Order(40)
public class AccessSynchronizer<A extends Artefact> implements Synchronizer<Access> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AccessSynchronizer.class);

    /**
     * The Constant FILE_EXTENSION_SECURITY_ACCESS.
     */
    private static final String FILE_EXTENSION_SECURITY_ACCESS = ".access";

    /**
     * The security access service.
     */
    private AccessService securityAccessService;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new security access synchronizer.
     *
     * @param securityAccessService the security access service
     */
    @Autowired
    public AccessSynchronizer(AccessService securityAccessService) {
        this.securityAccessService = securityAccessService;
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Access> getService() {
        return securityAccessService;
    }


    /**
     * Checks if is accepted.
     *
     * @param file  the file
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
        return Access.ARTEFACT_TYPE.equals(type);
    }

    /**
     * Load.
     *
     * @param location the location
     * @param content  the content
     * @return the list
     */
    @Override
    public List<Access> load(String location, byte[] content) {
        Constraints accessArtifact = GsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Constraints.class);
        Configuration.configureObject(accessArtifact);

        List<Access> accesses = accessArtifact.buildSecurityAccesses(location);

        for (Access access : accesses) {
            try {
            	access.updateKey();
            	Access maybe = getService().findByKey(access.getKey());
    			if (maybe != null) {
    				access.setId(maybe.getId());
    			}
                getService().save(access);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
                if (logger.isErrorEnabled()) {
                    logger.error("security access: {}", access);
                }
                if (logger.isErrorEnabled()) {
                    logger.error("content: {}", new String(content));
                }
            }
        }

        return accesses;
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
     * @param flow    the flow
     * @return true, if successful
     */
    @Override
    public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
        callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE_UPDATE);
        return true;
    }

    /**
     * Cleanup.
     *
     * @param access the security access
     */
    @Override
    public void cleanup(Access access) {
        try {
            getService().delete(access);
            callback.registerState(this, access, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, access, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
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
		return FILE_EXTENSION_SECURITY_ACCESS;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Access.ARTEFACT_TYPE;
	}
}
