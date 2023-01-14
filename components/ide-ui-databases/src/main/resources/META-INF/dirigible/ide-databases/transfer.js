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

const transferView = angular.module('transfer', ['ideUI', 'ideView']);

transferView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'transfer-view';
}]);

transferView.controller('TransferController', ['$scope', '$http', 'messageHub', function ($scope, $http, messageHub) {

    let databasesSvcUrl = "/services/v8/ide/databases";
    let transferWsUrl = "/websockets/v8/ide/data/transfer";

    $scope.databases = [];
    $scope.definition = {};
    $scope.definition.selectedSourceDatabase = 0;
    $scope.definition.selectedTargetDatabase = 0;
    $scope.sourceDatasources = [];
    $scope.targetDatasources = [];
    $scope.definition.selectedSourceDatasource = 0;
    $scope.definition.selectedTargetDatasource = 0;
    $scope.sourceSchemes = [];
    $scope.targetSchemes = [];
    $scope.definition.selectedSourceScheme = 0;
    $scope.definition.selectedTargetScheme = 0;

    $scope.allLogs = [];
    $scope.logs = [];
    $scope.autoScroll = true;

    let transferWebsocket = null;

    $scope.logLevelStatuses = {
        'INFO': 'fd-object-status--positive',
        'WARN': 'fd-object-status--critical',
        'ERROR': 'fd-object-status--negative'
    }

    $scope.test = function () {
        $scope.definition.selectedSourceDatabase = 1;
    };

    $scope.getDatabases = function () {
        $http.get(databasesSvcUrl)
            .then(function (data) {
                if (data.data.length > 0) {
                    for (let i = 0; i < data.data.length; i++) {
                        $scope.databases.push({ text: data.data[i], value: i });
                    }
                    if ($scope.databases[$scope.definition.selectedSourceDatabase]) {
                        $http.get(databasesSvcUrl + "/" + $scope.databases[$scope.definition.selectedSourceDatabase].text).then(function (sourceData) {
                            if (sourceData.data.length > 0) {
                                for (let i = 0; i < sourceData.data.length; i++) {
                                    $scope.sourceDatasources.push({ text: sourceData.data[i], value: i });
                                    $scope.targetDatasources.push({ text: sourceData.data[i], value: i });
                                }
                                if ($scope.sourceDatasources[$scope.definition.selectedSourceDatasource].text) {
                                    $http.get(databasesSvcUrl + "/" + $scope.databases[$scope.definition.selectedSourceDatabase].text + "/" + $scope.sourceDatasources[$scope.definition.selectedSourceDatasource].text).then(function (schemaData) {
                                        $scope.sourceSchemes = schemaData.data.schemas;
                                        $scope.targetSchemes = schemaData.data.schemas;
                                        if ($scope.sourceSchemes.length > 0) {
                                            for (let i = 0; i < $scope.sourceSchemes.length; i++) {
                                                $scope.sourceSchemes[i]['value'] = i;
                                                $scope.targetSchemes[i]['value'] = i;
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
    }
    setTimeout($scope.getDatabases, 500); // This is absolutely awful. Must be fixed.
    connect();

    $scope.databaseSourceChanged = function () {
        $http.get(databasesSvcUrl + '/' + $scope.databases[$scope.definition.selectedSourceDatabase].text)
            .then(function (data) {
                $scope.sourceDatasources.length = 0;
                for (let i = 0; i < data.data.length; i++) {
                    $scope.sourceDatasources.push({ text: data.data[i], value: i });
                }
                if ($scope.sourceDatasources.length > 0) {
                    $scope.definition.selectedSourceDatasource = 0;
                    if ($scope.sourceDatasources[$scope.definition.selectedSourceDatasource]) {
                        $http.get(databasesSvcUrl + "/" + $scope.databases[$scope.definition.selectedSourceDatabase].text + "/" + $scope.sourceDatasources[$scope.definition.selectedSourceDatasource].text).then(function (sdata) {
                            $scope.sourceSchemes = sdata.data.schemas;
                            $scope.definition.selectedSourceScheme = 0;
                            for (let i = 0; i < $scope.sourceSchemes.length; i++) {
                                $scope.sourceSchemes[i]['value'] = i;
                            }
                        });
                    }
                } else {
                    $scope.definition.selectedSourceDatasource = undefined;
                }
                //$scope.refreshDatabase();
            });
    };

    $scope.databaseTargetChanged = function () {
        $http.get(databasesSvcUrl + '/' + $scope.databases[$scope.definition.selectedTargetDatabase].text)
            .then(function (data) {
                $scope.targetDatasources.length = 0;
                for (let i = 0; i < data.data.length; i++) {
                    $scope.targetDatasources.push({ text: data.data[i], value: i });
                }
                if ($scope.targetDatasources.length > 0) {
                    $scope.definition.selectedTargetDatasource = 0;
                    if ($scope.targetDatasources[$scope.definition.selectedTargetDatasource]) {
                        $http.get(databasesSvcUrl + "/" + $scope.databases[$scope.definition.selectedTargetDatabase].text + "/" + $scope.targetDatasources[$scope.definition.selectedTargetDatasource].text).then(function (sdata) {
                            $scope.targetSchemes = sdata.data.schemas;
                            $scope.definition.selectedTargetScheme = 0;
                            for (let i = 0; i < $scope.targetSchemes.length; i++) {
                                $scope.targetSchemes[i]['value'] = i;
                            }
                        });
                    }
                } else {
                    $scope.definition.selectedDatasource = undefined;
                }
                //$scope.refreshDatabase();
            });
    };

    $scope.datasourceSourceChanged = function () {
        if ($scope.sourceDatasources[$scope.definition.selectedSourceDatasource]) {
            $http.get(databasesSvcUrl + "/" + $scope.databases[$scope.definition.selectedSourceDatabase].text + "/" + $scope.sourceDatasources[$scope.definition.selectedSourceDatasource].text).then(function (data) {
                $scope.sourceSchemes = data.data.schemas;
                $scope.definition.selectedSourceScheme = 0;
                for (let i = 0; i < $scope.sourceSchemes.length; i++) {
                    $scope.sourceSchemes[i]['value'] = i;
                }
            });
        }
    };

    $scope.datasourceTargetChanged = function () {
        if ($scope.targetDatasources[$scope.definition.selectedTargetDatasource]) {
            $http.get(databasesSvcUrl + "/" + $scope.databases[$scope.definition.selectedTargetDatabase].text + "/" + $scope.targetDatasources[$scope.definition.selectedTargetDatasource].text).then(function (data) {
                $scope.targetSchemes = data.data.schemas;
                $scope.definition.selectedTargetScheme = 0;
                for (let i = 0; i < $scope.targetSchemes.length; i++) {
                    $scope.targetSchemes[i]['value'] = i;
                }
            });
        }
    };

    $scope.startTransfer = function () {
        if ($scope.databases[$scope.definition.selectedSourceDatabase]
            && $scope.sourceDatasources[$scope.definition.selectedSourceDatasource]
            && $scope.sourceSchemes[$scope.definition.selectedSourceScheme]
            && $scope.databases[$scope.definition.selectedTargetDatabase]
            && $scope.targetDatasources[$scope.definition.selectedTargetDatasource]
            && $scope.targetSchemes[$scope.definition.selectedTargetScheme]) {
            let config = {
                "source": {
                    "type": $scope.databases[$scope.definition.selectedSourceDatabase].text,
                    "name": $scope.sourceDatasources[$scope.definition.selectedSourceDatasource].text
                },
                "target": {
                    "type": $scope.databases[$scope.definition.selectedTargetDatabase].text,
                    "name": $scope.targetDatasources[$scope.definition.selectedTargetDatasource].text
                },
                "configuration": {
                    "sourceSchema": $scope.sourceSchemes[$scope.definition.selectedSourceScheme].name,
                    "targetSchema": $scope.targetSchemes[$scope.definition.selectedTargetScheme].name
                }
            };

            transferData(config);
        }
    };

    function transferData(config) {
        $scope.clearLog();
        let message = JSON.stringify(config);
        transferWebsocket.send(message);
    }

    function connect() {

        try {
            transferWebsocket = new WebSocket(
                ((location.protocol === 'https:') ? "wss://" : "ws://")
                + window.location.host
                + window.location.pathname.substr(0, window.location.pathname.indexOf('/services/'))
                + transferWsUrl);
        } catch (e) {
            let record = {
                message: e.message,
                level: "ERROR",
                date: new Date().toISOString()
            };
            consoleLogMessage(record);
        }
        if (transferWebsocket) {
            transferWebsocket.onmessage = function (message) {
                let level = "INFO";
                if (message.data.indexOf("[ERROR]") >= 0) {
                    level = "ERROR";
                } else if (message.data.indexOf("[WARNING]") >= 0) {
                    level = "WARN";
                }
                let record = {
                    message: message.data,
                    level: level,
                    date: new Date().toISOString()
                };
                consoleLogMessage(record);

                if (level === "ERROR" || level === "WARN") {
                    messageHub.setStatusError(message.data);
                }

            };

            transferWebsocket.onerror = function (error) {
                let record = {
                    message: error.data,
                    level: "ERROR",
                    date: new Date().toISOString()
                };
                consoleLogMessage(record);
                messageHub.setStatusError(error.data);
            };

        }
    }

    function consoleLogMessage(record) {
        $scope.logs.push(record);
        $scope.$apply();
    }

    $scope.clearLog = function () {
        $scope.logs.length = 0;
    };

}]);
