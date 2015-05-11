/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;


public class RepositoryFactory {
	
	static IRepositoryProvider repositoryProvider;
	
	static {
		registerServices();
	}

	private static void registerServices() {
		// register script executor providers
		try {
			BundleContext context = RepositoryActivator.getContext();
			Collection<ServiceReference<IRepositoryProvider>> serviceReferences = context.getServiceReferences(IRepositoryProvider.class, null);
			for (Iterator<ServiceReference<IRepositoryProvider>> iterator = serviceReferences.iterator(); iterator.hasNext();) {
				ServiceReference<IRepositoryProvider> serviceReference = iterator.next();
				repositoryProvider = context.getService(serviceReference);
				break;
			}
		} catch (InvalidSyntaxException e) {
//			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	public static IRepository createRepository(Map<String, Object> parameters) 
		throws RepositoryCreationException {
		if (repositoryProvider == null) {
			registerServices();
			if (repositoryProvider == null) {
				throw new RepositoryCreationException("Repository provider has NOT been registered");
			}
		}
		return repositoryProvider.createRepository(parameters);
	}

}
