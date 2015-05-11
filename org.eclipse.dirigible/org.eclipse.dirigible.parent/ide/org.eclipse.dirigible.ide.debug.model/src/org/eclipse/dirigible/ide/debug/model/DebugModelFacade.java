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

package org.eclipse.dirigible.ide.debug.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.logging.Logger;

public class DebugModelFacade {
	
	private static final Logger logger = Logger.getLogger(DebugModelFacade.class); 
	
	private static final String ACTIVE_DEBUG_SESSION = "debug.session.active";
	
	private static DebugModelFacade debugModelFacade;
	
	private static Map<String, DebugModel> debugModels = Collections.synchronizedMap(new HashMap<String, DebugModel>());
	
	public static DebugModelFacade getInstance() {
		if (debugModelFacade == null) {
				debugModelFacade = new DebugModelFacade();
		}
		return debugModelFacade;
	}
	
	public DebugModel createDebugModel(String sessionId, String executionId, String userId, IDebugController debugController) {
		logger.debug("entering DebugModelFacade.createDebugModel() with sessionId:" + sessionId + ", executionId:" + executionId + ", userId:" + userId); 
		DebugModel debugModel = debugModels.get(executionId);
		if (debugModel == null) {
			debugModel = new DebugModel(debugController);
			debugModel.setSessionId(sessionId);
			debugModel.setExecutionId(executionId);
			debugModel.setUserId(userId);
			debugModels.put(executionId, debugModel);
			logger.debug("DebugModel created with sessionId: " + sessionId + ", executionId: " + executionId + ", userId: " + userId);
		}
		
		logger.debug("exiting DebugModelFacade.createDebugModel()");
		return debugModel;
	}
	
	public DebugModel getDebugModel(String executionId) {
		DebugModel debugModel = debugModels.get(executionId);
		if (debugModel == null) {
			logger.warn("Getting DebugModel with executionId: " + executionId + " failed - no such model exists");
		}
		return debugModel;
	}
	
	public void removeDebugModel(String executionId) {
		debugModels.remove(executionId);
		logger.debug("DebugModel with executionId: " + executionId + " removed");
	}

	public static Map<String, DebugModel> getDebugModels() {
		return debugModels;
	}
	
	public static DebugModel getActiveDebugModel() {
		DebugModel debugModel = (DebugModel) CommonParameters.getObject(ACTIVE_DEBUG_SESSION);
		if (debugModel == null) {
			logger.debug("Getting active DebugModel from session failed");
		}
		return debugModel;
	}
	
	public static void setActiveDebugModel(DebugModel debugModel) {
		CommonParameters.setObject(ACTIVE_DEBUG_SESSION, debugModel);
		logger.debug("Setting DebugModel in Session with sessionId: " + debugModel.getSessionId() + ", executionId: " + debugModel.getExecutionId() + ", userId: " + debugModel.getUserId());
	}
	
	public void clearDebugModels() {
		debugModels.clear();
		logger.debug("DebugModels cleared");
	}

}
