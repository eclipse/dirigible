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

package org.eclipse.dirigible.ide.template.ui.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.dirigible.ide.template.velocity.VelocityGenerator;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewerUtils;
import org.eclipse.dirigible.repository.logging.Logger;

public abstract class TemplateGenerator {

	private static final String THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF = Messages.TemplateGenerator_THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF;

	private static final Logger logger = Logger
			.getLogger(TemplateGenerator.class);

	private VelocityGenerator velocityGenerator = new VelocityGenerator();

	private List<IFile> createdFiles = new ArrayList<IFile>();

	protected abstract GenerationModel getModel();

	protected abstract Map<String, Object> prepareParameters();

	protected abstract String getLogTag();

	public void generate() throws Exception {
		generateFile(getModel().getTemplateLocation(), getModel()
				.getTargetLocation(), getModel().getFileName());
	}

	public void generateFile(String templateLocation, String targetLocation,
			String fileName) throws Exception {
		Map<String, Object> parameters = prepareParameters();
		InputStream in = this.getClass().getResourceAsStream(templateLocation);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		velocityGenerator.generate(in, out, parameters, getLogTag());
		byte[] bytes = out.toByteArray();
		bytes = afterGeneration(bytes);
		IPath location = new Path(targetLocation).append(fileName);
		createFile(location, bytes);
	}

	// default implementation - do nothing
	protected byte[] afterGeneration(byte[] bytes) {
		return bytes;
	}

	protected void createFile(IPath location, byte[] bytes) throws Exception {
		IWorkspace workspace = WorkspaceLocator.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IFile file = root.getFile(location);
		if (file.exists()) {
			// TODO add as Markers as well
			logger.warn(String.format(THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF, location));
		} else {
			if (!file.getParent().exists()) {
				IContainer parentContainer = file.getParent();
				if (parentContainer instanceof IFolder) {
					((IFolder) parentContainer).create(false, true, null);
				}
			}
			file.create(new ByteArrayInputStream(bytes), false, null);
			createdFiles.add(file);
		}
		IContainer parent = file.getParent();
		if (parent != null) {
			WorkspaceViewerUtils.expandElement(parent);
		}
		WorkspaceViewerUtils.selectElement(file);
	}

	protected void copyFile(String targetFileName, String templateLocation,
			Class<?> clazz) throws IOException, Exception {
		IPath location = new Path(getModel().getTargetLocation())
				.append(targetFileName);
		InputStream in = clazz.getResourceAsStream(templateLocation);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		createFile(location, out.toByteArray());
	}

	public List<IFile> getGeneratedFiles() {
		return createdFiles;
	}

	public VelocityGenerator getVelocityGenerator() {
		return velocityGenerator;
	}
}
