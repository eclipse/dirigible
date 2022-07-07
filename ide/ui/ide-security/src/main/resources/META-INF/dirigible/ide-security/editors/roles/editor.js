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
angular.module('page', [])
    .controller('PageController', function ($scope) {

        let messageHub = new FramesMessageHub();
        let contents;
        let csrfToken;

        $scope.openNewDialog = function () {
            $scope.actionType = 'new';
            $scope.entity = {};
            toggleEntityModal();
        };

        $scope.openEditDialog = function (entity) {
            $scope.actionType = 'update';
            $scope.entity = entity;
            toggleEntityModal();
        };

        $scope.openDeleteDialog = function (entity) {
            $scope.actionType = 'delete';
            $scope.entity = entity;
            toggleEntityModal();
        };

        $scope.close = function () {
            load();
            toggleEntityModal();
        };

        $scope.create = function () {
            let exists = $scope.roles.filter(function (e) {
                return e.name === $scope.entity.name;
            });
            if (exists.length === 0) {
                $scope.roles.push($scope.entity);
                toggleEntityModal();
            } else {
                $scope.error = "Role with a name [" + $scope.entity.name + "] already exists!";
            }
        };

        $scope.update = function () {
            // auto-wired
            toggleEntityModal();
        };

        $scope.delete = function () {
            $scope.roles = $scope.roles.filter(function (e) {
                return e !== $scope.entity;
            });
            toggleEntityModal();
        };

        function toggleEntityModal() {
            $('#entityModal').modal('toggle');
            $scope.error = null;
        }

        function getResource(resourcePath) {
            let xhr = new XMLHttpRequest();
            xhr.open('GET', resourcePath, false);
            xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
            xhr.send();
            if (xhr.status === 200) {
                csrfToken = xhr.getResponseHeader("x-csrf-token");
                return xhr.responseText;
            }
        }

        function loadContents(file) {
            if (file) {
                return getResource('/services/v4/ide/workspaces' + file);
            }
            console.error('file parameter is not present in the URL');
        }

        function getViewParameters() {
            if (window.frameElement.hasAttribute("data-parameters")) {
                let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
                $scope.file = params["file"];
            } else {
                let searchParams = new URLSearchParams(window.location.search);
                $scope.file = searchParams.get('file');
            }
        }

        function load() {
            getViewParameters();
            contents = loadContents($scope.file);
            $scope.roles = JSON.parse(contents);
        }

        load();

        function saveContents(text) {
            console.log('Save called...');
            if ($scope.file) {
                let xhr = new XMLHttpRequest();
                xhr.open('PUT', '/services/v4/ide/workspaces' + $scope.file);
                xhr.setRequestHeader('X-Requested-With', 'Fetch');
                xhr.setRequestHeader('X-CSRF-Token', csrfToken);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        console.log('file saved: ' + $scope.file);
                    }
                };
                xhr.send(text);
                messageHub.post({
                    name: $scope.file.substring($scope.file.lastIndexOf('/') + 1),
                    path: $scope.file.substring($scope.file.indexOf('/', 1)),
                    contentType: 'application/json+roles', // TODO: Take this from data-parameters
                    workspace: $scope.file.substring(1, $scope.file.indexOf('/', 1)),
                }, 'ide.file.saved');
                messageHub.post({ message: `File '${$scope.file}' saved` }, 'ide.status.message');
            } else {
                console.error('file parameter is not present in the request');
            }
        }

        $scope.save = function () {
            contents = angular.toJson($scope.roles);
            saveContents(contents);
        };

        messageHub.subscribe(
            function () {
                let roles = angular.toJson($scope.roles);
                if (contents !== roles) {
                    $scope.save();
                }
            },
            "editor.file.save.all"
        );

        messageHub.subscribe(
            function (msg) {
                let file = msg.data && typeof msg.data === 'object' && msg.data.file;
                let roles = angular.toJson($scope.roles);
                if (file && file === $scope.file && contents !== roles)
                    $scope.save();
            },
            "editor.file.save"
        );

        $scope.$watch(function () {
            let roles = angular.toJson($scope.roles);
            if (contents !== roles) {
                messageHub.post({ resourcePath: $scope.file, isDirty: true }, 'ide-core.setEditorDirty');
            } else {
                messageHub.post({ resourcePath: $scope.file, isDirty: false }, 'ide-core.setEditorDirty');
            }
        });

    });
