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
                    type: "normal", // normal, emphasized, transparent
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
                    type: "normal", // normal, emphasized, transparent
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
                callbackTopic = null
            ) {
                if (params !== undefined && !(typeof params === 'object' && !Array.isArray(params) && params !== null))
                    throw Error("showDialogWindow: params must be an object");
                messageHub.post({
                    dialogWindowId: dialogWindowId,
                    params: params,
                    callbackTopic: callbackTopic
                }, 'ide.dialogWindow');
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
                    throw Error("openEditor: resourcePath must be specified");
                if (isDirty === undefined)
                    throw Error("openEditor: isDirty must be specified");
                messageHub.post({
                    resourcePath: resourcePath,
                    isDirty: isDirty,
                }, 'ide-core.setEditorDirty');
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
                showSelectDialog: showSelectDialog,
                showDialogWindow: showDialogWindow,
                openView: openView,
                openPerspective: openPerspective,
                openEditor: openEditor,
                setEditorDirty: setEditorDirty,
                closeEditor: closeEditor,
                closeOtherEditors: closeOtherEditors,
                closeAllEditors: closeAllEditors,
                triggerEvent: trigger,
                'postMessage': post,
                onDidReceiveMessage: onMessage,
                unsubscribe: unsubscribe
            };
        }];
    });