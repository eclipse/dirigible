/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.viewer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class WorkspaceViewer {

	public static final Logger logger = Logger.getLogger(WorkspaceViewer.class.getCanonicalName());

	private static final String WORKSPACE_MENU = "Workspace Menu"; //$NON-NLS-1$

	private final TreeViewer viewer;

	private final MenuManager menuManager;

	private final IResourceChangeListener changeListener = new CustomResourceChangeListener();

	public WorkspaceViewer(Composite parent, int style) {
		this(parent, style, WorkspaceLocator.getWorkspace());
	}

	private WorkspaceViewer(Composite parent, int style, IWorkspace workspace) {
		// Create and configure viewer
		PatternFilter filter = new PatternFilter();
		FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		viewer = tree.getViewer();
		viewer.setContentProvider(new WorkspaceContentProvider());
		viewer.setLabelProvider(new WorkspaceLabelProvider());
		viewer.setSorter(new WorkspaceSorter());

		// viewer.addDragSupport(
		// DND.DROP_MOVE,
		// WorkspaceDragSourceListener.SUPPORTED_DND_SOURCE_TRANSFER_TYPES,
		// new WorkspaceDragSourceListener(viewer));
		// viewer.addDropSupport(
		// DND.DROP_MOVE,
		// WorkspaceDropTargetListener.SUPPORTED_DND_TARGET_TRANSFER_TYPES,
		// new WorkspaceDropTargetListener(viewer, workspace));

		viewer.getControl().addDisposeListener(new DisposeListener() {
			/**
			 *
			 */
			private static final long serialVersionUID = 2504065180094207979L;

			@Override
			public void widgetDisposed(DisposeEvent event) {
				releaseData();
			}
		});

		// Configure context menu
		menuManager = new MenuManager(WORKSPACE_MENU, "sample.MenuManager"); //$NON-NLS-1$
		menuManager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		final Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		if (workspace != null) {
			setInput(workspace.getRoot());
		} else {
			setInput(null);
		}
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public ISelectionProvider getSelectionProvider() {
		return viewer;
	}

	public Control getControl() {
		return (viewer != null) ? viewer.getControl() : null;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setInput(IContainer container) {
		final IContainer currentInput = getInput();
		if (!areEqual(currentInput, container)) {
			changeInput(currentInput, container);
		}
		// container.getWorkspace().addResourceChangeListener(changeListener);
		// viewer.setInput(container);
	}

	public IContainer getInput() {
		return (IContainer) viewer.getInput();
	}

	public void refresh() {
		Object[] elements = viewer.getExpandedElements();
		viewer.refresh();
		viewer.setExpandedElements(elements);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void dispose() {
		viewer.getControl().dispose();
	}

	private void changeInput(IContainer oldInput, IContainer newInput) {
		final IWorkspace oldWorkspace = (oldInput != null) ? oldInput.getWorkspace() : null;
		final IWorkspace newWorkspace = (newInput != null) ? newInput.getWorkspace() : null;
		if (!areEqual(oldWorkspace, newWorkspace)) {
			changeWorkspace(oldWorkspace, newWorkspace);
		}
		viewer.setInput(newInput);
	}

	private void changeWorkspace(IWorkspace oldWorkspace, IWorkspace newWorkspace) {
		if (oldWorkspace != null) {
			oldWorkspace.removeResourceChangeListener(changeListener);
		}
		if (newWorkspace != null) {
			newWorkspace.addResourceChangeListener(changeListener);
		}
	}

	private boolean areEqual(Object a, Object b) {
		if (a != null) {
			return a.equals(b);
		} else {
			return (b == null);
		}
	}

	private void releaseData() {
		final IContainer input = getInput();
		if (input != null) {
			input.getWorkspace().removeResourceChangeListener(changeListener);
		}
	}

	private class CustomResourceChangeListener implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			final Display display = Display.getCurrent();
			UISession uiSession = RWT.getUISession(display);
			uiSession.exec(new Runnable() {
				@Override
				public void run() {
					if (!viewer.getControl().isDisposed()) {
						try {
							refresh();
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});

		}
	}

}
