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
const tasksView = angular.module('tasks', ['ideUI', 'ideView']);

tasksView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'bpm';
}]);

tasksView.controller('TasksController', ['$scope','$http', '$timeout', 'messageHub', function ($scope, $http, $timeout, messageHub) {
    $scope.tasksList = [];
    $scope.tasksListAssignee = [];
    $scope.currentProcessInstanceId;
    $scope.selectedVariable = null;

    $scope.currentFetchDataTask = null;

    $scope.reload = function () {
        console.log("Reloading user tasks for current process instance id: " + $scope.currentProcessInstanceId)
        $scope.fetchData($scope.currentProcessInstanceId);
    };

    $scope.fetchData = function(processInstanceId) {
        $http.get('/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/tasks?type=groups', { params: { 'limit': 100 } })
             .then((response) => {
                $scope.tasksList = response.data;
              });

        $http.get('/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/tasks?type=assignee', { params: { 'limit': 100 } })
             .then((response) => {
                $scope.tasksListAssignee = response.data;
              });
    }

    messageHub.onDidReceiveMessage('instance.selected', function (msg) {
        const processInstanceId = msg.data.instance;
        $scope.fetchData(processInstanceId);
        $scope.$apply(function () {
            $scope.currentProcessInstanceId = processInstanceId;
        });
    });

    $scope.selectionChanged = function (variable) {
        if (variable) {
            $scope.selectedVariable = variable;
        }
    }

    $scope.claimTask = function() {
        const apiUrl = '/services/ide/bpm/bpm-processes/tasks/' + $scope.selectedVariable.id;
        const requestBody = { };

        $http({
            method: 'POST',
            url: apiUrl,
            data: requestBody,
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then((response) => {
            console.log('Successfully claimed task with id ' + $scope.selectedVariable.id);
        })
        .catch((error) => {
            console.error('Error making POST request:', error);
        });
    }

    $scope.getNoDataMessage = function () {
        return 'No tasks found.';
    }
}]);
