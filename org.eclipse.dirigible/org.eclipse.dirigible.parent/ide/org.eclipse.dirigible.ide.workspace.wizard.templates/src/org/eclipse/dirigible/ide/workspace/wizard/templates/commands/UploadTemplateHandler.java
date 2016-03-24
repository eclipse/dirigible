/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.wizard.templates.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.ide.workspace.ui.commands.AbstractWorkspaceHandler;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class UploadTemplateHandler extends AbstractWorkspaceHandler {

	private static final String THIS_PROCESS_WILL_OVERRIDE_YOUR_EXISTING_TEMPLATES_DO_YOU_WANT_TO_CONTINUE = "This process will override your existing templates in case of collision. Do you want to continue?";
	private static final String OVERRIDE_TEMPLATES = "Override Templates";
	private static final String CANNOT_CLOSE_INPUT_STREAM_TO_AN_UPLOADED_FILE = "Cannot close input stream to an uploaded file";
	private static final String UPLOAD_ERROR = "Upload error";
	private static final String REASON = "Reason";
	private static final String CANNOT_UPLOAD = "Cannot upload";
	private static final String CANNOT_SAVE_UPLOADED_FILE = "Cannot save uploaded file";
	private static final String UPLOAD_TEMPLATES_ARCHIVE = "Upload Templates Archive";
	private static final Logger error = Logger.getLogger(UploadTemplateHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog dlg = new FileDialog(HandlerUtil.getActiveShell(event), SWT.TITLE | SWT.MULTI);
		// dlg.setAutoUpload(true);
		dlg.setText(UPLOAD_TEMPLATES_ARCHIVE);
		// dlg.setFilterExtensions(new String[] { "*.zip" }); //$NON-NLS-1$
		String projectPath = dlg.open();

		if ((projectPath != null)
				&& MessageDialog.openConfirm(null, OVERRIDE_TEMPLATES, THIS_PROCESS_WILL_OVERRIDE_YOUR_EXISTING_TEMPLATES_DO_YOU_WANT_TO_CONTINUE)) {
			String fileName = null;
			for (String fullFileName : dlg.getFileNames()) {
				fileName = fullFileName.substring(fullFileName.lastIndexOf(File.separatorChar) + 1);
				InputStream in = null;
				try {
					in = new FileInputStream(fullFileName);
					IRepository repository = RepositoryFacade.getInstance().getRepository();
					String root = IRepositoryPaths.DB_DIRIGIBLE_TEMPLATES;
					repository.importZip(new ZipInputStream(in), root);
					refreshWorkspace();
				} catch (Exception e) {
					error.error(CANNOT_SAVE_UPLOADED_FILE + fileName, e);
					MessageDialog.openError(null, UPLOAD_ERROR, CANNOT_UPLOAD + fileName + REASON + e.getMessage());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							error.warn(CANNOT_CLOSE_INPUT_STREAM_TO_AN_UPLOADED_FILE, e);
						}
					}
				}
			}
		}
		return null;
	}

}
