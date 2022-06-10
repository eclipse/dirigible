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
let importView = angular.module('import', ['ideUI', 'ideView', 'ideWorkspace', 'ideTransport', 'angularFileUpload']);

importView.controller('ImportViewController', [
    '$scope',
    '$window',
    'messageHub',
    'workspaceApi',
    'transportApi',
    'FileUploader',
    function (
        $scope,
        $window,
        messageHub,
        workspaceApi,
        transportApi,
        FileUploader,
    ) {
        let projectImportUrl = transportApi.getProjectImportUrl();
        $scope.selectedWorkspace = { name: 'workspace' }; // Default
        $scope.workspaceNames = [];
        $scope.inDialog = false;
        $scope.inputAccept = '.zip';
        $scope.dropAreaTitle = 'Import Projects';
        $scope.dropAreaSubtitle = 'Drop zip file(s) here, or use the "+" button.';
        $scope.dropAreaMore = '';
        $scope.uploadPath = '';
        $scope.uploader = new FileUploader({
            url: projectImportUrl
        });

        $scope.getViewParameters = function () {
            let params = JSON.parse($window.frameElement.getAttribute("data-parameters"));
            if (params.container === 'dialog') {
                $scope.inDialog = true;
                $scope.uploadPath = params.uploadPath;
                $scope.selectedWorkspace.name = params.workspace;
                if (params.importType === 'file') {
                    $scope.inputAccept = '';
                    $scope.importType = params.importType;
                    $scope.dropAreaTitle = 'Import files';
                    $scope.dropAreaSubtitle = 'Drop file(s) here, or use the "+" button.';
                    $scope.dropAreaMore = `Files will be imported in "${params.uploadPath}"`;
                } else {
                    $scope.dropAreaTitle = 'Import files from zip';
                    $scope.dropAreaMore = `Files will be extracted in "${params.uploadPath}"`;
                    let pathSegments = params.uploadPath.split('/');
                    $scope.uploader.url = new UriBuilder().path(transportApi.getZipImportUrl().split('/')).path(params.workspace).path(pathSegments).build();
                    if (pathSegments.length <= 2) $scope.uploader.url += '/%252F';
                }
            }
            else $scope.reloadWorkspaceList();
        }

        $scope.uploader.filters.push({
            name: 'customFilter',
            fn: function (item /*{File|FileLikeObject}*/, options) {
                let type = item.type.slice(item.type.lastIndexOf('/') + 1);
                if ($scope.importType !== 'file')
                    if (type != 'zip' && type != 'x-zip' && type != 'x-zip-compressed') {
                        return false;
                    }
                return this.queue.length < 100;
            }
        });

        $scope.uploader.onBeforeUploadItem = function (item) {
            if (!$scope.inDialog) {
                item.url = new UriBuilder().path(projectImportUrl.split('/')).path($scope.selectedWorkspace.name).build();
            } else if ($scope.inDialog && $scope.importType === 'file') {
                item.headers = {
                    'Dirigible-Editor': 'Editor',
                    'Content-Type': 'application/octet-stream',
                    'Content-Transfer-Encoding': 'base64'
                };
                item.url = new UriBuilder().path('/services/v4/ide/workspaces'.split('/')).path($scope.selectedWorkspace.name).path(uploadPath.split('/')).path(item.name).build();
            }
        };

        $scope.uploader.onCompleteAll = function () {
            messageHub.postMessage('ide.workspace.changed', { workspace: $scope.selectedWorkspace.name }, true);
        };

        $scope.isSelectedWorkspace = function (name) {
            if ($scope.selectedWorkspace.name === name) return true;
            return false;
        };

        $scope.canUpload = function () {
            if ($scope.uploader.getNotUploadedItems().length) return '';
            return 'disabled';
        };

        $scope.addFiles = function () {
            $window.document.getElementById('input').click();
        };

        $scope.switchWorkspace = function (workspace) {
            if ($scope.selectedWorkspace.name !== workspace) {
                $scope.selectedWorkspace.name = workspace;
            }
        };

        $scope.reloadWorkspaceList = function () {
            let userSelected = JSON.parse(localStorage.getItem('DIRIGIBLE.workspace') || '{}');
            if (!userSelected.name) {
                $scope.selectedWorkspace.name = 'workspace'; // Default
            } else {
                $scope.selectedWorkspace.name = userSelected.name;
            }
            workspaceApi.listWorkspaceNames().then(function (response) {
                if (response.status === 200)
                    $scope.workspaceNames = response.data;
                else messageHub.setStatusError('Unable to load workspace list');
            });
        };

        messageHub.onDidReceiveMessage(
            'ide.workspaces.changed',
            function () {
                $scope.reloadWorkspaceList();
            },
            true
        );

        // Initialization
        $scope.getViewParameters();
    }]);