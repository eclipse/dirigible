/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('page', []);
angular.module('page').controller('PageController', function ($scope, $http) {
	
	$scope.openNewDialog = function() {
		$scope.actionType = 'new';
		$scope.entity = {};
		toggleEntityModal();
	};

	$scope.openEditDialog = function(entity) {
		$scope.actionType = 'update';
		$scope.entity = entity;
		toggleEntityModal();
	};

	$scope.openDeleteDialog = function(entity) {
		$scope.actionType = 'delete';
		$scope.entity = entity;
		toggleEntityModal();
	};

	$scope.close = function() {
		load();
		toggleEntityModal();
	};
	
	$scope.create = function() {
		var exists = $scope.roles.filter(function(e) {
			return e.name === $scope.entity.name;
		});
		if (exists.length === 0) {
			$scope.roles.push($scope.entity);
			toggleEntityModal();
		} else {
			$scope.error = "Role with a name [" + $scope.entity.name + "] already exists!";
		}
		
	};

	$scope.update = function() {
		// auto-wired
		toggleEntityModal();
	};

	$scope.delete = function() {
		$scope.roles = $scope.roles.filter(function(e) {
			return e !== $scope.entity;
		}); 
		toggleEntityModal();
	};

	
	function toggleEntityModal() {
		$('#entityModal').modal('toggle');
		$scope.error = null;
	}

	
	var messageHub = new FramesMessageHub();
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
	
	function loadContents(file) {
		if (file) {
			return getResource('../../../../../../services/v4/ide/workspaces' + file);
		}
		console.error('file parameter is not present in the URL');
	}

	function load() {
		var searchParams = new URLSearchParams(window.location.search);
		$scope.file = searchParams.get('file');
		contents = loadContents($scope.file);
		$scope.roles = JSON.parse(contents);
	}
	
	load();

	function saveContents(text) {
		console.log('Save called...');
		if ($scope.file) {
			var xhr = new XMLHttpRequest();
			xhr.open('PUT', '../../../../../../services/v4/ide/workspaces' + $scope.file);
			xhr.setRequestHeader('X-Requested-With', 'Fetch');
			xhr.setRequestHeader('X-CSRF-Token', csrfToken);
			xhr.onreadystatechange = function() {
				if (xhr.readyState === 4) {
					console.log('file saved: ' + $scope.file);
				}
			};
			xhr.send(text);
			messageHub.post({data: $scope.file}, 'editor.file.saved');
			messageHub.post({data: 'File [' + $scope.file + '] saved.'}, 'status.message');
		} else {
			console.error('file parameter is not present in the request');
		}
	}
	
	$scope.save = function() {
		contents = JSON.stringify($scope.roles);
		saveContents(contents);
	};
	
	$scope.$watch(function() {
		var roles = JSON.stringify($scope.roles);
		if (contents !== roles) {
			messageHub.post({data: $scope.file}, 'editor.file.dirty');
		}
	});
	

});
