/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('problems', [])
    .controller('ProblemsController', ['$scope', '$http', function ($scope, $http) {

        $http.get('../../../ops/problems').then(function(response) {
            $scope.problemsList = response.data;
        });

        $scope.updateStatus = function(status) {
            let ids = [];
            $scope.problemsList.filter(
                function (problem) {
                    if (problem.checked) {
                        ids.push(problem.id)
                    }
                }
            );
            $http.get('../../../ops/problems/update/status', {ids, status}).then(function(response) {
                $scope.problemsList = response.data;
            });
        };

        $scope.deleteByStatus = function(status) {
            $http.get('../../../ops/problems/delete/status', {status});
        }

        $scope.deleteSelected = function() {
            let ids = [];
            $scope.problemsList.filter(
                function (problem) {
                    if (problem.checked) {
                        ids.push(problem.id)
                    }
                }
            );
            $http.get('../../../ops/problems/delete/selected', {ids}).then(function(response) {
                $scope.problemsList = response.data;
            });
        }

        $scope.clear = function() {
            $http.get('../../../ops/problems/clear');
        }
    }]).config(function($sceProvider) {
    $sceProvider.enabled(false);
});