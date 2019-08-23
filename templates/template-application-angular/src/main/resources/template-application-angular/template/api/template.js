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

exports.getSources = function(parameters) {
	var sources = [];
	sources = sources.concat(getApi(parameters));
	sources = sources.concat(getLaunchpadApi(parameters));
	return sources;
};

function getApi(parameters) {
	return [{
		location: "/template-application-angular/api/http.js.template", 
		action: "copy",
		rename: "api/http.js",
	}, {
		location: "/template-application-angular/api/entity.js.template", 
		action: "generate",
		rename: "api/{{perspectiveName}}/{{fileName}}.js",
		engine: "velocity",
		collection: "models"
	}];
}

function getLaunchpadApi(parameters) {
	var sources = [];
	if (parameters && parameters.includeLaunchpad) {
		sources = [{
			location: "/template-application-angular/api/launchpad/menu.js.template", 
			action: "generate",
			rename: "api/launchpad/menu.js"
		}, {
			location: "/template-application-angular/api/launchpad/perspectives.js.template", 
			action: "generate",
			rename: "api/launchpad/perspectives.js"
		}, {
			location: "/template-application-angular/api/launchpad/tiles.js.template", 
			action: "generate",
			rename: "api/launchpad/tiles.js"
		}, {
			location: "/template-application-angular/api/launchpad/views.js.template", 
			action: "generate",
			rename: "api/launchpad/views.js"
		}];
	}
	return sources;
}
