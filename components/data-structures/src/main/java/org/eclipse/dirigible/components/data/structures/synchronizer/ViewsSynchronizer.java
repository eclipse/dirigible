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
import org.eclipse.dirigible.components.data.structures.domain.View;
import org.eclipse.dirigible.components.data.structures.domain.ViewLifecycle;
import org.eclipse.dirigible.components.data.structures.service.ViewService;
import org.eclipse.dirigible.components.data.structures.synchronizer.view.ViewCreateProcessor;
import org.eclipse.dirigible.components.data.structures.synchronizer.view.ViewDropProcessor;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class ViewsSynchronizer.
 *
 * @param <A> the generic type
 */
@Component
@Order(220)
public class ViewsSynchronizer<A extends Artefact> implements Synchronizer<View> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ViewsSynchronizer.class);
	
	/** The Constant FILE_EXTENSION_VIEW. */
	private static final String FILE_EXTENSION_VIEW = ".view";
	
	/** The view service. */
	private ViewService viewService;
	
	/** The datasources manager. */
	private DataSourcesManager datasourcesManager;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new view synchronizer.
	 *
	 * @param viewService the view service
	 * @param datasourcesManager the datasources manager
	 */
	@Autowired
	public ViewsSynchronizer(ViewService viewService, DataSourcesManager datasourcesManager) {
		this.viewService = viewService;
		this.datasourcesManager = datasourcesManager;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<View> getService() {
		return viewService;
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
		return View.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List<View> load(String location, byte[] content) {
		View view = JsonHelper.fromJson(new String(content, StandardCharsets.UTF_8), View.class);
		Configuration.configureObject(view);
		view.setLocation(location);
		if (view.getKind() == null) {
			view.setKind(view.getType());
		}
		view.setType(View.ARTEFACT_TYPE);
		view.updateKey();
		
		try {
			View maybe = getService().findByKey(view.getKey());
			if (maybe != null) {
				view.setId(maybe.getId());
			}
			getService().save(view);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("view: {}", view);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(view);
	}

	/**
	 * Prepare.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
//		// drop views in a reverse order
//		try {
//			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ViewLifecycle.DROP.toString());
//			callback.registerErrors(this, results, ViewLifecycle.DROP.toString(), ArtefactState.FAILED_DELETE);
//		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
//			callback.addError(e.getMessage());
//		}
	}
	
	/**
	 * Process.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
		
		// process views
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, ViewLifecycle.CREATE.toString());
			callback.registerErrors(this, results, ViewLifecycle.CREATE.toString(), ArtefactState.FAILED_CREATE);
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
		
			View view = null;
			if (wrapper.getArtefact() instanceof View) {
				view = (View) wrapper.getArtefact();
			} else {
				throw new UnsupportedOperationException(String.format("Trying to process %s as View", wrapper.getArtefact().getClass()));
			}
			
			ViewLifecycle flag = ViewLifecycle.valueOf(flow);
			switch (flag) {
			case UPDATE:
				executeViewUpdate(connection, view);
				callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED.toString(), ArtefactState.SUCCESSFUL_UPDATE, "");
				break;
			case CREATE:
				if (!SqlFactory.getNative(connection).exists(connection, view.getName())) {
					try {
						executeViewCreate(connection, view);
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE, "");
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
						callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.FAILED_CREATE, e.getMessage());
					}
				} else {
					if (logger.isWarnEnabled()) {logger.warn(format("View [{0}] already exists during the update process", view.getName()));}
					executeViewUpdate(connection, view);
					callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED.toString(), ArtefactState.SUCCESSFUL_UPDATE, "");
				}
				break;
			case DROP:
				if (SqlFactory.getNative(connection).exists(connection, view.getName())) {
					if (SqlFactory.getNative(connection).count(connection, view.getName()) == 0) {
						executeViewDrop(connection, view);
						callback.registerState(this, wrapper, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE, "");
					} else {
						String message = format("View [{1}] cannot be deleted during the update process, because it is not empty", view.getName());
						if (logger.isWarnEnabled()) {logger.warn(message);}
						callback.registerState(this, wrapper, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE, message);
					}
				}
				break;
			default:
				callback.registerState(this, wrapper, ArtefactLifecycle.FAILED.toString(), ArtefactState.FAILED, "Unknown flow: " + flow);
				throw new UnsupportedOperationException(flow);
			}
			return true;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, wrapper, ArtefactLifecycle.FAILED.toString(), ArtefactState.FAILED, e.getMessage());
			return false;
		}
	}

	/**
	 * Cleanup.
	 *
	 * @param view the view
	 */
	@Override
	public void cleanup(View view) {
		try {
			getService().delete(view);
			callback.registerState(this, view, ArtefactLifecycle.DELETED.toString(), ArtefactState.SUCCESSFUL_DELETE, "");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			callback.registerState(this, view, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED_DELETE, e.getMessage());
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
	 * Execute view update.
	 *
	 * @param connection
	 *            the connection
	 * @param viewModel
	 *            the view model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeViewUpdate(Connection connection, View viewModel) throws SQLException {
		if (logger.isInfoEnabled()) {logger.info("Processing Update View: " + viewModel.getName());}
		if (SqlFactory.getNative(connection).exists(connection, viewModel.getName())) {
			executeViewDrop(connection, viewModel);
			executeViewCreate(connection, viewModel);
		} else {
			executeViewCreate(connection, viewModel);
		}
	}

	/**
	 * Execute view create.
	 *
	 * @param connection
	 *            the connection
	 * @param viewModel
	 *            the view model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeViewCreate(Connection connection, View viewModel) throws SQLException {
		ViewCreateProcessor.execute(connection, viewModel);
	}
	
	/**
	 * Execute view drop.
	 *
	 * @param connection
	 *            the connection
	 * @param viewModel
	 *            the view model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeViewDrop(Connection connection, View viewModel) throws SQLException {
		ViewDropProcessor.execute(connection, viewModel);
	}
	
	/**
	 * Gets the file extension.
	 *
	 * @return the file extension
	 */
	@Override
	public String getFileExtension() {
		return FILE_EXTENSION_VIEW;
	}

	/**
	 * Gets the artefact type.
	 *
	 * @return the artefact type
	 */
	@Override
	public String getArtefactType() {
		return View.ARTEFACT_TYPE;
	}

}
