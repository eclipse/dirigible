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
angular.module('ui.entity-data.modeler', ['ngAnimate', 'ngSanitize', 'ui.bootstrap'])
	.controller('ModelerCtrl', function ($scope) {
		let ctrl = this;
		ctrl.$scope = $scope;

		ctrl.animationsEnabled = true;

		ctrl.layoutTypes = [
			{ "key": "MANAGE", "label": "Manage Entities" },
			{ "key": "MANAGE_MASTER", "label": "Manage Master Entities" },
			{ "key": "MANAGE_DETAILS", "label": "Manage Details Entities" },
			{ "key": "LIST", "label": "List Entities" },
			{ "key": "LIST_MASTER", "label": "List Master Entities" },
			{ "key": "LIST_DETAILS", "label": "List Details Entities" },
			{ "key": "REPORT_TABLE", "label": "Report in a Table Format" },
			{ "key": "REPORT_BAR", "label": "Report in a Bar Chart Format" },
			{ "key": "REPORT_LINE", "label": "Report in a Line Chart Format" },
			{ "key": "REPORT_PIE", "label": "Report in a Pie Chart Format" }
		];

		ctrl.dataTypes = [
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

		ctrl.widgetTypes = [
			{ "key": "TEXTBOX", "label": "Text Box" },
			{ "key": "TEXTAREA", "label": "Text Area" },
			{ "key": "DATE", "label": "Date Picker" },
			{ "key": "DROPDOWN", "label": "Dropdown" },
			{ "key": "CHECKBOX", "label": "Check Box" },
			{ "key": "LOOKUPDIALOG", "label": "Lookup Dialog" },
			{ "key": "NUMBER", "label": "Number" },
			{ "key": "COLOR", "label": "Color" },
			{ "key": "DATETIME-LOCAL", "label": "Datetime Local" },
			{ "key": "EMAIL", "label": "e-mail" },
			{ "key": "MONTH", "label": "Month" },
			{ "key": "RANGE", "label": "Range" },
			{ "key": "SEARCH", "label": "Search" },
			{ "key": "TEL", "label": "Telephone" },
			{ "key": "TIME", "label": "Time" },
			{ "key": "URL", "label": "URL" },
			{ "key": "WEEK", "label": "Week" }
		];

		ctrl.relationshipTypes = [
			{ "key": "ASSOCIATION", "label": "Association" },
			{ "key": "AGGREGATION", "label": "Aggregation" },
			{ "key": "COMPOSITION", "label": "Composition" },
			{ "key": "EXTENSION", "label": "Extension" }
		];

		ctrl.relationshipCardinalities = [
			{ "key": "1_1", "label": "one-to-one" },
			{ "key": "1_n", "label": "one-to-many" },
			{ "key": "n_1", "label": "many-to-one" },
		];

		ctrl.entityTypes = [
			{ "key": "PRIMARY", "label": "Primary Entity" },
			{ "key": "DEPENDENT", "label": "Dependent Entity" },
			{ "key": "REPORT", "label": "Report Entity" }
		];

		ctrl.isMajorTypes = [
			{ "key": "true", "label": "Show in table header" },
			{ "key": "false", "label": "Show in form only" }
		];

		ctrl.icons = [];

		ctrl.loadIcons = function () {
			return new Promise((resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open('GET', '/services/v4/web/resources/unicons/list.json');
				xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
				xhr.setRequestHeader('Dirigible-Editor', 'EntityDataModeler');
				xhr.onload = () => {
					if (xhr.status === 200) {
						resolve(xhr.responseText);
					} else {
						reject(xhr.status)
					}
					csrfToken = xhr.getResponseHeader("x-csrf-token");
				};
				xhr.onerror = () => reject(xhr.status);
				xhr.send();
			});
		};

		ctrl.loadModels = function () {
			return new Promise((resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open('POST', '/services/v4/ide/workspace-find/');
				xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
				xhr.setRequestHeader('Dirigible-Editor', 'EntityDataModeler');
				xhr.onload = () => {
					if (xhr.status === 200) {
						resolve(xhr.responseText);
					} else {
						reject(xhr.status)
					}
					csrfToken = xhr.getResponseHeader("x-csrf-token");
				};
				xhr.onerror = () => reject(xhr.status);
				xhr.send('*.model');
			});
		};

		ctrl.loadEntities = function () {
			return new Promise((resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open('GET', '/services/v4/ide/workspaces' + $scope.$parent.referencedModel);
				xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
				xhr.setRequestHeader('Dirigible-Editor', 'EntityDataModeler');
				xhr.onload = () => {
					if (xhr.status === 200) {
						resolve(xhr.responseText);
					} else {
						reject(xhr.status)
					}
					csrfToken = xhr.getResponseHeader("x-csrf-token");
				};
				xhr.onerror = () => reject(xhr.status);
				xhr.send();
			});
		};

		ctrl.loadIcons().then(
			result => ctrl.icons = JSON.parse(result),
			error => console.log(error)
		);

		ctrl.loadModels().then(
			result => ctrl.availableModels = JSON.parse(result),
			error => console.log(error)
		);

		ctrl.updateEntities = function () {
			ctrl.loadEntities().then(
				result => ctrl.availableEntities = $scope.$parent.availableEntities = JSON.parse(result).model.entities,
				error => console.log(error)
			);
		}

		// Save Entity's properties
		ctrl.okEntityProperties = function () {
			let clone = $scope.$parent.cell.value.clone();
			$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
			if (clone.entityType === 'PROJECTION') {
				$scope.$parent.graph.getSelectionCell().style = 'projection';
				$scope.$parent.graph.getSelectionCell().children.forEach(cell => cell.style = 'projectionproperty');
				$scope.$parent.graph.refresh();
			}
			if (clone.entityType === 'EXTENSION') {
				$scope.$parent.graph.getSelectionCell().style = 'extension';
				$scope.$parent.graph.getSelectionCell().children.forEach(cell => cell.style = 'extensionproperty');
				$scope.$parent.graph.refresh();
			}
		};

		// Save Property's properties
		ctrl.okPropertyProperties = function () {
			let clone = $scope.$parent.cell.value.clone();
			$scope.$parent.graph.model.setValue($scope.$parent.cell, clone);
		};

		// Save Connector's properties
		ctrl.okConnectorProperties = function () {
			let clone = $scope.$parent.cell.source.value.clone();
			$scope.$parent.graph.model.setValue($scope.$parent.cell.source, clone);

			let connector = new Connector();
			connector.name = $scope.$parent.cell.source.value.relationshipName;
			$scope.$parent.graph.model.setValue($scope.$parent.cell, connector);
		};

		// Save Navigation's properties
		ctrl.okNavigationProperties = function () {
			// var clone = $scope.$parent.sidebar;
			// $scope.$parent.graph.model.sidebar = clone;

			// var sidebarNavigation = new SidebarNavigation();
			// connector.name = $scope.$parent.cell.source.value.relationshipName;
			// $scope.$parent.graph.model.setValue($scope.$parent.cell, connector);
		};

		ctrl.availablePerspectives = function () {
			return $scope.$parent.graph.model.perspectives;
		};

		// Perspectives Management
		$scope.openPerspectiveNewDialog = function () {
			$scope.actionType = 'perspectiveNew';
			$scope.perspectiveEntity = {};
			togglePerspectiveEntityModal();
		};

		$scope.openPerspectiveEditDialog = function (entity) {
			$scope.actionType = 'perspectiveUpdate';
			$scope.perspectiveEntity = entity;
			togglePerspectiveEntityModal();
		};

		$scope.openPerspectiveDeleteDialog = function (entity) {
			$scope.actionType = 'perspectiveDelete';
			$scope.perspectiveEntity = entity;
			togglePerspectiveEntityModal();
		};

		$scope.closePerspective = function () {
			//load();
			togglePerspectiveEntityModal();
		};

		$scope.perspectiveCreate = function () {
			if (!$scope.$parent.graph.model.perspectives) {
				$scope.$parent.graph.model.perspectives = [];
			}
			let exists = $scope.$parent.graph.model.perspectives.filter(function (e) {
				return e.id === $scope.perspectiveEntity.id;
			});
			if (exists.length === 0) {
				$scope.$parent.graph.model.perspectives.push($scope.perspectiveEntity);
				togglePerspectiveEntityModal();
			} else {
				$scope.error = "Perspective with the id [" + $scope.perspectiveEntity.id + "] already exists!";
			}

		};

		$scope.perspectiveUpdate = function () {
			// auto-wired
			togglePerspectiveEntityModal();
		};

		$scope.perspectiveDelete = function () {
			if (!$scope.$parent.graph.model.perspectives) {
				$scope.$parent.graph.model.perspectives = [];
			}
			$scope.$parent.graph.model.perspectives = $scope.$parent.graph.model.perspectives.filter(function (e) {
				return e !== $scope.perspectiveEntity;
			});
			togglePerspectiveEntityModal();
		};

		function togglePerspectiveEntityModal() {
			$('#perspectiveEntityModal').modal('toggle');
			$scope.error = null;
		}
		// ----

		// Sidebar Management
		$scope.openSidebarNewDialog = function () {
			$scope.actionType = 'sidebarNew';
			$scope.sidebarEntity = {};
			toggleSidebarEntityModal();
		};

		$scope.openSidebarEditDialog = function (entity) {
			$scope.actionType = 'sidebarUpdate';
			$scope.sidebarEntity = entity;
			toggleSidebarEntityModal();
		};

		$scope.openSidebarDeleteDialog = function (entity) {
			$scope.actionType = 'sidebarDelete';
			$scope.sidebarEntity = entity;
			toggleSidebarEntityModal();
		};

		$scope.closeSidebar = function () {
			//load();
			toggleSidebarEntityModal();
		};

		$scope.sidebarCreate = function () {
			if (!$scope.$parent.graph.model.sidebar) {
				$scope.$parent.graph.model.sidebar = [];
			}
			let exists = $scope.$parent.graph.model.sidebar.filter(function (e) {
				return e.path === $scope.sidebarEntity.path;
			});
			if (exists.length === 0) {
				$scope.$parent.graph.model.sidebar.push($scope.sidebarEntity);
				toggleSidebarEntityModal();
			} else {
				$scope.error = "Navigation with the path [" + $scope.sidebarEntity.path + "] already exists!";
			}

		};

		$scope.sidebarUpdate = function () {
			// auto-wired
			toggleSidebarEntityModal();
		};

		$scope.sidebarDelete = function () {
			if (!$scope.$parent.graph.model.sidebar) {
				$scope.$parent.graph.model.sidebar = [];
			}
			$scope.$parent.graph.model.sidebar = $scope.$parent.graph.model.sidebar.filter(function (e) {
				return e !== $scope.sidebarEntity;
			});
			toggleSidebarEntityModal();
		};

		function toggleSidebarEntityModal() {
			$('#sidebarEntityModal').modal('toggle');
			$scope.error = null;
		}
		// ----

		main(document.getElementById('graphContainer'),
			document.getElementById('outlineContainer'),
			document.getElementById('toolbarContainer'),
			document.getElementById('sidebarContainer'),
			document.getElementById('statusContainer'));

	});