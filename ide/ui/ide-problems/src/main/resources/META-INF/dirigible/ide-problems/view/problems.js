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
        $scope.allProblems = [];
        $scope.selectAll = false;
        $scope.searchText = null;
        $scope.problemsList = [];

        function refreshList() {
            $http.get('/services/v4/ops/problems').then(function (response) {
                $scope.allProblems = response.data;
                $scope.problemsList = $scope.allProblems;
            });
            $scope.selectAll = false;
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

        function containsSearchText(problem) {
            if ($scope.searchText) {
                return problem.location.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.type.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.line.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.column.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.cause.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.expected.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.createdAt.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.createdBy.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.category.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.module.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.source.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.program.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                    problem.status.toLowerCase().includes($scope.searchText.toLowerCase());
            }
            return true;
        }

        this.refresh = function () {
            refreshList();
        }

        $scope.search = function () {
            $scope.problemsList = $scope.allProblems.filter(e => containsSearchText(e));
        };

        $scope.checkAll = function () {
            angular.forEach($scope.problemsList, function (problem) {
                problem.checked = $scope.selectAll;
            });
        };

        $scope.updateStatus = function (status) {
            $http.post('/services/v4/ops/problems/update/' + status, filterSelectedIds()).then(function (response) {
                $scope.allProblems = response.data;
                $scope.problemsList = $scope.allProblems;
                $scope.selectAll = false;
            });
        };

        $scope.deleteByStatus = function (status) {
            $http.delete('/services/v4/ops/problems/delete/' + status).then(function () {
                refreshList();
            });
        }

        $scope.deleteSelected = function () {
            $http.post('/services/v4/ops/problems/delete/selected', filterSelectedIds()).then(function () {
                refreshList();
            });
        }

        $scope.clear = function () {
            $http.delete('/services/v4/ops/problems/clear').then(function () {
                $scope.allProblems = [];
                $scope.problemsList = [];
                $scope.selectAll = false;
            });
        }
    }]).config(function ($sceProvider) {
        $sceProvider.enabled(false);
    });
