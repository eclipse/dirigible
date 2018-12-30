/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
exports.getTemplate = function() {
	return {
		"name": "Scheduled Job",
		"description": "Scheduled Job definition with a simple Javascript handler",
		"sources": [
		{
			"location": "/template-job/job.template", 
			"action": "generate",
			"rename": "{{fileName}}.job"
		},
		{
			"location": "/template-job/handler.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-handler.js"
		}],
		"parameters": []
	};
};
