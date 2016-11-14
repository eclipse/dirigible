/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

exports.activateProject = function(projectName) {
	$.getLifecycleService().activateProject(projectName, $.getRequest());
};

exports.publishProject = function(projectName) {
	$.getLifecycleService().publishProject(projectName, $.getRequest());
};

exports.publishTemplate = function(projectName) {
	$.getLifecycleService().publishTemplate(projectName, $.getRequest());
};

exports.activateAll = function() {
	$.getLifecycleService().activateAll($.getRequest());
};

exports.publishAll = function() {
	$.getLifecycleService().publishAll($.getRequest());
};