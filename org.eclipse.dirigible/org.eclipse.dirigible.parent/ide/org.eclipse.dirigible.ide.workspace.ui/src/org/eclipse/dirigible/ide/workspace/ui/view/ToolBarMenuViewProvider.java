/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.view;

import org.eclipse.core.resources.IProject;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.image.ImageUtils;
import org.eclipse.dirigible.ide.common.status.StatusLineManagerUtil;
import org.eclipse.dirigible.ide.publish.PublishException;
import org.eclipse.dirigible.ide.publish.PublishManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class ToolBarMenuViewProvider {

	private static final Logger logger = Logger.getLogger(ToolBarMenuViewProvider.class);

	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String SAVE = Messages.WorkspaceExplorerView_SAVE;
	private static final String SAVE_ALL = Messages.WorkspaceExplorerView_SAVE_ALL;
	private static final String NEW = Messages.WorkspaceExplorerView_NEW;

	private static final String PUBLISH_TOOL_TIP_TEXT = Messages.WorkspaceExplorerView_PUBLISH;
	private static final String ACTIVATE_TOOL_TIP_TEXT = Messages.WorkspaceExplorerView_ACTIVATE;

	private static final String PUBLISH_FAILED = Messages.WorkspaceExplorerView_PUBLISH_FAILED;
	private static final String ACTIVATION_FAILED = Messages.WorkspaceExplorerView_ACTIVATION_FAILED;

	private static final Image SAVE_ICON = ImageUtils
			.createImage(ImageUtils.getIconURL("org.eclipse.dirigible.ide.workspace.ui", "/resources/icons/", "save.png")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final Image SAVE_ALL_ICON = ImageUtils
			.createImage(ImageUtils.getIconURL("org.eclipse.dirigible.ide.workspace.ui", "/resources/icons/", "save_all.png")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final Image PUBLISH_ICON = ImageUtils
			.createImage(ImageUtils.getIconURL("org.eclipse.dirigible.ide.publish.ui", "/resources/icons/", "publish.png")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final Image ACTIVATE_ICON = ImageUtils
			.createImage(ImageUtils.getIconURL("org.eclipse.dirigible.ide.publish.ui", "/resources/icons/", "activate.png")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static void createToolBarMenu(Composite parent, Shell shell) {
		if (CommonIDEParameters.isRAP()) {
			int style = SWT.FLAT | SWT.WRAP | SWT.RIGHT | SWT.BORDER | SWT.SHADOW_OUT;

			final ToolBar toolBar = new ToolBar(parent, style);

			if (CommonIDEParameters.isSandboxEnabled()) {
				createActivateToolItem(toolBar, shell);
				createSeparator(toolBar);
			}

			createPublishToolItem(toolBar, shell);

			createSeparator(toolBar);

			createSaveToolItem(toolBar);

			createSeparator(toolBar);

			createSaveAllToolItem(toolBar);

			createSeparator(toolBar);

			createNewToolItem(parent, toolBar);
		}
	}

	private static ToolItem createSeparator(final ToolBar toolBar) {
		return new ToolItem(toolBar, SWT.SEPARATOR);
	}

	private static ToolItem createActivateToolItem(final ToolBar toolBar, final Shell shell) {
		ToolItem toolItem = createToolItem(toolBar, EMPTY, ACTIVATE_TOOL_TIP_TEXT, ACTIVATE_ICON);
		toolItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 2364528684607846153L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				StatusLineManagerUtil.getDefaultStatusLineManager().removeAll();
				try {
					activate();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					StatusLineManagerUtil.setErrorMessage(ex.getMessage());
					MessageDialog.openError(shell, PUBLISH_FAILED, ex.getMessage());
				}

				WebViewerView.refreshWebViewerViewIfVisible();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
		return toolItem;
	}

	private static ToolItem createPublishToolItem(final ToolBar toolBar, final Shell shell) {
		ToolItem toolItem = createToolItem(toolBar, EMPTY, PUBLISH_TOOL_TIP_TEXT, PUBLISH_ICON);
		toolItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 3334607710375924130L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				StatusLineManagerUtil.getDefaultStatusLineManager().removeAll();
				try {
					publish();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					StatusLineManagerUtil.setErrorMessage(ex.getMessage());
					MessageDialog.openError(shell, ACTIVATION_FAILED, ex.getMessage());
				}

				WebViewerView.refreshWebViewerViewIfVisible();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
		return toolItem;
	}

	private static ToolItem createNewToolItem(Composite parent, final ToolBar toolBar) {
		final Menu menu = NewMenuItemViewProvider.createMenu(parent);

		final ToolItem toolItem = createToolItem(toolBar, NEW, NEW, null, SWT.DROP_DOWN);
		toolItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = -2281618627759204367L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.ARROW) {
					Rectangle rect = toolItem.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = toolBar.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//

			}
		});

		return toolItem;
	}

	private static ToolItem createSaveToolItem(final ToolBar toolBar) {
		ToolItem toolItem = createToolItem(toolBar, EMPTY, SAVE, SAVE_ICON);
		toolItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = -8381124396267155406L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IPageListener saveAction = (IPageListener) ActionFactory.SAVE.create(window);
				saveAction.pageActivated(window.getActivePage());
				((IWorkbenchAction) saveAction).run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
		return toolItem;
	}

	private static ToolItem createSaveAllToolItem(final ToolBar toolBar) {
		ToolItem toolItem = createToolItem(toolBar, EMPTY, SAVE_ALL, SAVE_ALL_ICON);
		toolItem.addSelectionListener(new SelectionListener() {
			private static final long serialVersionUID = 6845514748708051108L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				ActionFactory.SAVE_ALL.create(window).run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//
			}
		});
		return toolItem;
	}

	private static ToolItem createToolItem(ToolBar toolBar, String text, String toolTipText, Image image) {
		return createToolItem(toolBar, text, toolTipText, image, SWT.PUSH);
	}

	private static ToolItem createToolItem(ToolBar toolBar, String text, String toolTipText, Image image, int style) {
		ToolItem toolItem = new ToolItem(toolBar, style);
		toolItem.setText(text);
		toolItem.setToolTipText(toolTipText);
		toolItem.setImage(image);
		return toolItem;
	}

	private static void publish() throws PublishException {
		for (IProject project : PublishManager.getProjects(getSelection())) {
			PublishManager.publishProject(project);
		}
	}

	private static void activate() throws PublishException {
		for (IProject project : PublishManager.getProjects(getSelection())) {
			PublishManager.activateProject(project);
		}
	}

	private static ISelection getSelection() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = workbenchWindow.getSelectionService();
		ISelection selection = selectionService.getSelection();
		return selection;
	}
}
