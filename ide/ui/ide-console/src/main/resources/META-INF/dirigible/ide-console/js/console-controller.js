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
const consoleView = angular.module('console', ['ideUI', 'ideView']);

consoleView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'console-view';
}]);

consoleView.controller('ConsoleController', ['$scope', 'messageHub', function ($scope, messageHub) {
    $scope.allLogs = [];
    $scope.logs = [];
    $scope.search = { text: '' };
    $scope.autoScroll = true;

    $scope.logLevels = {
        'LOG': { enabled: true },
        'INFO': { name: 'Info', enabled: true },
        'WARN': { name: 'Warning', enabled: true },
        'ERROR': { name: 'Error', enabled: true },
        'DEBUG': { name: 'Debug', enabled: true },
        'TRACE': { name: 'Trace', enabled: true }
    };

    $scope.logLevelStatuses = {
        'INFO': 'fd-object-status--positive',
        'WARN': 'fd-object-status--critical',
        'ERROR': 'fd-object-status--negative'
    }

    $scope.clearLog = function () {
        $scope.logs = [];
        $scope.allLogs = [];
        $scope.autoScroll = true;
    };

    $scope.search = function () {
        $scope.logs = $scope.allLogs.filter(e => isLogLevelEnabled(e) && isLogContainsSearchText(e));
    };

    $scope.selectLogLevel = function () {
        $scope.logs = $scope.allLogs.filter(e => isLogLevelEnabled(e) && isLogContainsSearchText(e));
    };

    $scope.toggleLogInfoLevel = function (logLevel, e) {
        $scope.logLevels[logLevel].enabled = !$scope.logLevels[logLevel].enabled;
        $scope.selectLogLevel();

        e.preventDefault();
    }

    $scope.getLogLevelLabel = function () {
        let logType;
        let enabledLogsCount = 0;
        const logLevels = Object.values($scope.logLevels).filter(x => x.name);
        for (let logLevel of logLevels) {
            if (logLevel.enabled) {
                logType = logLevel;
                enabledLogsCount++;
            }
        }
        return enabledLogsCount === 0 ? 'Hide all' :
            enabledLogsCount === 1 ? `${logType.name} only` :
                enabledLogsCount === logLevels.length ? 'All levels' : 'Custom levels';
    }

    function isLogContainsSearchText(record) {
        if ($scope.search.text) {
            return record.message.toLowerCase().includes($scope.search.text.toLowerCase());
        }
        return true;
    }

    function isLogLevelEnabled(record) {
        const logLevel = $scope.logLevels[record.level];
        return logLevel && logLevel.enabled;
    }

    function connectToLog() {
        let logSocket = null;
        try {
            logSocket = new WebSocket(
                ((location.protocol === 'https:') ? "wss://" : "ws://")
                + window.location.host
                + window.location.pathname.substr(0, window.location.pathname.indexOf('/services/'))
                + "/websockets/v4/ide/console");
        } catch (e) {
            let record = {
                message: e.message,
                level: "ERROR",
                date: new Date().toISOString()
            };
            consoleLogMessage(record);
        }
        if (logSocket) {
            logSocket.onmessage = function (message) {
                let record = JSON.parse(message.data);
                record.date = new Date(record.timestamp).toISOString();

                consoleLogMessage(record);

                if ($scope.autoScroll) {
                    $('#logContent').animate({
                        scrollTop: $('#logContent').get(0).scrollHeight
                    }, 1000);
                }

                if (record.level === 'ERROR' || record.level === 'WARN') {
                    messageHub.setStatusError(record.message);
                }
            };

            logSocket.onerror = function (error) {
                let record = {
                    message: "Connection problem! Check security roles assignments.",
                    level: "ERROR",
                    date: new Date().toISOString()
                };
                consoleLogMessage(record);
                messageHub.setStatusError(record.message);
            };
        }
    }

    function consoleLogMessage(record) {
        $scope.allLogs.push(record);
        $scope.selectLogLevel();
        $scope.$apply();
    }

    messageHub.onPublish(function () {
        $scope.$apply();
    });

    messageHub.onUnpublish(function () {
        $scope.$apply();
    });

    let logContentEl = $('#logContent');
    logContentEl.on('scroll', function () {
        let scrollHeight = logContentEl.get(0).scrollHeight;
        let scrollPosition = logContentEl.height() + logContentEl.scrollTop();
        $scope.autoScroll = ((scrollHeight - scrollPosition) / scrollHeight === 0);
    });

    connectToLog();
}]);
