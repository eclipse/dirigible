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
// Program starts here. Creates a sample graph in the
// DOM node with the specified ID. This function is invoked
// from the onLoad event handler of the document (see below).
function main(container, outline, toolbar, sidebar, status) {

	var ICON_ENTITY = 'list-alt';//'list-alt';
	var ICON_PROPERTY = 'bars';//'align-justify';
	var ICON_PROJECTION = 'sign-in';//'external-link';
	var ICON_EXTENSION = 'puzzle-piece';//'puzzle-piece';

	var $scope = $('#ModelerCtrl').scope();
	var csrfToken;
	var file;

	// Load model file
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
			return getResource('/services/v4/ide/workspaces' + file);
		}
		console.error('file parameter is not present in the URL');
	}

	function getViewParameters() {
		if (window.frameElement.hasAttribute("data-parameters")) {
			var params = JSON.parse(window.frameElement.getAttribute("data-parameters"));
			file = params["file"];
		} else {
			var searchParams = new URLSearchParams(window.location.search);
			file = searchParams.get('file');
		}
	}

	getViewParameters();
	var contents = loadContents(file);

	var messageHub = new FramesMessageHub();

	if (file)
		initializeModelJson(file.substring(0, file.lastIndexOf('.')) + '.model');

	function saveContents(text, file) {
		console.log('Save called...');
		if (file) {
			var xhr = new XMLHttpRequest();
			xhr.open('PUT', '/services/v4/ide/workspaces' + file);
			xhr.setRequestHeader('X-Requested-With', 'Fetch');
			xhr.setRequestHeader('X-CSRF-Token', csrfToken);
			xhr.onreadystatechange = function () {
				if (xhr.readyState === 4) {
					console.log('file saved: ' + file);
				}
			};
			xhr.send(text);
			messageHub.post({
				name: file.substring(file.lastIndexOf('/') + 1),
				path: file.substring(file.indexOf('/', 1)),
				contentType: 'application/entity-data-model+xml', // TODO: Take this from data-parameters
				workspace: file.substring(1, file.indexOf('/', 1)),
			}, 'ide.file.saved');
			messageHub.post({ message: `File '${file}' saved` }, 'ide.status.message');
		} else {
			console.error('file parameter is not present in the request');
		}
	}

	function initializeModelJson(file) {
		console.log('Save called...');
		if (file) {
			var xhr = new XMLHttpRequest();
			xhr.open('POST', '/services/v4/ide/workspaces' + file);
			xhr.setRequestHeader('X-Requested-With', 'Fetch');
			xhr.setRequestHeader('X-CSRF-Token', csrfToken);
			xhr.onreadystatechange = function () {
				if (xhr.readyState === 4) {
					console.log('file saved: ' + file);
				}
			};
			xhr.send('');
			messageHub.post({
				name: file.substring(file.lastIndexOf('/') + 1),
				path: file.substring(file.indexOf('/', 1)),
				contentType: 'application/entity-data-model+xml', // TODO: Take this from data-parameters
				workspace: file.substring(1, file.indexOf('/', 1)),
			}, 'ide.file.saved');
			messageHub.post({ message: `File '${file}' saved` }, 'ide.status.message');
		} else {
			console.error('file parameter is not present in the request');
		}
	}

	function saveModel(graph) {
		var model = createModel(graph);
		saveContents(model, file);
		// var modelJson = createModelJson(graph);
		// saveContents(modelJson, file.substring(0, file.lastIndexOf('.')) + '.model');
	}

	messageHub.subscribe(
		function () {
			saveModel(graph);
		},
		"editor.file.save.all"
	);

	messageHub.subscribe(
		function (msg) {
			let mfile = msg.data && typeof msg.data === 'object' && msg.data.file;
			if (mfile && mfile === file)
				saveModel(graph);
		},
		"editor.file.save"
	);

	var graph;
	// Checks if the browser is supported
	if (!mxClient.isBrowserSupported()) {
		// Displays an error message if the browser is not supported.
		mxUtils.error('Browser is not supported!', 200, false);
	} else {
		// Specifies shadow opacity, color and offset
		mxConstants.SHADOW_OPACITY = 0.5;
		mxConstants.SHADOWCOLOR = '#C0C0C0';
		mxConstants.SHADOW_OFFSET_X = 0;
		mxConstants.SHADOW_OFFSET_Y = 0;

		// Entity icon dimensions and position
		mxSwimlane.prototype.imageSize = 20;
		mxSwimlane.prototype.imageDx = 16;
		mxSwimlane.prototype.imageDy = 4;

		// Changes swimlane icon bounds
		mxSwimlane.prototype.getImageBounds = function (x, y, w, h) {
			return new mxRectangle(x + this.imageDx, y + this.imageDy, this.imageSize, this.imageSize);
		};

		// Defines an icon for creating new connections in the connection handler.
		// This will automatically disable the highlighting of the source vertex.
		mxConnectionHandler.prototype.connectImage = new mxImage('images/connector.gif', 16, 16);

		// Workaround for Internet Explorer ignoring certain CSS directives
		if (mxClient.IS_QUIRKS) {
			document.body.style.overflow = 'hidden';
			new mxDivResizer(container);
			new mxDivResizer(outline);
			new mxDivResizer(toolbar);
			new mxDivResizer(sidebar);
			new mxDivResizer(status);
		}

		// Creates the graph inside the given container. The
		// editor is used to create certain functionality for the
		// graph, such as the rubberband selection, but most parts
		// of the UI are custom in this example.
		var editor = new mxEditor();
		graph = editor.graph;
		var model = graph.model;

		initClipboard(graph);

		$scope.$parent.editor = editor;
		$scope.$parent.graph = graph;

		// Disables some global features
		graph.setConnectable(true);
		graph.setCellsDisconnectable(false);
		graph.setCellsCloneable(false);
		graph.swimlaneNesting = false;
		graph.dropEnabled = true;

		// Does not allow dangling edges
		graph.setAllowDanglingEdges(false);

		// Forces use of default edge in mxConnectionHandler
		graph.connectionHandler.factoryMethod = null;

		// Only entities are resizable
		graph.isCellResizable = function (cell) {
			return this.isSwimlane(cell);
		};

		// Only entities are movable
		graph.isCellMovable = function (cell) {
			return this.isSwimlane(cell);
		};

		// Sets the graph container and configures the editor
		editor.setGraphContainer(container);
		var config = mxUtils.load(
			'editors/config/keyhandler-minimal.xml').
			getDocumentElement();
		editor.configure(config);

		// Configures the automatic layout for the entity properties
		editor.layoutSwimlanes = true;
		editor.createSwimlaneLayout = function () {
			var layout = new mxStackLayout(this.graph, false);
			layout.fill = true;
			layout.resizeParent = true;

			// Overrides the function to always return true
			layout.isVertexMovable = function (cell) {
				return true;
			};

			return layout;
		};

		// Text label changes will go into the name field of the user object
		graph.model.valueForCellChanged = function (cell, value) {
			if (value.name != null) {
				return mxGraphModel.prototype.valueForCellChanged.apply(this, arguments);
			}
			var old = cell.value.name;
			cell.value.name = value;
			return old;
		};

		// Properties are dynamically created HTML labels
		graph.isHtmlLabel = function (cell) {
			return !this.isSwimlane(cell) &&
				!this.model.isEdge(cell);
		};

		// Edges are not editable
		graph.isCellEditable = function (cell) {
			return !this.model.isEdge(cell);
		};

		// Returns the name field of the user object for the label
		graph.convertValueToString = function (cell) {
			if (cell.value != null && cell.value.name != null) {
				return cell.value.name;
			}

			return mxGraph.prototype.convertValueToString.apply(this, arguments); // "supercall"
		};

		// Returns the type as the tooltip for property cells
		graph.getTooltip = function (state) {
			if (this.isHtmlLabel(state.cell)) {
				return 'Type: ' + state.cell.value.dataType;
			} else if (this.model.isEdge(state.cell)) {
				var source = this.model.getTerminal(state.cell, true);
				var parent = this.model.getParent(source);

				return parent.value.name + '.' + source.value.name;
			}

			return mxGraph.prototype.getTooltip.apply(this, arguments); // "supercall"
		};

		// Creates a dynamic HTML label for property fields
		graph.getLabel = function (cell) {
			if (this.isHtmlLabel(cell)) {
				var label = '';

				if (cell.value.dataPrimaryKey === 'true') {
					label += '<i title="Primary Key" class="fa fa-key" width="16" height="16" align="top"></i>&nbsp;';
				} else {
					label += '<img src="images/spacer.gif" width="9" height="1">&nbsp;';
				}

				if (cell.value.dataAutoIncrement === 'true') {
					label += '<i title="Auto Increment" class="fa fa-plus" width="16" height="16" align="top"></i>&nbsp;';
				} else if (cell.value.dataUnique === 'true') {
					label += '<i title="Unique" class="fa fa-check" width="16" height="16" align="top"></i>&nbsp;';
				} else {
					label += '<img src="images/spacer.gif" width="9" height="1">&nbsp;';
				}

				var suffix = mxUtils.htmlEntities(cell.value.dataType, false) + (cell.value.dataLength ? '(' + cell.value.dataLength + ')' : '');
				return label + mxUtils.htmlEntities(cell.value.name, false) + ":" + suffix;
			}

			return mxGraph.prototype.getLabel.apply(this, arguments); // "supercall"
		};

		// Removes the source vertex if edges are removed
		graph.addListener(mxEvent.REMOVE_CELLS, function (sender, evt) {
			var cells = evt.getProperty('cells');

			for (var i = 0; i < cells.length; i++) {
				var cell = cells[i];

				if (this.model.isEdge(cell)) {
					var terminal = this.model.getTerminal(cell, true);
					var parent = this.model.getParent(terminal);
					this.model.remove(terminal);
				}
			}
		});

		// Disables drag-and-drop into non-swimlanes.
		graph.isValidDropTarget = function (cell, cells, evt) {
			return this.isSwimlane(cell);
		};

		// Installs a popupmenu handler using local function (see below).
		graph.popupMenuHandler.factoryMethod = function (menu, cell, evt) {
			createPopupMenu(editor, graph, menu, cell, evt);
		};

		// Adds all required styles to the graph (see below)
		configureStylesheet(graph);

		// Adds sidebar icon for the entity object
		var entityObject = new Entity('EntityName');
		var entity = new mxCell(entityObject, new mxGeometry(0, 0, 200, 28), 'entity');

		entity.setVertex(true);
		addSidebarIcon(graph, sidebar, entity, ICON_ENTITY, 'Drag this to the diagram to create a new Entity', $scope);

		// Adds sidebar icon for the property object
		var propertyObject = new Property('propertyName');
		var property = new mxCell(propertyObject, new mxGeometry(0, 0, 0, 26));

		property.setVertex(true);
		property.setConnectable(false);

		addSidebarIcon(graph, sidebar, property, ICON_PROPERTY, 'Drag this to an Entity to create a new Property', $scope);

		// Adds primary key field into entity
		var firstProperty = property.clone();

		firstProperty.value.name = 'entityNameId';
		firstProperty.value.dataType = 'INTEGER';
		firstProperty.value.dataLength = 0;
		firstProperty.value.dataPrimaryKey = 'true';
		firstProperty.value.dataAutoIncrement = 'true';

		entity.insert(firstProperty);

		// Adds child properties for new connections between entities
		graph.addEdge = function (edge, parent, source, target, index) {

			if (source.style && source.style.startsWith('projection')) {
				return;
			}

			// Finds the primary key child of the target table
			var primaryKey = null;
			var childCount = this.model.getChildCount(target);

			for (var i = 0; i < childCount; i++) {
				var child = this.model.getChildAt(target, i);

				if (child.value.dataPrimaryKey === 'true') {
					primaryKey = child;
					break;
				}
			}

			if (primaryKey === null) {
				showAlert('Drop', 'Target Entity must have a Primary Key', $scope);
				return;
			}

			this.model.beginUpdate();
			try {
				var prop1 = this.model.cloneCell(property);
				if (target.style && target.style.startsWith('projection')) {
					prop1.value.name = primaryKey.parent.value.projectionReferencedEntity + primaryKey.value.name;
				} else {
					prop1.value.name = primaryKey.parent.value.name + primaryKey.value.name;
				}
				prop1.value.dataType = primaryKey.value.dataType;
				prop1.value.dataLength = primaryKey.value.dataLength;

				this.addCell(prop1, source);
				source = prop1;
				target = primaryKey;

				return mxGraph.prototype.addEdge.apply(this, arguments); // "supercall"
			} finally {
				this.model.endUpdate();
			}
		};

		// Adds sidebar icon for the projection entity object
		var projectionObject = new Entity('EntityName');
		var projection = new mxCell(projectionObject, new mxGeometry(0, 0, 200, 28), 'projection');

		projection.setVertex(true);
		addSidebarIcon(graph, sidebar, projection, ICON_PROJECTION, 'Drag this to the diagram to create a reference to an Entity from external model', $scope);

		// // Adds primary key field into projection entity
		// var keyProperty = property.clone();

		// keyProperty.value.name = 'Id';
		// keyProperty.value.dataType = 'INTEGER';
		// keyProperty.value.dataLength = 0;
		// keyProperty.value.dataPrimaryKey = 'true';
		// keyProperty.value.dataAutoIncrement = 'true';
		// keyProperty.style = 'projectionproperty';

		// projection.insert(keyProperty);

		// Adds sidebar icon for the extension entity object
		var extensionObject = new Entity('EntityName');
		var extension = new mxCell(extensionObject, new mxGeometry(0, 0, 200, 28), 'extension');

		extension.setVertex(true);
		addSidebarIcon(graph, sidebar, extension, ICON_EXTENSION, 'Drag this to the diagram to create a new Extension Entity', $scope);

		// Adds primary key field into projection entity
		keyProperty = property.clone();

		keyProperty.value.name = 'Id';
		keyProperty.value.dataType = 'INTEGER';
		keyProperty.value.dataLength = 0;
		keyProperty.value.dataPrimaryKey = 'true';
		keyProperty.value.dataAutoIncrement = 'true';
		keyProperty.style = 'extensionproperty';

		extension.insert(keyProperty);


		// Creates a new DIV that is used as a toolbar and adds
		// toolbar buttons.
		var spacer = document.createElement('div');
		spacer.style.display = 'inline';
		spacer.style.padding = '8px';


		//		addToolbarButton(editor, toolbar, 'test', 'Test', 'wrench', true);
		//		// Defines a new test action
		//		editor.addAction('test', function(editor, cell) {
		//			if (!cell) {
		//				cell = graph.getSelectionCell();
		//			}
		//			$scope.$parent.cell = cell;
		//			$scope.$apply();
		//			$('#entityPropertiesOpen').click();
		//		});


		addToolbarButton(editor, toolbar, 'save', 'Save', 'save', true);

		// Defines a new save action
		editor.addAction('save', function (editor, cell) {
			saveModel(graph);
		});

		addToolbarButton(editor, toolbar, 'properties', 'Properties', 'list-ul', true);

		// Defines a new properties action
		editor.addAction('properties', function (editor, cell) {
			if (!cell) {
				cell = graph.getSelectionCell();
				if (!cell) {
					showAlert('Error', 'Select an Entity, a Property or a Connector', $scope);
					return;
				}
			}
			if (cell.style && cell.style.startsWith('projection')) {
				return;
			}
			$scope.$parent.cell = cell;
			$scope.$apply();

			if (graph.isHtmlLabel(cell)) {
				if (cell) {
					// assume Entity's property
					//showProperties(graph, cell);
					$('#propertyPropertiesOpen').click();
				} else {
					showAlert('Error', 'Select a Property', $scope);
				}
			} else {
				// assume Entity or Connector
				if (cell.value && Entity.prototype.isPrototypeOf(cell.value)) {
					// assume Entity
					//showEntityProperties(graph, cell);
					$('#entityPropertiesOpen').click();
				} else {
					// assume Connector
					//showConnectorProperties(graph, cell);
					$('#connectorPropertiesOpen').click();
				}

			}
		});

		toolbar.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, toolbar, 'navigation', 'Navigation', 'map-signs', true);

		// Defines a new sidebarnav action
		editor.addAction('navigation', function (editor, cell) {

			//graph.getModel()
			$('#navigationPropertiesOpen').click();

			// $scope.$parent.cell = cell;
			// $scope.$apply();

		});

		// Defines a new move up action
		editor.addAction('moveup', function (editor, cell) {

			if (cell.parent.children.length > 1) {
				graph.getModel().beginUpdate();
				try {

					for (index = 0; index < cell.parent.children.length; index++) {
						var current = cell.parent.children[index];
						if (cell.id === current.id) {
							if (index > 0) {
								var previous = cell.parent.children[index - 1];
								var y = previous.geometry.y;
								previous.geometry.y = current.geometry.y;
								current.geometry.y = y;
								cell.parent.children[index - 1] = current;
								cell.parent.children[index] = previous;
								break;
							}

						}
					}
				} finally {
					graph.getModel().endUpdate();
					graph.refresh();
				}
			}

		});

		// Defines a new move down action
		editor.addAction('movedown', function (editor, cell) {

			if (cell.parent.children.length > 2) {
				graph.getModel().beginUpdate();
				try {

					for (index = 0; index < cell.parent.children.length; index++) {
						var current = cell.parent.children[index];
						if (cell.id === current.id) {
							if (index < cell.parent.children.length - 1) {
								var next = cell.parent.children[index + 1];
								var y = next.geometry.y;
								next.geometry.y = current.geometry.y;
								current.geometry.y = y;
								cell.parent.children[index + 1] = current;
								cell.parent.children[index] = next;
								break;
							}
						}
					}
				} finally {
					graph.getModel().endUpdate();
					graph.refresh();
				}
			}

		});

		toolbar.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, toolbar, 'copy', 'Copy', 'copy', true);
		// Defines a new save action
		editor.addAction('copy', function (editor, cell) {
			mxClipboard.copy(graph);
			//			document.execCommand("copy");
		});
		addToolbarButton(editor, toolbar, 'paste', 'Paste', 'paste', true);
		// Defines a new save action
		editor.addAction('paste', function (editor, cell) {
			mxClipboard.paste(graph);
			//			document.execCommand("paste");
		});

		toolbar.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, toolbar, 'undo', '', 'undo', true);
		addToolbarButton(editor, toolbar, 'redo', '', 'repeat', true);

		toolbar.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, toolbar, 'delete', 'Delete', 'times', true);

		toolbar.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, toolbar, 'show', 'Show', 'camera', true);
		addToolbarButton(editor, toolbar, 'print', 'Print', 'print', true);

		toolbar.appendChild(spacer.cloneNode(true));

		// Adds toolbar buttons into the status bar at the bottom
		// of the window.
		addToolbarButton(editor, status, 'collapseAll', 'Collapse All', 'minus', true);
		addToolbarButton(editor, status, 'expandAll', 'Expand All', 'plus', true);

		status.appendChild(spacer.cloneNode(true));

		addToolbarButton(editor, status, 'zoomIn', '', 'search-plus', true);
		addToolbarButton(editor, status, 'zoomOut', '', 'search-minus', true);
		addToolbarButton(editor, status, 'actualSize', '', 'search', true);
		addToolbarButton(editor, status, 'fit', '', 'arrows', true);

		// Creates the outline (navigator, overview) for moving
		// around the graph in the top, right corner of the window.
		var outln = new mxOutline(graph, outline);

		// Fades-out the splash screen after the UI has been loaded.
		var splash = document.getElementById('splash');
		if (splash != null) {
			try {
				mxEvent.release(splash);
				mxEffects.fadeOut(splash, 100, true);
			} catch (e) {

				// mxUtils is not available (library not loaded)
				splash.parentNode.removeChild(splash);
			}
		}
	}

	var doc = mxUtils.parseXml(contents);
	var codec = new mxCodec(doc.mxGraphModel);
	codec.decode(doc.documentElement.getElementsByTagName('mxGraphModel')[0], graph.getModel());

	deserializeFilter(graph);
	loadPerspectives(doc, graph);
	loadSidebar(doc, graph);
}

function deserializeFilter(graph) {
	var parent = graph.getDefaultParent();
	var childCount = graph.model.getChildCount(parent);

	// Base64 deserialization of the encoded properties
	for (var i = 0; i < childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		if (!graph.model.isEdge(child)) {
			if (child.value.feedUrl && child.value.feedUrl !== "") {
				child.value.feedUrl = atob(child.value.feedUrl);
			}
			if (child.value.feedUsername && child.value.feedUsername !== "") {
				child.value.feedUsername = atob(child.value.feedUsername);
			}
			if (child.value.feedPassword && child.value.feedPassword !== "") {
				child.value.feedPassword = atob(child.value.feedPassword);
			}
			if (child.value.feedSchedule && child.value.feedSchedule !== "") {
				child.value.feedSchedule = atob(child.value.feedSchedule);
			}
		}
	}
}

function loadPerspectives(doc, graph) {
	if (!graph.getModel().perspectives) {
		graph.getModel().perspectives = [];
	}
	for (var i = 0; i < doc.children.length; i++) {
		var element = doc.children[i];
		if (element.localName === "model") {
			for (var j = 0; j < element.children.length; j++) {
				var perspectives = element.children[j];
				if (perspectives.localName === "perspectives") {
					for (var k = 0; k < perspectives.children.length; k++) {
						var item = perspectives.children[k];
						var copy = {};
						for (var m = 0; m < item.children.length; m++) {
							var attribute = item.children[m];
							if (attribute.localName === "name") {
								copy.id = attribute.textContent;
							} else if (attribute.localName === "label") {
								copy.label = attribute.textContent;
							} else if (attribute.localName === "icon") {
								copy.icon = attribute.textContent;
							} else if (attribute.localName === "order") {
								copy.order = attribute.textContent;
							}
						}
						graph.getModel().perspectives.push(copy);
					}
					break;
				}
			}
			break;
		}
	}
}

function loadSidebar(doc, graph) {
	if (!graph.getModel().sidebar) {
		graph.getModel().sidebar = [];
	}
	for (var i = 0; i < doc.children.length; i++) {
		var element = doc.children[i];
		if (element.localName === "model") {
			for (var j = 0; j < element.children.length; j++) {
				var sidebar = element.children[j];
				if (sidebar.localName === "sidebar") {
					for (var k = 0; k < sidebar.children.length; k++) {
						var item = sidebar.children[k];
						var copy = {};
						for (var m = 0; m < item.children.length; m++) {
							var attribute = item.children[m];
							if (attribute.localName === "path") {
								copy.path = attribute.textContent;
							} else if (attribute.localName === "label") {
								copy.label = attribute.textContent;
							} else if (attribute.localName === "icon") {
								copy.icon = attribute.textContent;
							} else if (attribute.localName === "url") {
								copy.url = attribute.textContent;
							}
						}
						graph.getModel().sidebar.push(copy);
					}
					break;
				}
			}
			break;
		}
	}
}