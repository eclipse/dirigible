/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
angular.module('page', ["ideUI", "ideView"])
	.controller('PageController', function ($scope, messageHub, $window, ViewParameters) {
		let contents;
		let csrfToken;
		$scope.errorMessage = 'An unknown error was encountered. Please see console for more information.';
		$scope.forms = {
			editor: {},
		};
		$scope.state = {
			isBusy: true,
			error: false,
			busyText: "Loading...",
		};
		$scope.editColumnIndex = 0;
		$scope.editJoinIndex = 0;
		$scope.editConditionIndex = 0;
		$scope.editGroupingIndex = 0;
		$scope.editSortingIndex = 0;
		$scope.nameRegex = { patterns: ['^[a-zA-Z0-9_.:"-]*$'] };
		$scope.types = [
			{ value: "VARCHAR", label: "VARCHAR" },
			{ value: "CHAR", label: "CHAR" },
			{ value: "DATE", label: "DATE" },
			{ value: "TIME", label: "TIME" },
			{ value: "TIMESTAMP", label: "TIMESTAMP" },
			{ value: "INTEGER", label: "INTEGER" },
			{ value: "TINYINT", label: "TINYINT" },
			{ value: "BIGINT", label: "BIGINT" },
			{ value: "SMALLINT", label: "SMALLINT" },
			{ value: "REAL", label: "REAL" },
			{ value: "DOUBLE", label: "DOUBLE" },
			{ value: "BOOLEAN", label: "BOOLEAN" },
			{ value: "BLOB", label: "BLOB" },
			{ value: "DECIMAL", label: "DECIMAL" },
			{ value: "BIT", label: "BIT" },
		];
		$scope.aggregates = [
			{ value: "NONE", label: "NONE" },
			{ value: "COUNT", label: "COUNT" },
			{ value: "SUM", label: "SUM" },
			{ value: "AVG", label: "AVG" },
			{ value: "MIN", label: "MIN" },
			{ value: "MAX", label: "MAX" }
		];
		$scope.operations = [
			{ value: "=", label: "=" },
			{ value: "<>", label: "<>" },
			{ value: ">", label: ">" },
			{ value: ">=", label: ">=" },
			{ value: "<", label: "<" },
			{ value: "<=", label: "<=" },
			{ value: "IS NULL", label: "IS NULL" },
			{ value: "IS NOT NULL", label: "IS NOT NULL" },
			{ value: "BETWEEN", label: "BETWEEN" },
			{ value: "IN", label: "IN" },
			{ value: "LIKE", label: "LIKE" },
			{ value: "NOT LIKE", label: "NOT LIKE" }
		];
		$scope.joins = [
			{ value: "INNER", label: "INNER" },
			{ value: "LEFT", label: "LEFT" },
			{ value: "RIGHT", label: "RIGHT" },
			{ value: "FULL", label: "FULL" }
		];

		angular.element($window).bind("focus", function () {
			messageHub.setFocusedEditor($scope.dataParameters.file);
			messageHub.setStatusCaret('');
		});

		function getResource(resourcePath) {
			let xhr = new XMLHttpRequest();
			xhr.open('GET', resourcePath, false);
			xhr.setRequestHeader('X-CSRF-Token', 'Fetch');
			xhr.send();
			if (xhr.status === 200) {
				csrfToken = xhr.getResponseHeader("x-csrf-token");
				return xhr.responseText;
			} else {
				$scope.state.error = true;
				$scope.errorMessage = "Unable to load the file. See console, for more information.";
				messageHub.setStatusError(`Error loading '${$scope.dataParameters.file}'`);
				return '{}';
			}
		}

		$scope.load = function () {
			if (!$scope.state.error) {
				contents = getResource('/services/ide/workspaces' + $scope.dataParameters.file);
				$scope.report = JSON.parse(contents);
				contents = JSON.stringify($scope.report, null, 4);
				$scope.state.isBusy = false;
			}
		};

		function saveContents(text) {
			let xhr = new XMLHttpRequest();
			xhr.open('PUT', '/services/ide/workspaces' + $scope.dataParameters.file);
			xhr.setRequestHeader('X-Requested-With', 'Fetch');
			xhr.setRequestHeader('X-CSRF-Token', csrfToken);
			xhr.onreadystatechange = function () {
				if (xhr.readyState === 4) {
					messageHub.announceFileSaved({
						name: $scope.dataParameters.file.substring($scope.dataParameters.file.lastIndexOf('/') + 1),
						path: $scope.dataParameters.file.substring($scope.dataParameters.file.indexOf('/', 1)),
						contentType: $scope.dataParameters.contentType,
						workspace: $scope.dataParameters.file.substring(1, $scope.dataParameters.file.indexOf('/', 1)),
					});
					messageHub.setStatusMessage(`File '${$scope.dataParameters.file}' saved`);
					messageHub.setEditorDirty($scope.dataParameters.file, false);
					$scope.$apply(function () {
						$scope.state.isBusy = false;
					});
				}
			};
			xhr.onerror = function (error) {
				console.error(`Error saving '${$scope.dataParameters.file}'`, error);
				messageHub.setStatusError(`Error saving '${$scope.dataParameters.file}'`);
				messageHub.showAlertError('Error while saving the file', 'Please look at the console for more information');
				$scope.$apply(function () {
					$scope.state.isBusy = false;
				});
			};
			xhr.send(text);
		}

		$scope.save = function (_keySet, event) {
			if (event) event.preventDefault();
			if ($scope.forms.editor.$valid && !$scope.state.error) {
				$scope.state.busyText = "Saving...";
				$scope.state.isBusy = true;
				contents = JSON.stringify($scope.report, null, 4);
				saveContents(contents);
			}
		};

		messageHub.onEditorFocusGain(function (msg) {
			if (msg.resourcePath === $scope.dataParameters.file) messageHub.setStatusCaret('');
		});

		messageHub.onEditorReloadParameters(
			function (event) {
				$scope.$apply(() => {
					if (event.resourcePath === $scope.dataParameters.file) {
						$scope.dataParameters = ViewParameters.get();
					}
				});
			}
		);

		messageHub.onDidReceiveMessage(
			"editor.file.save.all",
			function () {
				if (!$scope.state.error && $scope.forms.editor.$valid) {
					let report = JSON.stringify($scope.report, null, 4);
					if (contents !== report) {
						$scope.save();
					}
				}
			},
			true,
		);

		messageHub.onDidReceiveMessage(
			"editor.file.save",
			function (msg) {
				if (!$scope.state.error && $scope.forms.editor.$valid) {
					let file = msg.data && typeof msg.data === 'object' && msg.data.file;
					if (file && file === $scope.dataParameters.file) {
						let report = JSON.stringify($scope.report, null, 4);
						if (contents !== report) $scope.save();
					}
				}
			},
			true,
		);

		$scope.$watch('report', function () {
			if (!$scope.state.error) {
				let report = JSON.stringify($scope.report, null, 4);
				messageHub.setEditorDirty($scope.dataParameters.file, contents !== report);
				$scope.generateQuery();
			}
		}, true);

		// Begin Columns Section
		messageHub.onDidReceiveMessage(
			"reportEditor.column.add",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						if (!$scope.report.columns) $scope.report.columns = [];
						$scope.report.columns.push({
							table: msg.data.formData[0].value,
							alias: msg.data.formData[1].value,
							name: msg.data.formData[2].value,
							type: msg.data.formData[3].value,
							aggregate: msg.data.formData[4].value
						});
					});
				}
				messageHub.hideFormDialog("reportEditorAddColumn");
			},
			true
		);

		messageHub.onDidReceiveMessage(
			"reportEditor.column.edit",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						$scope.report.columns[$scope.editColumnIndex].table = msg.data.formData[0].value;
						$scope.report.columns[$scope.editColumnIndex].alias = msg.data.formData[1].value;
						$scope.report.columns[$scope.editColumnIndex].name = msg.data.formData[2].value;
						$scope.report.columns[$scope.editColumnIndex].type = msg.data.formData[3].value;
						$scope.report.columns[$scope.editColumnIndex].aggregate = msg.data.formData[4].value;
					});
				}
				messageHub.hideFormDialog("reportEditorEditColumn");
			},
			true
		);

		$scope.addColumn = function () {
			let excludedAliases = [];
			let excludedNames = [];
			if ($scope.report.columns) {
				for (let i = 0; i < $scope.report.columns.length; i++) {
					excludedAliases.push($scope.report.columns[i].alias);
				}
				for (let i = 0; i < $scope.report.columns.length; i++) {
					excludedNames.push($scope.report.columns[i].name);
				}
			}
			messageHub.showFormDialog(
				"reportEditorAddColumn",
				"Add column",
				[{
					id: "teiTable",
					type: "input",
					label: "Table Alias",
					required: true,
					placeholder: "Enter table alias",
					minlength: 1,
					maxlength: 255,
					value: '',
				},
				{
					id: "teiAlias",
					type: "input",
					label: "Column Alias",
					required: true,
					placeholder: "Enter column alias",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedAliases,
					},
					value: '',
				},
				{
					id: "teiName",
					type: "input",
					label: "Column Name",
					required: true,
					placeholder: "Enter column name",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedNames,
					},
					value: '',
				},
				{
					id: "tedType",
					type: "dropdown",
					label: "Column Type",
					required: true,
					value: $scope.types[0].value,
					items: $scope.types,
				},
				{
					id: "tedAggregate",
					type: "dropdown",
					label: "Aggregate Function",
					required: true,
					value: $scope.aggregates[0].value,
					items: $scope.aggregates,
				}],
				[{
					id: "b1",
					type: "emphasized",
					label: "Add",
					whenValid: true,
				},
				{
					id: "b2",
					type: "transparent",
					label: "Cancel",
				}],
				"reportEditor.column.add",
				"Adding parameter..."
			);
		};

		$scope.editColumn = function (index) {
			$scope.editColumnIndex = index;
			let excludedAliases = [];
			for (let i = 0; i < $scope.report.columns.length; i++) {
				excludedAliases.push($scope.report.columns[i].alias);
			}
			let excludedNames = [];
			for (let i = 0; i < $scope.report.columns.length; i++) {
				if (i !== index)
					excludedNames.push($scope.report.columns[i].name);
			}
			messageHub.showFormDialog(
				"reportEditorEditColumn",
				"Edit column",
				[{
					id: "teiTable",
					type: "input",
					label: "Table Alias",
					required: true,
					placeholder: "Enter table alias",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedAliases,
					},
					value: $scope.report.columns[index].table,
				},
				{
					id: "teiAlias",
					type: "input",
					label: "Column Alias",
					required: true,
					placeholder: "Enter column alias",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedAliases,
					},
					value: $scope.report.columns[index].alias,
				},
				{
					id: "teiName",
					type: "input",
					label: "Column Name",
					required: true,
					placeholder: "Enter column name",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedNames,
					},
					value: $scope.report.columns[index].name,
				},
				{
					id: "tedType",
					type: "dropdown",
					label: "Column Type",
					required: true,
					value: $scope.report.columns[index].type,
					items: $scope.types,
				},
				{
					id: "tedAggregate",
					type: "dropdown",
					label: "Aggregate Function",
					required: true,
					value: $scope.report.columns[index].aggregate,
					items: $scope.aggregates,
				}],
				[{
					id: "b1",
					type: "emphasized",
					label: "Update",
					whenValid: true,
				},
				{
					id: "b2",
					type: "transparent",
					label: "Cancel",
				}],
				"reportEditor.column.edit",
				"Updating parameter..."
			);
		};

		$scope.deleteColumn = function (index) {
			messageHub.showDialogAsync(
				`Delete ${$scope.report.columns[index].name}?`,
				'This action cannot be undone.',
				[{
					id: 'b1',
					type: 'negative',
					label: 'Delete',
				},
				{
					id: 'b2',
					type: 'transparent',
					label: 'Cancel',
				}],
			).then(function (dialogResponse) {
				if (dialogResponse.data === 'b1') {
					$scope.$apply(function () {
						$scope.report.columns.splice(index, 1);
					});
				}
			});
		};
		// End Columns Section

		// Begin Joins Section
		messageHub.onDidReceiveMessage(
			"reportEditor.join.add",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						if (!$scope.report.joins) $scope.report.joins = [];
						$scope.report.joins.push({
							alias: msg.data.formData[0].value,
							name: msg.data.formData[1].value,
							type: msg.data.formData[2].value,
							condition: msg.data.formData[3].value
						});
					});
				}
				messageHub.hideFormDialog("reportEditorAddJoin");
			},
			true
		);

		messageHub.onDidReceiveMessage(
			"reportEditor.join.edit",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						$scope.report.joins[$scope.editJoinIndex].alias = msg.data.formData[0].value;
						$scope.report.joins[$scope.editJoinIndex].name = msg.data.formData[1].value;
						$scope.report.joins[$scope.editJoinIndex].type = msg.data.formData[2].value;
						$scope.report.joins[$scope.editJoinIndex].condition = msg.data.formData[3].value;
					});
				}
				messageHub.hideFormDialog("reportEditorEditJoin");
			},
			true
		);

		$scope.addJoin = function () {
			let excludedAliases = [];
			let excludedNames = [];
			if ($scope.report.joins) {
				for (let i = 0; i < $scope.report.joins.length; i++) {
					excludedAliases.push($scope.report.joins[i].alias);
				}
				for (let i = 0; i < $scope.report.joins.length; i++) {
					excludedNames.push($scope.report.joins[i].name);
				}
			}
			messageHub.showFormDialog(
				"reportEditorAddJoin",
				"Add join",
				[{
					id: "teiTable",
					type: "input",
					label: "Table Alias",
					required: true,
					placeholder: "Enter table alias",
					minlength: 1,
					maxlength: 255,
					value: '',
				},
				{
					id: "teiName",
					type: "input",
					label: "Table Name",
					required: true,
					placeholder: "Enter table name",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedNames,
					},
					value: '',
				},
				{
					id: "tedType",
					type: "dropdown",
					label: "Join Type",
					required: true,
					value: $scope.types[0].value,
					items: $scope.joins,
				},
				{
					id: "teiCondition",
					type: "input",
					label: "Join Condition",
					required: true,
					placeholder: "Enter join condition",
					minlength: 1,
					maxlength: 255,
					value: '',
				}],
				[{
					id: "b1",
					type: "emphasized",
					label: "Add",
					whenValid: true,
				},
				{
					id: "b2",
					type: "transparent",
					label: "Cancel",
				}],
				"reportEditor.join.add",
				"Adding parameter..."
			);
		};

		$scope.editJoin = function (index) {
			$scope.editJoinIndex = index;
			let excludedAliases = [];
			for (let i = 0; i < $scope.report.joins.length; i++) {
				excludedAliases.push($scope.report.joins[i].alias);
			}
			let excludedNames = [];
			for (let i = 0; i < $scope.report.joins.length; i++) {
				if (i !== index)
					excludedNames.push($scope.report.joins[i].name);
			}
			messageHub.showFormDialog(
				"reportEditorEditJoin",
				"Edit join",
				[{
					id: "teiTable",
					type: "input",
					label: "Table Alias",
					required: true,
					placeholder: "Enter table alias",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedAliases,
					},
					value: $scope.report.joins[index].alias,
				},
				{
					id: "teiName",
					type: "input",
					label: "Table Name",
					required: true,
					placeholder: "Enter table name",
					minlength: 1,
					maxlength: 255,
					inputRules: {
						excluded: excludedNames,
					},
					value: $scope.report.joins[index].name,
				},
				{
					id: "tedType",
					type: "dropdown",
					label: "Join Type",
					required: true,
					value: $scope.report.joins[index].type,
					items: $scope.joins,
				},
				{
					id: "teiCondition",
					type: "input",
					label: "Join Condition",
					required: true,
					placeholder: "Enter join condition",
					minlength: 1,
					maxlength: 255,
					value: $scope.report.joins[index].condition,
				}],
				[{
					id: "b1",
					type: "emphasized",
					label: "Update",
					whenValid: true,
				},
				{
					id: "b2",
					type: "transparent",
					label: "Cancel",
				}],
				"reportEditor.join.edit",
				"Updating parameter..."
			);
		};

		$scope.deleteJoin = function (index) {
			messageHub.showDialogAsync(
				`Delete ${$scope.report.joins[index].name}?`,
				'This action cannot be undone.',
				[{
					id: 'b1',
					type: 'negative',
					label: 'Delete',
				},
				{
					id: 'b2',
					type: 'transparent',
					label: 'Cancel',
				}],
			).then(function (dialogResponse) {
				if (dialogResponse.data === 'b1') {
					$scope.$apply(function () {
						$scope.report.joins.splice(index, 1);
					});
				}
			});
		};
		// End Joins Section

		// Begin Conditions Section
		messageHub.onDidReceiveMessage(
			"reportEditor.condition.add",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						if (!$scope.report.conditions) $scope.report.conditions = [];
						$scope.report.conditions.push({
							left: msg.data.formData[0].value,
							operation: msg.data.formData[1].value,
							right: msg.data.formData[2].value
						});
					});
				}
				messageHub.hideFormDialog("reportEditorAddCondition");
			},
			true
		);

		messageHub.onDidReceiveMessage(
			"reportEditor.condition.edit",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						$scope.report.conditions[$scope.editConditionIndex].left = msg.data.formData[0].value;
						$scope.report.conditions[$scope.editConditionIndex].operation = msg.data.formData[1].value;
						$scope.report.conditions[$scope.editConditionIndex].right = msg.data.formData[2].value;
					});
				}
				messageHub.hideFormDialog("reportEditorEditCondition");
			},
			true
		);

		$scope.addCondition = function () {
			messageHub.showFormDialog(
				"reportEditorAddCondition",
				"Add condition",
				[{
					id: "teiLeft",
					type: "input",
					label: "Left",
					required: true,
					placeholder: "Enter left operand",
					minlength: 1,
					maxlength: 255,
					value: '',
				},
				{
					id: "tedOperation",
					type: "dropdown",
					label: "Operation",
					required: true,
					value: $scope.operations[0].value,
					items: $scope.operations,
				},
				{
					id: "teiRight",
					type: "input",
					label: "Right",
					required: true,
					placeholder: "Enter right operand",
					minlength: 1,
					maxlength: 255,
					value: '',
				}],
				[{
					id: "b1",
					type: "emphasized",
					label: "Add",
					whenValid: true,
				},
				{
					id: "b2",
					type: "transparent",
					label: "Cancel",
				}],
				"reportEditor.condition.add",
				"Adding parameter..."
			);
		};

		$scope.editCondition = function (index) {
			$scope.editConditionIndex = index;
			messageHub.showFormDialog(
				"reportEditorEditCondition",
				"Edit condition",
				[{
					id: "teiLeft",
					type: "input",
					label: "Left",
					required: true,
					placeholder: "Enter left operand",
					minlength: 1,
					maxlength: 255,
					value: $scope.report.conditions[index].left,
				},
				{
					id: "tedOperation",
					type: "dropdown",
					label: "Type",
					required: true,
					value: $scope.report.conditions[index].operation,
					items: $scope.operations,
				},
				{
					id: "teiRight",
					type: "input",
					label: "Right",
					required: true,
					placeholder: "Enter right operand",
					minlength: 1,
					maxlength: 255,
					value: $scope.report.conditions[index].right,
				}],
				[{
					id: "b1",
					type: "emphasized",
					label: "Update",
					whenValid: true,
				},
				{
					id: "b2",
					type: "transparent",
					label: "Cancel",
				}],
				"reportEditor.condition.edit",
				"Updating parameter..."
			);
		};

		$scope.deleteCondition = function (index) {
			messageHub.showDialogAsync(
				`Delete ${$scope.report.conditions[index].name}?`,
				'This action cannot be undone.',
				[{
					id: 'b1',
					type: 'negative',
					label: 'Delete',
				},
				{
					id: 'b2',
					type: 'transparent',
					label: 'Cancel',
				}],
			).then(function (dialogResponse) {
				if (dialogResponse.data === 'b1') {
					$scope.$apply(function () {
						$scope.report.conditions.splice(index, 1);
					});
				}
			});
		};
		// End Conditions Section

		$scope.generateQuery = function () {
			$scope.query = "SELECT ";
			for (let i = 0; i < $scope.report.columns.length; i++) {
				if (i > 0) { $scope.query += ', ' }
				if ($scope.report.columns[i].aggregate !== undefined && $scope.report.columns[i].aggregate !== "NONE") {
					$scope.query += $scope.report.columns[i].aggregate + "(";
				}
				$scope.query += $scope.report.columns[i].table + "." + $scope.report.columns[i].name;
				if ($scope.report.columns[i].aggregate !== undefined && $scope.report.columns[i].aggregate !== "NONE") {
					$scope.query += ")";
				}
				$scope.query += ' as ' + $scope.report.columns[i].alias;
			}
			$scope.query += "\nFROM " + $scope.report.table + " as " + $scope.report.alias;

			for (let i = 0; i < $scope.report.joins.length; i++) {
				if (i > 0) { $scope.query += ', ' }
				$scope.query += "\n  " + $scope.report.joins[i].type + " JOIN " + $scope.report.joins[i].name + " " + $scope.report.joins[i].alias + " ON " + $scope.report.joins[i].condition;
			}

			$scope.query += "\nWHERE ";
			for (let i = 0; i < $scope.report.conditions.length; i++) {
				if (i > 0) { $scope.query += ', ' }
				$scope.query += $scope.report.conditions[i].left + " " + $scope.report.conditions[i].operation + " " + $scope.report.conditions[i].right;
			}
		}

		$scope.dataParameters = ViewParameters.get();
		if (!$scope.dataParameters.hasOwnProperty('file')) {
			$scope.state.error = true;
			$scope.errorMessage = "The 'file' data parameter is missing.";
		} else if (!$scope.dataParameters.hasOwnProperty('contentType')) {
			$scope.state.error = true;
			$scope.errorMessage = "The 'contentType' data parameter is missing.";
		} else $scope.load();
	});