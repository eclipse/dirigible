/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dirigible.ide.common.status.LogProgressMonitor;
import org.eclipse.dirigible.ide.workspace.dual.WorkspaceLocator;
import org.eclipse.dirigible.ide.workspace.ui.viewer.WorkspaceViewerUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.velocity.VelocityGenerator;

/**
 * Abstract Template Generator
 */
public abstract class TemplateGenerator {

	private static final Logger logger = Logger.getLogger(TemplateGenerator.class);

	private static final String THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF = Messages.TemplateGenerator_THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF;
	private static final String PARAMETER_PROJECT_NAME = "projectName"; //$NON-NLS-1$
	private static final String PARAMETER_PACKAGE_NAME = "packageName"; //$NON-NLS-1$
	private static final String PARAMETER_FILE_NAME = "fileName"; //$NON-NLS-1$
	private static final String PARAMETER_FILE_NAME_NO_EXTENSION = "fileNameNoExtension"; //$NON-NLS-1$

	private VelocityGenerator velocityGenerator = new VelocityGenerator();
	private List<IFile> createdFiles = new ArrayList<IFile>();

	/**
	 * Generate template
	 *
	 * @throws Exception
	 */
	public void generate() throws Exception {
		generate(null);
	}

	/**
	 * Generate template
	 *
	 * @param request
	 * @throws Exception
	 */
	public void generate(HttpServletRequest request) throws Exception {
		TemplateSourceMetadata[] sources = getModel().getTemplate().getTemplateMetadata().getSources();
		for (TemplateSourceMetadata next : sources) {
			String templateLocation = next.getLocation();
			String targetLocation = getTargetLocation(next);
			String fileName = getFileName(next);
			if (next.isGenerate()) {
				generateFile(templateLocation, targetLocation, fileName, request);
			} else {
				copyFile(templateLocation, targetLocation, fileName, request);
			}
		}
	}

	private String getFileName(TemplateSourceMetadata source) {
		String fileName = FilenameUtils.getBaseName(source.getName());
		String extension = FilenameUtils.getExtension(source.getName());
		if (source.isRenaming()) {
			fileName = String.format(source.getRename(), FilenameUtils.getBaseName(getModel().getFileName()));
		}
		return fileName + FilenameUtils.EXTENSION_SEPARATOR + extension;
	}

	private String getTargetLocation(TemplateSourceMetadata next) {
		StringBuilder targetLocation = new StringBuilder() //
				.append(getModel().getProjectName()) //
				.append(IRepository.SEPARATOR) //
				.append(next.getRootFolder()) //
				.append(IRepository.SEPARATOR) //
				.append(getModel().getProjectPackageName());
		if (next.getPackagePath() != null) {
			targetLocation //
					.append(IRepository.SEPARATOR) //
					.append(next.getPackagePath());
		}
		return targetLocation.toString();
	}

	/**
	 * @return List of the generated files
	 */
	public List<IFile> getGeneratedFiles() {
		return createdFiles;
	}

	protected abstract GenerationModel getModel();

	protected abstract String getLogTag();

	protected Map<String, Object> prepareParameters() {
		GenerationModel model = getModel();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(PARAMETER_PROJECT_NAME, model.getProjectName());
		if (model.getPackageName() != null) {
			parameters.put(PARAMETER_PACKAGE_NAME, model.getPackageName());
		} else {
			parameters.put(PARAMETER_PACKAGE_NAME, constructPackageName());
		}
		parameters.put(PARAMETER_FILE_NAME, model.getFileName());
		parameters.put(PARAMETER_FILE_NAME_NO_EXTENSION, model.getFileNameNoExtension());
		return parameters;
	}

	// default implementation - do nothing
	protected byte[] afterGeneration(byte[] bytes) {
		return bytes;
	}

	private void generateFile(String templateLocation, String targetLocation, String fileName, HttpServletRequest request) throws Exception {
		Map<String, Object> parameters = prepareParameters();
		InputStream in = GenerationModel.getInputStreamByTemplateLocation(templateLocation, request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		velocityGenerator.generate(in, out, parameters, getLogTag());
		byte[] bytes = out.toByteArray();
		bytes = afterGeneration(bytes);
		IPath location = new Path(targetLocation).append(fileName);
		createFile(location, bytes, request);
	}

	private void createFile(IPath location, byte[] bytes, HttpServletRequest request) throws Exception {
		IWorkspace workspace = WorkspaceLocator.getWorkspace(request);
		IWorkspaceRoot root = workspace.getRoot();
		IFile file = root.getFile(location);
		if (file.exists()) {
			// TODO add as Markers as well
			logger.warn(String.format(THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF, location));
		} else {
			IProgressMonitor monitor = null;
			if (request != null) {
				monitor = new LogProgressMonitor();
			}
			createMissingParents(file, monitor);
			file.create(new ByteArrayInputStream(bytes), false, monitor);
			createdFiles.add(file);
		}
		if (request == null) {
			IContainer parent = file.getParent();
			if (parent != null) {
				WorkspaceViewerUtils.expandElement(parent);
			}
			WorkspaceViewerUtils.selectElement(file);
		}
	}

	private void createMissingParents(IFile file, IProgressMonitor monitor) throws CoreException {
		Stack<IContainer> missingParents = new Stack<IContainer>();
		for (IContainer parent = file.getParent(); !parent.exists(); parent = parent.getParent()) {
			missingParents.push(parent);
		}
		while (!missingParents.isEmpty()) {
			IContainer next = missingParents.pop();
			if (next instanceof IFolder) {
				((IFolder) next).create(false, true, monitor);
			}
		}
	}

	private void copyFile(String templateLocation, String targetLocation, String fileName, HttpServletRequest request) throws IOException, Exception {
		IPath location = new Path(targetLocation).append(fileName);
		InputStream in = GenerationModel.getInputStreamByTemplateLocation(templateLocation, request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		createFile(location, out.toByteArray(), request);
	}

	private String constructPackageName() {
		return getModel().constructPackageName();
	}
}
