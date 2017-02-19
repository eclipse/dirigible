/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutorProvider;

public class EngineUtilsFactoryNonOSGi {

	private static final Logger logger = Logger.getLogger(EngineUtilsFactoryNonOSGi.class);

	private static Map<String, IScriptExecutorProvider> scriptExecutorProviders = Collections
			.synchronizedMap(new HashMap<String, IScriptExecutorProvider>());

	private static Map<String, IScriptExecutorProvider> scriptExecutorProvidersByAlias = Collections
			.synchronizedMap(new HashMap<String, IScriptExecutorProvider>());

	static String commandScriptExecutorProvider = "org.eclipse.dirigible.runtime.command.CommandScriptExecutorProvider";
	static String flowScriptExecutorProvider = "org.eclipse.dirigible.runtime.flow.FlowScriptExecutorProvider";
	static String jobScriptExecutorProvider = "org.eclipse.dirigible.runtime.job.JobSyncScriptExecutorProvider";
	static String jsScriptExecutorProvider = "org.eclipse.dirigible.runtime.js.JavaScriptScriptExecutorProvider";
	static String mobileScriptExecutorProvider = "org.eclipse.dirigible.runtime.mobile.MobileScriptExecutorProvider";
	static String sqlScriptExecutorProvider = "org.eclipse.dirigible.runtime.sql.SQLScriptExecutorProvider";
	static String webScriptExecutorProvider = "org.eclipse.dirigible.runtime.web.WebScriptExecutorProvider";
	static String wikiScriptExecutorProvider = "org.eclipse.dirigible.runtime.wiki.WikiScriptExecutorProvider";

	static {
		IScriptExecutorProvider scriptExecutorProvider = createScriptExecutorProvider(commandScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(flowScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(jobScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(jsScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(mobileScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(sqlScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(webScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);

		scriptExecutorProvider = createScriptExecutorProvider(wikiScriptExecutorProvider);
		scriptExecutorProviders.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
		scriptExecutorProvidersByAlias.put(scriptExecutorProvider.getType(), scriptExecutorProvider);
	}

	public static Set<String> getTypes() {
		return scriptExecutorProviders.keySet();
	}

	public static Set<String> getAliases() {
		return scriptExecutorProvidersByAlias.keySet();
	}

	public static IScriptExecutor createExecutor(String type, HttpServletRequest request) throws IOException {
		IScriptExecutorProvider scriptExecutorProvider = scriptExecutorProviders.get(type);
		IScriptExecutor scriptExecutor = scriptExecutorProvider.createExecutor(request);
		return scriptExecutor;
	}

	public static IScriptExecutor createExecutorByAlias(String alias, HttpServletRequest request) throws IOException {
		IScriptExecutorProvider scriptExecutorProvider = scriptExecutorProvidersByAlias.get(alias);
		IScriptExecutor scriptExecutor = scriptExecutorProvider.createExecutor(request);
		return scriptExecutor;
	}

	private static IScriptExecutorProvider createScriptExecutorProvider(String clazz) {
		try {
			return (IScriptExecutorProvider) Class.forName(clazz).newInstance();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
