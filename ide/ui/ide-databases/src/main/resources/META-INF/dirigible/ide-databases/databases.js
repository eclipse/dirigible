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

const databasesView = angular.module('databases', ['ideUI', 'ideView']);

databasesView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'databases-view';
}]);

databasesView.controller('DatabaseController', ['$scope', '$http', 'messageHub', function ($scope, $http, messageHub) {

    $scope.listDatabases = function () {
        $http.get('/services/v4/ide/database').then(function (response) {
            $scope.list = response.data;
        });
    }
    $scope.listDatabases();

    $scope.newDatabase = function () {
        $scope.database = {};
        $scope.isNew = true;
    }

    $scope.createDatabase = function () {
        $http.post('/services/v4/ide/database', JSON.stringify($scope.database))
            .then(function (response) {
                $scope.listDatabases();
            }, function (response) {
                console.error(response.data);
            });
    }

    $scope.editDatabase = function (database) {
        $scope.database = database;
        $scope.isNew = false;
    }

    $scope.deleteDatabase = function (database) {
        $http.delete('/services/v4/ide/database/' + database.id)
            .then(function (response) {
                $scope.listDatabases();
            }, function (response) {
                console.error(response.data);
            });
    }

    $scope.updateDatabase = function () {
        $http.put('/services/v4/ide/database/' + $scope.database.id, JSON.stringify($scope.database))
            .then(function (response) {
                $scope.listDatabases();
            }, function (response) {
                console.error(response.data);
            });
    }

}]);
