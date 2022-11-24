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
package org.eclipse.dirigible.components.data.metadata.service;


import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.data.metadata.load.DataSourceMetadataLoader;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSourceMetadataService {
	
	/** The data source service. */
	private final DataSourcesManager datasourceManager;
	
	/** The data loader. */
	private final DataSourceMetadataLoader datasourceMetadataLoader;
	
	

	/**
	 * Instantiates a new data source endpoint.
	 *
	 * @param datasourceService the datasource service
	 * @param datasourceMetadataLoader the datasource metadata loader
	 */
	@Autowired
	public DataSourceMetadataService(DataSourcesManager datasourceManager, DataSourceMetadataLoader datasourceMetadataLoader) {
		this.datasourceManager = datasourceManager;
		this.datasourceMetadataLoader = datasourceMetadataLoader;
	}
	
	public String getSchemaMetadata(String name, String schema) throws SQLException {
		javax.sql.DataSource datasource = datasourceManager.getDataSource(name);
		List<Table> model = datasourceMetadataLoader.getSchemaMetadata(schema, datasource);
		if (model != null) {
			return JsonHelper.toJson(model);
		}
		return null;
		
	}

	public String getStructureMetadata(String name, String schema, String structure) throws SQLException {
		javax.sql.DataSource datasource = datasourceManager.getDataSource(name);
		Table model = datasourceMetadataLoader.getTableMetadata(schema, structure, datasource);
		if (model != null) {
			return JsonHelper.toJson(model);
		}
		return null;
	}

}
