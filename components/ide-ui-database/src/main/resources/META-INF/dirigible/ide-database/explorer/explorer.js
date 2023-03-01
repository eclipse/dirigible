/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
// messageHub.fireFileOpen = function (fileDescriptor) {
// 	messageHub.post({ data: fileDescriptor }, 'fileselected');
// }
let database = angular.module('database', ['ideUI', 'ideView']);
database.controller('DatabaseController', function ($scope, $http, messageHub) {
	let databasesSvcUrl = "/services/data/";
	$scope.selectedDatabase;
	$scope.jstreeWidget = angular.element('#dgDatabases');
	$scope.spinnerColumns = {
		text: "Loading Columns...",
		type: "spinner",
		li_attr: { spinner: true },
	};
	$scope.spinnerIndices = {
		text: "Loading Indices...",
		type: "spinner",
		li_attr: { spinner: true },
	};
	$scope.jstreeConfig = {
		core: {
			check_callback: true,
			themes: {
				name: "fiori",
				variant: "compact",
			},
			data: []
		},
		search: {
			case_sensitive: false,
		},
		plugins: ['wholerow', 'state', 'types', 'dnd', 'unique', 'contextmenu'],
		dnd: {
			large_drop_target: true,
			large_drag_target: true,
		},
		state: { key: 'ide-databases' },
		types: {
			'default': {
				icon: "jstree-file",
			},
			folder: {
				icon: "jstree-folder",
			},
			file: {
				icon: "jstree-file",
			},
			project: {
				icon: "jstree-project",
			},
			table: {
				icon: "sap-icon--table-view",
			},
			tableView: {
				icon: "sap-icon--grid",
			},
			tableLock: {
				icon: "sap-icon--locked",
			},
			column: {
				icon: "sap-icon--table-column",
			},
			columns: {
				icon: "sap-icon--table-column",
			},
			indice: {
				icon: "sap-icon--table-row",
			},
			indices: {
				icon: "sap-icon--table-row",
			},
			schema: {
				icon: "sap-icon--database",
			},
			procedure: {
				icon: "sap-icon--workflow-tasks",
			},
			'function': {
				icon: "sap-icon--settings",
			},
			varchar: {
				icon: "sap-icon--sort-ascending",
			},
			nvarchar: {
				icon: "sap-icon--sort-ascending",
			},
			char: {
				icon: "sap-icon--text",
			},
			date: {
				icon: "sap-icon--calendar",
			},
			datetime: {
				icon: "sap-icon--date-time",
			},
			timestamp: {
				icon: "sap-icon--date-time",
			},
			smallint: {
				icon: "sap-icon--numbered-text",
			},
			tinyint: {
				icon: "sap-icon--numbered-text",
			},
			integer: {
				icon: "sap-icon--numbered-text",
			},
			float: {
				icon: "sap-icon--measuring-point",
			},
			double: {
				icon: "sap-icon--measuring-point",
			},
			decimal: {
				icon: "sap-icon--measuring-point",
			},
			bigint: {
				icon: "sap-icon--trend-up",
			},
			boolean: {
				icon: "sap-icon--checklist-item",
			},
			clob: {
				icon: "sap-icon--rhombus-milestone",
			},
			blob: {
				icon: "sap-icon--rhombus-milestone",
			},
			key: {
				icon: "sap-icon--key",
			},
			spinner: {
				icon: "jstree-spinner",
				valid_children: [],
			},
		},
		contextmenu: {
			items: function (node) {
				let ctxmenu = {};

				// Select contents
				if (node.original.type === 'table'
					|| node.original.type === 'base table'
					|| node.original.type === 'view') {
					ctxmenu.contents = {
						"separator_before": false,
						"label": "Show Contents",
						"action": function (data) {
							let tree = $.jstree.reference(data.reference);
							let node = tree.get_node(data.reference);
							let parentNodeName = tree.get_text(node.parent);
							let sqlCommand = "SELECT * FROM \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\"";
							messageHub.postMessage('database.sql.execute', sqlCommand);
						}.bind(this)
					};

					// Generate scripts
					// debugger
					let tree = $scope.jstreeWidget.jstree(true);
					let columnsMaybe = tree.get_node(node.children[0]);
					let internalMaybe = null;
					if (columnsMaybe) {
						internalMaybe = tree.get_node(columnsMaybe.children[0]);
					}
					// Select
					if ((node.original.type === 'table' || node.original.type === 'base table' || node.original.type === 'view')
						&& (internalMaybe !== null && internalMaybe.text !== "Loading Columns..." && internalMaybe.text !== "Loading Indices...")) {
						ctxmenu.selectScript = {
							"separator_before": true,
							"label": "Select",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								let parentNodeName = tree.get_text(node.parent);
								let columns = tree.get_node(node.children[0]);
								let sqlCommand = "SELECT ";
								for (let i = 0; i < columns.children.length; i++) {
									sqlCommand += tree.get_node(columns.children[i]).original.column.name;
									sqlCommand += ", ";
								}
								sqlCommand = sqlCommand.substring(0, sqlCommand.length - 2);
								sqlCommand += " FROM \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\"";
								messageHub.postMessage('database.sql.script', sqlCommand);
								$scope.jstreeWidget.jstree(true).refresh();

							}.bind(this)
						};
					}

					// Insert
					if ((node.original.type === 'table' || node.original.type === 'base table')
						&& (internalMaybe !== null && internalMaybe.text !== "Loading Columns..." && internalMaybe.text !== "Loading Indices...")) {
						ctxmenu.insertScript = {
							"separator_before": false,
							"label": "Insert",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								let parentNodeName = tree.get_text(node.parent);
								let columns = tree.get_node(node.children[0]);
								let sqlCommand = "INSERT INTO \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\" (";
								for (let i = 0; i < columns.children.length; i++) {
									sqlCommand += tree.get_node(columns.children[i]).original.column.name;
									sqlCommand += ", ";
								}
								sqlCommand = sqlCommand.substring(0, sqlCommand.length - 2);
								sqlCommand += ") VALUES (";
								for (let i = 0; i < columns.children.length; i++) {
									sqlCommand += "'" + tree.get_node(columns.children[i]).original.column.type;
									sqlCommand += "', ";
								}
								sqlCommand = sqlCommand.substring(0, sqlCommand.length - 2);
								sqlCommand += ")";
								messageHub.postMessage('database.sql.script', sqlCommand);
								$scope.jstreeWidget.jstree(true).refresh();

							}.bind(this)
						};
					}

					// Update
					if ((node.original.type === 'table' || node.original.type === 'base table')
						&& (internalMaybe !== null && internalMaybe.text !== "Loading Columns..." && internalMaybe.text !== "Loading Indices...")) {
						ctxmenu.updateScript = {
							"separator_before": false,
							"label": "Update",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								let parentNodeName = tree.get_text(node.parent);
								let columns = tree.get_node(node.children[0]);
								let sqlCommand = "UPDATE \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\" SET ";
								for (let i = 0; i < columns.children.length; i++) {
									sqlCommand += tree.get_node(columns.children[i]).original.column.name +
										" = '" + tree.get_node(columns.children[i]).original.column.type
									sqlCommand += "', ";
								}
								sqlCommand = sqlCommand.substring(0, sqlCommand.length - 2);
								sqlCommand += " WHERE ";
								for (let i = 0; i < columns.children.length; i++) {
									sqlCommand += tree.get_node(columns.children[i]).original.column.name +
										" = '" + tree.get_node(columns.children[i]).original.column.type
									sqlCommand += "' AND ";
								}
								sqlCommand = sqlCommand.substring(0, sqlCommand.length - 5);
								messageHub.postMessage('database.sql.script', sqlCommand);
								$scope.jstreeWidget.jstree(true).refresh();

							}.bind(this)
						};
					}

					// Delete
					if ((node.original.type === 'table' || node.original.type === 'base table')
						&& (internalMaybe !== null && internalMaybe.text !== "Loading Columns..." && internalMaybe.text !== "Loading Indices...")) {
						ctxmenu.deleteScript = {
							"separator_before": false,
							"label": "Delete",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								let parentNodeName = tree.get_text(node.parent);
								let columns = tree.get_node(node.children[0]);
								let sqlCommand = "DELETE FROM \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\"";
								sqlCommand += " WHERE ";
								for (let i = 0; i < columns.children.length; i++) {
									sqlCommand += tree.get_node(columns.children[i]).original.column.name +
										" = '" + tree.get_node(columns.children[i]).original.column.type
									sqlCommand += "' AND ";
								}
								sqlCommand = sqlCommand.substring(0, sqlCommand.length - 5);
								messageHub.postMessage('database.sql.script', sqlCommand);
								$scope.jstreeWidget.jstree(true).refresh();

							}.bind(this)
						};
					}

					// Drop table
					if (node.original.type === 'table' || node.original.type === 'base table') {
						ctxmenu.dropScript = {
							"separator_before": false,
							"label": "Drop",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								let parentNodeName = tree.get_text(node.parent);
								let sqlCommand = "DROP TABLE \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\"";
								messageHub.postMessage('database.sql.script', sqlCommand);
							}.bind(this)
						};
					}
					// Drop view
					if (node.original.type === 'view') {
						ctxmenu.dropScript = {
							"separator_before": false,
							"label": "Drop",
							"action": function (data) {
								let tree = $.jstree.reference(data.reference);
								let node = tree.get_node(data.reference);
								let parentNodeName = tree.get_text(node.parent);
								let sqlCommand = "DROP VIEW \"" + parentNodeName + "\"" + "." + "\"" + node.original.text + "\"";
								messageHub.postMessage('database.sql.script', sqlCommand);
							}.bind(this)
						};
					}

					ctxmenu.exportData = {
						"separator_before": true,
						"label": "Export Data",
						"action": function (data) {
							let tree = $.jstree.reference(data.reference);
							let node = tree.get_node(data.reference);
							let parentNodeName = tree.get_text(node.parent);
							let sqlCommand = parentNodeName + "." + node.original.text;
							messageHub.postMessage('database.data.export.artifact', sqlCommand);
						}.bind(this)
					};
					ctxmenu.exportMetadata = {
						"separator_before": false,
						"label": "Export Metadata",
						"action": function (data) {
							let tree = $.jstree.reference(data.reference);
							let node = tree.get_node(data.reference);
							let parentNodeName = tree.get_text(node.parent);
							let sqlCommand = parentNodeName + "." + node.original.text;
							messageHub.postMessage('database.metadata.export.artifact', sqlCommand);
						}.bind(this)
					};

				}

				// Procedure related actions
				if (node.original.kind === 'procedure') {
					ctxmenu.dropProcedure = {
						"separator_before": false,
						"label": "Drop",
						"action": function (data) {
							let tree = $.jstree.reference(data.reference);
							let node = tree.get_node(data.reference);
							let sqlCommand = "DROP PROCEDURE \"" + node.original.text + "\"";
							messageHub.postMessage('database.sql.script', sqlCommand);
						}.bind(this)
					};
				}

				// Schema related actions
				if (node.original.kind === 'schema') {
					ctxmenu.exportData = {
						"separator_before": false,
						"label": "Export Data",
						"action": function (data) {
							let tree = $.jstree.reference(data.reference);
							let node = tree.get_node(data.reference);
							let sqlCommand = node.original.text;
							messageHub.postMessage('database.data.export.schema', sqlCommand);
						}.bind(this)
					};
					ctxmenu.exportMetadata = {
						"separator_before": false,
						"label": "Export Metadata",
						"action": function (data) {
							let tree = $.jstree.reference(data.reference);
							let node = tree.get_node(data.reference);
							let sqlCommand = node.original.text;
							messageHub.postMessage('database.metadata.export.schema', sqlCommand);
						}.bind(this)
					};
				}
				return ctxmenu;
			}
		}
	};

	$scope.jstreeWidget.on('open_node.jstree', function (event, data) {
		if (data.node.children.length === 1 && $scope.jstreeWidget.jstree(true).get_text(data.node.children[0]) === "Loading Columns...") {
			expandColumns(event, data);
		} else if (data.node.children.length === 1 && $scope.jstreeWidget.jstree(true).get_text(data.node.children[0]) === "Loading Indices...") {
			expandIndices(event, data);
		}
	});

	function getDatabases() {
		$http.get(databasesSvcUrl)
			.then(function (data) {
				$scope.databases = data.data;
				if ($scope.databases.length > 0) {
					let storedDatabase = JSON.parse(localStorage.getItem('DIRIGIBLE.database'));
					if (storedDatabase !== null) {
						$scope.selectedDatabase = storedDatabase.type;
					} else {
						$scope.selectedDatabase = $scope.databases[0];
					}
					if ($scope.selectedDatabase) {
						messageHub.postMessage('database.database.selection.changed', $scope.selectedDatabase);
						$http.get(databasesSvcUrl + $scope.selectedDatabase + "/").then(function (data) {
							$scope.datasources = data.data;
							if ($scope.datasources.length > 0) {
								if (storedDatabase !== null) {
									$scope.selectedDatasource = storedDatabase.name;
								} else {
									$scope.selectedDatasource = $scope.datasources[0];
								}
								if ($scope.selectedDatasource) {
									messageHub.postMessage('database.datasource.selection.changed', $scope.selectedDatasource);
									$scope.refreshDatabase();
								}
							}
						});
					}
				}
			});
	}
	setTimeout(getDatabases, 500);

	let expandColumns = function (evt, data) {
		let parent = $scope.jstreeWidget.jstree(true).get_node(data.node);
		let tableParent = $scope.jstreeWidget.jstree(true).get_node(data.node.parent);
		let schemaParent = $scope.jstreeWidget.jstree(true).get_text(tableParent.parent);

		$scope.jstreeWidget.jstree("delete_node", $scope.jstreeWidget.jstree(true).get_node(data.node.children[0]));
		let position = 'last';

		$http.get(databasesSvcUrl + $scope.selectedDatabase + '/' + $scope.selectedDatasource
			+ '/' + schemaParent + '/' + tableParent.text + "?kind=" + tableParent.original.kind.toUpperCase())
			.then(function (data) {
				data.data.columns.forEach(function (column) {
					let icon = "sap-icon--grid";
					if (column.key) {
						icon = "sap-icon--key";
					} else {
						switch (column.type.toLowerCase()) {
							case "character varying":
							case "varchar":
							case "nvarchar":
							case "char":
								icon = "sap-icon--text";
								break;
							case "date":
								icon = "sap-icon--calendar";
								break;
							case "datetime":
							case "timestamp":
								icon = "sap-icon--date-time";
								break;
							case "bigint":
							case "smallint":
							case "tinyint":
							case "integer":
								icon = "sap-icon--numbered-text";
								break;
							case "float":
							case "double":
							case "double precision":
							case "decimal":
								icon = "sap-icon--measuring-point"
								break;
							case "boolean":
								icon = "sap-icon--sys-enter";
								break;
							case "clob":
							case "blob":
								icon = "sap-icon--rhombus-milestone";
								break;
						}
					}
					let nodeText = column.name + ' - <i style="font-size: smaller;">' + column.type + "(" + (column.size !== undefined ? column.size : (column.length !== undefined ? column.length : "N/A")) + ")</i>";
					let newNode = {
						id: parent.id + "$" + column.name,
						state: "open",
						text: nodeText,
						column: column,
						icon: icon
					};
					$scope.jstreeWidget.jstree("create_node", parent, newNode, position, false, false);
				})
			});
	}

	let expandIndices = function (evt, data) {
		let parent = $scope.jstreeWidget.jstree(true).get_node(data.node);
		let tableParent = $scope.jstreeWidget.jstree(true).get_node(data.node.parent);
		let schemaParent = $scope.jstreeWidget.jstree(true).get_text(tableParent.parent);

		$scope.jstreeWidget.jstree("delete_node", $scope.jstreeWidget.jstree(true).get_node(data.node.children[0]));
		let position = 'last';

		$http.get(databasesSvcUrl + $scope.selectedDatabase + '/' + $scope.selectedDatasource
			+ '/' + schemaParent + '/' + tableParent.text)
			.then(function (data) {
				data.data.indices.forEach(function (index) {
					let nodeText = index.name;
					let newNode = { state: "open", "text": nodeText, "id": parent.id + "$" + index.name, "icon": "sap-icon--bullet-text" };
					$scope.jstreeWidget.jstree("create_node", parent, newNode, position, false, false);
				})
			});
	}

	$scope.refreshDatabase = function () {
		if ($scope.jstreeWidget.jstree(true).settings === undefined) $scope.jstreeWidget.jstree($scope.jstreeConfig);
		if ($scope.selectedDatabase && $scope.selectedDatasource) {
			$http.get(databasesSvcUrl  + $scope.selectedDatabase + '/' + $scope.selectedDatasource)
				.then(function (data) {
					$scope.datasource = data.data;
					this.baseUrl = databasesSvcUrl + $scope.selectedDatabase + '/' + $scope.selectedDatasource;
					let schemas = $scope.datasource.schemas.map(function (schemas) {
						return build(schemas);
					})
					$scope.jstreeWidget.jstree(true).settings.core.data = schemas;
					$scope.jstreeWidget.jstree(true).refresh();
				}.bind(this));
		} else {
			$scope.jstreeWidget.jstree(true).settings.core.data = [];
			$scope.jstreeWidget.jstree(true).refresh();
		}
	};

	let build = function (f) {
		let children = [];
		let icon = 'sap-icon--grid';
		let name = f.name;
		if (f.kind == 'schema') {
			let tablesChildren = f.tables.map(function (_table) {
				return build(_table)
			});
			children = children.concat(tablesChildren);

			let proceduresChildren = f.procedures.map(function (_procedure) {
				return build(_procedure)
			});
			children = children.concat(proceduresChildren);

			let functionsChildren = f.functions.map(function (_function) {
				return build(_function)
			});
			children = children.concat(functionsChildren);

			icon = 'sap-icon--database';
		} else if (f.kind == 'table' && (f.type === 'TABLE' || f.type === 'BASE TABLE')) {
			children = [
				{ text: "Columns", "icon": "sap-icon--table-column", children: [$scope.spinnerColumns] },
				{ text: "Indices", "icon": "sap-icon--table-row", children: [$scope.spinnerIndices] },
			];
			icon = 'sap-icon--table-view';
		} else if (f.kind == 'table' && f.type === 'VIEW') {
			children = [
				{ text: "Columns", "icon": "sap-icon--table-column", children: [$scope.spinnerColumns] },
				{ text: "Indices", "icon": "sap-icon--table-row", children: [$scope.spinnerIndices] },
			];
			icon = 'sap-icon--grid';
		} else if (f.kind == 'table' && f.type !== 'TABLE' && f.type !== 'VIEW') {
			children = [
				{ text: "Columns", "icon": "sap-icon--table-column", children: [$scope.spinnerColumns] },
				{ text: "Indices", "icon": "sap-icon--table-row", children: [$scope.spinnerIndices] },
			];
			icon = 'sap-icon--locked';
		} else if (f.kind == 'procedure') { // && f.type === 'XXX'
			children = [
				{ text: "Columns", "icon": "sap-icon--table-column", children: [$scope.spinnerColumns] },
			];
			icon = 'sap-icon--workflow-tasks';
		} else if (f.kind == 'function') { // && f.type === 'XXX'
			children = [
				{ text: "Columns", "icon": "sap-icon--table-column", children: [$scope.spinnerColumns] },
			];
			icon = 'sap-icon--settings';
		} else if (f.kind == 'column') {
			icon = 'sap-icon--grid';
			name += ` [<i>${data.type}</i>(<i>${data.size}</i>)]`;
		}
		f.label = f.name;
		return {
			"text": name,
			"children": children,
			"type": (f.type) ? f.type.toLowerCase() : f.kind,
			"kind": f.kind,
			"_file": f,
			"icon": icon
		}
	}

	$scope.isSelectedDatabase = function (name) {
		if ($scope.selectedDatabase === name) return true;
		return false;
	};

	$scope.isSelectedDatasource = function (name) {
		if ($scope.selectedDatasource === name) return true;
		return false;
	};

	$scope.switchDatabase = function (name) {
		$scope.selectedDatabase = name;
		$http.get(databasesSvcUrl + $scope.selectedDatabase)
			.then(function (data) {
				$scope.datasources = data.data;
				if ($scope.datasources[0]) {
					$scope.selectedDatasource = $scope.datasources[0];
					messageHub.postMessage('database.database.selection.changed', $scope.selectedDatabase);
					messageHub.postMessage('database.datasource.selection.changed', $scope.selectedDatasource);
					$scope.switchDatasource();
				} else {
					$scope.selectedDatasource = undefined;
				}
				$scope.refreshDatabase();
			});
	};

	$scope.switchDatasource = function (name) {
		if (name) $scope.selectedDatasource = name;
		localStorage.setItem('DIRIGIBLE.database', JSON.stringify({ "type": $scope.selectedDatabase, "name": $scope.selectedDatasource }));
		messageHub.postMessage('database.datasource.selection.changed', $scope.selectedDatasource);
		$scope.refreshDatabase();
	};

	$scope.runSQL = function () {
		messageHub.postMessage('database.sql.run', {});
	};

});

function confirmRemove(type, name) {
	return confirm("Do you really want to delete the " + type + ": " + name);
}
