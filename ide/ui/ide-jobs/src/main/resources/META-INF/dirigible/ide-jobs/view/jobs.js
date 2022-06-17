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
angular.module('jobs', [])
	.controller('JobsController', ['$scope', '$http', function ($scope, $http) {

		$http.get('/services/v4/ops/jobs').then(function (response) {
			$scope.jobsList = response.data;
		});

		$scope.enable = function (job) {
			$http.post('/services/v4/ops/jobs/enable/' + job.name)
				.then(function (response) {
					console.info(response.data.name + " has been enabled.");
					job.enabled = true;
				}, function (response) {
					console.error(response.data);
				});
		}

		$scope.disable = function (job) {
			$http.post('/services/v4/ops/jobs/disable/' + job.name)
				.then(function (response) {
					console.info(response.data.name + " has been disabled.");
					job.enabled = false;
				}, function (response) {
					console.error(response.data);
				});
		}

		$scope.getLogs = function (job) {
			$scope.job = job;
			$http.get('/services/v4/ops/jobs/logs/' + job.name)
				.then(function (response) {
					$scope.name = job.name;
					$scope.logs = response.data;
				}, function (response) {
					console.error(response.data);
				});
		}

		$scope.getParameters = function (job) {
			$scope.job = job;
			$scope.result = "Job: " + $scope.job.name + " has not been triggered yet";
			$scope.map = new Array();
			$http.get('/services/v4/ops/jobs/parameters/' + job.name)
				.then(function (response) {
					$scope.name = job.name;
					$scope.parameters = response.data;
				}, function (response) {
					console.error(response.data);
				});
		}

		$scope.triggerJob = function () {
			$scope.map = new Array();
			for (let i in $scope.parameters) {
				let parameter = $scope.parameters[i];
				let entry = {};
				entry.name = parameter.name;
				entry.value = parameter.value;
				$scope.map.push(entry);
			}
			$http.post('/services/v4/ops/jobs/trigger/' + $scope.job.name, $scope.map)
				.then(function (response) {
					$scope.result = "Job: " + $scope.job.name + " has been triggered successfully";
				}, function (response) {
					console.error(response.data);
				});
		}

	}]);