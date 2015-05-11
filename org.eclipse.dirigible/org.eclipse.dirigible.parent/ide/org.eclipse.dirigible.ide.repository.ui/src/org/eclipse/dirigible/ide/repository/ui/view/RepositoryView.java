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

package org.eclipse.dirigible.ide.repository.ui.view;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.repository.ui.viewer.RepositoryViewer;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.eclipse.dirigible.repository.logging.Logger;

public class RepositoryView extends ViewPart implements IRefreshableView {

	private static final String COULD_NOT_ACCESS_REPOSITORY = Messages.RepositoryView_COULD_NOT_ACCESS_REPOSITORY;

	private static final String OPERATION_FAILED = Messages.RepositoryView_OPERATION_FAILED;

	private static final Logger logger = Logger.getLogger(RepositoryView.class);

	private static final String DOT = ". "; //$NON-NLS-1$

	private static final String COLON = ": "; //$NON-NLS-1$

	private static final String CHECK_LOGS_FOR_MORE_INFO = Messages.RepositoryView_CHECK_LOGS_FOR_MORE_INFO;

	private static final String COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR = Messages.RepositoryView_COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR;

	public static final String ID = "org.eclipse.dirigible.ide.repository.ui.view.RepositoryView"; //$NON-NLS-1$

	private static final String OPEN_COMMAND_ID = "org.eclipse.dirigible.ide.repository.ui.command.OpenHandler"; //$NON-NLS-1$

	private IRepository repository;

	private RepositoryViewer viewer;

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		try {
			repository = RepositoryFacade.getInstance().getRepository();
		} catch (RepositoryException ex) {
			throw new PartInitException(COULD_NOT_ACCESS_REPOSITORY, ex);
		}
	}

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());

		viewer = new RepositoryViewer(parent, SWT.NONE | SWT.MULTI);
		viewer.setRepository(repository);
		viewer.getTreeViewer().addDoubleClickListener(
				new IDoubleClickListener() {
					public void doubleClick(DoubleClickEvent event) {
						onWorkspaceViewerDoubleClicked(event);
					}
				});
		getSite().setSelectionProvider(viewer.getSelectionProvider());
		getSite().registerContextMenu(RepositoryViewer.MENU_REPOSITORY,
				viewer.getMenuManager(), viewer.getSelectionProvider());
	}

	private void onWorkspaceViewerDoubleClicked(DoubleClickEvent event) {
		ICommandService commandService = (ICommandService) getSite()
				.getService(ICommandService.class);
		IHandlerService handlerService = (IHandlerService) getSite()
				.getService(IHandlerService.class);
		Command command = commandService.getCommand(OPEN_COMMAND_ID);
		ExecutionEvent executionEvent = handlerService.createExecutionEvent(
				command, null);
		try {
			command.executeWithChecks(executionEvent);
		} catch (Exception ex) {
			logger.error(
					COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR,
					ex);
			MessageDialog.openError(null, OPERATION_FAILED,
					COULD_NOT_EXECUTE_OPEN_COMMAND_DUE_TO_THE_FOLLOWING_ERROR
							+ COLON + ex.getMessage() + DOT
							+ CHECK_LOGS_FOR_MORE_INFO);
		}
	}

	public void refresh() {
		if (viewer != null) {
			viewer.refresh();
		}
	}

	public void setFocus() {
		if (viewer != null) {
			viewer.setFocus();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter.isAssignableFrom(IRefreshableView.class)) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void dispose() {
		viewer = null;
		super.dispose();
	}
}
