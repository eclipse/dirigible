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
let ideBpmProcessContextView = angular.module('ide-bpm-process-context', ['ideUI', 'ideView']);

ideBpmProcessContextView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'bpm';
}]);

ideBpmProcessContextView.controller('IDEBpmProcessContextViewController', ['$scope', '$http', '$timeout', 'messageHub', function ($scope, $http, $timeout, messageHub,) {

    this.variablesList = [];
    this.currentProcessInstanceId = null;

    this.reload = function () {
        console.log("Reloading data for current process instance id: " + this.currentProcessInstanceId)
        this.fetchData(this.currentProcessInstanceId);
    };

    this.fetchData = function(processInstanceId) {
        $http.get('/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/variables', { params: { 'limit': 100 } })
                .then((response) => {
                    $scope.variables.variablesList = response.data;
                });
    }

    this.getNoDataMessage = function () {
        return 'No variables have been detected.';
    }

    messageHub.onDidReceiveMessage('instance.selected', function (msg) {
        $scope.$apply(function () {
            if (!msg.data.hasOwnProperty('instance')) {
                $scope.state.error = true;
                $scope.errorMessage = "The 'definition' parameter is missing.";
            } else {
                var processInstanceId = msg.data.instance;
                $scope.variables.currentProcessInstanceId = processInstanceId;
                $scope.variables.fetchData(processInstanceId);
            }
        });
    });
}]);