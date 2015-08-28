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

import org.eclipse.dirigible.ide.common.CommonParameters;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.logging.Logger;

public class DebugModelFacade {
	
	private static final Logger logger = Logger.getLogger(DebugModelFacade.class); 
	
	private static DebugModelFacade debugModelFacade;
	
//	private static Map<String, DebugSessionModel> debugModels = Collections.synchronizedMap(new HashMap<String, DebugSessionModel>());
	
	public static DebugModelFacade getInstance() {
		if (debugModelFacade == null) {
				debugModelFacade = new DebugModelFacade();
		}
		return debugModelFacade;
	}
	
//	public DebugSessionModel createDebugModel(String sessionId, String executionId, String userId, IDebugController debugController) {
//		logger.debug("entering DebugModelFacade.createDebugModel() with sessionId:" + sessionId + ", executionId:" + executionId + ", userId:" + userId); 
//		DebugSessionModel debugModel = debugModels.get(executionId);
//		if (debugModel == null) {
//			debugModel = new DebugSessionModel(debugController);
//			debugModel.setSessionId(sessionId);
//			debugModel.setExecutionId(executionId);
//			debugModel.setUserId(userId);
//			debugModels.put(executionId, debugModel);
//			logger.debug("DebugModel created with sessionId: " + sessionId + ", executionId: " + executionId + ", userId: " + userId);
//		}
//		
//		logger.debug("exiting DebugModelFacade.createDebugModel()");
//		return debugModel;
//	}
	
	public DebugSessionModel getDebugSessionModel(String executionId) {
		DebugSessionModel debugModel = getDebugModel().getSessionByExecutionId(executionId);
		if (debugModel == null) {
			logger.warn("Getting debug session with executionId: " + executionId + " failed - no such session exists");
		}
		return debugModel;
	}
	
	public void removeSession(String executionId) {
		DebugSessionModel session = getDebugModel().getSessionByExecutionId(executionId);
		getDebugModel().getSessions().remove(session);
		logger.debug("Debug session with executionId: " + executionId + " removed");
	}

//	public static Map<String, DebugSessionModel> getDebugModels() {
//		return debugModels;
//	}
	
	public static DebugModel getDebugModel() {
		DebugModel debugModel = (DebugModel) CommonParameters.getObject(DebugModel.DEBUG_MODEL);
		if (debugModel == null) {
			logger.debug("Debug model not created!");
		}
		return debugModel;
	}

	public static void createDebugModel(IDebugIDEController debugController) {
		DebugModel debugModel = new DebugModel(debugController);
		CommonParameters.setObject(DebugModel.DEBUG_MODEL, debugModel);
	}
	
}
