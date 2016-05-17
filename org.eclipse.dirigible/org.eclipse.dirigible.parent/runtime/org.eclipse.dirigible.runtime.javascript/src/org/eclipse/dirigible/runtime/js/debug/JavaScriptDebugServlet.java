/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.debug;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.ide.bridge.DirigibleBridge;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.debug.DebugManager;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.IDebugExecutor.DebugCommand;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.js.JavaScriptServlet;

/**
 * Servlet for JavaScript scripts execution
 */
public class JavaScriptDebugServlet extends JavaScriptServlet {

	private static final String HTTPS = "https://";

	private static final String HTTP = "http://";

	private static final long serialVersionUID = -9115022531455267478L;

	private static final Logger logger = Logger.getLogger(JavaScriptDebugServlet.class);

	private static final String DEBUG_ENDPOINT = "debug";

	private static WebSocketDebugBridgeServletInternal webSocketDebugBridgeServletInternal;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			setupDebugChannel();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected void setupDebugChannel() throws IOException {

		logger.debug("Setting debug channel internal ...");

		webSocketDebugBridgeServletInternal = new WebSocketDebugBridgeServletInternal();
		WebSocketDebugSessionServletInternal webScoketDebugSessionsServletInternal = new WebSocketDebugSessionServletInternal();
		DirigibleBridge.BRIDGES.put("websocket_debug_channel_internal", webSocketDebugBridgeServletInternal);
		DirigibleBridge.BRIDGES.put("websocket_debug_sessions_internal", webScoketDebugSessionsServletInternal);

		logger.debug("Debug channel internal has been set.");

	}

	@Override
	public JavaScriptDebuggingExecutor createExecutor(HttpServletRequest request) throws IOException {

		logger.debug("entering JavaScriptDebugServlet.createExecutor()");
		IRepository repository = getRepository(request);

		// setDebugConfiguration(request, repository);
		// String baseUrl = getBaseUrl(request);
		// String withoutServices = baseUrl.substring(0, baseUrl.indexOf("services"));
		// int indexOfHttps = withoutServices.indexOf(HTTPS);
		// int indexOfHttp = withoutServices.indexOf(HTTP);
		// if (indexOfHttps >= 0) {
		// withoutServices = withoutServices.substring(indexOfHttps + HTTPS.length(), withoutServices.length());
		// } else if (indexOfHttp >= 0) {
		// withoutServices = withoutServices.substring(indexOfHttp + HTTP.length(), withoutServices.length());
		// }
		// String websocketUrl = String.format("%s%s", withoutServices, DEBUG_ENDPOINT);
		// String devToolsLocation = String.format("%s/ui/devtools/front_end/inspector.html?ws=%s", baseUrl,
		// websocketUrl);
		// logger.info("Devtools destination is: " + devToolsLocation);
		DebugModel debugModel = DebugManager.getDebugModel(RequestUtils.getUser(request));
		if (debugModel == null) {
			String error = "Debug model is not present in the session";
			logger.error(error);
			throw new IOException(error);
		}

		String rootPath = getScriptingRegistryPath(request);
		logger.debug("rootPath=" + rootPath);
		JavaScriptDebuggingExecutor executor = new JavaScriptDebuggingExecutor(getRepository(request), rootPath, REGISTRY_SCRIPTING_DEPLOY_PATH,
				debugModel);

		logger.debug("exiting JavaScriptDebugServlet.createExecutor()");

		return executor;
	}

	// private void setDebugConfiguration(HttpServletRequest request, IRepository repository) {
	// setBaseRepositoryUrl(request);
	// setDebugResources(request, repository);
	// }

	// private void setBaseRepositoryUrl(HttpServletRequest request) {
	// String baseUrl = getBaseUrl(request);
	// String baseRepositoryURL = baseUrl + "/js-src";
	// DebugConfiguration.setBaseSourceUrl(baseRepositoryURL);
	// }

	// private String getBaseUrl(HttpServletRequest request) {
	// String scheme = request.getScheme();
	// String serverName = request.getServerName();
	// int serverPort = request.getServerPort();
	// String contextPath = request.getContextPath();
	//
	// StringBuilder baseUrlBuilder = new StringBuilder();
	// baseUrlBuilder.append(scheme).append("://").append(serverName).append(":").append(String.valueOf(serverPort));
	// if (isContextPathNotServices(contextPath)) {
	// baseUrlBuilder.append(contextPath);
	// }
	// return baseUrlBuilder.toString();
	// }

	// private boolean isContextPathNotServices(String contextPath) {
	// String contextPathServicesPattern = "(\\/services\\/).*$";
	// return (contextPath != null) && (!contextPath.matches(contextPathServicesPattern));
	// }

	// private void setDebugResources(HttpServletRequest request, IRepository repository) {
	// Map<String, List<IResource>> projectResourcesMap = new HashMap<String, List<IResource>>();
	// ICollection scriptingServices = repository.getCollection(REGISTRY_SCRIPTING_DEPLOY_PATH);
	// try {
	// List<ICollection> projects = scriptingServices.getCollections();
	// for (ICollection nextProject : projects) {
	// if (nextProject.exists()) {
	// String projectName = nextProject.getName();
	// ICollection collection = repository.getCollection(REGISTRY_SCRIPTING_DEPLOY_PATH + "/" + projectName);
	// List<IResource> resources = projectResourcesMap.get(projectName);
	// if (resources == null) {
	// resources = new ArrayList<IResource>();
	// }
	// resources.addAll(resourcesForCollection(collection));
	// projectResourcesMap.put(projectName, resources);
	// }
	// }
	//
	// List<IResource> systemResources = scriptingServices.getResources();
	// projectResourcesMap.put("ScriptingServices", systemResources);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// DebugConfiguration.setResources(projectResourcesMap);
	// }
	//
	// private List<IResource> resourcesForCollection(ICollection collection) throws IOException {
	// List<IResource> resources = new ArrayList<IResource>();
	// if (collection == null) {
	// return resources;
	// }
	// for (IEntity e : collection.getChildren()) {
	// if (e instanceof IResource) {
	// IResource res = (IResource) e;
	// if (shouldAddResource(res)) {
	// resources.add(res);
	// }
	// } else if (e instanceof ICollection) {
	// resources.addAll(resourcesForCollection((ICollection) e));
	// }
	// }
	// return resources;
	// }
	//
	// private boolean shouldAddResource(IResource res) throws IOException {
	// return res.getContentType().contains("javascript");
	// }

	@Override
	protected void postExecution(HttpServletRequest request) {
		endDebugSession(request);
	}

	private void endDebugSession(HttpServletRequest request) {
		// we've reached the end of the script, the debug session is over and should be removed from the list
		String userId = RequestUtils.getUser(request);
		WebSocketDebugSessionServletInternal.clearCurrentSession(userId);
	}
}
