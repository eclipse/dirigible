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

    $scope.dataParameters = ViewParameters.get();
    if (!$scope.dataParameters.hasOwnProperty('file')) {
        $scope.state.error = true;
        $scope.errorMessage = "The 'file' data parameter is missing.";
    } else {
        editorScope = $scope;
        const script = document.createElement('script');
        script.src = "designer/static/js/main.a9b4d6ee.js";
        document.getElementsByTagName('head')[0].appendChild(script);
    }
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