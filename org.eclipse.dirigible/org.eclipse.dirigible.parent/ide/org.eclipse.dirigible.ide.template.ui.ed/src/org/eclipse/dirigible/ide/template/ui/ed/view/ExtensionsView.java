/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.ed.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.common.CommonIDEUtils;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.template.ui.ed.wizard.Messages;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.commands.OpenHandler;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionDefinition;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionManager;
import org.eclipse.dirigible.repository.ext.extensions.ExtensionPointDefinition;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ExtensionsView extends ViewPart {

	private static final Logger logger = Logger.getLogger(ExtensionsView.class);

	public static final String EXTENSIONS_ERROR = Messages.ExtensionsView_EXTENSIONS_ERROR;

	private TreeViewer viewer;

	private ExtensionManager extensionManager = new ExtensionManager(RepositoryFacade.getInstance().getRepository(),
			DataSourceFacade.getInstance().getDataSource(CommonIDEParameters.getRequest()), CommonIDEParameters.getRequest());

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new ExtensionsContentProvider(extensionManager, viewer.getControl().getShell()));
		viewer.setLabelProvider(new ExtensionsLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(getExtensionPoints());

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object firstElement = selection.getFirstElement();

				if (firstElement != null) {
					if (firstElement instanceof ExtensionPointDefinition) {
						ExtensionPointDefinition extensionPoint = (ExtensionPointDefinition) firstElement;
						openEditor(CommonIDEUtils.formatToIDEPath(ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS, extensionPoint.getLocation())
								+ ICommonConstants.DOT + ICommonConstants.ARTIFACT_EXTENSION.EXTENSION_POINT);
					} else if (firstElement instanceof ExtensionDefinition) {
						ExtensionDefinition extension = (ExtensionDefinition) firstElement;
						openEditor(CommonIDEUtils.formatToIDEPath(ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS, extension.getLocation())
								+ ICommonConstants.DOT + ICommonConstants.ARTIFACT_EXTENSION.EXTENSION);
					}
				}
			}
		});
	}

	private void openEditor(String path) {
		IPath location = new Path(path);
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IFile file = root.getFile(location);
		if (file.exists()) {
			OpenHandler.open(file, 0);
		}
	}

	private List<ExtensionPointDefinition> getExtensionPoints() {
		List<ExtensionPointDefinition> extensionPoints = new ArrayList<ExtensionPointDefinition>();
		try {
			for (String nextExtensionPoint : extensionManager.getExtensionPoints()) {
				extensionPoints.add(extensionManager.getExtensionPoint(nextExtensionPoint));
			}
		} catch (Exception e) {
			logger.error(EXTENSIONS_ERROR, e);
			MessageDialog.openError(viewer.getControl().getShell(), EXTENSIONS_ERROR, e.getMessage());
		}
		return extensionPoints;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
