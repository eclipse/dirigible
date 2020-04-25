/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var manageTemplate = require('template-application-openui5/template/ui/manage');
var perspective = require('template-application-openui5/template/ui/perspective');
var launchpad = require('template-application-openui5/template/ui/launchpad');
var tiles = require('template-application-openui5/template/ui/tiles');

exports.getSources = function(parameters) {
    var sources = [];
    sources = sources.concat(manageTemplate.getSources(parameters));
    sources = sources.concat(perspective.getSources(parameters));
    sources = sources.concat(launchpad.getSources(parameters));
    sources = sources.concat(tiles.getSources(parameters));
    return sources;
};
