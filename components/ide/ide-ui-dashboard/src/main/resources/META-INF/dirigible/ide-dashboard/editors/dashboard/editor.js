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
angular.module('page', ['ideUI', 'ideView', 'ideWorkspace'])
	.controller('PageController', function ($scope, $http, messageHub, $window, workspaceApi, ViewParameters) {
		let contents;
		$scope.errorMessage = 'An unknown error was encountered. Please see console for more information.';
		$scope.forms = {
			editor: {},
		};
		$scope.state = {
			isBusy: true,
			error: false,
			busyText: 'Loading...',
		};
		$scope.isFileChanged = false;
		$scope.editColumnIndex = 0;

		$scope.editSectionIndex = 0;
		$scope.editGroupIndex = 0;
		$scope.editItemIndex = 0;

		$scope.nameRegex = { patterns: ['^[a-zA-Z0-9_.:"-]*$'] };

		angular.element($window).bind("focus", function () {
			messageHub.setFocusedEditor($scope.dataParameters.file);
			messageHub.setStatusCaret('');
		});

		$scope.load = function () {
			if (!$scope.state.error) {
				workspaceApi.loadContent('', $scope.dataParameters.file).then(function (response) {
					if (response.status === 200) {
						if (response.data === '') $scope.dashboard = {};
						else $scope.dashboard = response.data;
						contents = JSON.stringify($scope.dashboard, null, 4);
						$scope.$apply(function () {
							$scope.state.isBusy = false;
						});
					} else {
						$scope.$apply(function () {
							$scope.state.error = true;
							$scope.errorMessage = "There was a problem with loading the file";
							$scope.state.isBusy = false;
						});
					}
				});
			}
		};

		function saveContents(text) {
			workspaceApi.saveContent('', $scope.dataParameters.file, text).then(function (response) {
				if (response.status === 200) {
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
						$scope.isFileChanged = false;
					});
				} else {
					console.error(`Error saving '${$scope.dataParameters.file}'`);
					messageHub.setStatusError(`Error saving '${$scope.dataParameters.file}'`);
					messageHub.showAlertError('Error while saving the file', 'Please look at the console for more information');
					$scope.$apply(function () {
						$scope.state.isBusy = false;
					});
				}
			});
		}

		$scope.save = function (_keySet, event) {
			if (event) event.preventDefault();
			if ($scope.forms.editor.$valid && !$scope.state.error) {
				$scope.state.busyText = "Saving...";
				$scope.state.isBusy = true;
				contents = JSON.stringify($scope.dashboard, null, 4);
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
					if (isFileChanged) {
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
						if (isFileChanged) $scope.save();
					}
				}
			},
			true,
		);

		$scope.$watch('dashboard', function () {
			if (!$scope.state.error && !$scope.state.isBusy) {
				const dashboard = JSON.stringify($scope.dashboard, null, 4);
				$scope.isFileChanged = contents !== dashboard;
				messageHub.setEditorDirty($scope.dataParameters.file, $scope.isFileChanged);
			}
		}, true);

		// Begin Section ----------------------------------------------------------------------------------

		messageHub.onDidReceiveMessage(
			"dashboardEditor.section.add",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						if (!$scope.dashboard.sections) $scope.dashboard.sections = [];
						$scope.dashboard.sections.push({
							name: msg.data.formData[0].value,
						});
					});
				}
				messageHub.hideFormDialog("dashboardEditorAddSection");
			},
			true
		);

		messageHub.onDidReceiveMessage(
			"dashboardEditor.section.edit",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						$scope.dashboard.sections[$scope.editSectionIndex].name = msg.data.formData[0].value;
					});
				}
				messageHub.hideFormDialog("dashboardEditorEditSection");
			},
			true
		);

		$scope.addSection = function () {
			let excludedNames = [];
			if ($scope.dashboard.sections) {
				for (let i = 0; i < $scope.dashboard.sections.length; i++) {
					excludedNames.push($scope.dashboard.sections[i].name);
				}
			}
			messageHub.showFormDialog(
				"dashboardEditorAddSection",
				"Add section",
				[
					{
						id: "teiName",
						type: "input",
						label: "Section Name",
						required: true,
						placeholder: "Enter section name",
						minlength: 1,
						maxlength: 255,
						inputRules: {
							excluded: excludedNames,
						},
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
				"dashboardEditor.section.add",
				"Adding parameter..."
			);
		};

		$scope.editSection = function (index) {
			$scope.editSectionIndex = index;
			let excludedNames = [];
			for (let i = 0; i < $scope.dashboard.sections.length; i++) {
				if (i !== index)
					excludedNames.push($scope.dashboard.sections[i].name);
			}
			messageHub.showFormDialog(
				"dashboardEditorEditSection",
				"Edit section",
				[
					{
						id: "teiName",
						type: "input",
						label: "Section Name",
						required: true,
						placeholder: "Enter section name",
						minlength: 1,
						maxlength: 255,
						inputRules: {
							excluded: excludedNames,
						},
						value: $scope.dashboard.sections[index].name,
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
				"dashboardEditor.section.edit",
				"Updating parameter..."
			);
		};

		$scope.deleteSection = function (index) {
			messageHub.showDialogAsync(
				`Delete ${$scope.dashboard.sections[index].name}?`,
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
						$scope.dashboard.sections.splice(index, 1);
					});
				}
			});
		};

		// End Section ----------------------------------------------------------------------------------

		// Begin Group ----------------------------------------------------------------------------------

		messageHub.onDidReceiveMessage(
			"dashboardEditor.group.add",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						if (!$scope.dashboard.sections[$scope.editSectionIndex].groups) $scope.dashboard.sections[$scope.editSectionIndex].groups = [];
						$scope.dashboard.sections[$scope.editSectionIndex].groups.push({
							name: msg.data.formData[0].value,
							icon: msg.data.formData[1].value,
						});
					});
				}
				messageHub.hideFormDialog("dashboardEditorAddGroup");
			},
			true
		);

		messageHub.onDidReceiveMessage(
			"dashboardEditor.group.edit",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].name = msg.data.formData[0].value;
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].icon = msg.data.formData[1].value;
					});
				}
				messageHub.hideFormDialog("dashboardEditorEditGroup");
			},
			true
		);

		$scope.addGroup = function (sectionIndex) {
			$scope.editSectionIndex = sectionIndex;
			let excludedNames = [];
			if ($scope.dashboard.sections[$scope.editSectionIndex].groups) {
				for (let i = 0; i < $scope.dashboard.sections[$scope.editSectionIndex].groups.length; i++) {
					excludedNames.push($scope.dashboard.sections[$scope.editSectionIndex].groups[i].name);
				}
			}
			messageHub.showFormDialog(
				"dashboardEditorAddGroup",
				"Add group",
				[
					{
						id: "teiName",
						type: "input",
						label: "Group Name",
						required: true,
						placeholder: "Enter group name",
						minlength: 1,
						maxlength: 255,
						inputRules: {
							excluded: excludedNames,
						},
						value: '',
					},
					{
						id: "teiIcon",
						type: "input",
						label: "Icon Name",
						required: true,
						placeholder: "Enter icon name",
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
				"dashboardEditor.group.add",
				"Adding parameter..."
			);
		};

		$scope.editGroup = function (sectionIndex, index) {
			$scope.editSectionIndex = sectionIndex;
			$scope.editGroupIndex = index;
			let excludedNames = [];
			for (let i = 0; i < $scope.dashboard.sections[$scope.editSectionIndex].groups.length; i++) {
				if (i !== index)
					excludedNames.push($scope.dashboard.sections[$scope.editSectionIndex].groups[i].name);
			}
			messageHub.showFormDialog(
				"dashboardEditorEditGroup",
				"Edit group",
				[
					{
						id: "teiName",
						type: "input",
						label: "Group Name",
						required: true,
						placeholder: "Enter group name",
						minlength: 1,
						maxlength: 255,
						inputRules: {
							excluded: excludedNames,
						},
						value: $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].name,
					},
					{
						id: "teiIcon",
						type: "input",
						label: "Icon Name",
						required: true,
						placeholder: "Enter icon name",
						minlength: 1,
						maxlength: 255,
						value: $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].icon,
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
				"dashboardEditor.group.edit",
				"Updating parameter..."
			);
		};

		$scope.deleteGroup = function (sectionIndex, index) {
			$scope.editSectionIndex = sectionIndex;
			$scope.editGroupIndex = index;
			messageHub.showDialogAsync(
				`Delete ${$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].name}?`,
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
						$scope.dashboard.sections[$scope.editSectionIndex].groups.splice($scope.editGroupIndex, 1);
					});
				}
			});
		};

		// End Group ----------------------------------------------------------------------------------

		// Begin Item ----------------------------------------------------------------------------------

		messageHub.onDidReceiveMessage(
			"dashboardEditor.item.add",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						if (!$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items) $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items = [];
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items.push({
							name: msg.data.formData[0].value,
							view: msg.data.formData[1].value,
							link: msg.data.formData[2].value,
						});
					});
				}
				messageHub.hideFormDialog("dashboardEditorAddItem");
			},
			true
		);

		messageHub.onDidReceiveMessage(
			"dashboardEditor.item.edit",
			function (msg) {
				if (msg.data.buttonId === "b1") {
					$scope.$apply(function () {
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].name = msg.data.formData[0].value;
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].view = msg.data.formData[1].value;
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].link = msg.data.formData[2].value;
					});
				}
				messageHub.hideFormDialog("dashboardEditorEditItem");
			},
			true
		);

		$scope.addItem = function (sectionIndex, groupIndex) {
			$scope.editSectionIndex = sectionIndex;
			$scope.editGroupIndex = groupIndex;
			let excludedNames = [];
			if ($scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items) {
				for (let i = 0; i < $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items.length; i++) {
					excludedNames.push($scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[i].name);
				}
			}
			messageHub.showFormDialog(
				"dashboardEditorAddItem",
				"Add item",
				[
					{
						id: "teiName",
						type: "input",
						label: "Item Name",
						required: true,
						placeholder: "Enter item name",
						minlength: 1,
						maxlength: 255,
						inputRules: {
							excluded: excludedNames,
						},
						value: '',
					},
					{
						id: "teiView",
						type: "input",
						label: "Item View",
						required: true,
						placeholder: "Enter view name",
						minlength: 1,
						maxlength: 100,
						value: '',
					},
					{
						id: "teiLink",
						type: "input",
						label: "Item Link",
						required: true,
						placeholder: "Enter link name",
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
				"dashboardEditor.item.add",
				"Adding parameter..."
			);
		};

		$scope.editItem = function (sectionIndex, groupIndex, index) {
			$scope.editSectionIndex = sectionIndex;
			$scope.editGroupIndex = groupIndex;
			$scope.editItemIndex = index;
			let excludedNames = [];
			for (let i = 0; i < $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items.length; i++) {
				if (i !== index)
					excludedNames.push($scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[i].name);
			}
			messageHub.showFormDialog(
				"dashboardEditorEditItem",
				"Edit item",
				[
					{
						id: "teiName",
						type: "input",
						label: "Item Name",
						required: true,
						placeholder: "Enter item name",
						minlength: 1,
						maxlength: 255,
						inputRules: {
							excluded: excludedNames,
						},
						value: $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].name,
					},
					{
						id: "teiView",
						type: "input",
						label: "Item view",
						required: true,
						placeholder: "Enter item view",
						minlength: 1,
						maxlength: 255,
						value: $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].view,
					},
					{
						id: "teiLink",
						type: "input",
						label: "Item Link",
						required: true,
						placeholder: "Enter item link",
						minlength: 1,
						maxlength: 255,
						value: $scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].link,
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
				"dashboardEditor.item.edit",
				"Updating parameter..."
			);
		};

		$scope.deleteItem = function (sectionIndex, groupIndex, index) {
			$scope.editSectionIndex = sectionIndex;
			$scope.editGroupIndex = groupIndex;
			$scope.editItemIndex = index;
			messageHub.showDialogAsync(
				`Delete ${$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items[$scope.editItemIndex].name}?`,
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
						$scope.dashboard.sections[$scope.editSectionIndex].groups[$scope.editGroupIndex].items.splice($scope.editItemIndex, 1);
					});
				}
			});
		};

		// End Item ----------------------------------------------------------------------------------


		$scope.dataParameters = ViewParameters.get();
		if (!$scope.dataParameters.hasOwnProperty('file')) {
			$scope.state.error = true;
			$scope.errorMessage = "The 'file' data parameter is missing.";
		} else if (!$scope.dataParameters.hasOwnProperty('contentType')) {
			$scope.state.error = true;
			$scope.errorMessage = "The 'contentType' data parameter is missing.";
		} else $scope.load();




	});