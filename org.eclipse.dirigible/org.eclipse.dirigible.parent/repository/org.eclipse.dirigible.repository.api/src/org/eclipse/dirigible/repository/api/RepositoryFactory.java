/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class RepositoryFactory {

	static IRepositoryProvider localRepositoryProvider;

	static List<IRepositoryProvider> repositoryProviders = new ArrayList<IRepositoryProvider>();

	static {
		registerServices();
	}

	private static void registerServices() {
		// register repository providers
		try {
			BundleContext context = RepositoryActivator.getContext();
			Collection<ServiceReference<IRepositoryProvider>> serviceReferences = context.getServiceReferences(IRepositoryProvider.class, null);
			for (ServiceReference<IRepositoryProvider> serviceReference : serviceReferences) {
				IRepositoryProvider repositoryProvider = context.getService(serviceReference);
				repositoryProviders.add(repositoryProvider);
				if (repositoryProvider.getClass().getCanonicalName().equals("org.eclipse.dirigible.repository.local.LocalRepositoryProvider")) {
					localRepositoryProvider = repositoryProvider;
				}
			}
		} catch (InvalidSyntaxException e) {
			// logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static IRepository createRepository(Map<String, Object> parameters) throws RepositoryCreationException {
		if (localRepositoryProvider == null) {
			registerServices();
			if (localRepositoryProvider == null) {
				if (repositoryProviders.size() == 0) {
					throw new RepositoryCreationException("Local Repository Provider has NOT been registered");
				}
				// RCP case
				localRepositoryProvider = repositoryProviders.get(0);
			}
		}
		return localRepositoryProvider.createRepository(parameters);
	}

}
