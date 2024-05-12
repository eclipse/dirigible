/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let ideBpmHistoricProcessContextView = angular.module('ide-bpm-historic-process-context', ['ideUI', 'ideView']);

ideBpmHistoricProcessContextView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'bpm';
}]);

ideBpmHistoricProcessContextView.controller('IDEBpmHistoricProcessContextViewController', ['$scope', '$http', '$timeout', 'messageHub', function ($scope, $http, $timeout, messageHub,) {

    $scope.variablesList = [];
    $scope.currentProcessInstanceId = null;
    $scope.selectedVariable = null;

    $scope.selectionChanged = function (variable) {
        $scope.variablesList.forEach(variable => variable.selected = false);
        $scope.selectedVariable = variable;
        $scope.selectedVariable.selected = true;
    }

    $scope.reload = function () {
        console.log("Reloading data for current process instance id: " + $scope.currentProcessInstanceId)
        $scope.fetchData($scope.currentProcessInstanceId);
    };

    $scope.fetchData = function(processInstanceId) {
        $http.get('/services/ide/bpm/bpm-processes/historic-instances/' + processInstanceId + '/variables', { params: { 'limit': 100 } })
                .then((response) => {
                    $scope.variablesList = response.data;
                });
    }

    $scope.getNoDataMessage = function () {
        return 'No variables have been detected.';
    }

    messageHub.onDidReceiveMessage('historic.instance.selected', function (msg) {
        const processInstanceId = msg.data.instance;
        $scope.fetchData(processInstanceId);
        $scope.$apply(function () {
            $scope.currentProcessInstanceId = processInstanceId;
        });
    });


//sasho


}]);