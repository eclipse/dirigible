/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.extensions.ui.view;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.extensions.EExtensionException;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

public class ExtensionsManagerView extends ViewPart {

	private static final Logger logger = Logger.getLogger(ExtensionsManagerView.class);

	private TreeViewer viewer;
	private Action actionRemove;
	private Action actionRefresh;

	private ExtensionManager extensionManager = new ExtensionManager(RepositoryFacade.getInstance().getRepository(),
			DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()), CommonIDEParameters.getRequest());

	class NameSorter extends ViewerSorter {

		/**
		 *
		 */
		private static final long serialVersionUID = -8832089975378999206L;

		@Override
		public int compare(Viewer lviewer, Object e1, Object e2) {
			if ((e1 != null) && (e2 != null) && (e1 instanceof SimpleTreeNode) && (e2 instanceof SimpleTreeNode)
					&& !(((SimpleTreeNode) e1).isRootElement() ^ ((SimpleTreeNode) e2).isRootElement())) {
				return super.compare(lviewer, e1.toString(), e2.toString());
			}
			return super.compare(lviewer, e1, e2);
		}

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		PatternFilter filter = new PatternFilter();
		FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		viewer = tree.getViewer();
		TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
		column.setText(Messages.ExtensionsManagerView_EXTENSIONS);
		column.setWidth(300);

		viewer.setContentProvider(new ExtensionsViewContentProvider(extensionManager));
		viewer.setLabelProvider(new ExtensionsViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			/**
			 *
			 */
			private static final long serialVersionUID = -8683354310299838422L;

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ExtensionsManagerView.this.fillContextMenu(manager);
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
		manager.add(actionRemove);
		manager.add(new Separator());
		manager.add(actionRefresh);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionRemove);
		manager.add(actionRefresh);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionRemove);
		manager.add(actionRefresh);
	}

	private void makeActions() {

		actionRemove = new Action() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1336014167502247774L;

			@Override
			public void run() {

				if (!viewer.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) viewer.getSelection();
					Object[] selectedElements = selection.toArray();
					if (MessageDialog.openConfirm(viewer.getControl().getShell(), Messages.ExtensionsManagerView_DELETE_EXTENSIONS_TITLE,
							Messages.ExtensionsManagerView_DELETE_EXTENSIONS_DIALOG_DESCRIPTION)) {
						String extensionToDelete = ""; //$NON-NLS-1$
						try {

							for (Object element : selectedElements) {
								SimpleTreeNode simpleTreeNode = (SimpleTreeNode) element;
								extensionToDelete = simpleTreeNode.getName();
								if (simpleTreeNode.isRootElement()) {
									extensionManager.removeExtensionPoint(simpleTreeNode.getName());
								} else {
									extensionManager.removeExtension(simpleTreeNode.getName(), simpleTreeNode.getParent().getName());
								}
							}
						} catch (EExtensionException e) {
							MessageDialog.openError(viewer.getControl().getShell(), Messages.ExtensionsManagerView_FAILED_TO_DELETE_EXTENSION + extensionToDelete,
									e.getMessage());
						} finally {
							viewer.refresh();
						}
					}
				}
			}
		};
		actionRemove.setText(Messages.ExtensionsManagerView_DELETE_LABEL);
		actionRemove.setToolTipText(Messages.ExtensionsManagerView_DELETE_ACTION_TOOL_TIP);
		actionRemove.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

		actionRefresh = new Action() {
			/**
			 *
			 */
			private static final long serialVersionUID = 506492927597193506L;

			@Override
			public void run() {
				viewer.refresh();
			}
		};
		actionRefresh.setText(Messages.ExtensionsManagerView_REFRESH_LABEL);
		actionRefresh.setToolTipText(Messages.ExtensionsManagerView_REFRESH_ACTION_TOOLTIP);
		actionRefresh.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
