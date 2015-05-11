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

package org.eclipse.dirigible.runtime.js.debug;

import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.eclipse.dirigible.repository.ext.debug.IDebugProtocol;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.RuntimeActivator;

public class DebugProtocolUtils {

	private static final Logger logger = Logger.getLogger(DebugProtocolUtils.class);

	/**
	 * Retrieve the DebugProtocol(PropertyChangeSupport) from the target server
	 * environment
	 * 
	 * @return
	 * @throws NamingException
	 */
	public static IDebugProtocol lookupDebugProtocol() {
		logger.debug("entering JavaScriptDebugServlet.lookupDebugProtocol()");
		BundleContext context = RuntimeActivator.getContext();
		ServiceReference<IDebugProtocol> sr = context.getServiceReference(IDebugProtocol.class);
		IDebugProtocol debugProtocol = context.getService(sr);
		if (debugProtocol == null) {
			logger.error("DebugProtocol not present");
		}
		
		logger.debug("exiting JavaScriptDebugServlet.lookupDebuggerBridge()");
		return debugProtocol;
	}

	public static void send(IDebugProtocol debugProtocol, String commandId,
			String clientId, String commandBody) {
		logger.debug("DebugBridgUtils send() commandId: " + commandId + ", clientId: " + clientId
				+ ", body: " + commandBody);
		debugProtocol.firePropertyChange(commandId, clientId, commandBody);
	}

}
