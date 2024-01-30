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

const BpmFacade = Java.type("org.eclipse.dirigible.components.api.bpm.BpmFacade");

export function list() {
	var tasks = BpmFacade.getTasks();
	return JSON.parse(tasks);
};

export function getTaskVariables(taskId) {
	var variables = BpmFacade.getTaskVariables(taskId);
	return JSON.parse(variables);
};

export function setTaskVariables(taskId, variables) {
	BpmFacade.setTaskVariables(taskId, JSON.stringify(variables));
};

export function completeTask(taskId, variables) {
	BpmFacade.completeTask(taskId, JSON.stringify(variables));
};
