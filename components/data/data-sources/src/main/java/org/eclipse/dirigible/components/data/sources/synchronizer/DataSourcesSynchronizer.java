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
package org.eclipse.dirigible.components.data.sources.synchronizer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.helpers.JsonHelper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizersOrder;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.domain.DataSourceProperty;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class DataSourcesSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(SynchronizersOrder.DATASOURCE)
public class DataSourcesSynchronizer<A extends Artefact> implements Synchronizer<DataSource> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DataSourcesSynchronizer.class);
	
	/** The Constant FILE_DATASOURCE_EXTENSION. */
	public static final String FILE_DATASOURCE_EXTENSION = ".datasource";
	
	/** The datasource service. */
	private DataSourceService dataSourceService;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new datasourcess synchronizer.
	 *
	 * @param dataSourceService the data source service
	 */
	@Autowired
	public DataSourcesSynchronizer(DataSourceService dataSourceService) {
		this.dataSourceService = dataSourceService;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<DataSource> getService() {
		return dataSourceService;
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
	 * @param type the artefact
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return DataSource.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 * @throws ParseException the parse exception
	 */
	@Override
	public List<DataSource> parse(String location, byte[] content) throws ParseException {
		DataSource datasource = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), DataSource.class);
		Configuration.configureObject(datasource);
		datasource.setLocation(location);
		datasource.setType(DataSource.ARTEFACT_TYPE);
		datasource.updateKey();
		try {
			DataSource maybe = getService().findByKey(datasource.getKey());
			if (maybe != null) {
				datasource.setId(maybe.getId());
				datasource.getProperties().forEach(p -> {
					DataSourceProperty m = maybe.getProperty(p.getName());
					if (m != null) {
						p.setId(m.getId());
					}
				});
			}
			datasource.getProperties().forEach(p -> p.setDatasource(datasource));
			DataSource result = getService().save(datasource);
			return List.of(result);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("datasource: {}", datasource);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
			throw new ParseException(e.getMessage(), 0);
		}
	}
	
	/**
	 * Retrieve.
	 *
	 * @param location the location
	 * @return the list
	 */
	@Override
	public List<DataSource> retrieve(String location) {
		return getService().getAll();
	}
	
	/**
	 * Sets the status.
	 *
	 * @param artefact the artefact
	 * @param lifecycle the lifecycle
	 * @param error the error
	 */
	@Override
	public void setStatus(Artefact artefact, ArtefactLifecycle lifecycle, String error) {
		artefact.setLifecycle(lifecycle);
		artefact.setError(error);
		getService().save((DataSource) artefact);
	}
	
	/**
	 * Complete.
	 *
	 * @param wrapper the wrapper
	 * @param flow the flow
	 * @return true, if successful
	 */
	@Override
	public boolean complete(TopologyWrapper<Artefact> wrapper, ArtefactPhase flow) {
		try {
			DataSource datasource = null;
			if (wrapper.getArtefact() instanceof DataSource) {
				datasource = (DataSource) wrapper.getArtefact();
			} else {
				throw new UnsupportedOperationException(String.format("Trying to process %s as DataSource", wrapper.getArtefact().getClass()));
			}
			
			
			switch (flow) {
			case CREATE:
				if (datasource.getLifecycle().equals(ArtefactLifecycle.NEW)) {
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED, "");
				}
				break;
			case UPDATE:
				if (datasource.getLifecycle().equals(ArtefactLifecycle.MODIFIED)) {
					callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED, "");
				}
				break;
			case DELETE:
				if (datasource.getLifecycle().equals(ArtefactLifecycle.CREATED)
						|| datasource.getLifecycle().equals(ArtefactLifecycle.UPDATED)) {
					callback.registerState(this, wrapper, ArtefactLifecycle.DELETED, "");
				}
				break;
			}
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, wrapper, ArtefactLifecycle.FAILED, e.getMessage());
			return false;
		}
	}

	/**
	 * Cleanup.
	 *
	 * @param datasource the datasource
	 */
	@Override
	public void cleanup(DataSource datasource) {
		try {
			if (!datasource.getLocation().startsWith("API_")
					&& !datasource.getLocation().startsWith("ENV_")) {
				getService().delete(datasource);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, datasource, ArtefactLifecycle.DELETED, e.getMessage());
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
		return FILE_DATASOURCE_EXTENSION;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return DataSource.ARTEFACT_TYPE;
	}

}
