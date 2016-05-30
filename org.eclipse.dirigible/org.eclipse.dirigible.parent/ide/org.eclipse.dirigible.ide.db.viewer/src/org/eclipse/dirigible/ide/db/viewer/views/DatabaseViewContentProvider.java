/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.viewer.views;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.repository.datasource.DataSources;
import org.eclipse.dirigible.repository.datasource.DataSources.Filter;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

@SuppressWarnings("javadoc")
public class DatabaseViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private static final Logger logger = Logger.getLogger(DatabaseViewContentProvider.class);

	private static final String DIRIGIBLE_SYSTEM_TALBES_PREFIX = "DGB_";
	private static final String EMPTY = ""; //$NON-NLS-1$

	public enum Capability {
		ShowTableDefinition, ViewTableContent, ExportData, Delete;
	}

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 8868769345708033548L;

	private TreeParent invisibleRoot;

	public DatabaseViewer databaseViewer;

	public DatabaseViewContentProvider(DatabaseViewer databaseViewer) {
		this.databaseViewer = databaseViewer;
	}

	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		//
	}

	@Override
	public void dispose() {
		//
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent.equals(databaseViewer.getViewSite())) {
			if (invisibleRoot == null) {
				initialize();
			}
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).hasChildren();
		}
		return false;
	}

	private void initialize() {

		Connection connection = null;
		try {
			connection = this.databaseViewer.getDatabaseConnection();
			String dsName = DatabaseViewer.getSelectedDatasourceName();
			// DataSources dataSource = new DataSources(dsName);

			this.invisibleRoot = new TreeParent(EMPTY, this.databaseViewer);

			List<TreeParent> schemesContainerNode = new ArrayList<TreeParent>();
			String catalogName = null;// TODO never used... remove?
			// list schemes
			List<String> schemeNames = DataSources.listSchemeNames(connection, CommonIDEParameters.getSelectedDatasource(), catalogName, null);

			for (String schemeName : schemeNames) {

				TreeParent schemeContainerNode = new TreeParent(schemeName, this.databaseViewer);

				// get a list of all table names
				List<String> tableNames = DataSources.listTableNames(connection, CommonIDEParameters.getSelectedDatasource(), catalogName, schemeName,
						new Filter<String>() {
							@Override
							public boolean accepts(String tableName) {
								if (tableName.startsWith(DIRIGIBLE_SYSTEM_TALBES_PREFIX)) {
									if (!CommonIDEParameters.isUserInRole(IRoles.ROLE_OPERATOR)) {
										return false;
									}
								}
								return true;
							}
						});

				for (String tableName : tableNames) {
					TableDefinition tableDef = new TableDefinition(catalogName, schemeName, tableName);
					TreeObject tableNode = new TreeObject(tableName, tableDef);
					List<Capability> capabilities = tableNode.getTableDefinition().getCapabilities();
					if (!DataSources.getDialect(connection, CommonIDEParameters.getSelectedDatasource()).isSchemaless()) {
						capabilities.add(Capability.ShowTableDefinition);
					}
					capabilities.add(Capability.ViewTableContent);
					tableDef.setContentScript(DataSources.getDialect(connection, CommonIDEParameters.getSelectedDatasource())
							.getContentQueryScript(catalogName, schemeName, tableName));
					capabilities.add(Capability.ExportData);
					capabilities.add(Capability.Delete);
					schemeContainerNode.addChild(tableNode);
				}
				schemesContainerNode.add(schemeContainerNode);
			}

			TreeParent dataSourceContainerNode = new TreeParent(DataSources.getDataSourceLabel(connection, CommonIDEParameters.getSelectedDatasource()),
					this.databaseViewer);

			if (schemesContainerNode.size() == 1) {
				TreeObject[] tableNodes = schemesContainerNode.get(0).getChildren();
				for (TreeObject tableNode : tableNodes) {
					dataSourceContainerNode.addChild(tableNode);
				}
			} else {
				for (TreeParent schemeContainer : schemesContainerNode) {
					dataSourceContainerNode.addChild(schemeContainer);
				}
			}

			invisibleRoot.addChild(dataSourceContainerNode);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			TreeParent root = new TreeParent(e.getMessage(), this.databaseViewer);
			invisibleRoot = new TreeParent(EMPTY, this.databaseViewer);
			invisibleRoot.addChild(root);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

	}

	public void requestRefreshContent() {
		invisibleRoot = null;
	}
}
