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
const resultView = angular.module('result', ['ideUI', 'ideView']);

resultView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'result-view';
}]);

resultView.controller('DatabaseResultController', ['$scope', '$http', 'messageHub', function ($scope, $http, messageHub) {
    $scope.state = {
        isBusy: false,
        error: false,
        busyText: "Loading...",
    };
    $scope.tabNumber = 0;

    $scope.tabClicked = function (tab) {
        $scope.tabNumber = tab;
    };

    let csrfToken = null;

    $scope.procedureResults = [];
    $scope.hasMultipleProcedureResults = false;

    $http.get("", {headers: {"X-CSRF-Token": "Fetch"}}).then(function (response) {
        csrfToken = response.headers()["x-csrf-token"];
    }, function (response) {
        console.error("Error getting token.", response);
    });

    // $scope.database = "metadata";
    $scope.datasource = "DefaultDB";

    messageHub.onDidReceiveMessage("database.database.selection.changed", function (evt) {
        $scope.database = evt.data;
    }, true);

    messageHub.onDidReceiveMessage("database.datasource.selection.changed", function (evt) {
        $scope.datasource = evt.data;
    }, true);

    messageHub.onDidReceiveMessage("database.sql.execute", function (command) {
        $scope.state.error = false;
        $scope.state.isBusy = true;
        let url = "/services/data/" + $scope.datasource;
        let sql = command.data.trim().toLowerCase();
        if (sql.startsWith('select')) {
            $http({
                method: 'POST',
                url: url + "/query",
                data: command.data,
                headers: {
                    'Content-Type': 'text/plain',
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken
                }
            }).then(function (result) {
                cleanScope();
                if (result.data != null && result.data.length > 0) {
                    $scope.rows = result.data;
                    $scope.columns = [];
                    for (let i = 0; i < result.data.length; i++) {
                        for (let column in result.data[i]) {
                            $scope.columns.push(column);
                        }
                        break;
                    }
                } else if (result.data !== null && result.data.errorMessage !== null && result.data.errorMessage !== undefined) {
                    $scope.state.error = true;
                    $scope.errorMessage = result.data.errorMessage;
                } else {
                    $scope.result = 'Empty result';
                }
                $scope.state.isBusy = false;
            });
        } else if (sql.startsWith('call')) {
            $http({
                method: 'POST',
                url: url + "/procedure",
                data: command.data,
                headers: {
                    'Content-Type': 'text/plain',
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken
                }
            }).then(function (result) {
                cleanScope();
                if (result.data != null && result.data.length > 0) {
                    $scope.hasMultipleProcedureResults = result.data.length > 1;
                    if ($scope.hasMultipleProcedureResults) {
                        $scope.procedureResults.length = 0;
                        for (let resultIndex = 0; resultIndex < result.data.length; resultIndex++) {
                            let procedureResult = JSON.parse(result.data[resultIndex]);
                            let data = {
                                rows: procedureResult,
                                columns: []
                            };
                            for (let i = 0; i < procedureResult.length; i++) {
                                for (let column in procedureResult[i]) {
                                    data.columns.push(column);
                                }
                                break;
                            }
                            $scope.procedureResults.push(data);
                        }
                    } else {
                        result = JSON.parse(result.data[0]);
                        $scope.rows = result;
                        $scope.columns = [];
                        for (let i = 0; i < result.length; i++) {
                            for (let column in result[i]) {
                                $scope.columns.push(column);
                            }
                            break;
                        }
                    }
                } else if (result.data !== null && result.data.errorMessage !== null && result.data.errorMessage !== undefined) {
                    $scope.state.error = true;
                    $scope.errorMessage = result.data.errorMessage;
                } else {
                    $scope.result = 'Empty result';
                }
                $scope.state.isBusy = false;
            });
        } else if (sql.startsWith('query: ')) {
            $http({
                method: 'POST',
                url: url + "/query",
                data: command.data.substring(7).trim(),
                headers: {
                    'Content-Type': 'text/plain',
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken
                }
            }).then(function (result) {
                cleanScope();
                if (result.data != null && result.data.length > 0) {
                    $scope.rows = result.data;
                    $scope.columns = [];
                    for (let i = 0; i < result.data.length; i++) {
                        for (let column in result.data[i]) {
                            $scope.columns.push(column);
                        }
                        break;
                    }
                } else if (result.data !== null && result.data.errorMessage !== null && result.data.errorMessage !== undefined) {
                    $scope.state.error = true;
                    $scope.errorMessage = result.data.errorMessage;
                } else {
                    $scope.result = 'Empty result';
                }
                $scope.state.isBusy = false;
            });
        } else if (sql.startsWith('update: ')) {
            $http({
                method: 'POST',
                url: url + "/update",
                data: command.data.substring(8).trim(),
                headers: {
                    'Content-Type': 'text/plain',
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken
                }
            }).then(function (result) {
                cleanScope();
                if (!isNaN(result.data)) {
                    result = 'Rows updated: ' + result.data;
                } else if (result.data !== null) {
                    $scope.result = result.data;
                } else {
                    $scope.result = 'Empty result';
                }
                $scope.result = result.data;
                $scope.state.isBusy = false;
            });
        } else {
            $http({
                method: 'POST',
                url: url + "/update",
                data: command.data,
                headers: {
                    'Content-Type': 'text/plain', 'Accept': 'text/plain',
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken
                }
            }).then(function (result) {
                cleanScope();
                if (!isNaN(result.data)) {
                    result = 'Rows updated: ' + result.data;
                } else if (result.data !== null) {
                    $scope.result = result.data;
                } else {
                    $scope.result = 'Empty result';
                }
                $scope.result = result.data;
                $scope.state.isBusy = false;
            });
        }
    }, true);

	messageHub.onDidReceiveMessage("database.data.import.artifact", function (command) {
        let artifact = command.data.split('.');
        let url = "/services/data/import/" + $scope.datasource + "/" + artifact[0] + "/" + artifact[1];
        messageHub.showDialogWindow(
            "import",
            {
                importType: 'data',
                uploadPath: url,
                workspace: "", 
                table: $scope.datasource + " -> " + artifact[0] + " -> " + artifact[1],
            }
        );
    }, true);

    messageHub.onDidReceiveMessage("database.data.export.artifact", function (command) {
        let artifact = command.data.split('.');
        let url = "/services/data/export/" + $scope.datasource + "/" + artifact[0] + "/" + artifact[1];
        window.open(url);
    }, true);

    messageHub.onDidReceiveMessage("database.data.export.schema", function (command) {
        let schema = command.data;
        let url = "/services/data/export/" + $scope.datasource + "/" + schema;
        window.open(url);
    }, true);

    messageHub.onDidReceiveMessage("database.data.project.export.schema", function (command) {
        let schema = command.data;
        let url = "/services/data/project/csv/" + $scope.datasource + "/" + schema;
        $http({
            method: 'PUT',
            url: url,
            headers: {
                'X-Requested-With': 'Fetch',
                'X-CSRF-Token': csrfToken
            }
        }).then(function (resourceURI) {
            let fileURL= window.location.protocol + '//' + window.location.host + `/services/ide/workspaces/${schema}/${schema}`
            let msg = `Created requested files in Project [${schema}] in Workspace [${schema}]. \n To access it it please go to: ${fileURL}`;

            console.info(msg);

            messageHub.showDialog('', msg);
        }).catch(function (err) {
            console.error("Error in exporting data in project", err);
        });
    }, true);

    messageHub.onDidReceiveMessage("database.metadata.export.artifact", function (command) {
        let artifact = command.data.split('.');
        let url = "/services/data/definition/" + $scope.datasource + "/" + artifact[0] + "/" + artifact[1];
        window.open(url);
    }, true);

    messageHub.onDidReceiveMessage("database.metadata.export.schema", function (command) {
        let schema = command.data;
        let url = "/services/data/definition/" + $scope.datasource + "/" + schema;
        window.open(url);
    }, true);

    messageHub.onDidReceiveMessage("database.metadata.project.export.schema", function (command) {
        let schema = command.data;
        let url = "/services/data/project/metadata/" + $scope.datasource + "/" + schema;
        $http({
            method: 'PUT',
            url: url,
            headers: {
                'X-Requested-With': 'Fetch',
                'X-CSRF-Token': csrfToken
            }
        }).then(function (resourceURI) {
            let fileURL= window.location.protocol + '//' + window.location.host + `/services/ide/workspaces/${schema}/${schema}/${schema}.schema`
            let msg = `Created file [${schema}.schema] in Project [${schema}] in Workspace [${schema}]. \n To access it it please go to: ${fileURL}`;

            console.info(msg);

            messageHub.showDialog('', msg);
        }).catch(function (err) {
            console.error("Error in exporting metadata in project", err);
        });
    }, true);
    
    messageHub.onDidReceiveMessage("database.metadata.project.export.topology", function (command) {
        let schema = command.data;
        let url = "/services/data/project/topology/" + $scope.datasource + "/" + schema;
        window.open(url);
    }, true);
    
    messageHub.onDidReceiveMessage("database.data.anonymize.column", function (command) {
        let url = "/services/data/anonymize/column";
        $http({
                method: 'POST',
                url: url,
                data: command.data,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'Fetch',
                    'X-CSRF-Token': csrfToken
                }
            }).then(function (result) {
                cleanScope();
                if (result.data !== null && result.data.errorMessage !== null && result.data.errorMessage !== undefined) {
                    $scope.state.error = true;
                    $scope.errorMessage = result.data.errorMessage;
                } else {
                    $scope.result = 'Data anonymized.';
                }
                $scope.state.isBusy = false;
            }, function (result) {
                
                $scope.state.error = true;
                $scope.errorMessage = result.data.errorMessage;
                
                $scope.state.isBusy = false;
            });
        
    }, true);

    function cleanScope() {
        $scope.result = null;
        $scope.columns = null;
        $scope.rows = null;
        $scope.hasMultipleProcedureResults = false;
        $scope.procedureResults.length = 0;
    }

}]);
