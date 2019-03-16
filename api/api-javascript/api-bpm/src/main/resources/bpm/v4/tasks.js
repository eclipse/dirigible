/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * API v4 Tasks
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.list = function() {
	var tasks = org.eclipse.dirigible.api.v3.bpm.BpmFacade.getTasks();
	return JSON.parse(processId);
};

exports.getTaskVariables = function(taskId) {
	var variables = org.eclipse.dirigible.api.v3.bpm.BpmFacade.getTaskVariables(taskId);
	return JSON.parse(variables);
};

exports.setTaskVariables = function(taskId, variables) {
	org.eclipse.dirigible.api.v3.bpm.BpmFacade.setTaskVariables(taskId, JSON.stringify(variables));
};

exports.completeTask = function(taskId, variables) {
	org.eclipse.dirigible.api.v3.bpm.BpmFacade.completeTask(taskId, JSON.stringify(variables));
};
