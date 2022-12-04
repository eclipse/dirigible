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
angular.module('page', ["ideUI", "ideView"])
    .controller('PageController', function ($scope, messageHub, ViewParameters) {
        let contents;
        let csrfToken;
        $scope.errorMessage = 'Аn unknown error was encountered. Please see console for more information.';
        $scope.state = {
            isBusy: true,
            error: false,
            busyText: "Loading...",
        };
        $scope.methods = [
            { value: '*', label: '*' },
            { value: 'GET', label: 'GET' },
            { value: 'POST', label: 'POST' },
            { value: 'PUT', label: 'PUT' },
            { value: 'DELETE', label: 'DELETE' },
            { value: 'READ', label: 'READ' },
            { value: 'WRITE', label: 'WRITE' },
        ];
        $scope.scopes = [
            { value: 'HTTP', label: 'HTTP' },
            { value: 'CMIS', label: 'CMIS' },
        ];
        $scope.editConstraintIndex = 0;

        function getResource(resourcePath) {
            let xhr = new XMLHttpRequest();
            xhr.open('GET', resourcePath, false);
            xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
            xhr.send();
            if (xhr.status === 200) {
                csrfToken = xhr.getResponseHeader("x-csrf-token");
                return xhr.responseText;
            } else {
                $scope.state.error = true;
                $scope.errorMessage = "Unable to load the file. See console, for more information.";
                messageHub.setStatusError(`Error loading '${$scope.dataParameters.file}'`);
                return '{}';
            }
        }

        $scope.load = function () {
            if (!$scope.state.error) {
                contents = getResource('/services/v4/ide/workspaces' + $scope.dataParameters.file);
                $scope.access = JSON.parse(contents);
                contents = JSON.stringify($scope.access, null, 4);
                $scope.state.isBusy = false;
            }
        };

        function saveContents(text) {
            let xhr = new XMLHttpRequest();
            xhr.open('PUT', '/services/v4/ide/workspaces' + $scope.dataParameters.file);
            xhr.setRequestHeader('X-Requested-With', 'Fetch');
            xhr.setRequestHeader('X-CSRF-Token', csrfToken);
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    messageHub.announceFileSaved({
                        name: $scope.dataParameters.file.substring($scope.dataParameters.file.lastIndexOf('/') + 1),
                        path: $scope.dataParameters.file.substring($scope.dataParameters.file.indexOf('/', 1)),
                        contentType: $scope.dataParameters.contentType,
                        workspace: $scope.dataParameters.file.substring(1, $scope.dataParameters.file.indexOf('/', 1)),
                    });
                    messageHub.setStatusMessage(`File '${$scope.dataParameters.file}' saved`);
                    messageHub.setEditorDirty($scope.dataParameters.file, false);
                    $scope.$apply(function () {
                        $scope.state.isBusy = false;
                    });
                }
            };
            xhr.onerror = function (error) {
                console.error(`Error saving '${$scope.dataParameters.file}'`, error);
                messageHub.setStatusError(`Error saving '${$scope.dataParameters.file}'`);
                messageHub.showAlertError('Error while saving the file', 'Please look at the console for more information');
                $scope.$apply(function () {
                    $scope.state.isBusy = false;
                });
            };
            xhr.send(text);
        }

        $scope.save = function () {
            $scope.state.busyText = "Saving...";
            $scope.state.isBusy = true;
            contents = JSON.stringify($scope.access, null, 4);
            saveContents(contents);
        };

        messageHub.onDidReceiveMessage(
            "editor.file.save.all",
            function () {
                if (!$scope.state.error) {
                    let access = JSON.stringify($scope.access, null, 4);
                    if (contents !== access) {
                        $scope.save();
                    }
                }
            },
            true,
        );

        messageHub.onDidReceiveMessage(
            "editor.file.save",
            function (msg) {
                if (!$scope.state.error) {
                    let file = msg.data && typeof msg.data === 'object' && msg.data.file;
                    if (file && file === $scope.dataParameters.file) {
                        let access = JSON.stringify($scope.access, null, 4);
                        if (contents !== access) $scope.save();
                    }
                }
            },
            true,
        );

        messageHub.onDidReceiveMessage(
            "accessEditor.constraint.add",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.access.constraints.push({
                            path: msg.data.formData[0].value,
                            method: msg.data.formData[1].value,
                            scope: msg.data.formData[2].value,
                            roles: msg.data.formData[3].value
                                .split(',').map(element => element.trim()).filter(element => element !== ''),
                        });
                    });
                }
                messageHub.hideFormDialog("accessEditorAddConstraint");
            },
            true
        );

        messageHub.onDidReceiveMessage(
            "accessEditor.constraint.edit",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.access.constraints[$scope.editConstraintIndex].path = msg.data.formData[0].value;
                        $scope.access.constraints[$scope.editConstraintIndex].method = msg.data.formData[1].value;
                        $scope.access.constraints[$scope.editConstraintIndex].scope = msg.data.formData[2].value;
                        $scope.access.constraints[$scope.editConstraintIndex].roles = msg.data.formData[3].value
                            .split(',').map(element => element.trim()).filter(element => element !== '');
                    });
                }
                messageHub.hideFormDialog("accessEditorEditConstraint");
            },
            true
        );

        $scope.$watch('access', function () {
            if (!$scope.state.error) {
                let access = JSON.stringify($scope.access, null, 4);
                messageHub.setEditorDirty($scope.dataParameters.file, contents !== access);
            }
        }, true);

        $scope.addConstraint = function () {
            messageHub.showFormDialog(
                "accessEditorAddConstraint",
                "Add constraint",
                [{
                    id: "aeciPath",
                    type: "input",
                    label: "Path",
                    required: true,
                    placeholder: "Enter path",
                    minlength: 1,
                    maxlength: 255,
                    inputRules: {
                        patterns: ['^[a-zA-Z0-9_.-/$-]*$'],
                    },
                    value: '',
                },
                {
                    id: "aecdMethod",
                    type: "dropdown",
                    label: "Method",
                    required: true,
                    value: 'GET',
                    items: $scope.methods,
                },
                {
                    id: "aecdScope",
                    type: "dropdown",
                    label: "Scope",
                    required: true,
                    value: 'HTTP',
                    items: $scope.scopes,
                },
                {
                    id: "aeciRoles",
                    type: "input",
                    label: "Roles",
                    placeholder: "Comma separated roles",
                    value: '',
                }],
                [{
                    id: "b1",
                    type: "emphasized",
                    label: "Add",
                    whenValid: true,
                },
                {
                    id: "b2",
                    type: "transparent",
                    label: "Cancel",
                }],
                "accessEditor.constraint.add",
                "Adding constraint..."
            );
        };

        $scope.editConstraint = function (index) {
            $scope.editConstraintIndex = index;
            messageHub.showFormDialog(
                "accessEditorEditConstraint",
                "Edit constraint",
                [{
                    id: "aeciPath",
                    type: "input",
                    label: "Path",
                    required: true,
                    placeholder: "Enter path",
                    minlength: 1,
                    maxlength: 255,
                    inputRules: {
                        patterns: ['^[a-zA-Z0-9_.-/$-]*$'],
                    },
                    value: $scope.access.constraints[index].path,
                },
                {
                    id: "aecdMethod",
                    type: "dropdown",
                    label: "Method",
                    required: true,
                    value: $scope.access.constraints[index].method,
                    items: $scope.methods,
                },
                {
                    id: "aecdScope",
                    type: "dropdown",
                    label: "Scope",
                    required: true,
                    value: $scope.access.constraints[index].scope,
                    items: $scope.scopes,
                },
                {
                    id: "aeciRoles",
                    type: "input",
                    label: "Roles",
                    placeholder: "Comma separated roles",
                    value: $scope.access.constraints[index].roles.join(', '),
                }],
                [{
                    id: "b1",
                    type: "emphasized",
                    label: "Update",
                    whenValid: true,
                },
                {
                    id: "b2",
                    type: "transparent",
                    label: "Cancel",
                }],
                "accessEditor.constraint.edit",
                "Updating constraint..."
            );
        };

        $scope.deleteConstraint = function (index) {
            messageHub.showDialogAsync(
                `Delete ${$scope.access.constraints[index].path}?`,
                'This action cannot be undone.',
                [{
                    id: 'b1',
                    type: 'negative',
                    label: 'Delete',
                },
                {
                    id: 'b2',
                    type: 'transparent',
                    label: 'Cancel',
                }],
            ).then(function (dialogResponse) {
                if (dialogResponse.data === 'b1') {
                    $scope.$apply(function () {
                        $scope.access.constraints.splice(index, 1);
                    });
                }
            });
        };

        $scope.dataParameters = ViewParameters.get();
        if (!$scope.dataParameters.hasOwnProperty('file')) {
            $scope.state.error = true;
            $scope.errorMessage = "The 'file' data parameter is missing.";
        } else if (!$scope.dataParameters.hasOwnProperty('contentType')) {
            $scope.state.error = true;
            $scope.errorMessage = "The 'contentType' data parameter is missing.";
        } else $scope.load();
    });
