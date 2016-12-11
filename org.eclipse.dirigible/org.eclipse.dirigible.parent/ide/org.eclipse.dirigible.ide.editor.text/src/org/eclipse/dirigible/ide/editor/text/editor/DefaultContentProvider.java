/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.editor.text.editor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dirigible.ide.common.CommonIDEParameters;
import org.eclipse.dirigible.ide.editor.text.input.ContentEditorInput;
import org.eclipse.dirigible.ide.repository.RepositoryFacade;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

public class DefaultContentProvider implements IContentProvider, IExecutableExtension {

	private static final String CANNOT_SAVE_FILE_CONTENTS = Messages.DefaultContentProvider_CANNOT_SAVE_FILE_CONTENTS;
	private static final String CANNOT_READ_FILE_CONTENTS = Messages.DefaultContentProvider_CANNOT_READ_FILE_CONTENTS;
	private static final String WE_SHOULD_NEVER_GET_HERE = Messages.DefaultContentProvider_WE_SHOULD_NEVER_GET_HERE;
	private static final Logger LOGGER = Logger.getLogger(DefaultContentProvider.class);

	@Override
	public String getContent(IEditorInput input) throws ContentProviderException {
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			return readFile(fileInput.getFile());
		}
		if (input instanceof ContentEditorInput) {
			ContentEditorInput contentInput = (ContentEditorInput) input;
			return new String(contentInput.getContent(), ICommonConstants.UTF8);
		}
		throw new IllegalStateException(WE_SHOULD_NEVER_GET_HERE);
	}

	@Override
	public void save(IProgressMonitor monitor, IEditorInput input, String content, boolean overwrite) throws ContentProviderException {
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			writeFile(fileInput.getFile(), content.getBytes(ICommonConstants.UTF8));
		}
	}

	protected static final String readFile(IFile file) throws ContentProviderException {
		try {
			BufferedReader in = null;
			if (file.getClass().getCanonicalName().equals("org.eclipse.dirigible.ide.workspace.impl.File")) {
				in = new BufferedReader(new InputStreamReader(file.getContents()));
			} else {
				IResource resource = getFromRepository(file);
				in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(resource.getContent()), ICommonConstants.UTF8));
			}
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
				builder.append("\n"); //$NON-NLS-1$
			}
			return builder.toString();
		} catch (Exception ex) {
			LOGGER.error(CANNOT_READ_FILE_CONTENTS, ex);
			throw new ContentProviderException(CANNOT_READ_FILE_CONTENTS, ex);
		}
	}

	private static IResource getFromRepository(IFile file) {
		IRepository repository = RepositoryFacade.getInstance().getRepository();

		String resourcePath = IRepositoryPaths.DB_DIRIGIBLE_USERS + CommonIDEParameters.getUserName() + IRepositoryPaths.SEPARATOR
				+ IRepositoryPaths.WORKSPACE_FOLDER_NAME + file.getFullPath();
		IResource resource = repository.getResource(resourcePath);
		return resource;
	}

	protected static final void writeFile(IFile file, byte[] content) throws ContentProviderException {
		try {
			if (file.getClass().getCanonicalName().equals("org.eclipse.dirigible.ide.workspace.impl.File")) {
				file.setContents(new ByteArrayInputStream(content), false, false, null);
			} else {
				IResource resource = getFromRepository(file);
				resource.setContent(content);
			}
		} catch (CoreException ex) {
			LOGGER.error(CANNOT_SAVE_FILE_CONTENTS, ex);
			throw new ContentProviderException(CANNOT_SAVE_FILE_CONTENTS, ex);
		} catch (IOException e) {
			LOGGER.error(CANNOT_SAVE_FILE_CONTENTS, e);
			throw new ContentProviderException(CANNOT_SAVE_FILE_CONTENTS, e);
		}
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		//
	}
}
