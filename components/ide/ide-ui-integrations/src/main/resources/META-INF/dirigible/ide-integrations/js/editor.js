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
let editorView = angular.module('integrations', ['ideUI', 'ideView']);

let editorScope;

editorView.controller('EditorViewController', ['$scope', '$window', 'messageHub', 'ViewParameters', function ($scope, $window, messageHub, ViewParameters) {
    $scope.state = {
        isBusy: true,
        error: false,
        busyText: "Loading...",
    };
    $scope.errorMessage = '';

    angular.element($window).bind("focus", function () {
        messageHub.setFocusedEditor($scope.dataParameters.file);
        messageHub.setStatusCaret('');
    });

    messageHub.onEditorFocusGain(function (msg) {
        if (msg.resourcePath === $scope.dataParameters.file) messageHub.setStatusCaret('');
    });

    messageHub.onEditorReloadParameters(
        function (event) {
            $scope.$apply(() => {
                if (event.resourcePath === $scope.dataParameters.file) {
                    $scope.dataParameters = ViewParameters.get();
                }
            });
        }
    );

    $scope.dataParameters = ViewParameters.get();
    if (!$scope.dataParameters.hasOwnProperty('file')) {
        $scope.state.error = true;
        $scope.errorMessage = "The 'file' data parameter is missing.";
    } else {
        editorScope = $scope;
        const script = document.createElement('script');
        script.src = "designer/static/js/main.8b055e74.js";
        document.getElementsByTagName('head')[0].appendChild(script);
    }

    $scope.saveContents = function (text) {
        let xhr = new XMLHttpRequest();
        xhr.open('PUT', '/services/ide/workspaces' + $scope.dataParameters.file);
        //xhr.setRequestHeader('X-Requested-With', 'Fetch');
        //xhr.setRequestHeader('X-CSRF-Token', csrfToken);
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
                    $scope.isFileChanged = false;
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

    window.addEventListener('keydown',
        function (event) {
            if ((event.ctrlKey || event.metaKey) && event.key == 's') {
                event.preventDefault();
                if (window.designerApp) {
                    $scope.saveContents(window.designerApp.state.yaml);
                }
            }
        }
    );
}]);

function getBaseUrl() {
    return '/services/ide/workspaces';
}

function getFileName() {
    return editorScope.dataParameters.file;
}

function setStateBusy(isBusy, text) {
    editorScope.$apply(function () {
        editorScope.state.isBusy = isBusy;
        if (text) editorScope.state.text = text;
    });
}

function setStateError(isError, message) {
    editorScope.$apply(function () {
        editorScope.state.error = isError;
        editorScope.errorMessage = message;
    });
}