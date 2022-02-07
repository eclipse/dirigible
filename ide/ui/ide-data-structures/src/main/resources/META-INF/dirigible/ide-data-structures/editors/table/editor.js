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

		$scope.types = [
			{ "key": "VARCHAR", "label": "VARCHAR" },
			{ "key": "CHAR", "label": "CHAR" },
			{ "key": "DATE", "label": "DATE" },
			{ "key": "TIME", "label": "TIME" },
			{ "key": "TIMESTAMP", "label": "TIMESTAMP" },
			{ "key": "INTEGER", "label": "INTEGER" },
			{ "key": "TINYINT", "label": "TINYINT" },
			{ "key": "BIGINT", "label": "BIGINT" },
			{ "key": "SMALLINT", "label": "SMALLINT" },
			{ "key": "REAL", "label": "REAL" },
			{ "key": "DOUBLE", "label": "DOUBLE" },
			{ "key": "BOOLEAN", "label": "BOOLEAN" },
			{ "key": "BLOB", "label": "BLOB" },
			{ "key": "DECIMAL", "label": "DECIMAL" },
			{ "key": "BIT", "label": "BIT" }
		];

		$scope.openNewDialog = function () {
			$scope.actionType = 'new';
			$scope.entity = {};
			$scope.entity.type = "VARCHAR";
			$scope.entity.length = 20;
			toggleEntityModal();
		};

		$scope.openEditDialog = function (entity) {
			$scope.actionType = 'update';
			$scope.entity = entity;
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
			let exists = $scope.table.columns.filter(function (e) {
				return e.name === $scope.entity.name;
			});
			if (exists.length === 0) {
				$scope.table.columns.push($scope.entity);
				toggleEntityModal();
			} else {
				$scope.error = "Column with a name [" + $scope.entity.name + "] already exists!";
			}
		};

		$scope.update = function () {
			// auto-wired
			toggleEntityModal();
		};

		$scope.delete = function () {
			$scope.table.columns = $scope.table.columns.filter(function (e) {
				return e !== $scope.entity;
			});
			toggleEntityModal();
		};


		function toggleEntityModal() {
			$('#entityModal').modal('toggle');
			$scope.error = null;
		}

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

		function getViewParameters() {
			if (window.frameElement.hasAttribute("data-parameters")) {
				let params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
				$scope.file = params["file"];
			} else {
				let searchParams = new URLSearchParams(window.location.search);
				$scope.file = searchParams.get('file');
			}
		}

		function load() {
			getViewParameters();
			contents = loadContents($scope.file);
			$scope.table = JSON.parse(contents);
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
			} else {
				console.error('file parameter is not present in the request');
			}
		}

		$scope.save = function () {
			contents = JSON.stringify($scope.table);
			saveContents(contents);
		};

		$scope.$watch(function () {
			let table = JSON.stringify($scope.table);
			if (contents !== table) {
				messageHub.post({ data: $scope.file }, 'editor.file.dirty');
			}
		});

	});