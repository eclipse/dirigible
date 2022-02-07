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
let editorView = angular.module('image-app', []);

editorView.controller('ImageViewController', ['$scope', '$http', function ($scope) {
    $scope.imageLink = "";
    $scope.dataLoaded = false;

    function getViewParameters() {
        if (window.frameElement.hasAttribute("data-parameters")) {
            let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
            $scope.file = params["file"];
        } else {
            let searchParams = new URLSearchParams(window.location.search);
            $scope.file = searchParams.get('file');
        }
    }

    function loadFileContents() {
        getViewParameters();
        if ($scope.file) {
            $scope.imageLink = '/services/v4/ide/workspaces' + $scope.file;
            $scope.dataLoaded = true;
        } else {
            console.error('file parameter is not present in the URL');
        }
    }

    loadFileContents();

}]);