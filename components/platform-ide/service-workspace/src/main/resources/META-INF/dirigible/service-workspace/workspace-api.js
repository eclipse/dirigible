/*
 * Copyright (c) 2010-2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
class WorkspaceApi extends MessageHubApi {
    /**
     * Sends a message containing information on which workspace has been changed.
     * @param {string} workspace - Name of the changed workspace.
     * @param {Object.<any, any>} params - Any extra parameters.
     */ // @ts-ignore
    announceWorkspaceChanged({ workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.workspace.chnaged',
            data: {
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when a workspace has been changed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onWorkspaceChanged(handler) {
        return this.addMessageListener({ topic: 'platform.workspace.chnaged', handler: handler });
    }

    /**
     * Sends a message containing information on which file has to be opened.
     * @param {string} path - Full file path, including file name.
     * @param {string} contentType - The file content type.
     * @param {string} [editorId] - The ID of the preffered editor.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {Object.<any, any>} [params] - Extra parameters that will be passed to the view parameters of the editor.
     */ // @ts-ignore
    openFile({ path, contentType, editorId, workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.files.open',
            data: {
                name: path.substring(path.lastIndexOf('/') + 1, path.length),
                path: path,
                contentType: contentType,
                editorId: editorId,
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when a file has to be opened.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onOpenFile(handler) {
        return this.addMessageListener({ topic: 'platform.files.open', handler: handler });
    }

    /**
     * Sends a message containing information on which file has to be closed.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {Object.<any, any>} [params] - Extra parameters.
     */ // @ts-ignore
    closeFile({ path, workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.files.close',
            data: {
                path: path,
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when a file has to be closed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onCloseFile(handler) {
        return this.addMessageListener({ topic: 'platform.files.close', handler: handler });
    }

    /**
     * Sends a message when all files should be closed.
     * @param {Object.<any, any>} [params] - Extra parameters.
     */ // @ts-ignore
    closeAllFiles({ params } = {}) {
        this.postMessage({
            topic: 'platform.files.close.all',
            data: {
                params: params,
            }
        });
    }

    /**
     * Triggered when files have to be closed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onCloseAllFiles(handler) {
        return this.addMessageListener({ topic: 'platform.files.close.all', handler: handler });
    }

    /**
     * Sends a message containing information on which file has to be set to dirty.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {boolean} dirty - File dirty state.
     * @param {Object.<any, any>} [params] - Extra parameters.
     */ // @ts-ignore
    setFileDirty({ path, workspace, dirty, params } = {}) {
        this.postMessage({
            topic: 'platform.files.set-dirty',
            data: {
                path: path,
                workspace: workspace,
                dirty: dirty,
                params: params,
            }
        });
    }

    /**
     * Triggered when a file has to be set as dirty.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onSetFileDirty(handler) {
        return this.addMessageListener({ topic: 'platform.files.set-dirty', handler: handler });
    }

    /**
     * Gets the dirty state of a file.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {Object.<any, any>} [params] - Extra parameters.
     */ // @ts-ignore
    isFileDirty({ path, workspace, params } = {}) {
        const callbackTopic = `platform.files.is-dirty.${new Date().valueOf()}`;
        this.postMessage({
            topic: 'platform.files.is-dirty',
            data: {
                topic: callbackTopic,
                path: path,
                workspace: workspace,
                params: params,
            },
        });
        return new Promise((resolve) => {
            const callbackListener = this.addMessageListener({
                topic: callbackTopic,
                handler: (data) => {
                    this.removeMessageListener(callbackListener);
                    resolve(data);
                }
            });
        });
    }

    /**
     * Triggered when a file dirty state check is called.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onIsFileDirty(handler) {
        return this.addMessageListener({ topic: 'platform.files.is-dirty', handler: handler });
    }

    /**
     * Checks if a file is open.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {Object.<any, any>} [params] - Extra parameters.
     */ // @ts-ignore
    isFileOpen({ path, workspace, params } = {}) {
        const callbackTopic = `platform.files.is-open.${new Date().valueOf()}`;
        this.postMessage({
            topic: 'platform.files.is-open',
            data: {
                topic: callbackTopic,
                path: path,
                workspace: workspace,
                params: params,
            },
        });
        return new Promise((resolve) => {
            const callbackListener = this.addMessageListener({
                topic: callbackTopic,
                handler: (data) => {
                    this.removeMessageListener(callbackListener);
                    resolve(data);
                }
            });
        });
    }

    /**
     * Triggered when a file open state check is called.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onIsFileOpen(handler) {
        return this.addMessageListener({ topic: 'platform.files.is-open', handler: handler });
    }

    /**
     * Sends a message containing information on which file has been saved.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {string} [status] - Git status of the file.
     * @param {string} [contentType] - File content type.
     */ // @ts-ignore
    announceFileSaved({ path, workspace, status, contentType } = {}) {
        this.postMessage({
            topic: 'platform.files.saved',
            data: {
                name: path.substring(path.lastIndexOf('/') + 1, path.length),
                path: path,
                workspace: workspace,
                status: status,
                contentType: contentType,
            }
        });
    }

    /**
     * Triggered when a file has been saved.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFileSaved(handler) {
        return this.addMessageListener({ topic: 'platform.files.saved', handler: handler });
    }

    /**
     * Sends a message containing information on which file should be saved.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {string} [params] - Extra parameters.
     */ // @ts-ignore
    saveFile({ path, workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.file.save',
            data: {
                path: path,
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when a file has to be saved.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onSaveFile(handler) {
        return this.addMessageListener({ topic: 'platform.file.save', handler: handler });
    }

    /**
     * Tells all open editors to save their content.
     */ // @ts-ignore
    saveAll() { this.triggerEvent('platform.files.save.all'); }

    /**
     * Triggered when all files should be saved.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onSaveAll(handler) {
        return this.addMessageListener({ topic: 'platform.files.save.all', handler: handler });
    }

    /**
     * Sends a message containing information on which file, folder or project has been published.
     * @param {string} [path] - Full file, folder or project path, including name.
     * @param {string} workspace - The workspace that the file, folder or project belonges to.
     * @param {Object.<any, any>} [params] - Extra parameters that will be passed the listener.
     */ // @ts-ignore
    announcePublished({ path, workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.publisher.published',
            data: {
                path: path,
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when a file, folder or project has been published.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onPublished(handler) {
        return this.addMessageListener({ topic: 'platform.publisher.published', handler: handler });
    }

    /**
     * Sends a message containing information on which file, folder or project has been unpublished.
     * @param {string} [path] - Full file, folder or project path, including name.
     * @param {string} workspace - The workspace that the file, folder or project belonges to.
     * @param {Object.<any, any>} [params] - Extra parameters that will be passed the listener.
     */ // @ts-ignore
    announceUnpublished({ path, workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.publisher.unpublished',
            data: {
                path: path,
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when a file, folder or project has been published.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onUnpublished(handler) {
        return this.addMessageListener({ topic: 'platform.publisher.unpublished', handler: handler });
    }

    /**
     * Sends a message containing information on which file has been selected.
     * @param {string} path - Full file path, including file name.
     * @param {string} contentType - The file content type.
     * @param {string} workspace - The workspace that the file belonges to.
     */ // @ts-ignore
    announceFileSelected({ path, contentType, workspace } = {}) {
        this.postMessage({
            topic: 'platform.files.selected',
            data: {
                name: path.substring(path.lastIndexOf('/') + 1, path.length),
                path: path,
                contentType: contentType,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a file has been deleted.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFileSelected(handler) {
        return this.addMessageListener({ topic: 'platform.files.selected', handler: handler });
    }

    /**
     * Sends a message containing information on which file has been deleted.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     */ // @ts-ignore
    announceFileDeleted({ path, workspace } = {}) {
        this.postMessage({
            topic: 'platform.files.deleted',
            data: {
                // name: path.substring(path.lastIndexOf('/') + 1, path.length),
                path: path,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a file has been deleted.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFileDeleted(handler) {
        return this.addMessageListener({ topic: 'platform.files.deleted', handler: handler });
    }

    /**
     * Sends a message containing information on which file has been renamed.
     * @param {string} oldPath - Old file path, including file name.
     * @param {string} newPath - New file path, including file name.
     * @param {string} contentType - The file content type.
     * @param {string} workspace - The workspace that the file belonges to.
     */ // @ts-ignore
    announceFileRenamed({ oldPath, newPath, contentType, workspace } = {}) {
        this.postMessage({
            topic: 'platform.files.renamed',
            data: {
                // oldName: oldPath.substring(oldPath.lastIndexOf('/') + 1, oldPath.length),
                newName: newPath.substring(newPath.lastIndexOf('/') + 1, newPath.length),
                oldPath: oldPath,
                newPath: newPath,
                contentType: contentType,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a file has been renamed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFileRenamed(handler) {
        return this.addMessageListener({ topic: 'platform.files.renamed', handler: handler });
    }

    /**
     * Sends a message containing information on which file has been moved.
     * @param {string} oldPath - Old file path, including file name.
     * @param {string} newPath - New file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     */ // @ts-ignore
    announceFileMoved({ oldPath, newPath, workspace } = {}) {
        this.postMessage({
            topic: 'platform.files.moved',
            data: {
                // oldName: oldPath.substring(oldPath.lastIndexOf('/') + 1, oldPath.length),
                newName: newPath.substring(newPath.lastIndexOf('/') + 1, newPath.length),
                oldPath: oldPath,
                newPath: newPath,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a file has been moved.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFileMoved(handler) {
        return this.addMessageListener({ topic: 'platform.files.moved', handler: handler });
    }


    /**
     * Tells an editor that it should reload its view parameters.
     * @param {string} path - Full file path, including file name.
     * @param {string} workspace - The workspace that the file belonges to.
     * @param {object} [params] - Custom parameters.
     */ // @ts-ignore
    reloadEditorParams({ path, workspace, params } = {}) {
        this.postMessage({
            topic: 'platform.files.reload-params',
            data: {
                path: path,
                workspace: workspace,
                params: params,
            }
        });
    }

    /**
     * Triggered when an editor should reload its view parameters.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onReloadEditorParams(handler) {
        return this.addMessageListener({ topic: 'platform.files.reload-params', handler: handler });
    }

    /**
     * Sends a message containing information on which folder has been deleted.
     * @param {string} path - Full folder path, including folder name.
     * @param {string} workspace - The workspace that the folder belonges to.
     */ // @ts-ignore
    announceFolderDeleted({ path, workspace } = {}) {
        this.postMessage({
            topic: 'platform.folders.deleted',
            data: {
                // name: path.substring(path.lastIndexOf('/') + 1, path.length),
                path: path,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a folder has been deleted.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFolderDeleted(handler) {
        return this.addMessageListener({ topic: 'platform.folders.deleted', handler: handler });
    }

    /**
     * Sends a message containing information on which folder has been renamed.
     * @param {string} oldPath - Old folder path, including folder name.
     * @param {string} newPath - New folder path, including folder name.
     * @param {string} contentType - The folder content type.
     * @param {string} workspace - The workspace that the folder belonges to.
     */ // @ts-ignore
    announceFolderRenamed({ oldPath, newPath, contentType, workspace } = {}) {
        this.postMessage({
            topic: 'platform.folders.renamed',
            data: {
                // oldName: oldPath.substring(oldPath.lastIndexOf('/') + 1, oldPath.length),
                // newName: newPath.substring(newPath.lastIndexOf('/') + 1, newPath.length),
                oldPath: oldPath,
                newPath: newPath,
                contentType: contentType,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a folder has been renamed.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFolderRenamed(handler) {
        return this.addMessageListener({ topic: 'platform.folders.renamed', handler: handler });
    }

    /**
     * Sends a message containing information on which folder has been moved.
     * @param {string} oldPath - Old folder path, including folder name.
     * @param {string} newPath - New folder path, including folder name.
     * @param {string} workspace - The workspace that the folder belonges to.
     */ // @ts-ignore
    announceFolderMoved({ oldPath, newPath, workspace } = {}) {
        this.postMessage({
            topic: 'platform.folders.moved',
            data: {
                // oldName: oldPath.substring(oldPath.lastIndexOf('/') + 1, oldPath.length),
                // newName: newPath.substring(newPath.lastIndexOf('/') + 1, newPath.length),
                oldPath: oldPath,
                newPath: newPath,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a file has been moved.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onFolderMoved(handler) {
        return this.addMessageListener({ topic: 'platform.folders.moved', handler: handler });
    }

    /**
     * Sends a message containing information on which project has been deleted.
     * @param {string} project - Project name.
     * @param {string} workspace - The workspace that the project belonges to.
     */ // @ts-ignore
    announceProjectDeleted({ project, workspace } = {}) {
        this.postMessage({
            topic: 'platform.files.deleted',
            data: {
                project: project,
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a project has been deleted.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onProjectDeleted(handler) {
        return this.addMessageListener({ topic: 'platform.shell.notification', handler: handler });
    }

    /**
     * Sends a message containing information on which workspace has been created.
     * @param {string} workspace - Workspace name.
     */ // @ts-ignore
    announceWorkspaceCreated({ workspace } = {}) {
        this.postMessage({
            topic: 'platform.workspaces.created',
            data: {
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a workspace has been created.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onWorkspaceCreated(handler) {
        return this.addMessageListener({ topic: 'platform.workspaces.created', handler: handler });
    }

    /**
     * Sends a message containing information on which workspace has been modified.
     * @param {string} workspace - Workspace name.
     */ // @ts-ignore
    announceWorkspaceModified({ workspace } = {}) {
        this.postMessage({
            topic: 'platform.workspaces.modified',
            data: {
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a workspace has been modified.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onWorkspaceModified(handler) {
        return this.addMessageListener({ topic: 'platform.workspaces.modified', handler: handler });
    }

    /**
     * Sends a message containing information on which workspace has been deleted.
     * @param {string} workspace - Workspace name.
     */ // @ts-ignore
    announceWorkspaceDeleted({ workspace } = {}) {
        this.postMessage({
            topic: 'platform.workspaces.deleted',
            data: {
                workspace: workspace,
            }
        });
    }

    /**
     * Triggered when a workspace has been created.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onWorkspaceDeleted(handler) {
        return this.addMessageListener({ topic: 'platform.workspaces.deleted', handler: handler });
    }

    /**
     * Gets all file paths from the currently opened editors.
     * @param {string} basePath - If provided, it will only return files with a matching base path.
     * @return {Promise} - Returns a promise with a list of paths as a parameter.
     */ // @ts-ignore
    getCurrentlyOpenedFiles({ basePath = '/' } = {}) {
        const callbackTopic = `platform.editors.opened.${new Date().valueOf()}`;
        this.postMessage({
            topic: 'platform.editors.opened',
            data: {
                basePath: basePath,
                topic: callbackTopic,
            }
        });
        return new Promise((resolve) => {
            const callbackListener = this.addMessageListener({
                topic: callbackTopic,
                handler: (data) => {
                    this.removeMessageListener(callbackListener);
                    resolve(data);
                }
            });
        });
    }

    /**
     * Triggered when a list of opened files is requested.
     * @param handler - Callback function.
     * @returns - A reference to the listener. In order to remove/disable the listener, you need to use this reference and pass it to the 'removeMessageListener' function.
     */
    onGetCurrentlyOpenedFiles(handler) {
        return this.addMessageListener({ topic: 'platform.editors.opened', handler: handler });
    }
}