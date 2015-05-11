/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.ide.dialogs;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemMessages;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;

/**
 * FileSystemSelectionArea is the area used to select the file system.
 * 
 * @since 3.2
 * 
 */

public class FileSystemSelectionArea {

	private Label fileSystemTitle;
	private ComboViewer fileSystems;

	/**
	 * Create a new instance of the receiver.
	 */
	public FileSystemSelectionArea() {

	}

	/**
	 * Create the contents of the receiver in composite.
	 * 
	 * @param composite
	 */
	public void createContents(Composite composite) {

		fileSystemTitle = new Label(composite, SWT.NONE);
		fileSystemTitle.setText(FileSystemMessages.FileSystemSelection_title);

		fileSystems = new ComboViewer(composite, SWT.READ_ONLY);

		fileSystems.getControl().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL
						| GridData.GRAB_HORIZONTAL));

		fileSystems.setLabelProvider(new LabelProvider() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1016234770078283634L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			public String getText(Object element) {
				return ((FileSystemConfiguration) element).getLabel();
			}
		});

		fileSystems.setContentProvider(new IStructuredContentProvider() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4602504832139703303L;

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {
				// Nothing to do
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements
			 * (java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				return FileSystemSupportRegistry.getInstance()
						.getConfigurations();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
			 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(org.eclipse.jface.viewers.Viewer viewer,
					Object oldInput, Object newInput) {
				// Nothing to do
			}

		});

		fileSystems.setInput(this);
		fileSystems.setSelection(new StructuredSelection(
				FileSystemSupportRegistry.getInstance()
						.getDefaultConfiguration()));
	}

	/**
	 * Return the selected configuration.
	 * 
	 * @return FileSystemConfiguration or <code>null</code> if nothing is
	 *         selected.
	 */
	public FileSystemConfiguration getSelectedConfiguration() {
		ISelection selection = fileSystems.getSelection();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			if (structured.size() == 1) {
				return (FileSystemConfiguration) structured.getFirstElement();
			}
		}

		return null;
	}

	/**
	 * Set the enablement state of the widget.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		fileSystemTitle.setEnabled(enabled);
		fileSystems.getControl().setEnabled(enabled);

	}
}
