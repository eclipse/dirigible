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
let editorView = angular.module('editor-app', ['ideUI', 'ideView']);

editorView.controller('EditorViewController', ['$scope', '$window', '$http', 'messageHub', 'ViewParameters', function ($scope, $window, $http, messageHub, ViewParameters) {
    $scope.state = {
        isBusy: true,
        error: false,
        busyText: "Loading...",
    };

    $scope.integrationData = '';

    angular.element($window).bind("focus", function () {
        messageHub.setFocusedEditor($scope.dataParameters.file);
        messageHub.setStatusCaret('');
    });

    messageHub.onEditorFocusGain(function (msg) {
        if (msg.resourcePath === $scope.dataParameters.file) messageHub.setStatusCaret('');
    });

    $scope.dataParameters = ViewParameters.get();
    if (!$scope.dataParameters.hasOwnProperty('file')) {
        $scope.state.error = true;
        $scope.errorMessage = "The 'file' data parameter is missing.";
    } else {
        // Do something and don't forget to set '$scope.state.isBusy' to 'false'
        // loadFileContents();
    }

    function loadFileContents() {
        $http.get('/services/ide/workspaces' + $scope.dataParameters.file)
            .then(function (response) {
                let contents = response.data;
                if (!contents || !Array.isArray(contents)) {
                    contents = [];
                }
                $scope.integrationData = contents;
                $scope.state.isBusy = false;
            }, function (response) {
                $scope.state.error = true;
                $scope.errorMessage = "Unable to load the file. See console, for more information.";
                messageHub.setStatusError(`Error loading '${$scope.dataParameters.file}'`);
                if (response.data) {
                    if ("error" in response.data) {
                        console.error("Error loading file:", response.data.error.message);
                    }
                } else console.error("Error loading file:", response);
            });
    }
}]);
