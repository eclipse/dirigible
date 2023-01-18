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
    .controller('PageController', function ($scope, messageHub, $window, ViewParameters) {
        let contents;
        let csrfToken;
        $scope.errorMessage = 'Аn unknown error was encountered. Please see console for more information.';
        $scope.state = {
            isBusy: true,
            error: false,
            busyText: "Loading...",
        };
        $scope.editRoleIndex = 0;

        angular.element($window).bind("focus", function () {
            messageHub.setFocusedEditor($scope.dataParameters.file);
            messageHub.setStatusCaret('');
        });

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
                $scope.roles = JSON.parse(contents);
                contents = JSON.stringify($scope.roles, null, 4);
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
            if (!$scope.state.error) {
                $scope.state.busyText = "Saving...";
                $scope.state.isBusy = true;
                contents = JSON.stringify($scope.roles, null, 4);
                saveContents(contents);
            }
        };

        messageHub.onEditorFocusGain(function (msg) {
            if (msg.resourcePath === $scope.dataParameters.file) messageHub.setStatusCaret('');
        });

        messageHub.onDidReceiveMessage(
            "editor.file.save.all",
            function () {
                if (!$scope.state.error) {
                    let roles = JSON.stringify($scope.roles, null, 4);
                    if (contents !== roles) {
                        $scope.save();
                    }
                }
            },
            true,
        );

        messageHub.onDidReceiveMessage(
            "roleEditor.role.add",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.roles.push({
                            name: msg.data.formData[0].value,
                            description: msg.data.formData[1].value,
                        });
                    });
                }
                messageHub.hideFormDialog("rolesEditorAddRole");
            },
            true
        );

        messageHub.onDidReceiveMessage(
            "roleEditor.role.edit",
            function (msg) {
                if (msg.data.buttonId === "b1") {
                    $scope.$apply(function () {
                        $scope.roles[$scope.editRoleIndex].name = msg.data.formData[0].value;
                        $scope.roles[$scope.editRoleIndex].description = msg.data.formData[1].value;
                    });
                }
                messageHub.hideFormDialog("rolesEditorEditRole");
            },
            true
        );

        messageHub.onDidReceiveMessage(
            "editor.file.save",
            function (msg) {
                if (!$scope.state.error) {
                    let file = msg.data && typeof msg.data === 'object' && msg.data.file;
                    if (file && file === $scope.dataParameters.file) {
                        let roles = JSON.stringify($scope.roles, null, 4);
                        if (contents !== roles) $scope.save();
                    }
                }
            },
            true,
        );

        $scope.$watch('roles', function () {
            if (!$scope.state.error) {
                let roles = JSON.stringify($scope.roles, null, 4);
                messageHub.setEditorDirty($scope.dataParameters.file, contents !== roles);
            }
        }, true);

        $scope.addRole = function () {
            messageHub.showFormDialog(
                "rolesEditorAddRole",
                "Add role",
                [
                    {
                        id: "reriName",
                        type: "input",
                        label: "Name",
                        required: true,
                        placeholder: "Enter name",
                        minlength: 1,
                        maxlength: 255,
                        inputRules: {
                            patterns: ['^[a-zA-Z0-9_.-]*$'],
                        },
                        value: '',
                    },
                    {
                        id: "reriRoles",
                        type: "input",
                        label: "Description",
                        placeholder: "Enter description",
                        value: '',
                    },
                ],
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
                "roleEditor.role.add",
                "Adding role..."
            );
        };

        $scope.editRole = function (index) {
            $scope.editRoleIndex = index;
            messageHub.showFormDialog(
                "rolesEditorEditRole",
                "Edit role",
                [{
                    id: "reriName",
                    type: "input",
                    label: "Name",
                    required: true,
                    placeholder: "Enter name",
                    minlength: 1,
                    maxlength: 255,
                    inputRules: {
                        patterns: ['^[a-zA-Z0-9_.-]*$'],
                    },
                    value: $scope.roles[index].name,
                },
                {
                    id: "reriRoles",
                    type: "input",
                    label: "Description",
                    placeholder: "Enter description",
                    value: $scope.roles[index].description,
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
                "roleEditor.role.edit",
                "Updating role..."
            );
        };

        $scope.deleteRole = function (index) {
            messageHub.showDialogAsync(
                `Delete ${$scope.roles[index].name}?`,
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
                        $scope.roles.splice(index, 1);
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
