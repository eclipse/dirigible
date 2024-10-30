/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('platformDialogs', ['blimpKit', 'platformView']).directive('dialogs', (Extensions) => ({
    restrict: 'E',
    replace: true,
    transclude: false,
    link: (scope) => {
        let cachedWindows;
        Extensions.getWindows().then((response) => {
            cachedWindows = response.data;
        }, (error) => {
            console.log(error);
        });
        // @ts-ignore
        const dialogApi = new DialogApi();

        scope.messageBoxes = [];
        const alertListener = dialogApi.onAlert((data) => {
            scope.$apply(() => scope.messageBoxes.push(data));
        });
        scope.closeAlert = (buttonId) => {
            if (scope.messageBoxes[0].topic) dialogApi.postMessage({ topic: scope.messageBoxes[0].topic, data: buttonId });
            scope.messageBoxes.shift();
        };

        scope.dialogs = [];
        const dialogListener = dialogApi.onDialog((data) => {
            scope.$apply(() => scope.dialogs.push(data));
        });
        scope.closeDialog = (buttonId) => {
            if (buttonId) dialogApi.postMessage({ topic: scope.dialogs[0].topic, data: buttonId });
            else dialogApi.triggerEvent(scope.dialogs[0].topic);
            scope.dialogs.shift();
        };

        scope.busyDialog = { id: '', message: '' };
        const busyDialogListener = dialogApi.onBusyDialog((data) => {
            scope.$apply(() => {
                if (data.close && scope.busyDialog.id === data.id) {
                    scope.busyDialog.id = '';
                    scope.busyDialog.message = '';
                } else if (!scope.busyDialog.id) {
                    scope.busyDialog.id = data.id;
                    scope.busyDialog.message = data.message;
                } else if (scope.busyDialog.id === data.id) {
                    scope.busyDialog.message = data.message;
                }
            });
        });

        scope.formDialog = { form: {} };
        scope.formDialogs = [];
        const formDialogListener = dialogApi.onFormDialog((data) => {
            scope.$apply(() => scope.formDialogs.push(data));
        });
        scope.visibleOn = (visibleOn) => {
            if (!visibleOn) return true;
            else if (visibleOn.value) {
                return scope.formDialogs[0].form[visibleOn.key].value === visibleOn.value;
            } else {
                return scope.formDialog.form[`n${visibleOn.key}`].$valid;
            }
        };
        scope.enabledOn = (enabledOn) => {
            if (enabledOn.value) {
                return scope.formDialogs[0].form[enabledOn.key].value === enabledOn.value;
            } else {
                return scope.formDialog.form[`n${enabledOn.key}`].$valid;
            }
        };
        scope.closeFormDialog = (submit) => {
            if (submit) {
                const formData = {};
                for (let i = 0, keys = Object.keys(scope.formDialogs[0].form); i < keys.length; i++) {
                    // @ts-ignore
                    formData[keys[i]] = scope.formDialogs[0].form[keys[i]].value;
                }
                dialogApi.postMessage({
                    topic: scope.formDialogs[0].topic,
                    data: formData
                });
            } else dialogApi.triggerEvent(scope.formDialogs[0].topic);
            scope.formDialogs.shift();
        };

        scope.dialogWindows = [];
        const dialogWindowListener = dialogApi.onWindow((data) => {
            if (data.id) {
                const viewConfig = cachedWindows.find(v => v.id === data.id);
                scope.$apply(() => {
                    if (!viewConfig) {
                        scope.messageBoxes.push({
                            title: 'Unknown window',
                            message: `Window with id '${data.id}' does not exist.`,
                            type: AlertTypes.Error,
                            buttons: [{ id: 'close', label: 'Close' }]
                        });
                    } else {
                        scope.dialogWindows.push({
                            ...data,
                            ...viewConfig,
                        });
                    }
                });
            } else scope.$apply(() => scope.dialogWindows.push(data));
        });
        scope.closeDialogWindow = () => {
            if (scope.dialogWindows[0].callbackTopic) dialogApi.triggerEvent(scope.dialogWindows[0].callbackTopic);
            scope.dialogWindows.shift();
        };

        scope.$on('$destroy', () => {
            dialogApi.removeMessageListener(alertListener);
            dialogApi.removeMessageListener(dialogListener);
            dialogApi.removeMessageListener(busyDialogListener);
            dialogApi.removeMessageListener(formDialogListener);
            dialogApi.removeMessageListener(dialogWindowListener);
        });
    },
    templateUrl: '/services/web/platform-core/ui/templates/dialogs.html',
}));