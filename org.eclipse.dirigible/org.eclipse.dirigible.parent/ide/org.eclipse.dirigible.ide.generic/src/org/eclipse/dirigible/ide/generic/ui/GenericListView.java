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

package org.eclipse.dirigible.ide.generic.ui;

import java.io.IOException;
import java.net.URI;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.dirigible.ide.common.CommonUtils;
import org.eclipse.dirigible.ide.common.UriValidator;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.logging.Logger;

public class GenericListView extends ViewPart {

	private static final String REMOVE_LOCATION_FROM_THE_GENERIC_VIEWS_LIST = "Remove Location from the Generic Views list";
	
	private static final String OPEN_LOCATION_FROM_THE_GENERIC_VIEWS_LIST = "Open Location from the Generic Views list";

	private static final String ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_LOCATION = "Are you sure you want to remove this location?";

	private static final String REMOVE_LOCATION = "Remove Location";
	
	private static final String OPEN_LOCATION = "Open Location";

	private static final String ADD_LOCATION_TO_THE_GENERIC_VIEWS_LIST = "Add Location to the Generic Views list";

	public static final String GENERIC_VIEW_ERROR = "Generic View Error";

	private static final String URL = "URL";

	private static final String ADD_LOCATION = "Add Location";

	private static final String LOCATION = "Location";

	private static final String NAME = "Name";

	private static final Logger logger = Logger
			.getLogger(GenericListView.class);

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.dirigible.ide.generic.ui.GenericListView"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action actionAddLocation;
	private Action actionRemoveLocation;
	private Action actionOpenLocation;
	
	private GenericListManager genericListManager = GenericListManager.getInstance(
			RepositoryFacade.getInstance().getRepository());

	public TreeViewer getViewer() {
		return viewer;
	}

	public GenericListManager getGenericListManager() {
		return genericListManager;
	}

	class NameSorter extends ViewerSorter {

		private static final long serialVersionUID = -8832089975378999206L;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 != null && e2 != null
					&& e1 instanceof GenericLocationMetadata
					&& e2 instanceof GenericLocationMetadata) {
				return super.compare(viewer,
						((GenericLocationMetadata) e1).getLocation(),
						((GenericLocationMetadata) e2).getLocation());
			}
			return super.compare(viewer, e1, e2);
		}

	}

	/**
	 * The constructor.
	 */
	public GenericListView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		// PatternFilter filter = new PatternFilter();
		// FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL
		// | SWT.V_SCROLL, filter, true);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTree().setHeaderVisible(true);
		TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
		column.setText(NAME);
		column.setWidth(150);
		column = new TreeColumn(viewer.getTree(), SWT.LEFT);
		column.setText(LOCATION);
		column.setWidth(500);

		viewer.setContentProvider(new GenericListViewContentProvider(this));
		viewer.setLabelProvider(new GenericListViewLabelProvider());
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
			private static final long serialVersionUID = -8683354310299838422L;

			public void menuAboutToShow(IMenuManager manager) {
				GenericListView.this.fillContextMenu(manager);
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
		hookDoubleClickAction();
	}
	
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				actionOpenLocation.run();
			}
		});
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionAddLocation);
		manager.add(actionRemoveLocation);
		manager.add(actionOpenLocation);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionAddLocation);
		manager.add(actionRemoveLocation);
		manager.add(actionOpenLocation);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionAddLocation);
		manager.add(actionRemoveLocation);
		manager.add(actionOpenLocation);
	}

	private void makeActions() {
		actionAddLocation = new Action() {
			private static final long serialVersionUID = -6534944336694980431L;

			public void run() {
				InputDialog dlg = new InputDialog(viewer.getControl()
						.getShell(), ADD_LOCATION, URL,
						"http://", new UriValidator()); //$NON-NLS-1$
				if (dlg.open() == Window.OK) {
					try {
						// TODO - parse for name
						URI uri = new URI(dlg.getValue());
						genericListManager.addLocation(
								getNameForLocation(uri), dlg.getValue());
						viewer.refresh();
					} catch (Exception e) {
						logger.error(GENERIC_VIEW_ERROR, e);
						MessageDialog.openError(viewer.getControl().getShell(),
								GENERIC_VIEW_ERROR, e.getMessage());
					}
				}
			}

			private String getNameForLocation(URI uri) throws IOException {
				String name = CommonUtils.replaceNonAlphaNumericCharacters(uri.getHost());
				String originalName = name;
				int i=1;
				while (genericListManager.existsLocation(name)) {
					name = originalName + i++;
				}
				
				return name;
			}
		};
		actionAddLocation.setText(ADD_LOCATION);
		actionAddLocation.setToolTipText(ADD_LOCATION_TO_THE_GENERIC_VIEWS_LIST);
		actionAddLocation.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actionRemoveLocation = new Action() {
			private static final long serialVersionUID = 1336014167502247774L;

			public void run() {

				if (!viewer.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) viewer
							.getSelection();
					GenericLocationMetadata location = (GenericLocationMetadata) selection.getFirstElement();
					if (MessageDialog
							.openConfirm(
									viewer.getControl().getShell(),
									REMOVE_LOCATION,
									ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_LOCATION
											+ location.getLocation())) {
						try {
							genericListManager.removeLocation(location.getName());
							viewer.refresh();
						} catch (Exception e) {
							logger.error(GENERIC_VIEW_ERROR, e);
							MessageDialog
									.openError(viewer.getControl().getShell(),
											GENERIC_VIEW_ERROR, e.getMessage());
						}
					}
				}
			}
		};
		actionRemoveLocation.setText(REMOVE_LOCATION);
		actionRemoveLocation
				.setToolTipText(REMOVE_LOCATION_FROM_THE_GENERIC_VIEWS_LIST);
		actionRemoveLocation.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
		
		
		actionOpenLocation = new Action() {
			private static final long serialVersionUID = 1336014167502247774L;

			public void run() {

				if (!viewer.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) viewer
							.getSelection();
					GenericLocationMetadata location = (GenericLocationMetadata) selection.getFirstElement();
					
					IWorkbench wb = PlatformUI.getWorkbench();
					IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
					IWorkbenchPage page = win.getActivePage();
					String id = GenericPerspective.GENERIC_VIEW_ID;
					try {
						IViewPart view = page.showView(id, location.getName(), IWorkbenchPage.VIEW_VISIBLE);
						((GenericView) view).setSiteUrl(location.getLocation());
						((GenericView) view).setViewTitle(location.getName());
					} catch (PartInitException e) {
						logger.error(GENERIC_VIEW_ERROR, e);
					}
						
				}
			}
		};
		actionOpenLocation.setText(OPEN_LOCATION);
		actionOpenLocation
				.setToolTipText(OPEN_LOCATION_FROM_THE_GENERIC_VIEWS_LIST);
		actionOpenLocation.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
}