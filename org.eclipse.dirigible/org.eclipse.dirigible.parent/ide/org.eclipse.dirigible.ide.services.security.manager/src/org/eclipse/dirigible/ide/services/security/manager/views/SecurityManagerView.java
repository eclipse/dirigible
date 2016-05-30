/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.services.security.manager.views;

import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.security.SecurityException;
import org.eclipse.dirigible.repository.ext.security.SecurityLocationMetadata;
import org.eclipse.dirigible.repository.ext.security.SecurityManager;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class SecurityManagerView extends ViewPart {

	private static final String REFRESH_THE_LIST_OF_PROTECTED_LOCATIONS = Messages.SecurityManagerView_REFRESH_THE_LIST_OF_PROTECTED_LOCATIONS;

	private static final String REFRESH = Messages.SecurityManagerView_REFRESH;

	private static final String REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS = Messages.SecurityManagerView_REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS;

	private static final String ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS = Messages.SecurityManagerView_ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS;

	private static final String PROTECT_A_GIVEN_RELATIVE_URL_TRANSITIVELY = Messages.SecurityManagerView_PROTECT_A_GIVEN_RELATIVE_URL_TRANSITIVELY;

	private static final String PROTECTED_URL = Messages.SecurityManagerView_PROTECTED_URL;

	private static final String ROLES = Messages.SecurityManagerView_ROLES;

	private static final String LOCATION = Messages.SecurityManagerView_LOCATION;

	private static final String LOCATION_IS_TOO_LONG = Messages.SecurityManagerView_LOCATION_IS_TOO_LONG;

	private static final Logger logger = Logger.getLogger(SecurityManagerView.class);

	private static final String UNSECURE_LOCATION = Messages.SecurityManagerView_UNSECURE_LOCATION;

	private static final String SECURE_LOCATION = Messages.SecurityManagerView_SECURE_LOCATION;

	public static final String SECURITY_ERROR = Messages.SecurityManagerView_SECURITY_ERROR;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipse.dirigible.ide.services.security.manager.views.SecurityManagerView"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action actionSecure;
	private Action actionUnsecure;
	private Action actionRefresh;

	private SecurityManager securityManager = SecurityManager.getInstance(RepositoryFacade.getInstance().getRepository(),
			DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()));

	public TreeViewer getViewer() {
		return viewer;
	}

	public SecurityManager getSecurityManager() {
		return securityManager;
	}

	class NameSorter extends ViewerSorter {

		/**
		 *
		 */
		private static final long serialVersionUID = -8832089975378999206L;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if ((e1 != null) && (e2 != null) && (e1 instanceof SecurityLocationMetadata) && (e2 instanceof SecurityLocationMetadata)) {
				return super.compare(viewer, ((SecurityLocationMetadata) e1).getLocation(), ((SecurityLocationMetadata) e2).getLocation());
			}
			return super.compare(viewer, e1, e2);
		}

	}

	/**
	 * The constructor.
	 */
	public SecurityManagerView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		// PatternFilter filter = new PatternFilter();
		// FilteredTree tree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL
		// | SWT.V_SCROLL, filter, true);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTree().setHeaderVisible(true);
		TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
		column.setText(LOCATION);
		column.setWidth(300);
		column = new TreeColumn(viewer.getTree(), SWT.LEFT);
		column.setText(ROLES);
		column.setWidth(500);

		viewer.setContentProvider(new SecurityViewContentProvider(this));
		viewer.setLabelProvider(new SecurityViewLabelProvider());
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
				SecurityManagerView.this.fillContextMenu(manager);
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
		manager.add(actionSecure);
		manager.add(actionUnsecure);
		manager.add(new Separator());
		manager.add(actionRefresh);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionSecure);
		manager.add(actionUnsecure);
		manager.add(actionRefresh);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionSecure);
		manager.add(actionUnsecure);
		manager.add(actionRefresh);
	}

	private void makeActions() {
		actionSecure = new Action() {
			/**
			 *
			 */
			private static final long serialVersionUID = -6534944336694980431L;

			@Override
			public void run() {
				InputDialog dlg = new InputDialog(viewer.getControl().getShell(), SECURE_LOCATION, PROTECTED_URL, "/project1/securedFolder", //$NON-NLS-1$
						new LengthValidator());
				if (dlg.open() == Window.OK) {
					try {
						securityManager.secureLocation(dlg.getValue(), CommonIDEParameters.getRequest());
						viewer.refresh();
					} catch (SecurityException e) {
						logger.error(SECURITY_ERROR, e);
						MessageDialog.openError(viewer.getControl().getShell(), SECURITY_ERROR, e.getMessage());
					}
				}
			}
		};
		actionSecure.setText(SECURE_LOCATION);
		actionSecure.setToolTipText(PROTECT_A_GIVEN_RELATIVE_URL_TRANSITIVELY);
		actionSecure.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_STOP));

		actionUnsecure = new Action() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1336014167502247774L;

			@Override
			public void run() {

				if (!viewer.getSelection().isEmpty()) {
					StructuredSelection selection = (StructuredSelection) viewer.getSelection();
					SecurityLocationMetadata location = (SecurityLocationMetadata) selection.getFirstElement();
					if (MessageDialog.openConfirm(viewer.getControl().getShell(), UNSECURE_LOCATION,
							ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS + location.getLocation())) {
						try {
							securityManager.unsecureLocation(location.getLocation());
							viewer.refresh();
						} catch (SecurityException e) {
							logger.error(SECURITY_ERROR, e);
							MessageDialog.openError(viewer.getControl().getShell(), SECURITY_ERROR, e.getMessage());
						}
					}
				}
			}
		};
		actionUnsecure.setText(UNSECURE_LOCATION);
		actionUnsecure.setToolTipText(REMOVE_THE_SELECTED_LOCATION_FROM_THE_LIST_OF_PROTECTED_LOCATIONS);
		actionUnsecure.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

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
		actionRefresh.setText(REFRESH);
		actionRefresh.setToolTipText(REFRESH_THE_LIST_OF_PROTECTED_LOCATIONS);
		actionRefresh.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	class LengthValidator implements IInputValidator {

		/**
		 *
		 */
		private static final long serialVersionUID = 553319995495098208L;

		/**
		 * Validates the String. Returns null for no error, or an error message
		 *
		 * @param newText
		 *            the String to validate
		 * @return String
		 */
		@Override
		public String isValid(String newText) {
			int len = newText.length();

			// Determine if input is too short or too long
			if (len > 1000) {
				return LOCATION_IS_TOO_LONG;
			}

			// Input must be OK
			return null;
		}
	}
}
