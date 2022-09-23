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
package org.eclipse.dirigible.core.security.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.core.security.api.AccessException;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.eclipse.dirigible.core.security.artefacts.AccessSynchronizationArtefactType;
import org.eclipse.dirigible.core.security.artefacts.RoleSynchronizationArtefactType;
import org.eclipse.dirigible.core.security.definition.AccessDefinition;
import org.eclipse.dirigible.core.security.definition.RoleDefinition;
import org.eclipse.dirigible.core.security.service.SecurityCoreService;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Security Synchronizer.
 */
public class SecuritySynchronizer extends AbstractSynchronizer {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SecuritySynchronizer.class);

	/** The Constant ROLES_PREDELIVERED. */
	private static final Map<String, RoleDefinition[]> ROLES_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, RoleDefinition[]>());

	/** The Constant ACCESS_PREDELIVERED. */
	private static final Map<String, List<AccessDefinition>> ACCESS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, List<AccessDefinition>>());

	/** The Constant ROLES_SYNCHRONIZED. */
	private static final Set<String> ROLES_SYNCHRONIZED = Collections.synchronizedSet(new HashSet<String>());

	/** The Constant ACCESS_SYNCHRONIZED. */
	private static final Set<String> ACCESS_SYNCHRONIZED = Collections.synchronizedSet(new HashSet<String>());

	/** The security core service. */
	private SecurityCoreService securityCoreService = new SecurityCoreService();
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	/** The Constant ACCESS_ARTEFACT. */
	private static final AccessSynchronizationArtefactType ACCESS_ARTEFACT = new AccessSynchronizationArtefactType();
	
	/** The Constant ROLE_ARTEFACT. */
	private static final RoleSynchronizationArtefactType ROLE_ARTEFACT = new RoleSynchronizationArtefactType();

	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (SecuritySynchronizer.class) {
			if (beforeSynchronizing()) {
				if (logger.isTraceEnabled()) {logger.trace("Synchronizing Roles and Access artifacts...");}
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						int immutableRolesCount = ROLES_PREDELIVERED.size();
						int immutableAccessCount = ACCESS_PREDELIVERED.size();
						int mutableRolesCount = ROLES_SYNCHRONIZED.size();
						int mutableAccessCount = ACCESS_SYNCHRONIZED.size();
						cleanup();
						clearCache();
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable Roles: {0}, Immutable Accesses: {1}, Mutable Roles: {2}, Mutable Accesses: {3}", 
								immutableRolesCount, immutableAccessCount, mutableRolesCount, mutableAccessCount));
					} else {
						if (logger.isDebugEnabled()) {logger.debug("Synchronization has been disabled");}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Roles and Access artifacts failed.", e);}
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						if (logger.isErrorEnabled()) {logger.error("Synchronizing process for Roles and Access files failed in registering the state log.", e);}
					}
				}
				if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Roles and Access artifacts.");}
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		SecuritySynchronizer synchronizer = new SecuritySynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered roles.
	 *
	 * @param rolesPath
	 *            the roles path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredRoles(String rolesPath) throws IOException {
		InputStream in = SecuritySynchronizer.class.getResourceAsStream("/META-INF/dirigible" + rolesPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			RoleDefinition[] roleDefinitions = securityCoreService.parseRoles(json);
			for (RoleDefinition roleDefinition : roleDefinitions) {
				roleDefinition.setLocation(rolesPath);
			}
			ROLES_PREDELIVERED.put(rolesPath, roleDefinitions);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Register pre-delivered access.
	 *
	 * @param accessPath
	 *            the access path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredAccess(String accessPath) throws IOException {
		InputStream in = SecuritySynchronizer.class.getResourceAsStream("/META-INF/dirigible" + accessPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			List<AccessDefinition> accessDefinitions = securityCoreService.parseAccessDefinitions(json);
			for (AccessDefinition accessDefinition : accessDefinitions) {
				accessDefinition.setLocation(accessPath);
			}
			ACCESS_PREDELIVERED.put(accessPath, accessDefinitions);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		ROLES_SYNCHRONIZED.clear();
		ACCESS_SYNCHRONIZED.clear();
		securityCoreService.clearCache();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing predelivered Roles and Access artifacts...");}

		// Roles
		for (RoleDefinition[] roleDefinitions : ROLES_PREDELIVERED.values()) {
			for (RoleDefinition roleDefinition : roleDefinitions) {
				synchronizeRole(roleDefinition);
			}
		}

		// Access
		for (List<AccessDefinition> accessDefinitions : ACCESS_PREDELIVERED.values()) {
			for (AccessDefinition accessDefinition : accessDefinitions) {
				accessDefinition.setHash("" + accessDefinition.hashCode());
				synchronizeAccess(accessDefinition);
			}
		}

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing predelivered Roles and Access artifacts.");}
	}

	/**
	 * Synchronize role.
	 *
	 * @param roleDefinition
	 *            the role definition
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeRole(RoleDefinition roleDefinition) throws SynchronizationException {
		try {
			if (!securityCoreService.existsRole(roleDefinition.getName())) {
				securityCoreService.createRole(roleDefinition.getName(), roleDefinition.getLocation(), roleDefinition.getDescription());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new Role [{}] from location: {}", roleDefinition.getName(), roleDefinition.getLocation());}
				applyArtefactState(roleDefinition, ROLE_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				RoleDefinition existing = securityCoreService.getRole(roleDefinition.getName());
				if (!roleDefinition.equals(existing)) {
					if (!roleDefinition.getLocation().equals(existing.getLocation())) {
						String errorMessage = format("Trying to update the Role [{0}] already set from location [{1}] with a location [{2}]",
								roleDefinition.getName(), existing.getLocation(), roleDefinition.getLocation());
						applyArtefactState(roleDefinition, ROLE_ARTEFACT, ArtefactState.FAILED_UPDATE, errorMessage);
						throw new SynchronizationException(errorMessage);
					}
					securityCoreService.updateRole(roleDefinition.getName(), roleDefinition.getLocation(), roleDefinition.getDescription());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified Role [{}] from location: {}", roleDefinition.getName(), roleDefinition.getLocation());}
					applyArtefactState(roleDefinition, ROLE_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			ACCESS_SYNCHRONIZED.add(roleDefinition.getLocation());
		} catch (AccessException e) {
			applyArtefactState(roleDefinition, ROLE_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, roleDefinition.getLocation(), ROLE_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize access.
	 *
	 * @param accessDefinition
	 *            the access definition
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeAccess(AccessDefinition accessDefinition) throws SynchronizationException {
		try {
			if (!securityCoreService.existsAccessDefinition(accessDefinition.getScope(), accessDefinition.getPath(), accessDefinition.getMethod(), accessDefinition.getRole())) {
				securityCoreService.createAccessDefinition(accessDefinition.getLocation(), accessDefinition.getScope(), accessDefinition.getPath(), accessDefinition.getMethod(),
						accessDefinition.getRole(), accessDefinition.getDescription(), accessDefinition.getHash());
				if (logger.isInfoEnabled()) {logger.info("Synchronized a new Access definition [[{}]-[{}]-[{}]] from location: {}", accessDefinition.getPath(),
						accessDefinition.getMethod(), accessDefinition.getRole(), accessDefinition.getLocation());}
				applyArtefactState(accessDefinition, ACCESS_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				AccessDefinition existing = securityCoreService.getAccessDefinition(accessDefinition.getScope(), accessDefinition.getPath(), accessDefinition.getMethod(),
						accessDefinition.getRole());
				if (!accessDefinition.equals(existing)) {
					if (!accessDefinition.getLocation().equals(existing.getLocation())) {
						String errorMessage = format("Trying to update the Access definition for [{0}-{1}-{2}] already set from location [{3}] with a location [{4}]",
								accessDefinition.getPath(), accessDefinition.getMethod(), accessDefinition.getRole(), existing.getLocation(),
								accessDefinition.getLocation());
						applyArtefactState(accessDefinition, ACCESS_ARTEFACT, ArtefactState.FAILED_UPDATE, errorMessage);
						throw new SynchronizationException(errorMessage);
					}
					securityCoreService.updateAccessDefinition(existing.getId(), accessDefinition.getLocation(), accessDefinition.getScope(), accessDefinition.getPath(),
							accessDefinition.getMethod(), accessDefinition.getRole(), accessDefinition.getDescription(), accessDefinition.getHash());
					if (logger.isInfoEnabled()) {logger.info("Synchronized a modified Access definition [[{}]-[{}]-[{}]] from location: {}", accessDefinition.getPath(),
							accessDefinition.getMethod(), accessDefinition.getRole(), accessDefinition.getLocation());}
					applyArtefactState(accessDefinition, ACCESS_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			ACCESS_SYNCHRONIZED.add(accessDefinition.getLocation());
		} catch (AccessException e) {
			applyArtefactState(accessDefinition, ACCESS_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, accessDefinition.getLocation(), ACCESS_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize registry.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Synchronizing Extension Points and Extensions from Registry...");}

		super.synchronizeRegistry();

		if (logger.isTraceEnabled()) {logger.trace("Done synchronizing Extension Points and Extensions from Registry.");}
	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource the resource
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(ISecurityCoreService.FILE_EXTENSION_ROLES)) {
			RoleDefinition[] roleDefinitions = securityCoreService.parseRoles(resource.getContent());
			for (RoleDefinition roleDefinition : roleDefinitions) {
				roleDefinition.setLocation(getRegistryPath(resource));
				synchronizeRole(roleDefinition);
			}

		}
		if (resourceName.endsWith(ISecurityCoreService.FILE_EXTENSION_ACCESS)) {
			List<AccessDefinition> accessDefinitions = securityCoreService.parseAccessDefinitions(resource.getContent());
			String hash = DigestUtils.md5Hex(resource.getContent());
			try {
				securityCoreService.dropModifiedAccessDefinitions(getRegistryPath(resource), hash);
			} catch (AccessException e) {
				if (logger.isErrorEnabled()) {logger.error("Error deleting the modified Access Definitions", e);}
			}
			
			for (AccessDefinition accessDefinition : accessDefinitions) {
				accessDefinition.setLocation(getRegistryPath(resource));
				accessDefinition.setHash(hash);
				synchronizeAccess(accessDefinition);
			}

		}
	}

	/**
	 * Cleanup.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		if (logger.isTraceEnabled()) {logger.trace("Cleaning up Roles and Access artifacts...");}
		super.cleanup();

		try {
			List<RoleDefinition> roleDefinitions = securityCoreService.getRoles();
			for (RoleDefinition roleDefinition : roleDefinitions) {
				if (!ACCESS_SYNCHRONIZED.contains(roleDefinition.getLocation())) {
					securityCoreService.removeRole(roleDefinition.getName());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up Role [{}] from location: {}", roleDefinition.getName(), roleDefinition.getLocation());}
				}
			}

			List<AccessDefinition> accessDefinitions = securityCoreService.getAccessDefinitions();
			for (AccessDefinition accessDefinition : accessDefinitions) {
				if (!ACCESS_SYNCHRONIZED.contains(accessDefinition.getLocation())) {
					securityCoreService.removeAccessDefinition(accessDefinition.getId());
					if (logger.isWarnEnabled()) {logger.warn("Cleaned up Access definition [[{}]-[{}]-[{}]] from location: {}", accessDefinition.getPath(),
							accessDefinition.getMethod(), accessDefinition.getRole(), accessDefinition.getLocation());}
				}
			}
		} catch (AccessException e) {
			throw new SynchronizationException(e);
		}

		if (logger.isTraceEnabled()) {logger.trace("Done cleaning up Roles and Access artifacts.");}
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "ACCESS";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-core-security";
	
	/**
	 * Use to log problem from artifact processing.
	 *
	 * @param errorMessage the error message
	 * @param errorType the error type
	 * @param location the location
	 * @param artifactType the artifact type
	 */
	private static void logProblem(String errorMessage, String errorType, String location, String artifactType) {
		try {
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, SecuritySynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e.getMessage());}
		}
	}
}
