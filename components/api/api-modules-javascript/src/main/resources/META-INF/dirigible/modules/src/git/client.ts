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

export function initRepository(user: string, email: string, workspaceName: string, projectName: string, repositoryName: string, commitMessage: string): void {
    GitFacade.initRepository(user, email, workspaceName, projectName, repositoryName, commitMessage);
}

export function commit(user: string, userEmail: string, workspaceName: string, repositoryName: string, commitMessage: string, add: boolean): void  {
    GitFacade.commit(user, userEmail, workspaceName, repositoryName, commitMessage, add);
}

export function getGitRepositories(workspaceName: string): any[] /* ProjectDescriptor[] */ {
    return GitFacade.getGitRepositories(workspaceName);
}

export function getHistory(repositoryName: string, workspaceName: string, path: string): any[] /* GitCommitInfo[] */ {
    return GitFacade.getHistory(repositoryName, workspaceName, path);
}

export function deleteRepository(workspaceName: string, repositoryName: string): void {
    GitFacade.deleteRepository(workspaceName, repositoryName);
}

export function cloneRepository(workspaceName: string, repositoryUri: string, username: string, password: string, branch: string): any /* cloneRepository */ {
    return GitFacade.cloneRepository(workspaceName, repositoryUri, username, password, branch);
}

export function pull(workspaceName: string, repositoryName: string, username: string, password: string) {
    GitFacade.pull(workspaceName, repositoryName, username, password);
}

export function push(workspaceName: string, repositoryName: string, username: string, password: string): void {
    return GitFacade.push(workspaceName, repositoryName, username, password);
}

export function checkout(workspaceName: string, repositoryName: string, branchName: string): void {
    GitFacade.checkout(workspaceName, repositoryName, branchName);
}

export function createBranch(workspaceName: string, repositoryName: string, branchName: string, startingPoint: string): void {
    GitFacade.createBranch(workspaceName, repositoryName, branchName, startingPoint);
}

export function deleteBranch(workspaceName: string, repositoryName: string, branchName: string): void {
    GitFacade.createBranch(workspaceName, repositoryName, branchName);
}

export function renameBranch(workspaceName: string, repositoryName: string, oldName: string, newName: string): void {
    GitFacade.createBranch(workspaceName, repositoryName, oldName, newName);
}

export function createRemoteBranch(workspaceName: string, repositoryName: string, branchName: string, startingPoint: string, username: string, password: string): void {
    GitFacade.createRemoteBranch(workspaceName, repositoryName, branchName, startingPoint, username, password);
}

export function deleteRemoteBranch(workspaceName: string, repositoryName: string, branchName: string, username: string, password: string): void {
    GitFacade.createRemoteBranch(workspaceName, repositoryName, branchName, username, password);
}

export function hardReset(workspaceName: string, repositoryName: string): void {
    GitFacade.hardReset(workspaceName, repositoryName);
}

export function rebase(workspaceName: string, repositoryName: string, branchName: string): void {
    GitFacade.rebase(workspaceName, repositoryName, branchName);
}

export function status(workspaceName: string, repositoryName: string): any /* Status */ {
    return GitFacade.status(workspaceName, repositoryName);
}

export function getBranch(workspaceName: string, repositoryName: string): string {
    return GitFacade.getBranch(workspaceName, repositoryName);
}

export function getLocalBranches(workspaceName: string, repositoryName: string): any[] /* GitBranch[] */ {
    return GitFacade.getLocalBranches(workspaceName, repositoryName);
}

export function getRemoteBranches(workspaceName: string, repositoryName: string): any[] /* GitBranch[] */ {
    return GitFacade.getRemoteBranches(workspaceName, repositoryName);
}

export function getUnstagedChanges(workspaceName: string, repositoryName: string): any /* GitChangedFiles */ {
    return GitFacade.getUnstagedChanges(workspaceName, repositoryName);
}

export function getStagedChanges(workspaceName: string, repositoryName: string): any /* GitChangedFiles */ {
    return GitFacade.getStagedChanges(workspaceName, repositoryName);
}

export function getFileContent(workspaceName: string, repositoryName: string, filePath: string, revStr: string): string {
    return GitFacade.getFileContent(workspaceName, repositoryName, filePath, revStr);
}
