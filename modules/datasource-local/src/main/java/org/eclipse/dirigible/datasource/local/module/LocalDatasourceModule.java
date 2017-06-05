/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.datasource.local.module;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.datasource.api.IDatasource;
import org.eclipse.dirigible.datasource.local.LocalDatasource;

/**
 * Module for managing Local Datasource instantiation and binding
 */
public class LocalDatasourceModule extends AbstractDirigibleModule {

	@Override
	protected void configure() {
		Configuration.load("/dirigible-datasource-local.properties");
		String datasourceProvider = Configuration.get(IDatasource.DIRIGIBLE_DATASOURCE_PROVIDER, IDatasource.DIRIGIBLE_DATASOURCE_PROVIDER_LOCAL);

		if (LocalDatasource.TYPE.equals(datasourceProvider)) {
			bind(IDatasource.class).toInstance(createInstance());
		}
	}

	private IDatasource createInstance() {
		String rootFolder = Configuration.get(LocalDatasource.DIRIGIBLE_LOCAL_DATASOURCE_ROOT_FOLDER);
		return new LocalDatasource(rootFolder);
	}
}
