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
const databasesView = angular.module('databases', ['ideUI', 'ideView']);

databasesView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'databases-view';
}]);

databasesView.controller('DatabaseController', ['$scope', '$http', 'messageHub', function ($scope, $http, messageHub) {

    $scope.listDatabases = function () {
        $http.get('/services/data/sources/').then(function (response) {
            $scope.list = response.data;
        });
    }
    $scope.listDatabases();

    $scope.getDatabaseDetailsFormItems = (values = {}, isNew = true) => {
        let ret = [];

        if (isNew) ret.push({
            id: 'name',
            type: 'input',
            label: 'Name',
            required: true,
            placeholder: 'Enter Name...',
            value: values['name'] || ''
        });

        ret.push(
            {
                id: 'driver',
                type: 'dropdown',
                label: 'Driver',
                required: true,
                placeholder: 'Select Driver...',
                value: values['driver'],
                items: $scope.drivers.map(x => ({ label: x.text, value: x.value }))
            },
            {
                id: 'url',
                type: 'input',
                label: 'URL',
                required: true,
                placeholder: 'Enter URL...',
                value: values['url'] || ''
            },
            {
                id: 'username',
                type: 'input',
                label: 'Username',
                required: false,
                placeholder: 'Enter Username...',
                value: values['username'] || ''
            },
            {
                id: 'password',
                type: 'input',
                inputType: 'password',
                label: 'Password',
                required: false,
                placeholder: 'Enter Password...',
                value: values['password'] || ''
            },
            {
                id: 'parameters',
                type: 'input',
                label: 'Parameters',
                required: false,
                placeholder: 'Enter Parameters, e.g. name1=value1,name2=value2 ...',
                value: $scope.joinParameters(values['properties']) || ''
            }
        );
        return ret;
    }
    
    $scope.joinParameters = function(parameters) {
		let result = '';
		if (parameters) {
			parameters.forEach(function(current){
				result += current.name + '=' + current.value + ',';
			});
			return result.slice(0, -1);
		}
		return result;
	}

    $scope.newDatabase = function () {
        messageHub.showFormDialog(
            'createDatabaseDialog',
            'New Database',
            $scope.getDatabaseDetailsFormItems(),
            [{
                id: 'btnOK',
                type: 'emphasized',
                label: 'Create',
                whenValid: true
            },
            {
                id: 'btnCancel',
                type: 'transparent',
                label: 'Close',
            }],
            'ide-databases.database.create',
            'Please, wait...'
        );
    }

    messageHub.onDidReceiveMessage(
        'ide-databases.database.create',
        function (msg) {
            if (msg.data.buttonId === 'btnOK') {
                let database = msg.data.formData.reduce((ret, item) => {
                    ret[item.id] = item.value;
                    return ret;
                }, {});
                $http.post('/services/data/sources', JSON.stringify(database))
                    .then(function (response) {
                        messageHub.hideFormDialog('createDatabaseDialog');
                        $scope.listDatabases();
                    }, function (response) {
                        messageHub.updateFormDialog(
                            'createDatabaseDialog',
                            msg.data.formData,
                            'Please, wait...',
                            response.data
                        );
                    });
            } else {
                messageHub.hideFormDialog('createDatabaseDialog');
            }
        },
        true
    );

    $scope.editDatabase = function (database) {
        $scope.database = {
            id: database.id,
            name: database.name
        };

        messageHub.showFormDialog(
            'editDatabaseDialog',
            `Edit Database ${database.name}`,
            $scope.getDatabaseDetailsFormItems(database, false),
            [{
                id: 'btnOK',
                type: 'emphasized',
                label: 'Update',
                whenValid: true
            },
            {
                id: 'btnCancel',
                type: 'transparent',
                label: 'Close',
            }],
            'ide-databases.database.edit',
            'Please, wait...'
        );
    }

    messageHub.onDidReceiveMessage(
        'ide-databases.database.edit',
        function (msg) {
            if (msg.data.buttonId === 'btnOK') {
                let database = msg.data.formData.reduce((ret, item) => {
                    ret[item.id] = item.value;
                    return ret;
                }, {});
                $http.put('/services/data/sources/' + $scope.database.id, JSON.stringify({ name: $scope.database.name, ...database }))
                    .then(function (response) {
                        messageHub.hideFormDialog('editDatabaseDialog');
                        $scope.listDatabases();
                    }, function (response) {
                        messageHub.updateFormDialog(
                            'editDatabaseDialog',
                            msg.data.formData,
                            'Please, wait...',
                            response.data
                        );
                    });
            } else {
                messageHub.hideFormDialog('editDatabaseDialog');
            }
        },
        true
    );

    $scope.deleteDatabase = function (database) {
        $scope.database = {
            id: database.id
        };

        messageHub.showDialog(
            'Delete Database',
            'Are you sure you want to delete the selected database?',
            [{
                id: 'btnOK',
                type: 'emphasized',
                label: 'OK',
            },
            {
                id: 'btnCancel',
                type: 'transparent',
                label: 'Cancel',
            }],
            'ide-databases.database.delete'
        );
    }

    messageHub.onDidReceiveMessage(
        'ide-databases.database.delete',
        function (msg) {
            if (msg.data === 'btnOK' && $scope.database.id) {
                $http.delete('/services/data/sources/' + $scope.database.id)
                    .then(function (response) {
                        $scope.listDatabases();
                    }, function (response) {
                        console.error(response.data);
                    });
            }
        },
        true
    );

    $scope.drivers = [];
    $scope.drivers.push({ "text": "H2 - org.h2.Driver", "value": "org.h2.Driver" });
    $scope.drivers.push({ "text": "PostgreSQL - org.postgresql.Driver", "value": "org.postgresql.Driver" });
    $scope.drivers.push({ "text": "MySQL - com.mysql.jdbc.Driver", "value": "com.mysql.jdbc.Driver" });
    $scope.drivers.push({ "text": "SAP HANA - com.sap.db.jdbc.Driver", "value": "com.sap.db.jdbc.Driver" });

    $scope.urls = {};
    $scope.urls["org.h2.Driver"] = "jdbc:h2:path/name";
    $scope.urls["org.postgresql.Driver"] = "jdbc:postgresql://host:port/database";
    $scope.urls["com.mysql.jdbc.Driver"] = "jdbc:mysql://host:port/database";
    $scope.urls["com.sap.db.jdbc.Driver"] = "jdbc:sap://host:port/?encrypt=true&validateCertificate=false";

    $scope.driverChanged = function () {
        $scope.database.url = $scope.urls[$scope.database.driver];
        $scope.database.username = "";
        $scope.database.password = "";
    }

}]);
