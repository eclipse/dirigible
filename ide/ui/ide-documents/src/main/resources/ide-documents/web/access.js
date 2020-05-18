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
angular.module('page', []);
angular.module('page').controller('PageController', function ($scope, $http) {

	String.prototype.replaceAll = function(find, replace) {
		return this.replace(new RegExp(find, 'g'), replace);
	};

	$scope.methods = [{
		'key': '*',
		'label': '*'
	}, {
		'key': 'READ',
		'label': 'READ'
	}, {
		'key': 'WRITE',
		'label': 'WRITE'
	}];

	$scope.scopes = [{
		'key': 'CMIS',
		'label': 'CMIS'
	}];

	$scope.openNewDialog = function () {
		$scope.actionType = 'new';
		$scope.entity = {};
		$scope.entity.method = '*';
		$scope.entity.scope = 'CMIS';
		toggleEntityModal();
	};

	$scope.openEditDialog = function (entity) {
		$scope.actionType = 'update';
		$scope.entity = entity;
		$scope.entity.scope = 'CMIS';
		toggleEntityModal();
	};

	$scope.openDeleteDialog = function (entity) {
		$scope.actionType = 'delete';
		$scope.entity = entity;
		toggleEntityModal();
	};

	$scope.close = function () {
		load();
		toggleEntityModal();
	};

	$scope.create = function () {
		$scope.access.constraints.push($scope.entity);
		$scope.save();
		toggleEntityModal();
	};

	$scope.update = function () {
		// auto-wired
		$scope.save();
		toggleEntityModal();
	};

	$scope.delete = function () {
		$scope.access.constraints = $scope.access.constraints.filter(function (e) {
			return e !== $scope.entity;
		});
		$scope.save();
		toggleEntityModal();
	};


	function toggleEntityModal() {
		$('#entityModal').modal('toggle');
		$scope.error = null;
	}

	var contents;
	var csrfToken;

	function getResource(resourcePath) {
		var xhr = new XMLHttpRequest();
		xhr.open('GET', resourcePath, false);
		xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
		xhr.send();
		if (xhr.status === 200) {
			csrfToken = xhr.getResponseHeader("x-csrf-token");
			return xhr.responseText;
		}
	}

	function loadContents() {
		return getResource('../../../../../../services/v4/js/ide-documents/services/access');
	}

	function load() {
		contents = loadContents();
		$scope.access = JSON.parse(contents);
		$scope.access.constraints.forEach(function (constraint) {
			constraint.rolesLine = constraint.roles.join();
		});

	}

	load();

	function saveContents(text) {
		var xhr = new XMLHttpRequest();
		xhr.open('PUT', '../../../../../../services/v4/js/ide-documents/services/access');
		xhr.setRequestHeader('X-Requested-With', 'Fetch');
		xhr.setRequestHeader('X-CSRF-Token', csrfToken);
		xhr.send(text);
	}

	var serializeAccess = function () {
		var accessContents = JSON.parse(JSON.stringify($scope.access));
		accessContents.constraints.forEach(function (constraint) {
			if (constraint.rolesLine) {
				constraint.roles = constraint.rolesLine.replaceAll(' ', '').split(',');
				delete constraint.rolesLine;
				delete constraint.$$hashKey;
			}
		});
		return accessContents;
	}

	$scope.save = function () {
		var accessContents = serializeAccess();
		contents = JSON.stringify(accessContents);
		saveContents(contents);
	};

});