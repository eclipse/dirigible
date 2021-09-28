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
        $scope.selectAll = false;
        $scope.searchText = "";
        $scope.problemsList = [];
        $scope.limit = 25;

        function fetchData() {
            $http.get('/services/v4/ops/problems/search', {params: {'condition': $scope.searchText, 'limit': $scope.limit}}).then(function (response) {
                $scope.problemsList = response.data.result;
            });
            $scope.selectAll = false;
        }

        function refreshList() {
            $scope.searchText = "";
            $scope.limit = 25;

            fetchData();
        }

        refreshList();

        function filterSelectedIds() {
            let selectedIds = [];
            $scope.problemsList.filter(
                function (problem) {
                    if (problem.checked) {
                        selectedIds.push(problem.id)
                    }
                }
            );
            return selectedIds;
        }

        this.refresh = function () {
            refreshList();
        }

        this.nextBatch = function (size) {
            $scope.limit = $scope.limit + size;
            fetchData();
        }

        $scope.checkAll = function () {
            angular.forEach($scope.problemsList, function (problem) {
                problem.checked = $scope.selectAll;
            });
        };

        $scope.updateStatus = function (status) {
            $http.post('/services/v4/ops/problems/update/' + status, filterSelectedIds()).then(function () {
                fetchData();
                $scope.selectAll = false;
            });
        };

        $scope.deleteByStatus = function (status) {
            $http.delete('/services/v4/ops/problems/delete/' + status).then(function () {
                fetchData()
                $scope.selectAll = false;
            });
        }

        $scope.deleteSelected = function () {
            $http.post('/services/v4/ops/problems/delete/selected', filterSelectedIds()).then(function () {
                fetchData();
                $scope.selectAll = false;
            });
        }

        $scope.clear = function () {
            $http.delete('/services/v4/ops/problems/clear').then(function () {
                fetchData();
                $scope.selectAll = false;
            });
        }
    }]).config(function ($sceProvider) {
        $sceProvider.enabled(false);
    });
