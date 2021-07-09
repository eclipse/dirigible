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
        $scope.logs = $scope.allLogs.filter(e => isLogLevelEnabled(e) && isLogContainsSearchText(e));
    };

    $scope.selectLogLevel = function() {
        $scope.logs = $scope.allLogs.filter(e => isLogLevelEnabled(e) && isLogContainsSearchText(e));
    };

    function isLogContainsSearchText(record) {
        if ($scope.searchText) {
            return record.message.toLowerCase().includes($scope.searchText.toLowerCase());
        }
        return true;
    }

    function isLogLevelEnabled(record) {
        switch(record.level) {
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
    }
    function connectToLog() {
        let logSocket = null;
        try {
            logSocket = new WebSocket(
                ((location.protocol === 'https:') ? "wss://" : "ws://")
                + window.location.host
                + window.location.pathname.substr(0, window.location.pathname.indexOf('/services/'))
                + "/websockets/v4/ide/console");
        } catch(e) {
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

                $('#logContent').animate({
                    scrollBottom: $('#logContent').get(0).scrollHeight
                }, 2000);

                if (record.level === 'ERROR' || record.level === 'WARN') {
                    $messageHub.message('status.error', record.message);
                } else if (record.level === 'INFO') {
                    $messageHub.message('status.message', record.message);
                }
            };

            logSocket.onerror = function (error) {
                let record = {
                    message: "Connection problem! Check security roles assignments.",
                    level: "ERROR",
                    date: new Date().toISOString()
                };
                consoleLogMessage(record);
                $messageHub.message('status.error', record.message);
            };
        }
    }
    connectToLog();

    function consoleLogMessage(record) {
        $scope.allLogs.push(record);
        $scope.selectLogLevel();
        $scope.$apply();
        window.scrollBy(0, 100)
    }

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
