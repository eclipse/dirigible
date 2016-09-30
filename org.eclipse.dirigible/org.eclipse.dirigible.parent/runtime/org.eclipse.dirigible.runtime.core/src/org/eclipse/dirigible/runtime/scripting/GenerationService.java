/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.ext.generation.IGenerationService;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorker;
import org.eclipse.dirigible.repository.ext.generation.IGenerationWorkerProvider;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.RuntimeActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class GenerationService implements IGenerationService {

	private static final Logger logger = Logger.getLogger(GenerationService.class);

	static List<IGenerationWorkerProvider> generationWorkerProviders = new ArrayList<IGenerationWorkerProvider>();

	static {
		registerServices();
	}

	private static void registerServices() {
		// register repository providers
		try {
			BundleContext context = RuntimeActivator.getContext();

			registerGenerationWorkerProviders(context);

		} catch (InvalidSyntaxException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private static void registerGenerationWorkerProviders(BundleContext context) throws InvalidSyntaxException {

		logger.info("Registering Generation Worker Providers...");

		// IGenerationWorkerProvider services
		Collection<ServiceReference<IGenerationWorkerProvider>> serviceReferences = context.getServiceReferences(IGenerationWorkerProvider.class,
				null);
		for (ServiceReference<IGenerationWorkerProvider> serviceReference : serviceReferences) {
			IGenerationWorkerProvider generationWorkerProvider = context.getService(serviceReference);
			generationWorkerProviders.add(generationWorkerProvider);

			logger.info(String.format("%s added to the list of available Repository Providers", generationWorkerProvider.getType()));
		}

	}

	@Override
	public IGenerationWorker getGenerationWorker(String type, HttpServletRequest request) {
		try {
			if (generationWorkerProviders.isEmpty()) {
				registerServices();
			}
			for (IGenerationWorkerProvider provider : generationWorkerProviders) {
				if (provider.getType().equals(type)) {
					return provider.createWorker(request);
				}
			}
		} catch (InvalidSyntaxException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String[] getGenerationWorkerTypes() {
		List<String> types = new ArrayList<String>();
		try {
			if (generationWorkerProviders.isEmpty()) {
				registerServices();
			}
			for (IGenerationWorkerProvider provider : generationWorkerProviders) {
				types.add(provider.getType());
			}
			return types.toArray(new String[] {});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
