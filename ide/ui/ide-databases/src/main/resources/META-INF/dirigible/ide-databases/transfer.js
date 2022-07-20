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

    let databasesSvcUrl = "/services/v4/ide/databases";
    let transferWsUrl = "/websockets/v4/ide/data/transfer";

    $scope.databases = [];
    $scope.definition = {};
    $scope.definition.selectedSourceDatabase;
    $scope.definition.selectedTargetDatabase;
    $scope.sourceDatasources;
    $scope.targetDatasources;
    $scope.definition.selectedSourceDatasource;
    $scope.definition.selectedTargetDatasource;
    $scope.sourceSchemes;
    $scope.targetSchemes;
    $scope.definition.selectedSourceScheme;
    $scope.definition.selectedTargetScheme;

    $scope.allLogs = [];
    $scope.logs = [];
    $scope.autoScroll = true;

    let logContentEl = $('#logContent');
    let transferWebsocket = null;

    $scope.logLevelStatuses = {
        'INFO': 'fd-object-status--positive',
        'WARN': 'fd-object-status--critical',
        'ERROR': 'fd-object-status--negative'
    }

    function getDatabases() {
        $http.get(databasesSvcUrl)
            .then(function (data) {
                $scope.databases = data.data;
                if ($scope.databases.length > 0) {
                    $scope.definition.selectedSourceDatabase = $scope.databases[0];
                    $scope.definition.selectedTargetDatabase = $scope.databases[0];
                    if ($scope.definition.selectedSourceDatabase) {
                        $http.get(databasesSvcUrl + "/" + $scope.definition.selectedSourceDatabase).then(function (data) {
                            $scope.sourceDatasources = data.data;
                            $scope.targetDatasources = data.data;
                            if ($scope.sourceDatasources.length > 0) {
                                $scope.definition.selectedSourceDatasource = $scope.sourceDatasources[0];
                                $scope.definition.selectedTargetDatasource = $scope.targetDatasources[0];
                                if ($scope.definition.selectedSourceDatasource) {
                                    $http.get(databasesSvcUrl + "/" + $scope.definition.selectedSourceDatabase + "/" + $scope.definition.selectedSourceDatasource).then(function (data) {
                                        $scope.sourceSchemes = data.data.schemas;
                                        $scope.targetSchemes = data.data.schemas;
                                        if ($scope.sourceSchemes.length > 0) {
                                            $scope.definition.selectedSourceScheme = $scope.sourceSchemes[0].name;
                                            $scope.definition.selectedTargetScheme = $scope.targetSchemes[0].name;

                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
    }
    setTimeout(getDatabases, 500);
    connect();

    $scope.databaseSourceChanged = function (evt) {
        $http.get(databasesSvcUrl + '/' + $scope.definition.selectedSourceDatabase)
            .then(function (data) {
                $scope.sourceDatasources = data.data;
                if ($scope.sourceDatasources[0]) {
                    $scope.definition.selectedSourceDatasource = $scope.sourceDatasources[0];
                    if ($scope.definition.selectedSourceDatasource) {
                        $http.get(databasesSvcUrl + "/" + $scope.definition.selectedSourceDatabase + "/" + $scope.definition.selectedSourceDatasource).then(function (data) {
                            $scope.sourceSchemes = data.data.schemas;
                            if ($scope.sourceSchemes.length > 0) {
                                $scope.definition.selectedSourceScheme = $scope.sourceSchemes[0];
                            }
                        });
                    }
                } else {
                    $scope.definition.selectedSourceDatasource = undefined;
                }
                //$scope.refreshDatabase();
            });
    };

    $scope.databaseTargetChanged = function (evt) {
        $http.get(databasesSvcUrl + '/' + $scope.definition.selectedTargetDatabase)
            .then(function (data) {
                $scope.targetDatasources = data.data;
                if ($scope.targetDatasources[0]) {
                    $scope.definition.selectedTargetDatasource = $scope.targetDatasources[0];
                    if ($scope.definition.selectedTargetDatasource) {
                        $http.get(databasesSvcUrl + "/" + $scope.definition.selectedTargetDatabase + "/" + $scope.definition.selectedTargetDatasource).then(function (data) {
                            $scope.targetSchemes = data.data.schemas;
                            if ($scope.targetSchemes.length > 0) {
                                $scope.definition.selectedTargetScheme = $scope.targetSchemes[0];
                            }
                        });
                    }
                } else {
                    $scope.definition.selectedDatasource = undefined;
                }
                //$scope.refreshDatabase();
            });
    };

    $scope.datasourceSourceChanged = function (evt) {
        if ($scope.definition.selectedSourceDatasource) {
            $http.get(databasesSvcUrl + "/" + $scope.definition.selectedSourceDatabase + "/" + $scope.definition.selectedSourceDatasource).then(function (data) {
                $scope.sourceSchemes = data.data.schemas;
                if ($scope.sourceSchemes.length > 0) {
                    $scope.definition.selectedSourceScheme = $scope.sourceSchemes[0];
                }
            });
        }
    };

    $scope.datasourceTargetChanged = function (evt) {
        if ($scope.definition.selectedTargetDatasource) {
            $http.get(databasesSvcUrl + "/" + $scope.definition.selectedTargetDatabase + "/" + $scope.definition.selectedTargetDatasource).then(function (data) {
                $scope.targetSchemes = data.data.schemas;
                if ($scope.targetSchemes.length > 0) {
                    $scope.definition.selectedTargetScheme = $scope.targetSchemes[0];
                }
            });
        }
    };

    $scope.startTransfer = function () {
        if ($scope.definition.selectedSourceDatabase
            && $scope.definition.selectedSourceDatasource
            && $scope.definition.selectedSourceScheme
            && $scope.definition.selectedTargetDatabase
            && $scope.definition.selectedTargetDatasource
            && $scope.definition.selectedTargetScheme) {
            let config = {
                "source": {
                    "type": $scope.definition.selectedSourceDatabase,
                    "name": $scope.definition.selectedSourceDatasource
                },
                "target": {
                    "type": $scope.definition.selectedTargetDatabase,
                    "name": $scope.definition.selectedTargetDatasource
                },
                "configuration": {
                    "sourceSchema": $scope.definition.selectedSourceScheme.name,
                    "targetSchema": $scope.definition.selectedTargetScheme.name
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
        $scope.logs = [];
    };

}]);
