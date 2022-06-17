/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var workspace = require("platform/v4/workspace");
var lifecycle = require("platform/v4/lifecycle");
var bytes = require("io/v4/bytes");
var assertTrue = require('utils/assert').assertTrue;

var user = "dirigible";
var workspaceName = "workspace";
var projectName = "project";

var myWorkspace = workspace.createWorkspace(workspaceName);
var myProject = myWorkspace.createProject("project");
var myFile = myProject.createFile(projectName);
myFile.setContent(bytes.textToByteArray("console.log('Hello World!');"));

var publishResult = lifecycle.publish(user, workspaceName, projectName);
var unpublishResult = lifecycle.unpublish(user, workspaceName, projectName);

assertTrue(publishResult);
assertTrue(unpublishResult);