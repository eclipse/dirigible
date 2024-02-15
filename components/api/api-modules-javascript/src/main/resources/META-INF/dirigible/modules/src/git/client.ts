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

export class Client {
    public static initRepository(user: string, email: string, workspaceName: string, projectName: string, repositoryName: string, commitMessage: string): void {
        GitFacade.initRepository(user, email, workspaceName, projectName, repositoryName, commitMessage);
    }

    public static commit(user: string, userEmail: string, workspaceName: string, repositoryName: string, commitMessage: string, add: boolean): void  {
        GitFacade.commit(user, userEmail, workspaceName, repositoryName, commitMessage, add);
    }

    public static getGitRepositories(workspaceName: string): any[] /* ProjectDescriptor[] */ {
        return GitFacade.getGitRepositories(workspaceName);
    }

    public static getHistory(repositoryName: string, workspaceName: string, path: string): any[] /* GitCommitInfo[] */ {
        return GitFacade.getHistory(repositoryName, workspaceName, path);
    }

    public static deleteRepository(workspaceName: string, repositoryName: string): void {
        GitFacade.deleteRepository(workspaceName, repositoryName);
    }

    public static cloneRepository(workspaceName: string, repositoryUri: string, username: string, password: string, branch: string): any /* cloneRepository */ {
        return GitFacade.cloneRepository(workspaceName, repositoryUri, username, password, branch);
    }

    public static pull(workspaceName: string, repositoryName: string, username: string, password: string) {
        GitFacade.pull(workspaceName, repositoryName, username, password);
    }

    public static push(workspaceName: string, repositoryName: string, username: string, password: string): void {
        return GitFacade.push(workspaceName, repositoryName, username, password);
    }

    public static checkout(workspaceName: string, repositoryName: string, branchName: string): void {
        GitFacade.checkout(workspaceName, repositoryName, branchName);
    }

    public static createBranch(workspaceName: string, repositoryName: string, branchName: string, startingPoint: string): void {
        GitFacade.createBranch(workspaceName, repositoryName, branchName, startingPoint);
    }

    public static deleteBranch(workspaceName: string, repositoryName: string, branchName: string): void {
        GitFacade.createBranch(workspaceName, repositoryName, branchName);
    }

    public static renameBranch(workspaceName: string, repositoryName: string, oldName: string, newName: string): void {
        GitFacade.createBranch(workspaceName, repositoryName, oldName, newName);
    }

    public static createRemoteBranch(workspaceName: string, repositoryName: string, branchName: string, startingPoint: string, username: string, password: string): void {
        GitFacade.createRemoteBranch(workspaceName, repositoryName, branchName, startingPoint, username, password);
    }

    public static deleteRemoteBranch(workspaceName: string, repositoryName: string, branchName: string, username: string, password: string): void {
        GitFacade.createRemoteBranch(workspaceName, repositoryName, branchName, username, password);
    }

    public static hardReset(workspaceName: string, repositoryName: string): void {
        GitFacade.hardReset(workspaceName, repositoryName);
    }

    public static rebase(workspaceName: string, repositoryName: string, branchName: string): void {
        GitFacade.rebase(workspaceName, repositoryName, branchName);
    }

    public static status(workspaceName: string, repositoryName: string): any /* Status */ {
        return GitFacade.status(workspaceName, repositoryName);
    }

    public static getBranch(workspaceName: string, repositoryName: string): string {
        return GitFacade.getBranch(workspaceName, repositoryName);
    }

    public static getLocalBranches(workspaceName: string, repositoryName: string): any[] /* GitBranch[] */ {
        return GitFacade.getLocalBranches(workspaceName, repositoryName);
    }

    public static getRemoteBranches(workspaceName: string, repositoryName: string): any[] /* GitBranch[] */ {
        return GitFacade.getRemoteBranches(workspaceName, repositoryName);
    }

    public static getUnstagedChanges(workspaceName: string, repositoryName: string): any /* GitChangedFiles */ {
        return GitFacade.getUnstagedChanges(workspaceName, repositoryName);
    }

    public static getStagedChanges(workspaceName: string, repositoryName: string): any /* GitChangedFiles */ {
        return GitFacade.getStagedChanges(workspaceName, repositoryName);
    }

    public static getFileContent(workspaceName: string, repositoryName: string, filePath: string, revStr: string): string {
        return GitFacade.getFileContent(workspaceName, repositoryName, filePath, revStr);
    }
}