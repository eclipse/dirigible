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
let bpmImageView = angular.module('bpm-image-app', ['ideUI', 'ideView']);

bpmImageView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'IDEBPMWorkspace';
}]);

bpmImageView.controller('BpmImageViewController', ['$scope', 'messageHub', function ($scope, messageHub) {
    $scope.imageLink = "";
    $scope.state = {
        isBusy: false,
        error: false,
        busyText: "Loading...",
    };

    $scope.loadImageLink = function (filename) {
        $scope.imageLink = `/services/ide/bpm/bpm-processes/${filename}/image`;
        $scope.state.isBusy = false;
    };

    messageHub.onDidReceiveMessage('image-viewer.image', function (msg) {
        $scope.$apply(function () {
            $scope.state.isBusy = true;
            if (!msg.data.hasOwnProperty('filename')) {
                $scope.state.error = true;
                $scope.errorMessage = "The 'filename' parameter is missing.";
            } else {
                $scope.state.error = false;
                $scope.loadImageLink(msg.data.filename);
            }
        });
    });
}]);