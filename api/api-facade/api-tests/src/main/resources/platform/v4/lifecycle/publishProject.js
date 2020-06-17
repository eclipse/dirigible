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
var workspace = require("workspace/v4/manager");
var lifecycle = require("platform/v4/lifecycle");
var bytes = require("io/v4/bytes");

var user = "dirigible";
var workspaceName = "workspace";
var projectName = "project";

var myWorkspace = workspace.createWorkspace(workspaceName);
var myProject = myWorkspace.createProject("project");
var myFile = myProject.createFile(projectName);
myFile.setContent(bytes.textToByteArray("console.log('Hello World!');"));

var publishResult = lifecycle.publish(user, workspaceName, projectName);
var unpublishResult = lifecycle.unpublish(user, workspaceName, projectName);

publishResult === true && unpublishResult === true;