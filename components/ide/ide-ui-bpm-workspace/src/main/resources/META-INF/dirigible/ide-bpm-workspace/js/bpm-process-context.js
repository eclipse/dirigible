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

    this.selectAll = false;
    this.searchText = "";
    this.filterBy = "";
    this.displaySearch = false;
    this.instancesList = [];
    this.pageSize = 10;
    this.currentPage = 1;

    this.currentFetchDataInstance = null;

    $scope.reload = function () {
        //fetchData();
    };

    this.toggleSearch = function () {
        this.displaySearch = !this.displaySearch;
    }

    messageHub.onDidReceiveMessage('instance.selected', function (msg) {
        $scope.$apply(function () {
            if (!msg.data.hasOwnProperty('instance')) {
                $scope.state.error = true;
                $scope.errorMessage = "The 'definition' parameter is missing.";
            } else {
                var processInstanceId = msg.data.instance;
                $http.get('/services/ide/bpm/bpm-processes/instance/' + processInstanceId + '/variables', { params: { 'condition': $scope.filterBy, 'limit': 100 } })
                                .then((response) => {
                                console.log("Data: " + response.data);
                                    if ($scope.instances.instancesList.length < response.data.length) {
                                        //messageHub.showAlertInfo("User instances", "A new user task has been added");
                                    }

                                    $scope.instances.instancesList = response.data;
                                });

            }
        });
    });

    this.selectAllChanged = function () {
        for (let instance of this.instancesList) {
            instance.selected = this.selectAll;
        }
    }

    this.selectionChanged = function (instance) {
        this.selectAll = this.instancesList.every(x => x.selected = false);
        messageHub.postMessage('diagram.instance', { instance: instance.id });
        messageHub.postMessage('instance.selected', { instance: instance.id });
        instance.selected = true;

    }

    this.clearSearch = function () {
        this.searchText = "";
        this.filterBy = "";
        fetchData();
    }

    this.getSelectedCount = function () {
        return this.instancesList.reduce((c, instance) => {
            if (instance.selected) c++;
            return c;
        }, 0);
    }

    this.hasSelected = function () {
        return this.instancesList.some(x => x.selected);
    }

    this.applyFilter = function () {
        this.filterBy = this.searchText;
        fetchData();
    }

    this.getNoDataMessage = function () {
        return this.filterBy ? 'No instances found.' : 'No instances have been detected.';
    }

    this.inputSearchKeyUp = function (e) {
        if (this.lastSearchKeyUp) {
            $timeout.cancel(this.lastSearchKeyUp);
            this.lastSearchKeyUp = null;
        }

        switch (e.key) {
            case 'Escape':
                this.searchText = this.filterBy || '';
                break;
            case 'Enter':
                this.applyFilter();
                break;
            default:
                if (this.filterBy !== this.searchText) {
                    this.lastSearchKeyUp = $timeout(() => {
                        this.lastSearchKeyUp = null;
                        this.applyFilter();
                    }, 250);
                }
                break;
        }
    }

    this.onPageChange = function (pageNumber) {
        fetchData({ pageNumber });
    }

    this.onItemsPerPageChange = function (itemsPerPage) {
        fetchData({ pageSize: itemsPerPage });
    }

    this.refresh = function () {
        fetchData();
    }

    this.deleteSelected = function () {
        const selectedIds = this.instancesList.reduce((ret, instance) => {
            if (instance.selected)
                ret.push(instance.id);
            return ret;
        }, []);
    }


}]);