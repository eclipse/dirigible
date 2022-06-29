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
const jobsView = angular.module('jobs', ['ideUI', 'ideView']);

jobsView.config(["messageHubProvider", function (messageHubProvider) {
	messageHubProvider.eventIdPrefix = 'jobs-view';
}]);

jobsView.controller('JobsController', ['$scope', '$http', 'messageHub', function ($scope, $http, messageHub) {

	$http.get('/services/v4/ops/jobs').then(function (response) {
		$scope.list = response.data;
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

	$scope.clearLogs = function (name) {
		$http.post('/services/v4/ops/jobs/clear/' + $scope.job.name)
			.then(function (response) {
				messageHub.showAlertInfo(
					"Job Logs",
					'Execution logs of the job ' + name + ' has been deleted.'
				);
				$http.get('/services/v4/ops/jobs/logs/' + name)
					.then(function (response) {
						$scope.name = name;
						$scope.logs = response.data;
					}, function (response) {
						console.error(response.data);
					});
			}, function (response) {
				console.error(response.data);
			});
	}

	$scope.getEmails = function (job) {
		$scope.job = job;
		$http.get('/services/v4/ops/jobs/emails/' + job.name)
			.then(function (response) {
				$scope.name = job.name;
				$scope.job.email = 'my-email@examle.com';
				$scope.emails = response.data;
			}, function (response) {
				console.error(response.data);
			});
	}

	$scope.addEmail = function () {
		$http.post('/services/v4/ops/jobs/emailadd/' + $scope.job.name, $scope.job.email)
			.then(function (response) {
				$scope.getEmails($scope.job);
			}, function (response) {
				console.error(response.data);
			});
	}

	$scope.removeEmail = function (id) {
		$http.delete('/services/v4/ops/jobs/emailremove/' + id)
			.then(function (response) {
				$scope.getEmails($scope.job);
			}, function (response) {
				console.error(response.data);
			});
	}

	$scope.checkIfValid = function () {
		console.log("Value is = " + $scope.job.email);
	}

	$scope.$watch(
		function ($scope) { return angular.toJson($scope); },
		function () { console.log("changed"); }
	);

}]);