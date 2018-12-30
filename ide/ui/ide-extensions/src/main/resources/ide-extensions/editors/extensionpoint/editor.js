/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
angular.module('page', []);
angular.module('page').controller('PageController', function ($scope, $http) {
	
	var messageHub = new FramesMessageHub();
	var contents;
	
	function getResource(resourcePath) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', resourcePath, false);
        xhr.send();
        if (xhr.status === 200) {
        	return xhr.responseText;
        }
	}
	
	function loadContents(file) {
		if (file) {
			return getResource('../../../../../../services/v3/ide/workspaces' + file);
		}
		console.error('file parameter is not present in the URL');
	}

	function load() {
		var searchParams = new URLSearchParams(window.location.search);
		$scope.file = searchParams.get('file');
		contents = loadContents($scope.file);
		$scope.extensionpoint = JSON.parse(contents);
	}
	
	load();

	function saveContents(text) {
		console.log('Save called...');
		if ($scope.file) {
			var xhr = new XMLHttpRequest();
			xhr.open('PUT', '../../../../../../services/v3/ide/workspaces' + $scope.file);
			xhr.setRequestHeader('X-Requested-With', 'Fetch');
			xhr.onreadystatechange = function() {
				if (xhr.readyState === 4) {
					console.log('file saved: ' + $scope.file);
				}
			};
			xhr.send(text);
			messageHub.post({data: $scope.file}, 'editor.file.saved');
		} else {
			console.error('file parameter is not present in the request');
		}
	}

	$scope.save = function() {
		contents = JSON.stringify($scope.extensionpoint);
		saveContents(contents);
	};
	
	$scope.$watch(function() {
		var extensionpoint = JSON.stringify($scope.extensionpoint);
		if (contents !== extensionpoint) {
			messageHub.post({data: $scope.file}, 'editor.file.dirty');
		}
	});
	

});