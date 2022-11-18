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

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class SecurityRoleSynchronizer.
 *
 * @param <A> the generic type
 */

@Component
@Order(30)
public class RoleSynchronizer<A extends Artefact> implements Synchronizer<Role> {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RoleSynchronizer.class);

    /**
     * The Constant FILE_EXTENSION_SECURITY_ROLE.
     */
    public static final String FILE_EXTENSION_SECURITY_ROLE = ".role";

    /**
     * The security role service.
     */
    private RoleService securityRoleService;

    /**
     * The synchronization callback.
     */
    private SynchronizerCallback callback;

    /**
     * Instantiates a new security role synchronizer.
     *
     * @param securityRoleService the security role service
     */
    @Autowired
    public RoleSynchronizer(RoleService securityRoleService) {
        this.securityRoleService = securityRoleService;
    }

    /**
     * Gets the service.
     *
     * @return the service
     */
    @Override
    public ArtefactService<Role> getService() {
        return securityRoleService;
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
        return Role.ARTEFACT_TYPE.equals(type);
    }

    /**
     * Load.
     *
     * @param location the location
     * @param content  the content
     * @return the list
     */
    @Override
    public List load(String location, byte[] content) {
        Role[] securityRoles = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), Role[].class);

        Integer roleIndex = 1;

        for (Role role : securityRoles) {
            role.setLocation(location);
            role.setName(roleIndex.toString());
            role.setType(Role.ARTEFACT_TYPE);
            role.updateKey();

            try {
            	Role maybe = getService().findByKey(role.getKey());
    			if (maybe != null) {
    				role.setId(maybe.getId());
    			}
                getService().save(role);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
                if (logger.isErrorEnabled()) {logger.error("security role: {}", role);}
                if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
            }
            roleIndex++;
        }
        return List.of(securityRoles);
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
     * @param role the security role
     */
    @Override
    public void cleanup(Role role) {
        try {
            getService().delete(role);
            callback.registerState(this, role, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            callback.addError(e.getMessage());
            callback.registerState(this, role, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
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
		return FILE_EXTENSION_SECURITY_ROLE;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Role.ARTEFACT_TYPE;
	}
}
