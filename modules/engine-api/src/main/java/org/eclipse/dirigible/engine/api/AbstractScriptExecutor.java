/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.engine.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScriptExecutor extends AbstractResourceExecutor implements IScriptExecutor {

	private static final Logger logger = LoggerFactory.getLogger(AbstractScriptExecutor.class);

	protected abstract Object executeServiceModule(String module,
			Map<Object, Object> executionContext) throws ScriptingException;


	@Override
	public Module retrieveModule(String root, String module) throws RepositoryException {
		return retrieveModule(root, module, null);
	}
	
	@Override
	public Module retrieveModule(String root, String module, String extension) throws RepositoryException {

		// try from the registry
		String resourcePath = createResourcePath(root, module, extension);
		final IResource resource = getRepository().getResource(resourcePath);
		if (resource.exists()) {
			return new Module(getModuleName(resource.getPath()), resource.getPath(), getResourceContent(root, module));
		}
		
		// not found
		final String logMsg = String.format("There is no resource [%s] at the specified Service path: %s", (module + extension), root);
		logger.error(logMsg);
		throw new RepositoryException(logMsg);
	}

//	@Override
//	public List<Module> retrieveModulesByExtension(String root, String extension) throws RepositoryException {
//		Map<String, Module> modules = new HashMap<String, Module>();
//		List<IEntity> entities = getRepository().searchName(root, "%" + extension, false);
//		for (IEntity entity : entities) {
//			if (entity.exists()) {
//				String path = entity.getPath();
//				String moduleName = getModuleName(path);
//				Module module = new Module(moduleName, path, getResourceContent(root, path), entity.getInformation());
//				modules.put(moduleName, module);
//			}
//		}
//		return Arrays.asList(modules.values().toArray(new Module[] {}));
//	}

	private String getModuleName(String path) {
//		path = FilenameUtils.separatorsToUnix(path);
//		String workspace = ICommonConstants.WORKSPACE + ICommonConstants.SEPARATOR;
//		String scriptingServices = getModuleType(path) // ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES
//				+ ICommonConstants.SEPARATOR;
//		int indexOfSandbox = path.indexOf(ICommonConstants.SANDBOX);
//		int indexOfRegistry = path.indexOf(ICommonConstants.REGISTRY);
//		String result = null;
//		if ((indexOfSandbox > 0) || (indexOfRegistry > 0)) {
//			int indexOfScriptingServices = path.indexOf(scriptingServices);
//			result = path.substring(indexOfScriptingServices + scriptingServices.length());
//		} else {
//			int indexOfWorkspace = path.indexOf(workspace);
//			result = path.substring(indexOfWorkspace + workspace.length());
//			result = result.replace(scriptingServices, "");
//		}
//		return result;
		return path;
	}

}
