/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.ui.commands;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dirigible.ide.workspace.ui.shared.FocusableWizardPage;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * The upload wizard page, that contains: 1) an info message about the possible
 * user input 2) a browse button for selecting files 3) an SWT list that
 * contains the selected .dsv files
 */
public class UploadDataWizardPage extends FocusableWizardPage {

	private static final String FILE_EXTENSION_DSV = "*.dsv"; //$NON-NLS-1$

	private static final long serialVersionUID = -5429650850261852397L;

	private static final String INFO_MSG = Messages.UploadDataWizardPage_INFO_MSG;

	private static final String PAGE_NAME = "Main Page"; //$NON-NLS-1$

	private static final String PAGE_TITLE = Messages.UploadDataWizardPage_PAGE_TITLE;

	private static final String PAGE_DESCRIPTION = Messages.UploadDataWizardPage_PAGE_DESCRIPTION;

	private FileDialog dlg;

	private List fileList;

	private java.util.Set<String> filesPaths = new HashSet<String>();

	protected UploadDataWizardPage() {
		super(PAGE_NAME);
		setTitle(PAGE_TITLE);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(3, false));
		createInfoMsgArea(composite);
		createFilesArea(composite);
	}

	private void createInfoMsgArea(Composite composite) {
		// create the info image
		GridData gridDataInfoImage = new GridData();
		gridDataInfoImage.horizontalAlignment = GridData.END;
		gridDataInfoImage.verticalAlignment = GridData.FILL;
		Label infoImage = new Label(composite, SWT.NONE);
		infoImage.setImage(JFaceResources.getImage(TitleAreaDialog.DLG_IMG_MESSAGE_INFO));
		infoImage.setLayoutData(gridDataInfoImage);
		// create the info text
		Text infoMsg = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		GridData gridDataInfoMsg = new GridData(GridData.FILL_HORIZONTAL);
		gridDataInfoMsg.horizontalSpan = 2;
		gridDataInfoMsg.widthHint = 400;
		infoMsg.setText(INFO_MSG);
		infoMsg.setLayoutData(gridDataInfoMsg);
	}

	private void createFilesArea(Composite composite) {
		// create the files label
		Label fileLabel = new Label(composite, SWT.NONE);
		fileLabel.setText(Messages.UploadDataWizardPage_FILES);
		// create the files list
		fileList = new List(composite, SWT.BORDER | SWT.V_SCROLL);
		fileList.setLayoutData(new GridData(GridData.FILL_BOTH));
		// create the browse button
		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText(Messages.UploadDataWizardPage_BROWSE);
		browseButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 2205011133121002918L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleFileBrowseButtonPressed(e);
			}
		});
		browseButton.setEnabled(true);
		browseButton.setVisible(true);
		setFocusable(browseButton);
	}

	private void handleFileBrowseButtonPressed(SelectionEvent event) {
		dlg = new FileDialog(getShell(), SWT.TITLE | SWT.MULTI);
		// dlg.setAutoUpload(true);
		dlg.setText(Messages.UploadDataWizardPage_UPLOAD_DATA);
		// dlg.setFilterExtensions(new String[] { FILE_EXTENSION_DSV });
		dlg.open();
		if (dlg.getFileNames().length > 0) {
			filesPaths.addAll(Arrays.asList(dlg.getFileNames()));
			setFilesInUI();
			setPageComplete(true);
		}
	}

	@Override
	public boolean isPageComplete() {
		return !filesPaths.isEmpty();
	}

	private void setFilesInUI() {
		fileList.removeAll();
		for (String file : filesPaths) {
			String fileName = new File(file).getName();
			fileList.add(fileName);
		}
	}

	public Set<String> getFilePaths() {
		return filesPaths;
	}

}
