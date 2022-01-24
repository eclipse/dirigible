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
angular.module('logs', [])
	.controller('LogsController', ['$scope', '$http', function ($scope, $http) {

		const SELECT_LOG_TEXT = "Select log file...";

		$scope.selectedLog = null;
		$http.get('/services/v4/ops/logs').then(function (response) {
			$scope.logsList = [SELECT_LOG_TEXT];
			$scope.logsList.push(...response.data);
			$scope.selectedLog = SELECT_LOG_TEXT;
		});

		$scope.logChanged = function () {
			if ($scope.selectedLog && $scope.selectedLog !== SELECT_LOG_TEXT) {
				$http.get('/services/v4/ops/logs/' + $scope.selectedLog).then(function (response) {
					$scope.logContent = response.data;
				});
			} else {
				$scope.logContent = "";
			}
		}

	}]);