/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('console', [])
.factory('$messageHub', [function () {
    var messageHub = new FramesMessageHub();
    var message = function (evtName, data) {
        messageHub.post({ data: data }, evtName);
    };
    var on = function (topic, callback) {
        messageHub.subscribe(callback, topic);
    };
    return {
        message: message,
        on: on
    };
}])
.controller('ConsoleController', ['$scope', '$messageHub', function ($scope, $messageHub) {
    $scope.allLogs = [];
    $scope.logs = [];
    $scope.showDate = true;
    $scope.searchText = null;
    $scope.logLevelLogEnabled = true;
    $scope.logLevelInfoEnabled = true;
    $scope.logLevelWarnEnabled = true;
    $scope.logLevelErrorEnabled = true;
    $scope.logLevelDebugEnabled = true;
    $scope.logLevelTraceEnabled = true;

    $scope.clearLog = function() {
        $scope.logs = [];
        $scope.allLogs = [];
    };

    $scope.search = function() {
        $scope.logs = $scope.allLogs.filter(e => e.message.toLowerCase().includes($scope.searchText.toLowerCase()));
    };

    $scope.selectLogLevel = function() {
        $scope.logs = $scope.allLogs.filter(e => {
            switch(e.level) {
                case "LOG":
                    return $scope.logLevelLogEnabled;
                case "INFO":
                    return $scope.logLevelInfoEnabled;
                case "WARN":
                    return $scope.logLevelWarnEnabled;
                case "ERROR":
                    return $scope.logLevelErrorEnabled;
                case "DEBUG":
                    return $scope.logLevelDebugEnabled;
                case "TRACE":
                    return $scope.logLevelTraceEnabled;
            }
            return e.message.toLowerCase().includes($scope.searchText.toLowerCase());
        });
    };

    function connectToLog() {
        let logSocket = null;
        try {
            logSocket = new WebSocket(
                ((location.protocol === 'https:') ? "wss://" : "ws://")
                + window.location.host
                + window.location.pathname.substr(0, window.location.pathname.indexOf('/services/'))
                + "/websockets/v4/ide/console");
        } catch(e) {
            $("#logContent").after("<div class='console-row console-row-error' style='word-wrap: break-word;'>[" + new Date().toISOString() + "][error]" + e.message + "</div>");
        }
        if (logSocket) {
            logSocket.onmessage = function (message) {
                var record = JSON.parse(message.data);
                
                record.date = new Date(record.timestamp).toISOString();

                $scope.logs.push(record);
                $scope.allLogs.push(record);
                $scope.$apply();

                // $(lastLogId).after("<div id='" + id + "' class='console-row console-row-" + record.level.toLowerCase() + "''>[" + date.toISOString() + "]" + " [" + record.level + "] " + record.message + "</div>");
                // lastLogId = "#" + id;

                // TODO Check if message hub is used properly!
                if (record.level === 'ERROR' || record.level === 'WARN') {
                    $messageHub.message(record.message, 'status.error');
                } else if (record.level === 'INFO') {
                    $messageHub.message(record.message, 'status.message');
                }
            };
            logSocket.onerror = function (error) {
                var message = 'Connection problem! Check security roles assignments.';
                $("#logContent").after("<div id='0' class='console-row console-row-error'>[" + new Date().toISOString() + "]" + " [ERROR] " + message + "</div>");
                $messageHub.message(message, 'status.error');
            };
        }
    }
    connectToLog();

    $messageHub.on('workspace.file.published', function (msg) {
        this.refresh();
        $scope.$apply();
    }.bind(this));

    $messageHub.on('workspace.file.unpublished', function (msg) {
        this.refresh();
        $scope.$apply();
    }.bind(this));

    $scope.cancel = function (e) {
        if (e.keyCode === 27) {
            $scope.previewForm.preview.$rollbackViewValue();
        }
    };

}]).config(function ($sceProvider) {
    $sceProvider.enabled(false);
});
