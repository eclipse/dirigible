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
angular.module('page', [])
	.controller('PageController', function ($scope) {

		let messageHub = new FramesMessageHub();
		let contents;

		function getResource(resourcePath) {
			let xhr = new XMLHttpRequest();
			xhr.open('GET', resourcePath, false);
			xhr.send();
			if (xhr.status === 200) {
				return xhr.responseText;
			}
		}

		function loadContents(file) {
			if (file) {
				return getResource('/services/v4/ide/workspaces' + file);
			}
			console.error('file parameter is not present in the URL');
		}

		function load() {
			let searchParams = new URLSearchParams(window.location.search);
			$scope.file = searchParams.get('file');
			contents = loadContents($scope.file);
			$scope.websocket = JSON.parse(contents);
		}

		load();

		function saveContents(text) {
			console.log('Save called...');
			if ($scope.file) {
				let xhr = new XMLHttpRequest();
				xhr.open('PUT', '/services/v4/ide/workspaces' + $scope.file);
				xhr.onreadystatechange = function () {
					if (xhr.readyState === 4) {
						console.log('file saved: ' + $scope.file);
					}
				};
				xhr.send(text);
				messageHub.post({ data: $scope.file }, 'editor.file.saved');
				messageHub.post({ data: 'File [' + $scope.file + '] saved.' }, 'status.message');
			} else {
				console.error('file parameter is not present in the request');
			}
		}

		$scope.save = function () {
			contents = JSON.stringify($scope.websocket);
			saveContents(contents);
		};

		$scope.$watch(function () {
			let websocket = JSON.stringify($scope.websocket);
			if (contents !== websocket) {
				messageHub.post({ data: $scope.file }, 'editor.file.dirty');
			}
		});

	});