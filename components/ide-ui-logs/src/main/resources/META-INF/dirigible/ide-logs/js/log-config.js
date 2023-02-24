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
const logconfigurationsView = angular.module('logconfigurations', ['ideUI', 'ideView', 'ngRoute']);

logconfigurationsView.config(["messageHubProvider", function (messageHubProvider) {
    messageHubProvider.eventIdPrefix = 'logconfigurations-view';
}]);

logconfigurationsView.controller('LogConfigurationsController', ['$scope', '$http', '$route', 'messageHub', function ($scope, $http, $route, messageHub) {

    $scope.search = {
        name: ''
    }

    let logConfigurationsApi = '/services/ide/logconfig/';

    function loadLogConfigurations() {
        $http.get(logConfigurationsApi)
            .then(function (data) {
                $scope.logConfigurations = data.data;
            });
    }

    $scope.setSeverity = function (loggerName, loggerLevel) {
        $http({
				method: "POST",
				url: logConfigurationsApi + "severity/" + loggerName,
				data: loggerLevel,
				headers: {
			        "Content-Type": "text/plain"
			    }
			})			
            .then(function (data) {
                let logger = (element) => element.name === loggerName;
                let i = $scope.logConfigurations.findIndex(logger);
                $scope.logConfigurations[i].severity = data.data;
                $route.reload();
            }), function (data) {
                let logger = (element) => element.name === loggerName;
                let i = $scope.logConfigurations.findIndex(logger);
                $scope.logConfigurations[i].severity = data.data;
                $route.reload()
            }
    }
    loadLogConfigurations();
}]);