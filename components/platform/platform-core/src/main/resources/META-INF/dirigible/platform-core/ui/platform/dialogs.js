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
angular.module('platformDialogs', ['blimpKit', 'platformView']).directive('dialogs', function (Extensions) {
    return {
        restrict: 'E',
        replace: true,
        transclude: false,
        link: function (scope, _element) {
            let cachedWindows;
            Extensions.getWindows().then(function (response) {
                cachedWindows = response.data;
            }, (error) => {
                console.log(error);
            });
            // @ts-ignore
            const messageHub = new MessageHubApi();

            scope.messageBoxes = [];
            const alertListener = messageHub.addMessageListener({
                topic: 'platform.alert',
                handler: (data) => {
                    scope.$apply(() => scope.messageBoxes.push(data));
                }
            });
            scope.closeAlert = function (buttonId) {
                if (scope.messageBoxes[0].topic) messageHub.postMessage({ topic: scope.messageBoxes[0].topic, data: buttonId });
                scope.messageBoxes.shift();
            };

            scope.dialogs = [];
            const dialogListener = messageHub.addMessageListener({
                topic: 'platform.dialog',
                handler: (data) => {
                    scope.$apply(() => scope.dialogs.push(data));
                }
            });
            scope.closeDialog = function (buttonId) {
                if (buttonId) messageHub.postMessage({ topic: scope.dialogs[0].topic, data: buttonId });
                else messageHub.triggerEvent(scope.dialogs[0].topic);
                scope.dialogs.shift();
            };

            scope.busyDialog = { id: '', message: '' };
            const busyDialogListener = messageHub.addMessageListener({
                topic: 'platform.dialog.busy',
                handler: (data) => {
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
                }
            });

            scope.formDialog = { form: {} };
            scope.formDialogs = [];
            const formDialogListener = messageHub.addMessageListener({
                topic: 'platform.dialog.form',
                handler: (data) => {
                    scope.$apply(() => scope.formDialogs.push(data));
                }
            });
            scope.visibleOn = function (visibleOn) {
                if (!visibleOn) return true;
                else if (visibleOn.value) {
                    return scope.formDialogs[0].form[visibleOn.key].value === visibleOn.value;
                } else {
                    return scope.formDialog.form[`n${visibleOn.key}`].$valid;
                }
            };
            scope.enabledOn = function (enabledOn) {
                if (enabledOn.value) {
                    return scope.formDialogs[0].form[enabledOn.key].value === enabledOn.value;
                } else {
                    return scope.formDialog.form[`n${enabledOn.key}`].$valid;
                }
            };
            scope.closeFormDialog = function (submit) {
                if (submit) {
                    const formData = {};
                    for (let i = 0, keys = Object.keys(scope.formDialogs[0].form); i < keys.length; i++) {
                        // @ts-ignore
                        formData[keys[i]] = scope.formDialogs[0].form[keys[i]].value;
                    }
                    messageHub.postMessage({
                        topic: scope.formDialogs[0].topic,
                        data: formData
                    });
                } else messageHub.triggerEvent(scope.formDialogs[0].topic);
                scope.formDialogs.shift();
            };

            scope.dialogWindows = [];
            const dialogWindowListener = messageHub.addMessageListener({
                topic: 'platform.dialog.window',
                handler: (data) => {
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
                }
            });
            scope.closeDialogWindow = function () {
                if (scope.dialogWindows[0].callbackTopic) messageHub.triggerEvent(scope.dialogWindows[0].callbackTopic);
                scope.dialogWindows.shift();
            };

            scope.$on('$destroy', function () {
                messageHub.removeMessageListener(alertListener);
                messageHub.removeMessageListener(dialogListener);
                messageHub.removeMessageListener(busyDialogListener);
                messageHub.removeMessageListener(formDialogListener);
                messageHub.removeMessageListener(dialogWindowListener);
            });
        },
        templateUrl: '/services/web/platform-core/ui/templates/dialogs.html'
    }
});