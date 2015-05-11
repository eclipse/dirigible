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

package org.eclipse.dirigible.ide.workspace.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.ide.repository.ui.view.IRefreshableView;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.commands.AbstractWorkspaceHandler;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewer;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewerUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public class WorkspaceExplorerView extends ViewPart implements IRefreshableView {

	public static final String VIEW_ID = "org.eclipse.dirigible.ide.workspace.ui.view.WorkspaceExplorerView";

	private static final String CHECK_LOGS_FOR_MORE_INFO = Messages.WorkspaceExplorerView_CHECK_LOGS_FOR_MORE_INFO;

	private static final String COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR = Messages.WorkspaceExplorerView_COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR;

	private static final String OPERATION_FAILED = Messages.WorkspaceExplorerView_OPERATION_FAILED;

	private static final String COULD_NOT_EXECUTE_OPEN_COMMAND = Messages.WorkspaceExplorerView_COULD_NOT_EXECUTE_OPEN_COMMAND;

	private static final Logger logger = Logger.getLogger(WorkspaceExplorerView.class);

	private static final String OPEN_COMMAND_ID = "org.eclipse.dirigible.ide.workspace.ui.commands.OpenHandler"; //$NON-NLS-1$

	private WorkspaceViewer viewer = null;

	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		ToolBarMenuViewProvider.createToolBarMenu(parent, getSite().getShell());

		viewer = new WorkspaceViewer(parent, SWT.MULTI);
		viewer.getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				onWorkspaceViewerDoubleClicked(event);
			}
		});
		getSite().setSelectionProvider(viewer.getSelectionProvider());
		getSite().registerContextMenu("org.eclipse.dirigible.ide.workspace.ui.view.Menu", //$NON-NLS-1$
				viewer.getMenuManager(), viewer.getSelectionProvider());

		setSelectedProjectFromRequest();

	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		AbstractWorkspaceHandler.attachSelectionListener(site);
	}

	@Override
	public void dispose() {
		AbstractWorkspaceHandler.detachSelectionListener(getSite());
		super.dispose();
	}

	private void setSelectedProjectFromRequest() {
		try {
			String projectName = CommonParameters.get(CommonParameters.PARAMETER_PROJECT);
			if (projectName != null) {
				List<Object> selected = new ArrayList<Object>();
				TreeItem[] treeItems = viewer.getViewer().getTree().getItems();
				for (int i = 0; i < treeItems.length; i++) {
					TreeItem treeItem = treeItems[i];
					Object treeObject = treeItem.getData();
					if (treeObject instanceof IProject) {
						if (projectName.equals(((IProject) treeObject).getName())) {
							selected.add(treeObject);
							break;
						}
					}
				}

				viewer.getViewer().setExpandedElements(selected.toArray(new Object[] {}));
			}
		} catch (Exception e) {
			// do nothing - just usability feature, which should not bother user
			// when breaks
		}
	}

	private void onWorkspaceViewerDoubleClicked(DoubleClickEvent event) {
		ICommandService commandService = (ICommandService) getSite().getService(
				ICommandService.class);
		IHandlerService handlerService = (IHandlerService) getSite().getService(
				IHandlerService.class);
		Command command = commandService.getCommand(OPEN_COMMAND_ID);
		ExecutionEvent executionEvent = handlerService.createExecutionEvent(command, null);
		try {
			command.executeWithChecks(executionEvent);
		} catch (Exception ex) {
			logger.error(COULD_NOT_EXECUTE_OPEN_COMMAND, ex);
			MessageDialog.openError(null, OPERATION_FAILED,
					COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR + ex.getMessage()
							+ CHECK_LOGS_FOR_MORE_INFO);
		}
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (Object element : structuredSelection.toArray()) {
				if (element instanceof IFolder || element instanceof IProject) {
					WorkspaceViewerUtils.doubleClickedElement(element);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFocus() {
		viewer.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh() {
		viewer.refresh();
	}

	public WorkspaceViewer getViewer() {
		return viewer;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IRefreshableView.class.equals(adapter)) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	protected IWorkspace getWorkspace() {
		return WorkspaceLocator.getWorkspace();
	}

}
