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
let messageHub = new FramesMessageHub();

messageHub.fireFileOpen = function (fileDescriptor) {
	messageHub.post({ data: fileDescriptor }, 'fileselected');
}

angular.module('database', []).controller('DatabaseController', function ($scope, $http) {

	let databasesSvcUrl = "/services/v4/ide/databases";
	$scope.selectedDatabase;
	$scope.jstree;

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
						messageHub.post($scope.selectedDatabase, 'database.database.selection.changed');
						$http.get(databasesSvcUrl + "/" + $scope.selectedDatabase).then(function (data) {
							$scope.datasources = data.data;
							if ($scope.datasources.length > 0) {
								if (storedDatabase !== null) {
									$scope.selectedDatasource = storedDatabase.name;
								} else {
									$scope.selectedDatasource = $scope.datasources[0];
								}
								if ($scope.selectedDatasource) {
									messageHub.post($scope.selectedDatasource, 'database.datasource.selection.changed');
									$scope.refreshDatabase();
								}
							}
						});
					}
				}
			});
	}
	setTimeout(getDatabases, 500);

	$scope.refreshDatabase = function () {
		if ($scope.selectedDatabase && $scope.selectedDatasource) {
			$http.get(databasesSvcUrl + '/' + $scope.selectedDatabase + '/' + $scope.selectedDatasource)
				.then(function (data) {
					$scope.datasource = data.data;
					this.baseUrl = databasesSvcUrl + '/' + $scope.selectedDatabase + '/' + $scope.selectedDatasource;
					let schemas = $scope.datasource.schemas.map(function (schemas) {
						return build(schemas);
					})
					if ($scope.jstree) {
						$('.database').jstree(true).settings.core.data = schemas;
						$('.database').jstree(true).refresh();
					} else {
						$scope.jstree = $('.database').jstree({
							"core": {
								"data": schemas,
								"themes": {
									"name": "default",
									"responsive": false,
									"dots": false,
									"icons": true,
									"variant": "small",
									"stripes": true
								},
								'check_callback': function (o, n, p, i, m) {
									if (m && m.dnd && m.pos !== 'i') { return false; }
									if (o === "move_node" || o === "copy_node") {
										if (this.get_node(n).parent === this.get_node(p).id) { return false; }
									}
									return true;
								}
							},
							'types': {
								'default': {
									'icon': "fa fa-file"
								},
								'folder': {
									'icon': "fa fa-folder"
								},
								'file': {
									'icon': "fa fa-file"
								},
								'project': {
									'icon': "fa fa-folder"
								}
							},
							'contextmenu': {
								'items': function (node) {
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
												let sqlCommand = "SELECT * FROM \"" + node.original.text + "\"";
												messageHub.post({ data: sqlCommand }, 'database.sql.execute');
											}.bind(this)
										};
										// Drop table
										if (node.original.type === 'table' || node.original.type === 'base table') {
											ctxmenu.dropTable = {
												"separator_before": true,
												"label": "Drop Table",
												"action": function (data) {
													let tree = $.jstree.reference(data.reference);
													let node = tree.get_node(data.reference);
													if (confirmRemove("TABLE", node.original.text)) {
														let sqlCommand = "DROP TABLE \"" + node.original.text + "\"";
														messageHub.post({ data: sqlCommand }, 'database.sql.execute');
														$('.database').jstree(true).refresh();
													}
												}.bind(this)
											};
										}
										// Drop view
										if (node.original.type === 'view') {
											ctxmenu.dropTable = {
												"separator_before": true,
												"label": "Drop View",
												"action": function (data) {
													let tree = $.jstree.reference(data.reference);
													let node = tree.get_node(data.reference);
													if (confirmRemove("VIEW", node.original.text)) {
														let sqlCommand = "DROP VIEW \"" + node.original.text + "\"";
														messageHub.post({ data: sqlCommand }, 'database.sql.execute');
														$('.database').jstree(true).refresh();
													}
												}.bind(this)
											};
										}
									}

									// Procedure related actions
									if (node.original.kind === 'procedure') {
										ctxmenu.dropProcedure = {
											"separator_before": false,
											"label": "Drop Procedure",
											"action": function (data) {
												let tree = $.jstree.reference(data.reference);
												let node = tree.get_node(data.reference);
												if (confirmRemove("PROCEDURE", node.original.text)) {
													let sqlCommand = "DROP PROCEDURE \"" + node.original.text + "\"";
													messageHub.post({ data: sqlCommand }, 'database.sql.execute');
													$('.database').jstree(true).refresh();
												}
											}.bind(this)
										};
									}
									return ctxmenu;
								}
							},
							"plugins": ['state', 'dnd', 'types', 'contextmenu', 'unique']
						})
							.on('select_node.jstree', function (e, data) {
								//
							})
							.on('dblclick.jstree', function (evt, node) {
								let data = $('.database').jstree().get_selected(true);
								let kind = $('.database').jstree().get_node(evt.target).original.kind;
								// if(['table'].indexOf(type)<0)
								// 								messageHub.fireFileOpen(data[0].original._file);
							})
							.on('open_node.jstree', function (evt, data) {
								if (data.node.children.length === 1 && $('.database').jstree().get_node(data.node.children[0]).original === "Loading Columns...") {

									let parent = $('.database').jstree().get_node(data.node);
									let tableParent = $('.database').jstree().get_node(data.node.parent);
									let schemaParent = $('.database').jstree().get_node(tableParent.parent);

									$('.database').jstree("delete_node", $('.database').jstree().get_node(data.node.children[0]));
									let position = 'last';

									$http.get(databasesSvcUrl + '/' + $scope.selectedDatabase + '/' + $scope.selectedDatasource
										+ '/' + schemaParent.text + '/' + tableParent.text + "?kind=" + tableParent.original.kind.toUpperCase())
										.then(function (data) {
											data.data.columns.forEach(function (column) {
												let icon = "fa fa-th-large";
												if (column.key) {
													icon = "fa fa-key";
												} else {
													switch (column.type.toLowerCase()) {
														case "varchar":
														case "nvarchar":
															icon = "fa fa-sort-alpha-asc";
															break;
														case "char":
															icon = "fa fa-font";
															break;
														case "date":
															icon = "fa fa-calendar";
															break;
														case "datetime":
														case "timestamp":
															icon = "fa fa-clock-o";
															break;
														case "smallint":
														case "tinyint":
														case "integer":
															icon = "fa fa-list-ol";
															break;
														case "float":
														case "double":
														case "decimal":
															icon = "fa fa-percent"
															break;
														case "bigint":
															icon = "fa fa-signal";
															break;
														case "boolean":
															icon = "fa fa-toggle-on";
															break;
														case "clob":
														case "blob":
															icon = "fa fa-ellipsis-h";
															break;
													}
												}
												let nodeText = column.name + ' - <i style="font-size: smaller;">' + column.type + "(" + (column.size !== undefined ? column.size : (column.length !== undefined ? column.length : "N/A")) + ")</i>";
												let newNode = {
													id: parent.id + "$" + column.name,
													state: "open",
													text: nodeText,
													icon: icon
												};
												let child = $('.database').jstree("create_node", parent, newNode, position, false, false);
											})
										});
								} else if (data.node.children.length === 1 && $('.database').jstree().get_node(data.node.children[0]).original === "Loading Indices...") {

									let parent = $('.database').jstree().get_node(data.node);
									let tableParent = $('.database').jstree().get_node(data.node.parent);
									let schemaParent = $('.database').jstree().get_node(tableParent.parent);

									$('.database').jstree("delete_node", $('.database').jstree().get_node(data.node.children[0]));
									let position = 'last';

									$http.get(databasesSvcUrl + '/' + $scope.selectedDatabase + '/' + $scope.selectedDatasource
										+ '/' + schemaParent.text + '/' + tableParent.text)
										.then(function (data) {
											data.data.indices.forEach(function (index) {
												let nodeText = index.name;
												let newNode = { state: "open", "text": nodeText, "id": parent.id + "$" + index.name, "icon": "fa fa-list-ul" };
												let child = $('.database').jstree("create_node", parent, newNode, position, false, false);
											})
										});
								}

								//data.instance.set_icon(data.node, 'fa fa-folder-open-o');
							})
							.on('close_node.jstree', function (evt, data) {
								//data.instance.set_icon(data.node, 'fa fa-folder-o');
							});
					}
				}.bind(this));
		} else {
			$('.database').jstree(true).settings.core.data = [];
			$('.database').jstree(true).refresh();
		}
	};

	let build = function (f) {
		let children = [];
		let icon = 'fa fa-th-large';
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

			icon = 'fa fa-database';
		} else if (f.kind == 'table' && (f.type === 'TABLE' || f.type === 'BASE TABLE')) {
			//children = ['Loading...'];
			children = [
				{ text: "Columns", "icon": "fa fa-columns", children: ['Loading Columns...'] },
				{ text: "Indices", "icon": "fa fa-bars", children: ['Loading Indices...'] },
			];

			icon = 'fa fa-table';
		} else if (f.kind == 'table' && f.type === 'VIEW') {
			//children = ['Loading...'];
			children = [
				{ text: "Columns", "icon": "fa fa-columns", children: ['Loading Columns...'] },
				{ text: "Indices", "icon": "fa fa-bars", children: ['Loading Indices...'] },
			];

			icon = 'fa fa-th';
		} else if (f.kind == 'table' && f.type !== 'TABLE' && f.type !== 'VIEW') {
			//children = ['Loading...'];
			children = [
				{ text: "Columns", "icon": "fa fa-columns", children: ['Loading Columns...'] },
				{ text: "Indices", "icon": "fa fa-bars", children: ['Loading Indices...'] },
			];

			icon = 'fa fa-lock';
		} else if (f.kind == 'procedure') { // && f.type === 'XXX'
			//children = ['Loading...'];
			children = [
				{ text: "Columns", "icon": "fa fa-columns", children: ['Loading Columns...'] },
			];

			icon = 'fa fa-cog';
		} else if (f.kind == 'function') { // && f.type === 'XXX'
			//children = ['Loading...'];
			children = [
				{ text: "Columns", "icon": "fa fa-columns", children: ['Loading Columns...'] },
			];

			icon = 'fa fa-calculator';
		} else if (f.kind == 'column') {
			icon = 'fa fa-th-large';
			name += ' [<i>' + f.type + '</i>';
			name += ' <i>(' + f.size + ')</i>';
			name += ']';
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

	$scope.databaseChanged = function (evt) {
		$http.get(databasesSvcUrl + '/' + $scope.selectedDatabase)
			.then(function (data) {
				$scope.datasources = data.data;
				if ($scope.datasources[0]) {
					$scope.selectedDatasource = $scope.datasources[0];
					messageHub.post($scope.selectedDatabase, 'database.database.selection.changed');
					messageHub.post($scope.selectedDatasource, 'database.datasource.selection.changed');
					$scope.datasourceChanged();
				} else {
					$scope.selectedDatasource = undefined;
				}
				$scope.refreshDatabase();
			});
	};

	$scope.datasourceChanged = function (evt) {
		localStorage.setItem('DIRIGIBLE.database', JSON.stringify({ "type": $scope.selectedDatabase, "name": $scope.selectedDatasource }));
		messageHub.post($scope.selectedDatasource, 'database.datasource.selection.changed');
		$scope.refreshDatabase();
	};

	$scope.runSQL = function (evt) {
		messageHub.post({}, 'database.sql.run');
	};

});

function confirmRemove(type, name) {
	return confirm("Do you really want to delete the " + type + ": " + name);
}
