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
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.db.viewer.views.DatabaseViewContentProvider.Capability;
import org.eclipse.dirigible.ide.db.viewer.views.actions.DeleteTableAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.ExportDataAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.RefreshViewAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.ShowTableDefinitionAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.ViewTableContentAction;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * Database Viewer represents the structure of the tenant specific schema
 */
public class DatabaseViewer extends ViewPart implements IDatabaseConnectionFactory, ISelectionChangedListener {

	private static final Logger logger = Logger.getLogger(DatabaseViewer.class);

	private static final String SELECTED_DATASOURCE_NAME = "SELECTED_DATASOURCE_NAME";

	static final String DEFAULT_DATASOURCE_NAME = "Default";

	private static final String DATABASE_VIEW = Messages.DatabaseViewer_DATABASE_VIEW;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.dirigible.ide.db.viewer.views.DatabaseViewer"; //$NON-NLS-1$

	protected TreeViewer viewer;
	@SuppressWarnings("unused")
	private DrillDownAdapter drillDownAdapter;
	protected Action viewTableContentAction;
	private Action deleteAction;
	private Action exportDataAction;
	private Action doubleClickAction;
	private boolean isOperator;

	private ShowTableDefinitionAction showTableDefinitionAction;

	protected RefreshViewAction refreshViewAction;

	class NameSorter extends ViewerSorter {
		private static final long serialVersionUID = -7067479902071396325L;
	}

	/**
	 * The constructor.
	 */
	public DatabaseViewer() {
		isOperator = CommonIDEParameters.isUserInRole(IRoles.ROLE_OPERATOR);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	@SuppressWarnings("unused")
	public void createPartControl(Composite parent) {

		parent.setLayout(new GridLayout());
		DatabaseViewerToolBar databaseViewerToolBar = new DatabaseViewerToolBar();
		databaseViewerToolBar.addSelectionChangedListener(this);
		databaseViewerToolBar.createToolBar(parent, getSite().getShell());

		PatternFilter filter = new PatternFilter();
		FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		viewer = tree.getViewer();
		DrillDownAdapter drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(initContentProvider());
		viewer.setLabelProvider(new DatabaseViewLabelProvider(this));
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
		// "org.eclipse.dirigible.ide.db.viewer.views.DatabaseViewer");

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	protected IContentProvider initContentProvider() {
		return new DatabaseViewContentProvider(this);
	}

	protected IFilter getSchemaFilter(Connection connection) throws SQLException {
		return null;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			private static final long serialVersionUID = -4330735691305481160L;

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				DatabaseViewer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(showTableDefinitionAction);
		manager.add(viewTableContentAction);
		manager.add(exportDataAction);
		manager.add(new Separator());
		if (isOperator) {
			manager.add(deleteAction);
		}
		manager.add(new Separator());
		manager.add(refreshViewAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (!TreeParent.class.isInstance(obj)) {
			if (TreeObject.class.isInstance(obj)) {
				TreeObject tObject = (TreeObject) obj;
				List<Capability> capabilities = tObject.getTableDefinition().getCapabilities();
				if ((capabilities != null) && !capabilities.isEmpty()) {
					if (capabilities.contains(Capability.ShowTableDefinition)) {
						manager.add(showTableDefinitionAction);
					}
					if (capabilities.contains(Capability.ViewTableContent)) {
						manager.add(viewTableContentAction);
					}
					if (capabilities.contains(Capability.ExportData)) {
						manager.add(exportDataAction);
					}
					if (!manager.isEmpty()) {
						manager.add(new Separator());
					}
					if (isOperator && capabilities.contains(Capability.Delete)) {
						manager.add(deleteAction);
					}
					if (!manager.isEmpty()) {
						manager.add(new Separator());
					}
				}
			}
		}
		manager.add(refreshViewAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(showTableDefinitionAction);
		manager.add(viewTableContentAction);
		manager.add(exportDataAction);
		manager.add(new Separator());
		if (isOperator) {
			manager.add(deleteAction);
		}
		manager.add(new Separator());
		manager.add(refreshViewAction);
	}

	private void makeActions() {
		createRefreshAction();
		createExportDataAction();
		createViewTableContentAction();
		createShowTableDefinitionAction();
		if (isOperator) {
			deleteAction = new DeleteTableAction(viewer);
		}
		doubleClickAction = new ShowTableDefinitionAction(viewer);

	}

	protected void createRefreshAction() {
		refreshViewAction = new RefreshViewAction(viewer);
	}

	protected void createViewTableContentAction() {
		viewTableContentAction = new ViewTableContentAction(viewer, "org.eclipse.dirigible.ide.db.viewer.views.SQLConsole"); //$NON-NLS-1$

	}

	protected void createShowTableDefinitionAction() {
		showTableDefinitionAction = new ShowTableDefinitionAction(viewer);
	}

	protected void createExportDataAction() {
		exportDataAction = new ExportDataAction(viewer);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	protected void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), DATABASE_VIEW, message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public Connection getDatabaseConnection() throws SQLException {
		return getConnectionFromSelectedDatasource();
	}

	public static String getSelectedDatasourceName() throws SQLException {
		return CommonIDEParameters.get(SELECTED_DATASOURCE_NAME);
	}

	/**
	 * Create connection from the data-source selected at this view
	 *
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnectionFromSelectedDatasource() throws SQLException {
		String datasourceName = CommonIDEParameters.get(SELECTED_DATASOURCE_NAME);
		DataSource dataSource = null;
		if ((datasourceName == null) || datasourceName.equals(DEFAULT_DATASOURCE_NAME)) {
			logger.debug("No selected datasource found. Make use of the default one");
			dataSource = DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest());
			if (dataSource != null) {
				Connection connection = dataSource.getConnection();
				return connection;
			}
			logger.error("Trying to use the default datasource, but it is null");
		} else {
			logger.debug(String.format("Selected datasource found %s", datasourceName));
			dataSource = DataSourceFacade.getInstance().getNamedDataSource(CommonIDEParameters.getRequest(), datasourceName);
			if (dataSource != null) {
				Connection connection = dataSource.getConnection();
				return connection;
			}
			logger.error(String.format("Selected datasource found %s, but the datasource itself is null", datasourceName));
		}
		return null;
	}

	boolean showSchemes() {
		return false;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() instanceof DatabaseViewerToolBar) {
			String datasourceName = (String) ((IStructuredSelection) event.getSelection()).getFirstElement();
			CommonIDEParameters.set(SELECTED_DATASOURCE_NAME, datasourceName);
			viewer.setContentProvider(initContentProvider());
			viewer.refresh();
		}
	}

}
