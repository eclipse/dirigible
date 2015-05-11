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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.dirigible.ide.editor.text.input.ContentEditorInput;
import org.eclipse.dirigible.ide.repository.ui.command.OpenHandler;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.IResourceVersion;

public class ResourceHistoryView extends ViewPart {

	private static final String RESOURCE_HISTORY = Messages.ResourceHistoryView_RESOURCE_HISTORY;

	private static final String CREATED_BY = Messages.ResourceHistoryView_CREATED_BY;

	private static final String CREATED_AT = Messages.ResourceHistoryView_CREATED_AT;

	private static final String VERSION = Messages.ResourceHistoryView_VERSION;

	public static final Logger logger = LoggerFactory
			.getLogger(ResourceHistoryView.class);

	public static final String ID = "org.eclipse.dirigible.ide.repository.ui.view.ResourceHistoryView"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action doubleClickAction;

	private IResource selectedResource = null;

	private final ISelectionListener selectionListener = new SelectionListenerImpl();

	class ViewContentProvider implements ITreeContentProvider {
		private static final long serialVersionUID = -2635382675151862078L;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			//
		}

		public void dispose() {
			//
		}

		public Object[] getElements(Object parent) {
			if (selectedResource != null) {
				List<IResourceVersion> resourceVersions;
				String error = null;
				try {
					resourceVersions = selectedResource.getResourceVersions();
					if (resourceVersions != null) {
						return resourceVersions.toArray(new IResourceVersion[] {});
					} else {
						return new IResourceVersion[] {};
					}
				} catch (IOException e) {
					error = e.getMessage();
					logger.error(e.getMessage(), e);
				}
				return new String[] { error };
				// List<IResource> resources = new ArrayList<IResource>();
				// resources.add(selectedResource);
				// return resources.toArray(new IResource[]{});
			} else {
				return new IResourceVersion[] {};
				// return new IResource[]{};
			}
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9141488769336871391L;

		@Override
		public String getColumnText(Object obj, int index) {
			if (obj instanceof IResourceVersion) {
				IResourceVersion resourceVersion = (IResourceVersion) obj;
				switch (index) {
				case 0:
					return resourceVersion.getVersion() + ""; //$NON-NLS-1$
				case 1:
					return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(resourceVersion.getCreatedAt()); //$NON-NLS-1$
				case 2:
					return resourceVersion.getCreatedBy();
				default:
					break;
				}
			}
			return getText(obj);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}

	class VersionSorter extends ViewerSorter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3859467470664110687L;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 == null || e2 == null) {
				return 0;
			}
			if (e1 instanceof IResourceVersion
					|| e2 instanceof IResourceVersion) {
				return ((IResourceVersion) e2).getVersion()
						- ((IResourceVersion) e1).getVersion();
			}
			return 0;
		}
	}

	/**
	 * The constructor.
	 */
	public ResourceHistoryView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new VersionSorter());
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		createTreeHeader(tree);
		viewer.setInput(getViewSite());
		makeActions();
		hookDoubleClickAction();
	}

	private void createTreeHeader(Tree tree) {
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(VERSION);
		column.setWidth(70);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(CREATED_AT);
		column.setWidth(250);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(CREATED_BY);
		column.setWidth(300);
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7611215993365020899L;

			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof IResourceVersion) {
					openEditorForResource((IResourceVersion) obj);
				}
			}
		};
	}

	private boolean openEditorForResource(IResourceVersion file) {
		String editorId = OpenHandler.TEXT_EDITOR_ID;
		IEditorInput input;
		try {
			input = new ContentEditorInput(
					file.getVersion() + "", file.getPath(), file.getContent()); //$NON-NLS-1$
			return openEditor(editorId, input);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			showMessage(e.getMessage());
		}
		return false;
	}

	private static boolean openEditor(String id, IEditorInput input) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			page.openEditor(input, id);
			return true;
		} catch (PartInitException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	public void showMessage(String message) {
		logger.error(message);
		MessageDialog.openError(viewer.getControl().getShell(),
				RESOURCE_HISTORY, message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void handleElementSelected(Object element) {

		if (element instanceof IResource) {
			IResource resource = (IResource) element;
			this.selectedResource = resource;
			this.viewer.refresh();
		}

	}

	private class SelectionListenerImpl implements ISelectionListener {

		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				selectionChanged((IStructuredSelection) selection);
			}
		}

		private void selectionChanged(IStructuredSelection selection) {
			Object element = selection.getFirstElement();
			if (element != null) {
				handleElementSelected(element);
			}
		}

	}

	private void attachSelectionListener(IWorkbenchPartSite site) {
		if (site == null) {
			return;
		}
		final ISelectionService selectionService = getSelectionService(site);
		if (selectionService != null) {
			selectionService.addSelectionListener(selectionListener);
		}
	}

	private void detachSelectionListener(IWorkbenchPartSite site) {
		if (site == null) {
			return;
		}
		final ISelectionService selectionService = getSelectionService(site);
		if (selectionService != null) {
			selectionService.removeSelectionListener(selectionListener);
		}
	}

	private ISelectionService getSelectionService(IWorkbenchPartSite site) {
		final IWorkbenchWindow window = site.getWorkbenchWindow();
		if (window == null) {
			return null;
		}
		return window.getSelectionService();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		attachSelectionListener(site);
		super.init(site);
	}

	@Override
	public void dispose() {
		detachSelectionListener(getSite());
		super.dispose();
	}
}