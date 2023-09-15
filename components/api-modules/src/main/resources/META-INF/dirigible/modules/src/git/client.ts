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

const GitFacade = Java.type("org.eclipse.dirigible.components.api.git.GitFacade");

export function initRepository(user, email, workspaceName, projectName, repositoryName, commitMessage) {
    return GitFacade.initRepository(user, email, workspaceName, projectName, repositoryName, commitMessage);
}

export function commit(user, userEmail, workspaceName, repositoryName, commitMessage, add)  {
    return GitFacade.commit(user, userEmail, workspaceName, repositoryName, commitMessage, add);
}

export function getGitRepositories(workspaceName) {
    return GitFacade.getGitRepositories(workspaceName);
}

export function getHistory(repositoryName, workspaceName, path) {
    return GitFacade.getHistory(repositoryName, workspaceName, path);
}

export function deleteRepository(workspaceName, repositoryName) {
    return GitFacade.deleteRepository(workspaceName, repositoryName);
}

export function cloneRepository(workspaceName, repositoryUri, username, password, branch) {
    return GitFacade.cloneRepository(workspaceName, repositoryUri, username, password, branch);
}

export function pull(workspaceName, repositoryName, username, password) {
    return GitFacade.pull(workspaceName, repositoryName, username, password);
}

export function push(workspaceName, repositoryName, username, password) {
    return GitFacade.push(workspaceName, repositoryName, username, password);
}

export function checkout(workspaceName, repositoryName, branchName) {
    return GitFacade.checkout(workspaceName, repositoryName, branchName);
}

export function createBranch(workspaceName, repositoryName, branchName, startingPoint) {
    return GitFacade.createBranch(workspaceName, repositoryName, branchName, startingPoint);
}

export function deleteBranch(workspaceName, repositoryName, branchName) {
    return GitFacade.createBranch(workspaceName, repositoryName, branchName);
}

export function renameBranch(workspaceName, repositoryName, oldName, newName) {
    return GitFacade.createBranch(workspaceName, repositoryName, oldName, newName);
}

export function createRemoteBranch(workspaceName, repositoryName, branchName, startingPoint, username, password) {
    return GitFacade.createRemoteBranch(workspaceName, repositoryName, branchName, startingPoint, username, password);
}

export function deleteRemoteBranch(workspaceName, repositoryName, branchName, username, password) {
    return GitFacade.createRemoteBranch(workspaceName, repositoryName, branchName, username, password);
}

export function hardReset(workspaceName, repositoryName) {
    return GitFacade.hardReset(workspaceName, repositoryName);
}

export function rebase(workspaceName, repositoryName, branchName) {
    return GitFacade.rebase(workspaceName, repositoryName, branchName);
}

export function status(workspaceName, repositoryName) {
    return GitFacade.status(workspaceName, repositoryName);
}

export function getBranch(workspaceName, repositoryName) {
    return GitFacade.getBranch(workspaceName, repositoryName);
}

export function getLocalBranches(workspaceName, repositoryName) {
    return GitFacade.getLocalBranches(workspaceName, repositoryName);
}

export function getRemoteBranches(workspaceName, repositoryName) {
    return GitFacade.getRemoteBranches(workspaceName, repositoryName);
}

export function getUnstagedChanges(workspaceName, repositoryName) {
    return GitFacade.getUnstagedChanges(workspaceName, repositoryName);
}

export function getStagedChanges(workspaceName, repositoryName) {
    return GitFacade.getStagedChanges(workspaceName, repositoryName);
}

export function getFileContent(workspaceName, repositoryName, filePath, revStr) {
    return GitFacade.getFileContent(workspaceName, repositoryName, filePath, revStr);
}
