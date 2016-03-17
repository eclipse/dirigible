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
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class RepositoryFactory {

	private static final Logger logger = Logger.getLogger(RepositoryFactory.class.getCanonicalName());

	static IRepositoryProvider localRepositoryProvider;

	static IMasterRepositoryProvider masterRepositoryProvider;

	static List<IRepositoryProvider> repositoryProviders = new ArrayList<IRepositoryProvider>();

	static List<IMasterRepositoryProvider> masterRepositoryProviders = new ArrayList<IMasterRepositoryProvider>();

	static {
		registerServices();
	}

	private static void registerServices() {
		// register repository providers
		try {
			BundleContext context = RepositoryActivator.getContext();

			registerRepositoryProviders(context);

			registerMasterRepositoryProviders(context);

		} catch (InvalidSyntaxException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	private static void registerRepositoryProviders(BundleContext context) throws InvalidSyntaxException {

		logger.info("Registering Repository Providers...");

		String defaultRepositoryProvider = System.getProperty(ICommonConstants.INIT_PARAM_REPOSITORY_PROVIDER);
		String localRepositoryProviderClass = "org.eclipse.dirigible.repository.local.LocalRepositoryProvider";

		// IRepositoryProvider services
		Collection<ServiceReference<IRepositoryProvider>> serviceReferences = context.getServiceReferences(IRepositoryProvider.class, null);
		for (ServiceReference<IRepositoryProvider> serviceReference : serviceReferences) {
			IRepositoryProvider repositoryProvider = context.getService(serviceReference);
			repositoryProviders.add(repositoryProvider);

			logger.info(String.format("%s added to the list of available Repository Providers", repositoryProvider.getClass().getCanonicalName()));

			if (defaultRepositoryProvider != null) {
				if (repositoryProvider.getClass().getCanonicalName().equals(defaultRepositoryProvider)) {
					logger.info(String.format("Repository Provider %s, set as Local Repository Provider",
							repositoryProvider.getClass().getCanonicalName()));
					localRepositoryProvider = repositoryProvider;
				}
			} else if (repositoryProvider.getClass().getCanonicalName().equals(localRepositoryProviderClass)) {
				logger.info("Standard Local Repository Provider is used");
				localRepositoryProvider = repositoryProvider;
			}
		}

		if (localRepositoryProvider == null) {
			for (Object element : repositoryProviders) {
				IRepositoryProvider repositoryProvider = (IRepositoryProvider) element;
				if (repositoryProvider.getClass().getCanonicalName().equals(localRepositoryProviderClass)) {
					logger.info("Fallback to standard Local Repository Provider is used");
					localRepositoryProvider = repositoryProvider;
				}
			}
		}
	}

	private static void registerMasterRepositoryProviders(BundleContext context) throws InvalidSyntaxException {

		logger.info("Registering Master Repository Providers...");

		String defaultMasterRepositoryProvider = System.getProperty(ICommonConstants.INIT_PARAM_REPOSITORY_PROVIDER_MASTER);

		// IMasterRepositoryProvider services
		Collection<ServiceReference<IMasterRepositoryProvider>> masterServiceReferences = context
				.getServiceReferences(IMasterRepositoryProvider.class, null);
		for (ServiceReference<IMasterRepositoryProvider> serviceReference : masterServiceReferences) {
			IMasterRepositoryProvider repositoryProvider = context.getService(serviceReference);
			masterRepositoryProviders.add(repositoryProvider);
			if (defaultMasterRepositoryProvider != null) {
				if (repositoryProvider.getClass().getCanonicalName().equals(defaultMasterRepositoryProvider)) {
					logger.info(String.format("Master Repository Provider %s, set as Master Repository Provider",
							repositoryProvider.getClass().getCanonicalName()));
					masterRepositoryProvider = repositoryProvider;
				}
			}
		}
	}

	/**
	 * Create a Repository instance used for local operations
	 *
	 * @param parameters
	 * @return local repository
	 * @throws RepositoryCreationException
	 */
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
		IRepository repository = localRepositoryProvider.createRepository(parameters);
		return repository;
	}

	/**
	 * Create a Master Repository from which the content can be synchronized as initial load or reset
	 *
	 * @param parameters
	 * @return master repository
	 * @throws RepositoryCreationException
	 */
	public static IMasterRepository createMasterRepository(Map<String, Object> parameters) throws RepositoryCreationException {
		if (masterRepositoryProvider == null) {
			registerServices();
			if (masterRepositoryProvider == null) {
				if (masterRepositoryProviders.size() == 0) {
					logger.info("Master Repository Provider has NOT been registered");
					return null;
				}
			}
		}
		if (masterRepositoryProvider == null) {
			return null;
		}
		IMasterRepository masterRepository = masterRepositoryProvider.createRepository(parameters);
		return masterRepository;
	}

}
