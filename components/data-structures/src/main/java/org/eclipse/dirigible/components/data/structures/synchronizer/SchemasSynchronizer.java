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
package org.eclipse.dirigible.components.data.structures.synchronizer;

import static java.text.MessageFormat.format;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;
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
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.structures.domain.Schema;
import org.eclipse.dirigible.components.data.structures.domain.SchemaLifecycle;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.eclipse.dirigible.components.data.structures.domain.View;
import org.eclipse.dirigible.components.data.structures.service.SchemaService;
import org.eclipse.dirigible.components.data.structures.synchronizer.schema.SchemaCreateProcessor;
import org.eclipse.dirigible.components.data.structures.synchronizer.schema.SchemaDropProcessor;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class SchemasSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(210)
public class SchemasSynchronizer<A extends Artefact> implements Synchronizer<Schema> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SchemasSynchronizer.class);
	
	/** The Constant FILE_EXTENSION_SCHEMA. */
	private static final String FILE_EXTENSION_SCHEMA = ".schema";
	
	/** The schema service. */
	private SchemaService schemaService;
	
	/** The datasources manager. */
	private DataSourcesManager datasourcesManager;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new schema synchronizer.
	 *
	 * @param schemaService the schema service
	 * @param datasourcesManager the datasources manager
	 */
	@Autowired
	public SchemasSynchronizer(SchemaService schemaService, DataSourcesManager datasourcesManager) {
		this.schemaService = schemaService;
		this.datasourcesManager = datasourcesManager;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Schema> getService() {
		return schemaService;
	}

	/**
	 * Checks if is accepted.
	 *
	 * @param file the file
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
	 * @param type the type
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Schema.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List<Schema> load(String location, byte[] content) {
		Schema schema = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), Schema.class);
		Configuration.configureObject(schema);
		schema.setLocation(location);
		schema.setType(Schema.ARTEFACT_TYPE);
		schema.updateKey();
		
		for (Table table : schema.getTables()) {
			if (table.getKind() == null) {
				table.setKind(table.getType());
			}
			table.setType(Table.ARTEFACT_TYPE);
		}
		
		for (View view : schema.getViews()) {
			if (view.getKind() == null) {
				view.setKind(view.getType());
			}
			view.setType(View.ARTEFACT_TYPE);
		}
		
		try {
			Schema maybe = getService().findByKey(schema.getKey());
			if (maybe != null) {
				schema.setId(maybe.getId());
			}
			getService().save(schema);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("schema: {}", schema);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(schema);
	}

	/**
	 * Prepare.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
		// drop schemas in a reverse order
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, SchemaLifecycle.DROP.toString());
			callback.registerErrors(this, results, SchemaLifecycle.DROP.toString(), ArtefactState.FAILED_DELETE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
	}
	
	/**
	 * Process.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
		
		// process schemas
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, SchemaLifecycle.CREATE.toString());
			callback.registerErrors(this, results, SchemaLifecycle.CREATE.toString(), ArtefactState.FAILED_CREATE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
		
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
		
		try (Connection connection = datasourcesManager.getDefaultDataSource().getConnection()) {
		
			Schema schema = null;
			if (wrapper.getArtefact() instanceof Schema) {
				schema = (Schema) wrapper.getArtefact();
			} else {
				throw new UnsupportedOperationException(String.format("Trying to process %s as Schema", wrapper.getArtefact().getClass()));
			}
			
			SchemaLifecycle flag = SchemaLifecycle.valueOf(flow);
			switch (flag) {
			case UPDATE:
				executeSchemaUpdate(connection, schema);
				break;
			case CREATE:
				if (!SqlFactory.getNative(connection).exists(connection, schema.getName())) {
					try {
						executeSchemaCreate(connection, schema);
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE);
					}
				} else {
					if (logger.isWarnEnabled()) {logger.warn(format("Schema [{0}] already exists during the update process", schema.getName()));}
					executeSchemaUpdate(connection, schema);
					callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED.toString(), ArtefactState.SUCCESSFUL_UPDATE);
				}
				break;
			case DROP:
				if (SqlFactory.getNative(connection).exists(connection, schema.getName())) {
					if (SqlFactory.getNative(connection).count(connection, schema.getName()) == 0) {
						executeSchemaDrop(connection, schema);
					} else {
						String message = format("Schema [{1}] cannot be deleted during the update process, because it is not empty", schema.getName());
						if (logger.isWarnEnabled()) {logger.warn(message);}
						callback.registerState(this, wrapper, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
					}
				}
				break;
			default:
				throw new UnsupportedOperationException(flow);
			}
			return true;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			return false;
		}
	}

	/**
	 * Cleanup.
	 *
	 * @param schema the schema
	 */
	@Override
	public void cleanup(Schema schema) {
		try {
			getService().delete(schema);
			callback.registerState(this, schema, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, schema, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE);
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
	 * Execute schema update.
	 *
	 * @param connection
	 *            the connection
	 * @param schemaModel
	 *            the schema model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeSchemaUpdate(Connection connection, Schema schemaModel) throws SQLException {
		if (logger.isInfoEnabled()) {logger.info("Processing Update Schema: " + schemaModel.getName());}
		if (SqlFactory.getNative(connection).exists(connection, schemaModel.getName())) {
			executeSchemaDrop(connection, schemaModel);
			executeSchemaCreate(connection, schemaModel);
		} else {
			executeSchemaCreate(connection, schemaModel);
		}
	}

	/**
	 * Execute schema create.
	 *
	 * @param connection
	 *            the connection
	 * @param schemaModel
	 *            the schema model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeSchemaCreate(Connection connection, Schema schemaModel) throws SQLException {
		SchemaCreateProcessor.execute(connection, schemaModel);
	}
	
	/**
	 * Execute schema drop.
	 *
	 * @param connection
	 *            the connection
	 * @param schemaModel
	 *            the schema model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeSchemaDrop(Connection connection, Schema schemaModel) throws SQLException {
		SchemaDropProcessor.execute(connection, schemaModel);
	}
	
	/**
	 * Gets the file extension.
	 *
	 * @return the file extension
	 */
	@Override
	public String getFileExtension() {
		return FILE_EXTENSION_SCHEMA;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return Schema.ARTEFACT_TYPE;
	}

}
