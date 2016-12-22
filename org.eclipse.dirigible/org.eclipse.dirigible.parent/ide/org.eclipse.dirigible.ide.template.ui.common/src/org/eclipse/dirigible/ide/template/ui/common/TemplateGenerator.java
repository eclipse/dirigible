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
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.repository.velocity.VelocityGenerator;

public abstract class TemplateGenerator {

	private static final String THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF = Messages.TemplateGenerator_THE_FILE_ALREADY_EXISTS_SKIPPED_GENERATION_OF;

	private static final Logger logger = Logger.getLogger(TemplateGenerator.class);

	private VelocityGenerator velocityGenerator = new VelocityGenerator();

	private List<IFile> createdFiles = new ArrayList<IFile>();

	protected abstract GenerationModel getModel();

	protected Map<String, Object> prepareParameters() {
		GenerationModel model = getModel();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("projectName", model.getProjectName()); //$NON-NLS-1$
		if (model.getPackageName() != null) {
			parameters.put("packageName", model.getPackageName()); //$NON-NLS-1$
		} else {
			parameters.put("packageName", constructPackageName()); //$NON-NLS-1$
		}
		parameters.put("fileName", model.getFileName()); //$NON-NLS-1$
		parameters.put("fileNameNoExtension", model.getFileNameNoExtension()); //$NON-NLS-1$

		return parameters;
	}

	protected abstract String getLogTag();

	public void generate() throws Exception {
		generate(null);
	}

	public void generate(HttpServletRequest request) throws Exception {
		TemplateSourceMetadata[] sources = getModel().getTemplate().getTemplateMetadata().getSources();
		for (int i = 0; i < sources.length; i++) {
			String targetLocation = getModel().getTargetLocation();
			String name = sources[i].getName();
			// alternative location check for surrounding template target
			targetLocation = calcTargetLocation(targetLocation, name);
			if ((name != null) && (name.indexOf(IRepository.SEPARATOR) > 1)) {
				name = name.substring(name.indexOf(IRepository.SEPARATOR));
			}
			String renaming = sources[i].getRename();
			if ((renaming != null) && !"".equals(renaming)) {
				// rename the surrounding template target
				String baseFilename = FilenameUtils.getBaseName(getModel().getFileName());
				String originalExtension = FilenameUtils.getExtension(name);
				name = String.format(renaming, baseFilename) + FilenameUtils.EXTENSION_SEPARATOR + originalExtension;
			}

			if (sources[i].isGenerate()) {
				if (i == 0) {
					// leading template
					generateFile(sources[i].getLocation(), targetLocation, getModel().getFileName(), request);
				} else {
					// surrounding templates
					generateFile(sources[i].getLocation(), targetLocation, name, request);
				}
			} else {
				copyFile(name, targetLocation, sources[i].getLocation(), request);
			}
		}

	}

	private String calcTargetLocation(String targetLocation, String name) {
		if ((name != null) && (name.indexOf(IRepository.SEPARATOR) > 1)) {
			String alternativeTarget = null;
			if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.DATA_STRUCTURES;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.EXTENSION_DEFINITIONS;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.INTEGRATION_SERVICES;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.SECURITY_CONSTRAINTS;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.TEST_CASES)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.TEST_CASES;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.WEB_CONTENT;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.WIKI_CONTENT;
			} else if (name.startsWith(ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS)) {
				alternativeTarget = ICommonConstants.ARTIFACT_TYPE.MOBILE_APPLICATIONS;
			}
			if (alternativeTarget != null) {
				IPath location = new Path(targetLocation);
				IPath target = new Path(IRepository.SEPARATOR);
				String[] segments = location.segments();
				int i = 1;
				for (String segment : segments) {
					if (i != 2) {
						target = target.append(segment);
					} else {
						target = target.append(alternativeTarget);
					}
					i++;
				}
				String relativePath = name.substring(alternativeTarget.length());
				relativePath = relativePath.substring(0, relativePath.lastIndexOf(IRepository.SEPARATOR));
				target = target.append(relativePath);
				return target.toString();
			}
		}
		return targetLocation;
	}

	public void generateFile(String templateLocation, String targetLocation, String fileName) throws Exception {
		generateFile(templateLocation, targetLocation, fileName, null);
	}

	public void generateFile(String templateLocation, String targetLocation, String fileName, HttpServletRequest request) throws Exception {
		Map<String, Object> parameters = prepareParameters();
		InputStream in = GenerationModel.getInputStreamByTemplateLocation(templateLocation, request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		velocityGenerator.generate(in, out, parameters, getLogTag());
		byte[] bytes = out.toByteArray();
		bytes = afterGeneration(bytes);
		IPath location = new Path(targetLocation).append(fileName);
		createFile(location, bytes, request);
	}

	// default implementation - do nothing
	protected byte[] afterGeneration(byte[] bytes) {
		return bytes;
	}

	protected void createFile(IPath location, byte[] bytes, HttpServletRequest request) throws Exception {
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

	protected void copyFile(String targetFileName, String targetLocation, String templateLocation, HttpServletRequest request)
			throws IOException, Exception {
		IPath location = new Path(targetLocation).append(targetFileName);
		InputStream in = GenerationModel.getInputStreamByTemplateLocation(templateLocation, request);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		createFile(location, out.toByteArray(), request);
	}

	public List<IFile> getGeneratedFiles() {
		return createdFiles;
	}

	public VelocityGenerator getVelocityGenerator() {
		return velocityGenerator;
	}

	public String constructPackageName() {
		return getModel().constructPackageName();
	}
}
