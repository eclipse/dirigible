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
angular.module('ideMessageHub', [])
    .provider('messageHub', function MessageHubProvider() {
        this.eventIdPrefix = '';
        this.eventIdDelimiter = '.';
        this.$get = [function messageHubFactory() {
            let messageHub = new FramesMessageHub();
            let trigger = function (eventId, absolute = false) {
                if (!eventId)
                    throw Error('eventId argument must be a valid string, identifying an existing event');
                if (!absolute && this.eventIdPrefix !== '') eventId = this.eventIdPrefix + this.eventIdDelimiter + eventId;
                messageHub.post({}, eventId);
            }.bind(this);
            let post = function (eventId, data, absolute = false) {
                if (!eventId)
                    throw Error('eventId argument must be a valid string, identifying an existing event');
                if (!absolute && this.eventIdPrefix !== "") eventId = this.eventIdPrefix + this.eventIdDelimiter + eventId;
                messageHub.post({ data: data }, eventId);
            }.bind(this);
            let onMessage = function (eventId, callbackFunc, absolute = false) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                if (!absolute && this.eventIdPrefix !== "") eventId = this.eventIdPrefix + this.eventIdDelimiter + eventId;
                return messageHub.subscribe(callbackFunc, eventId);
            }.bind(this);
            let showStatusBusy = function (message) {
                if (!message)
                    throw Error("Status: you must provide a message");
                messageHub.post({
                    message: message,
                }, 'ide.status.busy');
            };
            let hideStatusBusy = function () {
                messageHub.post({
                    message: '',
                }, 'ide.status.busy');
            };
            let setStatusMessage = function (message) {
                if (!message)
                    throw Error("Status: you must provide a message");
                messageHub.post({
                    message: message,
                }, 'ide.status.message');
            };
            let setStatusError = function (message) {
                if (!message)
                    throw Error("Status: you must provide a message");
                messageHub.post({
                    message: message,
                }, 'ide.status.error');
            };
            let setStatusCaret = function (text) {
                messageHub.post({
                    text: text,
                }, 'ide.status.caret');
            };
            let showAlert = function (title, message, type) {
                messageHub.post({
                    title: title,
                    message: message,
                    type: type
                }, 'ide.alert');
            };
            let showAlertSuccess = function (title, message) {
                showAlert(title, message, "success");
            };
            let showAlertInfo = function (title, message) {
                showAlert(title, message, "info");
            };
            let showAlertWarning = function (title, message) {
                showAlert(title, message, "warning");
            };
            let showAlertError = function (title, message) {
                showAlert(title, message, "error");
            };
            let showDialog = function (
                title = "",
                body = "",
                buttons = [{
                    id: "b1",
                    type: "normal", // normal, emphasized, negative, transparent
                    label: "Ok",
                }],
                callbackTopic = null,
                loader = false,
                header = "",
                subheader = "",
                footer = ""
            ) {
                if (buttons.length === 0)
                    throw Error("Dialog: There must be at least one button");
                messageHub.post({
                    header: header,
                    subheader: subheader,
                    title: title,
                    body: body,
                    footer: footer,
                    loader: loader,
                    buttons: buttons,
                    callbackTopic: callbackTopic
                }, 'ide.dialog');
            };
            let showDialogAsync = function (
                title = "",
                body = "",
                buttons = [{
                    id: "b1",
                    type: "normal", // normal, emphasized, negative, transparent
                    label: "Ok",
                }],
                loader = false,
                header = "",
                subheader = "",
                footer = ""
            ) {
                return new Promise((resolve, reject) => {
                    if (buttons.length === 0)
                        reject(new Error("Dialog: There must be at least one button"));

                    const callbackTopic = `ide.dialog.${new Date().valueOf()}`;

                    messageHub.post({
                        header: header,
                        subheader: subheader,
                        title: title,
                        body: body,
                        footer: footer,
                        loader: loader,
                        buttons: buttons,
                        callbackTopic: callbackTopic
                    }, 'ide.dialog');

                    const handler = messageHub.subscribe(function (msg) {
                        messageHub.unsubscribe(handler);
                        resolve(msg);
                    }, callbackTopic);
                });
            };
            let showFormDialog = function (
                id,
                title = "",
                items = [],
                buttons = [{
                    id: "b1",
                    type: "normal", // normal, emphasized, transparent
                    label: "Ok",
                }],
                callbackTopic = null,
                loadingMessage = "",
                header = "",
                subheader = "",
                footer = "",
            ) {
                if (!id)
                    throw Error("Form Dialog: You must specify a dialog id");
                if (items.length === 0)
                    throw Error("Form Dialog: There must be at least one form item");
                if (buttons.length === 0)
                    throw Error("Form Dialog: There must be at least one button");
                if (!callbackTopic)
                    throw Error("Form Dialog: There must be a callback topic");
                messageHub.post({
                    id: id,
                    header: header,
                    subheader: subheader,
                    title: title,
                    items: items,
                    loadingMessage: loadingMessage,
                    footer: footer,
                    buttons: buttons,
                    callbackTopic: callbackTopic,
                }, 'ide.formDialog.show');
            };
            let updateFormDialog = function (
                id,
                items = [],
                loadingMessage,
                subheader = "",
                footer,
            ) {
                if (!id)
                    throw Error("Form Dialog: You must specify a dialog id");
                if (items.length === 0)
                    throw Error("Form Dialog: There must be at least one form item");
                messageHub.post({
                    id: id,
                    subheader: subheader,
                    footer: footer,
                    items: items,
                    loadingMessage: loadingMessage,
                }, 'ide.formDialog.update');
            };
            let hideFormDialog = function (id) {
                if (!id)
                    throw Error("Form Dialog: You must specify a dialog id");
                messageHub.post({ id: id }, 'ide.formDialog.hide');
            };
            let showLoadingDialog = function (
                id,
                title = "",
                status = ""
            ) {
                if (!id)
                    throw Error("Loading Dialog: You must specify a dialog id");
                messageHub.post({
                    id: id,
                    title: title,
                    status: status
                }, 'ide.loadingDialog.show');
            };
            let updateLoadingDialog = function (
                id,
                status = "",
            ) {
                if (!id)
                    throw Error("Loading Dialog: You must specify a dialog id");
                messageHub.post({
                    id: id,
                    status: status,
                }, 'ide.loadingDialog.update');
            };
            let hideLoadingDialog = function (id) {
                if (!id)
                    throw Error("Loading Dialog: You must specify a dialog id");
                messageHub.post({ id: id }, 'ide.loadingDialog.hide');
            };
            let showBusyDialog = function (
                id,
                text = '',
                callbackTopic = '',
            ) {
                if (!id)
                    throw Error("Busy Dialog: You must specify a dialog id");
                messageHub.post({
                    id: id,
                    text: text,
                    callbackTopic: callbackTopic
                }, 'ide.busyDialog.show');
            };
            let hideBusyDialog = function (id) {
                if (!id)
                    throw Error("Busy Dialog: You must specify a dialog id");
                messageHub.post({ id: id }, 'ide.busyDialog.hide');
            };
            let showSelectDialog = function (
                title,
                listItems,
                callbackTopic,
                isSingleChoice = true,
                hasSearch = false
            ) {
                if (title === undefined)
                    throw Error("Select dialog: Title must be specified");
                if (listItems === undefined || !Array.isArray(listItems))
                    throw Error("Select dialog: You must provide a list of strings.");
                else if (listItems.length === 0)
                    throw Error("Select dialog: List is empty");
                if (callbackTopic === undefined)
                    throw Error("Select dialog: Callback topic must pe specified");
                messageHub.post({
                    title: title,
                    listItems: listItems,
                    callbackTopic: callbackTopic,
                    isSingleChoice: isSingleChoice,
                    hasSearch: hasSearch
                }, 'ide.selectDialog');
            };
            let showDialogWindow = function (
                dialogWindowId = "",
                params,
                callbackTopic = null,
                closable = true,
            ) {
                if (params !== undefined && !(typeof params === 'object' && !Array.isArray(params) && params !== null))
                    throw Error("showDialogWindow: params must be an object");
                messageHub.post({
                    dialogWindowId: dialogWindowId,
                    params: params,
                    callbackTopic: callbackTopic,
                    closable: closable
                }, 'ide.dialogWindow');
            };
            let closeDialogWindow = function (dialogWindowId = "") {
                if (dialogWindowId === undefined)
                    throw Error("closeDialogWindow: you must provide an ID");
                messageHub.post({
                    dialogWindowId: dialogWindowId,
                }, 'ide.dialogWindow.close');
            };
            let openView = function (viewId, params) {
                if (viewId === undefined)
                    throw Error("openView: viewId must be specified");
                if (params !== undefined && !(typeof params === 'object' && !Array.isArray(params) && params !== null))
                    throw Error("openView: params must be an object");
                messageHub.post({
                    viewId: viewId,
                    params: params,
                }, 'ide-core.openView');
            };
            let openPerspective = function (link, params) {
                if (link === undefined)
                    throw Error("openPerspective: link must be specified");
                messageHub.post({
                    link: link,
                    params: params,
                }, 'ide-core.openPerspective');
            };
            let openEditor = function (
                resourcePath,
                resourceLabel,
                contentType,
                editorId,
                extraArgs
            ) {
                if (resourcePath === undefined)
                    throw Error("openEditor: resourcePath must be specified");
                messageHub.post({
                    resourcePath: resourcePath,
                    resourceLabel: resourceLabel,
                    contentType: contentType,
                    editorId: editorId,
                    extraArgs: extraArgs,
                }, 'ide-core.openEditor');
            };
            let setEditorDirty = function (
                resourcePath,
                isDirty,
            ) {
                if (resourcePath === undefined)
                    throw Error("setEditorDirty: resourcePath must be specified");
                if (isDirty === undefined)
                    throw Error("setEditorDirty: isDirty must be specified");
                messageHub.post({
                    resourcePath: resourcePath,
                    isDirty: isDirty,
                }, 'ide-core.setEditorDirty');
            };
            let setFocusedEditor = function (resourcePath) {
                if (resourcePath === undefined)
                    throw Error("setFocusedEditor: resourcePath must be specified");
                messageHub.post({ resourcePath: resourcePath }, 'ide-core.setFocusedEditor');
            };
            let setEditorFocusGain = function (resourcePath) {
                if (resourcePath === undefined)
                    throw Error("setFocusedEditor: resourcePath must be specified");
                messageHub.post({ resourcePath: resourcePath }, 'ide-core.setEditorFocusGain');
            };
            let onEditorFocusGain = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide-core.setEditorFocusGain');
            };
            let closeEditor = function (
                resourcePath,
            ) {
                if (resourcePath === undefined)
                    throw Error("closeEditor: resourcePath must be specified");
                messageHub.post({
                    resourcePath: resourcePath,
                }, 'ide-core.closeEditor');
            };
            let closeOtherEditors = function (
                resourcePath,
            ) {
                if (resourcePath === undefined)
                    throw Error("closeEditor: resourcePath must be specified");
                messageHub.post({
                    resourcePath: resourcePath,
                }, 'ide-core.closeOtherEditors');
            };
            let closeAllEditors = function () {
                messageHub.post({}, 'ide-core.closeAllEditors');
            };
            let unsubscribe = function (handler) {
                messageHub.unsubscribe(handler);
            };
            /**
             * fileDescriptor object:
             * {
             *   name: 'example.js',
             *   path: '/project/folder/example.js',
             *   contentType: 'text/javascript',
             *   workspace: 'example'
             * }
            */
            let announceFileSelected = function (fileDescriptor) {
                if (fileDescriptor !== undefined && !(typeof fileDescriptor === 'object' && !Array.isArray(fileDescriptor) && fileDescriptor !== null))
                    throw Error('You must provide an appropriate file descriptor object, containing name, path, contentType and workspace');
                messageHub.post(fileDescriptor, 'ide.file.selected');
            };
            let onFileSelected = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.selected');
            };
            let announceFileOpened = function (fileDescriptor) {
                messageHub.post(fileDescriptor, 'ide.file.opened');
            };
            let onFileOpened = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.opened');
            };
            let announceFileCreated = function (fileDescriptor) {
                if (fileDescriptor !== undefined && !(typeof fileDescriptor === 'object' && !Array.isArray(fileDescriptor) && fileDescriptor !== null))
                    throw Error('You must provide an appropriate file descriptor object, containing name, path, contentType and workspace');
                messageHub.post(fileDescriptor, 'ide.file.created');
            };
            let onFileCreated = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.created');
            };
            let announceFileDeleted = function (fileDescriptor) {
                if (fileDescriptor !== undefined && !(typeof fileDescriptor === 'object' && !Array.isArray(fileDescriptor) && fileDescriptor !== null))
                    throw Error('You must provide an appropriate file descriptor object, containing name, path, contentType and workspace');
                messageHub.post(fileDescriptor, 'ide.file.deleted');
            };
            let onFileDeleted = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.deleted');
            };
            /**
             * fileDescriptor object for rename:
             * {
             *   oldName: 'sample.js',
             *   name: 'example.js',
             *   oldPath: '/project/folder/sample.js',
             *   path: '/project/folder/example.js',
             *   contentType: 'text/javascript',
             *   workspace: 'example'
             * }
            */
            let announceFileRenamed = function (fileDescriptor) {
                if (fileDescriptor !== undefined && !(typeof fileDescriptor === 'object' && !Array.isArray(fileDescriptor) && fileDescriptor !== null))
                    throw Error('You must provide an appropriate file descriptor object, containing name, path, contentType and workspace');
                messageHub.post(fileDescriptor, 'ide.file.renamed');
            };
            let onFileRenamed = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.renamed');
            };
            let announceFileSaved = function (fileDescriptor) {
                if (fileDescriptor !== undefined && !(typeof fileDescriptor === 'object' && !Array.isArray(fileDescriptor) && fileDescriptor !== null))
                    throw Error('You must provide an appropriate file descriptor object, containing name, path, contentType and workspace');
                messageHub.post(fileDescriptor, 'ide.file.saved');
            };
            let onFileSaved = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.saved');
            };
            let announceFileMoved = function (fileDescriptor) {
                messageHub.post(fileDescriptor, 'ide.file.moved');
            };
            let onFileMoved = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.moved');
            };
            let announceFileCopied = function (fileDescriptor) {
                messageHub.post(fileDescriptor, 'ide.file.copied');
            };
            let onFileCopied = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.file.copied');
            };
            let announcePublish = function (fileDescriptor) {
                messageHub.post(fileDescriptor, 'ide.event.publish');
            };
            let onPublish = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.event.publish');
            };
            let announceUnpublish = function (fileDescriptor) {
                messageHub.post(fileDescriptor, 'ide.event.unpublish');
            };
            let onUnpublish = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.event.unpublish');
            };
            let announceWorkspaceChanged = function (workspace) {
                if (workspace !== undefined && !(typeof workspace === 'object' && !Array.isArray(workspace) && workspace !== null) && !workspace.hasOwnProperty('name'))
                    throw Error('You must provide an appropriate workspace object, containing the "name" key');
                messageHub.post({ data: workspace }, 'ide.workspace.changed');
            };
            let onWorkspaceChanged = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.workspace.changed');
            };
            let announceWorkspacesModified = function () {
                trigger('ide.workspaces.modified', true);
            };
            let onWorkspacesModified = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.workspaces.modified');
            };
            let announceRepositoryModified = function () {
                trigger('ide.repository.modified', true);
            };
            let onRepositoryModified = function (callbackFunc) {
                if (typeof callbackFunc !== 'function')
                    throw Error('Callback argument must be a function');
                return messageHub.subscribe(callbackFunc, 'ide.repository.modified');
            };
            return {
                showStatusBusy: showStatusBusy,
                hideStatusBusy: hideStatusBusy,
                setStatusMessage: setStatusMessage,
                setStatusError: setStatusError,
                setStatusCaret: setStatusCaret,
                showAlertSuccess: showAlertSuccess,
                showAlertInfo: showAlertInfo,
                showAlertWarning: showAlertWarning,
                showAlertError: showAlertError,
                showDialog: showDialog,
                showDialogAsync: showDialogAsync,
                showFormDialog: showFormDialog,
                updateFormDialog: updateFormDialog,
                hideFormDialog: hideFormDialog,
                showLoadingDialog: showLoadingDialog,
                updateLoadingDialog: updateLoadingDialog,
                hideLoadingDialog: hideLoadingDialog,
                showBusyDialog: showBusyDialog,
                hideBusyDialog: hideBusyDialog,
                showSelectDialog: showSelectDialog,
                showDialogWindow: showDialogWindow,
                closeDialogWindow: closeDialogWindow,
                openView: openView,
                openPerspective: openPerspective,
                openEditor: openEditor,
                setEditorDirty: setEditorDirty,
                setFocusedEditor: setFocusedEditor,
                setEditorFocusGain: setEditorFocusGain,
                onEditorFocusGain: onEditorFocusGain,
                closeEditor: closeEditor,
                closeOtherEditors: closeOtherEditors,
                closeAllEditors: closeAllEditors,
                triggerEvent: trigger,
                'postMessage': post,
                onDidReceiveMessage: onMessage,
                unsubscribe: unsubscribe,
                announceFileSelected: announceFileSelected,
                onFileSelected: onFileSelected,
                announceFileOpened: announceFileOpened,
                onFileOpened: onFileOpened,
                announceFileCreated: announceFileCreated,
                onFileCreated: onFileCreated,
                announceFileDeleted: announceFileDeleted,
                onFileDeleted: onFileDeleted,
                announceFileRenamed: announceFileRenamed,
                onFileRenamed: onFileRenamed,
                announceFileSaved: announceFileSaved,
                onFileSaved: onFileSaved,
                announceFileMoved: announceFileMoved,
                onFileMoved: onFileMoved,
                announceFileCopied: announceFileCopied,
                onFileCopied: onFileCopied,
                announcePublish: announcePublish,
                onPublish: onPublish,
                announceUnpublish: announceUnpublish,
                onUnpublish: onUnpublish,
                announceWorkspaceChanged: announceWorkspaceChanged,
                onWorkspaceChanged: onWorkspaceChanged,
                announceWorkspacesModified: announceWorkspacesModified,
                onWorkspacesModified: onWorkspacesModified,
                announceRepositoryModified: announceRepositoryModified,
                onRepositoryModified: onRepositoryModified,
            };
        }];
    });