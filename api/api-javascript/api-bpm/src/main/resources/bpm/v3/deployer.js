/*
 * Copyright (c) 2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var java = require('core/v3/java');

exports.deployProcess = function(location) {
	var processId = java.call('org.eclipse.dirigible.api.v3.bpm.BpmFacade', 'deployProcess', [location]);
	return processId;
};

exports.undeployProcess = function(processId) {
	java.call('org.eclipse.dirigible.api.v3.bpm.BpmFacade', 'undeployProcess', [processId]);
};

