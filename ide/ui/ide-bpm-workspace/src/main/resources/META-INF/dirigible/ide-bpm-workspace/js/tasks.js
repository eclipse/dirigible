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
    messageHubProvider.eventIdPrefix = 'tasks-view';
}]);

tasksView.controller('TasksController', ['$http', '$timeout', 'messageHub', function ($http, $timeout, messageHub) {
    this.selectAll = false;
    this.searchText = "";
    this.filterBy = "";
    this.displaySearch = false;
    this.tasksList = [];
    this.pageSize = 10;
    this.currentPage = 1;

    this.currentFetchDataTask = null;

    const fetchData = (args) => {
        if (this.currentFetchDataTask) {
            clearInterval(this.currentFetchDataTask);
        }

        this.currentFetchDataTask = setInterval(() => {
                const pageNumber = (args && args.pageNumber) || this.currentPage;
                const pageSize = (args && args.pageSize) || this.pageSize;
                const limit = pageNumber * pageSize;
                const startIndex = (pageNumber - 1) * pageSize;
                if (startIndex >= this.totalRows) {
                    return;
                }

                $http.get('/services/v4/js/ide-bpm-workspace/api/tasks.mjs/tasks', { params: { 'condition': this.filterBy, 'limit': limit } })
                    .then((response) => {
                        if (this.tasksList.length < response.data.length ) {
                            messageHub.showAlertInfo("User tasks", "A new user task has been added");
                        }

                        this.tasksList = response.data;
                        this.selectionChanged();
                    });
        }, 1000);

    }

    fetchData();

    this.toggleSearch = function () {
        this.displaySearch = !this.displaySearch;
    }

    this.selectAllChanged = function () {
        for (let task of this.tasksList) {
            task.selected = this.selectAll;
        }
    }

    this.selectionChanged = function () {
        this.selectAll = this.tasksList.every(x => x.selected);
    }

    this.clearSearch = function () {
        this.searchText = "";
        this.filterBy = "";
        fetchData();
    }

    this.getSelectedCount = function () {
        return this.tasksList.reduce((c, task) => {
            if (task.selected) c++;
            return c;
        }, 0);
    }

    this.hasSelected = function () {
        return this.tasksList.some(x => x.selected);
    }

    this.applyFilter = function () {
        this.filterBy = this.searchText;
        fetchData();
    }

    this.getNoDataMessage = function () {
        return this.filterBy ? 'No tasks found.' : 'No tasks have been detected.';
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
        const selectedIds = this.tasksList.reduce((ret, task) => {
            if (task.selected)
                ret.push(task.id);
            return ret;
        }, []);
    }

    this.showInfo = function (task) {
        messageHub.showDialogWindow(
            "task-details",
            { taskDetails: task }
        );
    }
}]);
