/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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


	}]);