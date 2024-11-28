/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
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
    $scope.isCreateDialogOpen = false;
    $scope.isEditDialogOpen = false;
    $scope.isDeleteDialogOpen = false;
    $scope.schemaName = null;
    $scope.tableName = null;
    $scope.selectedRow = null;
    $scope.selectedRowOriginal = null;

    const CrudService = require('./crud.js');

    $scope.createRow = function () {
        $scope.newRowData = {};
        $scope.isCreateDialogOpen = true;
    };

    $scope.confirmCreate = function () {
        if (!$scope.newRowData) {
            console.error("No data provided for the new row.");
            return;
        }

        $scope.tableColumns = $scope.tableColumns || [];

        const columns = [];
        const parameters = [];

        $scope.tableColumns.forEach((column) => {
            if ($scope.newRowData[column] !== undefined) {
                columns.push(`"${column}"`);
                parameters.push($scope.newRowData[column]);
            }
        });

        if (columns.length === 0) {
            console.error("No valid columns found for the new row.");
            return;
        }

        const sqlCommand = `
        INSERT INTO "${$scope.schemaName}"."${$scope.tableName}"
        (${columns.join(", ")})
        VALUES (${columns.map(() => "?").join(", ")})
    `;

        CrudService.executeQuery({ query: sqlCommand, parameters })
            .then(() => {
                $scope.isCreateDialogOpen = false;

                // Refresh the table
                messageHub.postMessage('database.sql.showContent', {
                    schemaName: $scope.schemaName,
                    tableName: $scope.tableName
                });
            })
            .catch((error) => {
                console.error("Failed to create row:", error);
            });
    };


    $scope.editRow = function (row) {
        console.log("Row object:", row);
        $scope.selectedRow = angular.copy(row);
        $scope.selectedRowOriginal = angular.copy(row);

        console.log("Selected Row:", $scope.selectedRow);

        $scope.isEditDialogOpen = true;
    };


    $scope.saveRow = function () {
        if (!$scope.selectedRow) {
            console.error("No row selected for editing.");
            return;
        }

        const updatedData = $scope.selectedRow;

        $scope.primaryKeyColumns = $scope.primaryKeyColumns || [];
        const parameters = [];
        const setClauses = [];

        Object.keys(updatedData).forEach((key) => {
            if (!$scope.primaryKeyColumns.includes(key)) {
                setClauses.push(`"${key}" = ?`);
                parameters.push(updatedData[key]);
            }
        });

        if (setClauses.length === 0) {
            console.log("No fields to update.");
            return;
        }

        const whereClauses = [];
        $scope.primaryKeyColumns.forEach((key) => {
            whereClauses.push(`"${key}" = ?`);
            parameters.push(updatedData[key]);
        });

        if (whereClauses.length === 0) {
            console.error("No primary key columns found for the table.");
            return;
        }

        const sqlCommand = `
        UPDATE "${$scope.schemaName}"."${$scope.tableName}"
        SET ${setClauses.join(", ")}
        WHERE ${whereClauses.join(" AND ")}
    `;

        CrudService.executeQuery({ query: sqlCommand, parameters })
            .then(() => {
                $scope.isEditDialogOpen = false;

                // Refresh the table
                messageHub.postMessage('database.sql.showContent', {
                    schemaName: $scope.schemaName,
                    tableName: $scope.tableName
                });
            })
            .catch((error) => {
                console.error("Failed to create row:", error);
            });
    };

    $scope.closeEditDialog = function () {
        $scope.isEditDialogOpen = false;

    };

    $scope.openDeleteDialog = function (row) {
        $scope.selectedRow = angular.copy(row);
        $scope.isDeleteDialogOpen = true;
    };

    $scope.confirmDelete = function () {
        if (!$scope.selectedRow) {
            console.error("No row selected for deletion.");
            return;
        }

        $scope.primaryKeyColumns = $scope.primaryKeyColumns || [];

        const whereClauses = [];
        const parameters = [];
        $scope.primaryKeyColumns.forEach((key) => {
            if ($scope.selectedRow[key] !== undefined) {
                whereClauses.push(`"${key}" = ?`);
                parameters.push($scope.selectedRow[key]);
            }
        });

        if (whereClauses.length === 0) {
            console.error("No primary key columns found for the table.");
            return;
        }

        const sqlCommand = `
        DELETE FROM "${$scope.schemaName}"."${$scope.tableName}"
        WHERE ${whereClauses.join(" AND ")}
    `;

        executeQuery({
            data: { query: sqlCommand, parameters: parameters }
        });

        $scope.isDeleteDialogOpen = false;

        messageHub.postMessage('database.sql.showContent', {
            schemaName: $scope.schemaName,
            tableName: $scope.tableName
        });
    };


    $scope.closeDeleteDialog = function () {
        $scope.selectedRow = null;
        $scope.isDeleteDialogOpen = false;

    };

    function extractSpecialAndPrimaryKeys(tableMetadata) {
        const specialColumns = [];
        const primaryKeyColumns = [];

        tableMetadata.columns.forEach(column => {
            const type = column.type.toUpperCase();
            if (type === 'BLOB' || type === 'CLOB') {
                specialColumns.push(column.name);
            }

            if (column.primaryKey) {
                primaryKeyColumns.push(column.name);
            }
        });

        return { specialColumns, primaryKeyColumns };
    }

    $http.get("", { headers: { "X-CSRF-Token": "Fetch" } }).then(function (response) {
        csrfToken = response.headers()["x-csrf-token"];
    }, function (response) {
        console.error("Error getting token.", response);
    });

    // $scope.database = "metadata";
    // TODO get it from configuration
    $scope.datasource = "DefaultDB";

    messageHub.onDidReceiveMessage("database.database.selection.changed", function (evt) {
        $scope.database = evt.data;
    }, true);

    messageHub.onDidReceiveMessage("database.datasource.selection.changed", function (evt) {
        $scope.datasource = evt.data;
    }, true);

    $scope.showProgress = function () {
        $scope.state.isBusy = true;
        messageHub.showStatusBusy("Executing query...");
    };

    $scope.hideProgress = function () {
        $scope.state.isBusy = false;
        messageHub.hideStatusBusy();
        $scope.$apply();
    };

    function executeQuery(command) {

        $scope.state.error = false;
        $scope.showProgress();
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
            }).then(
                function (result) {
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
                    $scope.hideProgress();
                }, function (reject) {
                    cleanScope();
                    $scope.state.error = true;
                    $scope.errorMessage = reject.data.message;
                    console.error(reject);
                    $scope.hideProgress();
                }
            );
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
            }).then(
                function (result) {
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
                    $scope.hideProgress();
                }, function (reject) {
                    cleanScope();
                    $scope.state.error = true;
                    $scope.errorMessage = reject.data.message;
                    console.error(reject);
                    $scope.hideProgress();
                }
            );
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
            }).then(
                function (result) {
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
                    $scope.hideProgress();
                }, function (reject) {
                    cleanScope();
                    $scope.state.error = true;
                    $scope.errorMessage = reject.data.message;
                    console.error(reject);
                    $scope.hideProgress();
                }
            );
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
            }).then(
                function (result) {
                    cleanScope();
                    if (!isNaN(result.data)) {
                        result = 'Rows updated: ' + result.data;
                    } else if (result.data !== null) {
                        $scope.result = result.data;
                    } else {
                        $scope.result = 'Empty result';
                    }
                    $scope.result = result.data;
                    $scope.hideProgress();
                }, function (reject) {
                    cleanScope();
                    $scope.state.error = true;
                    $scope.errorMessage = reject.data.message;
                    console.error(reject);
                    $scope.hideProgress();
                }
            );
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
            }).then(
                function (result) {
                    cleanScope();
                    if (!isNaN(result.data)) {
                        result = 'Rows updated: ' + result.data;
                    } else if (result.data !== null) {
                        $scope.result = result.data;
                    } else {
                        $scope.result = 'Empty result';
                    }
                    $scope.result = result.data;
                    $scope.hideProgress();
                }, function (reject) {
                    cleanScope();
                    $scope.state.error = true;
                    $scope.errorMessage = reject.data.message;
                    console.error(reject);
                    $scope.hideProgress();
                }
            );
        }
    }

    messageHub.onDidReceiveMessage("database.sql.showContent", function (event) {

        let data = event.data;
        $scope.schemaName = data.schemaName;
        $scope.tableName = data.tableName;
        let sqlCommand = "SELECT * FROM \"" + data.schemaName + "\"" + "." + "\"" + data.tableName + "\";\n";
        executeQuery({ data: sqlCommand });
        $http.get("/services/data/definition/" + $scope.datasource + '/' + $scope.schemaName
            + '/' + $scope.tableName)
            .then(function (response) {
                $scope.metadata = response.data; // Set the metadata once the response is received
                console.log("Metadata fetched:", $scope.metadata);
                // Extract primary key columns and special columns from the metadata
                const extractedKeys = extractSpecialAndPrimaryKeys($scope.metadata);
                $scope.primaryKeyColumns = extractedKeys.primaryKeyColumns;
                $scope.specialColumns = extractedKeys.specialColumns;
            })
            .catch(function (error) {
                console.error("Error fetching metadata:", error);
            });
    });

    messageHub.onDidReceiveMessage("database.sql.execute", executeQuery, true);

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
            let fileURL = window.location.protocol + '//' + window.location.host + `/services/ide/workspaces/${schema}/${schema}`
            let msg = `Created requested files in Project [${schema}] in Workspace [${schema}]. \n To access it it please go to: ${fileURL}`;
            console.info(msg);
            messageHub.showAlertSuccess('Export', msg);
        }).catch(function (err) {
            messageHub.showAlertError("Export error", "Error in exporting data in project");
            console.error("Error in exporting data in project", err);
        });
    }, true);

    messageHub.onDidReceiveMessage("database.metadata.project.export.model", function (command) {
        let schema = command.data;
        let url = "/services/data/project/model/" + $scope.datasource + "/" + schema;
        $http({
            method: 'PUT',
            url: url,
            headers: {
                'X-Requested-With': 'Fetch',
                'X-CSRF-Token': csrfToken
            }
        }).then(function (resourceURI) {
            let fileURL = window.location.protocol + '//' + window.location.host + `/services/ide/workspaces/${schema}/${schema}`
            let msg = `Created requested file in Project [${schema}] in Workspace [${schema}]. \n To access it it please go to: ${fileURL}`;
            console.info(msg);
            messageHub.showAlertSuccess('Export', msg);
        }).catch(function (err) {
            messageHub.showAlertError("Export error", "Error in exporting data in project");
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
            let fileURL = window.location.protocol + '//' + window.location.host + `/services/ide/workspaces/${schema}/${schema}/${schema}.schema`
            let msg = `Created file [${schema}.schema] in Project [${schema}] in Workspace [${schema}]. \n To access it it please go to: ${fileURL}`;
            console.info(msg);
            messageHub.showAlertSuccess('Export', msg);
        }).catch(function (err) {
            messageHub.showAlertError("Export error", "Error in exporting metadata in project");
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
        }).then(
            function (result) {
                cleanScope();
                if (result.data !== null && result.data.errorMessage !== null && result.data.errorMessage !== undefined) {
                    $scope.state.error = true;
                    $scope.errorMessage = result.data.errorMessage;
                } else {
                    $scope.result = 'Data anonymized.';
                }
                $scope.hideProgress();
            }, function (result) {
                cleanScope();
                $scope.state.error = true;
                $scope.errorMessage = result.data.errorMessage;
                $scope.hideProgress();
            }
        );
    }, true);

    function cleanScope() {
        $scope.result = null;
        $scope.columns = null;
        $scope.rows = null;
        $scope.hasMultipleProcedureResults = false;
        $scope.procedureResults.length = 0;
    }

    messageHub.onDidReceiveMessage("database.sql.error", function (error) {
        $scope.state.error = true;
        $scope.errorMessage = error.data;
        $scope.hideProgress();
    }, true);

}]);
