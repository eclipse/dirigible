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

package org.eclipse.dirigible.ide.workspace.dual;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

public class DirectRenderer {
	
	public static final String SANDBOX_CONTEXT = "sandbox"; //$NON-NLS-1$
	public static final String DEBUG_CONTEXT = "debug"; //$NON-NLS-1$
	
	public static String renderContent(String location) {
		String message = null;
		try {
			String url = "http://local:0" + location;
			String module = location.substring(1);
			int moduleStart = module.indexOf('/');
			module = module.substring(moduleStart);
			String alias = location.substring(1, moduleStart + 1);
			int dash = alias.indexOf('-');
			if (dash > 0) {
				alias = alias.substring(0, dash);
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			LocalHttpServletRequest request = new LocalHttpServletRequest(new URL(url)); 
			LocalHttpServletResponse response = new LocalHttpServletResponse(baos);
			
			if (location.contains(SANDBOX_CONTEXT)) {
				request.setAttribute(SANDBOX_CONTEXT, true);
			}
			if (location.contains(DEBUG_CONTEXT)) {
				request.setAttribute(DEBUG_CONTEXT, true);
			}
			
			Map<Object, Object> executionContext = new HashMap<Object, Object>();
			// type
			IScriptExecutor scriptExecutor = EngineUtils.createExecutorByAlias(alias, request);
			// location/module
			scriptExecutor.executeServiceModule(request, response, module, executionContext);
			response.getWriter().flush();
			response.getOutputStream().flush();
			return new String(baos.toByteArray(), "UTF-8");
		} catch (MalformedURLException e) {
			message = e.getMessage();
		} catch (UnsupportedEncodingException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		}
		return message;
	}

}
