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
const loggersView = angular.module('loggers', ['ideUI', 'ideView', 'ngRoute']);

loggersView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'loggers-view';
}]);

loggersView.controller('LoggersController', ['$scope', '$http', '$route', 'messageHub', function ($scope, $http, $route, messageHub) {

    $scope.search = {
        name: ''
    }

    let loggersApi = '/services/ide/loggers/';

    function loadLoggers() {
        $http.get(loggersApi)
            .then(function (data) {
                $scope.loggers = data.data;
            });
    }

    $scope.setSeverity = function (loggerName, loggerLevel) {
        $http({
				method: "POST",
				url: loggersApi + "severity/" + loggerName,
				data: loggerLevel,
				headers: {
			        "Content-Type": "text/plain"
			    }
			})			
            .then(function (data) {
                let logger = (element) => element.name === loggerName;
                let i = $scope.loggers.findIndex(logger);
                $scope.loggers[i].severity = data.data;
                $route.reload();
            }), function (data) {
                let logger = (element) => element.name === loggerName;
                let i = $scope.loggers.findIndex(logger);
                $scope.loggers[i].severity = data.data;
                $route.reload()
            }
    }
    loadLoggers();
}]);