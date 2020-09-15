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
angular.module('deployer', []);
angular.module('deployer')
.controller('DeployController', function ($scope, $http) {

	var defaultDirigibleInstances = [{
		'id': 1,
		'name': 'Local',
		'host': 'http://localhost:8080',
		'displayName': 'Local (http://localhost:8080)'
	}, {
		'id': 2,
		'name': 'Trial',
		'host': 'http://trial.dirigible.io',
		'displayName': 'Trial'
	}, {
		'id': 2,
		'name': 'Trial (Eclipse)',
		'host': 'http://dirigible.eclipse.org',
		'displayName': 'Trial (Eclipse)'
	}];

	function setDefaultDirigibleInstnaces() {
		var dirigibleInstnaces = getDirigibleInstances();
		if (dirigibleInstnaces.length === 0) {
			for (var i = 0; i < defaultDirigibleInstances.length; i++) {
				addDirigibleInstance(defaultDirigibleInstances[i]);
			}
		}
	}
	setDefaultDirigibleInstnaces();

	$scope.queryParams = getQueryParams();
	$scope.env = getEnvParams();

	function loadDirigibleInstance() {
		$scope.dirigibleInstances = getDirigibleInstances();
	}
	loadDirigibleInstance();

	function getEnvParams() {
		var env = [];
		$scope.envParamsProvided = Boolean($scope.queryParams.env);
		if ($scope.envParamsProvided) {
			var envKeys = $scope.queryParams.env.split(",");
			for (var i = 0; i < envKeys.length; i ++) {
				env.push({
					key: envKeys[i]
				});
			}
		}
		return env;
	}

	function getDirigibleInstances() {
		var dirigibleInstances = JSON.parse(window.localStorage.getItem('DIRIGIBLE.instances'));
		return dirigibleInstances !== null ? dirigibleInstances : [];
	}

	function addDirigibleInstance(instance) {
		var dirigibleInstances = getDirigibleInstances();
		dirigibleInstances.push(instance);
		window.localStorage.setItem('DIRIGIBLE.instances', JSON.stringify(dirigibleInstances));
	}

	function getQueryParams () {
		var result = {};
		$scope.queryParamsProvided = false;
		if (window.location.search.length > 0) {
			var queryParams = window.location.search.slice(1).split('&');
			for (var i = 0 ; i < queryParams.length; i ++) {
				var pair = queryParams[i].split('=');
				result[pair[0]] = pair[1];
			}
		}

		var respositoryFound = false;
		var uriFound = false;
		for (var next in result) {
			if (next === 'repository') {
				respositoryFound = true;
			} else if (next === 'uri') {
				uriFound = true;
			}
		}
		$scope.queryParamsProvided = respositoryFound && uriFound;
		return result;
	}

	$scope.addInstance = function() {
		addDirigibleInstance({
			'id': generateUUID(),
			'name': $scope.newInstanceName,
			'host': $scope.newInstanceHost.substring(0, $scope.newInstanceHost.indexOf("/", 8)),
			'displayName': $scope.newInstanceName
		});
		loadDirigibleInstance();
		toggleModal();
	};

	$scope.run = function() {
		window.location.href = getDeployUrl($scope.selectedDirigibleInstance);
	};

	function generateUUID(){
		var dt = new Date().getTime();
		var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			var r = (dt + Math.random()*16)%16 | 0;
			dt = Math.floor(dt/16);
			return (c === 'x' ? r : r&0x3 | 0x8).toString(16);
		});
		return uuid;
	}

	function getDeployUrl(instance) {
		var url = instance.host + '/services/v4/web/ide-git/index.html?repository=' + $scope.queryParams.repository;
		if ($scope.queryParams.uri) {
			url += '&uri=' + $scope.queryParams.uri;
		}
		if ($scope.queryParams.branch) {
			url += '&branch=' + $scope.queryParams.branch;
		}
		if ($scope.env && $scope.env.length > 0) {
			url += "&env=" + encodeURIComponent(JSON.stringify($scope.env));
		}
		return  url;
	}

	$scope.openAddInstanceDialog = function() {
		toggleModal();
	};

	function toggleModal() {
		$('#addInstanceModal').modal('toggle');
	}
});
