/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Tasks
 */

exports.list = function() {
	var tasks = org.eclipse.dirigible.components.api.bpm.BpmFacade.getTasks();
	return JSON.parse(tasks);
};

exports.getTaskVariables = function(taskId) {
	var variables = org.eclipse.dirigible.components.api.bpm.BpmFacade.getTaskVariables(taskId);
	return JSON.parse(variables);
};

exports.setTaskVariables = function(taskId, variables) {
	org.eclipse.dirigible.components.api.bpm.BpmFacade.setTaskVariables(taskId, JSON.stringify(variables));
};

exports.completeTask = function(taskId, variables) {
	org.eclipse.dirigible.components.api.bpm.BpmFacade.completeTask(taskId, JSON.stringify(variables));
};
