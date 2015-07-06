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

package org.eclipse.dirigible.ide.db.viewer.views;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.datasource.DataSourceFacade;
import org.eclipse.dirigible.ide.db.viewer.views.actions.DeleteTableAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.ExportDataAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.RefreshViewAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.ShowTableDefinitionAction;
import org.eclipse.dirigible.ide.db.viewer.views.actions.ViewTableContentAction;
import org.eclipse.dirigible.repository.ext.security.IRoles;

/**
 * Database Viewer represents the structure of the tenant specific schema
 * 
 */
public class DatabaseViewer extends ViewPart implements IDbConnectionFactory {

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
		isOperator = CommonParameters.isUserInRole(IRoles.ROLE_OPERATOR);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@SuppressWarnings("unused")
	public void createPartControl(Composite parent) {
		PatternFilter filter = new PatternFilter();
		FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
				filter, true);
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
				manager.add(showTableDefinitionAction);
				manager.add(viewTableContentAction);
				manager.add(exportDataAction);
				manager.add(new Separator());
				if (isOperator) {
					manager.add(deleteAction);
				}
				manager.add(new Separator());
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
	
	protected void createExportDataAction(){
		exportDataAction = new ExportDataAction(viewer);
	}
	
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
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
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public Connection getDatabaseConnection() throws SQLException {
		return getConnectionFromSelectedDatasource();
	}

	/**
	 * Create connection from the data-source selected at this view
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnectionFromSelectedDatasource()
			throws SQLException {
		DataSource dataSource = DataSourceFacade.getInstance().getDataSource();
		Connection connection = dataSource.getConnection();
		return connection;
	}

	public boolean showSchemes() {
		return false;
	}

}