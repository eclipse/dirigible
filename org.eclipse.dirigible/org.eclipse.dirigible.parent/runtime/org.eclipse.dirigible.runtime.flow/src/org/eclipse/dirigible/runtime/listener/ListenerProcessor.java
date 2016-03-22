/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.eclipse.dirigible.repository.ext.utils.InstanceUtils;
import org.eclipse.dirigible.repository.ext.utils.JsonUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.listener.log.ListenerLog;
import org.eclipse.dirigible.runtime.listener.log.ListenerLogRecordDAO;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.utils.EngineUtils;

public class ListenerProcessor {

	private static final Logger logger = Logger.getLogger(ListenerProcessor.class);

	public static void executeByEngineType(String module, Map<Object, Object> executionContext, Listener listener) {
		logListener(listener, executionContext, ListenerLog.STATUS_STARTED, "");

		try {
			Set<String> types = EngineUtils.getTypes();
			for (String type : types) {
				if ((type != null) && type.equalsIgnoreCase(listener.getType())) {
					IScriptExecutor scriptExecutor = EngineUtils.createExecutor(type, null);
					scriptExecutor.executeServiceModule(null, null, module, executionContext);
					break;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logListener(listener, executionContext, ListenerLog.STATUS_FAILED, e.getMessage());
		}

		logListener(listener, executionContext, ListenerLog.STATUS_COMPLETED, "");
	}

	private static void logListener(Listener listener, Map<Object, Object> executionContext, int status, String message) {
		ListenerLog listenerLog = new ListenerLog();
		listenerLog.setInstance(InstanceUtils.getInstanceName());
		listenerLog.setListenerName(listener.getName());
		listenerLog.setListenerUUID(listener.getListenerUUID());
		listenerLog.setStatus(status);
		listenerLog.setMessage(message);
		listenerLog.setContext(JsonUtils.mapToJson(executionContext));

		try {
			ListenerLogRecordDAO.insert(listenerLog);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}
}
