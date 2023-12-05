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
const git = require('git/client');
const assertTrue = require('test/assert').assertTrue;
const workspaceManager = require("platform/workspace");

const user = 'dirigible';
const email = 'dirigible@eclipse.com';
const workspaceName = 'test-workspace';
const projectName = 'test-project';
const repositoryName = projectName;

const workspace = workspaceManager.createWorkspace(workspaceName);
const project = workspace.createProject(projectName);
let firstFile = project.createFile("firstFile.js");
firstFile.setText('first file content');

git.initRepository(user, email, workspaceName, projectName, repositoryName, 'initial commit');

let repos = git.getGitRepositories(workspaceName);
console.log("Repository name " + repos[0].getName())
assertTrue(repos[0].getName() === repositoryName);

let secondFile = project.createFile("secondFile.js");
secondFile.setText('second file content');
console.log("Getting staged changes...");
let staged = git.getStagedChanges(workspaceName, repositoryName);
console.log("Staged changes count: " + staged.size())
assertTrue(staged.size() === 0);

console.log("Getting unstaged changes...");
let unstaged = git.getUnstagedChanges(workspaceName, repositoryName);
console.log("Unstaged changes count: " + unstaged.size())
assertTrue(unstaged.size() === 1);

git.commit(user, email, workspaceName, repositoryName, "second file added", true);
const his = git.getHistory(repositoryName, workspaceName, projectName);

console.log("History size: " + his.size())
assertTrue(his.size() === 2);

let branches = git.getLocalBranches(workspaceName, repositoryName);
console.log("Local branches size: " + branches.size());
assertTrue(branches.size() === 1);

git.createBranch(workspaceName, repositoryName, 'new-branch', 'master');
branches = git.getLocalBranches(workspaceName, repositoryName);
console.log("New local branches size: " + branches.size());
assertTrue(branches.size() === 2);

const status = git.status(workspaceName, repositoryName);
console.log("Status is clean: " + status.isClean());
assertTrue(status.isClean() === true);

console.log("Deleting test repo...");
git.deleteRepository(workspaceName, repositoryName);
console.log("Repositories size at the end: " + git.getGitRepositories(workspaceName).size());
assertTrue(git.getGitRepositories(workspaceName).size() === 0);