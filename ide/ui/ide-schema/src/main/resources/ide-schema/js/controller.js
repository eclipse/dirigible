// Program starts here. Creates a sample graph in the
// DOM node with the specified ID. This function is invoked
// from the onLoad event handler of the document (see below).
function main(container, outline, toolbar, sidebar, status) {
	
	// Load schema file
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
			return getResource('/services/v3/ide/workspaces'+file);
		}
		console.error('file parameter is not present in the URL');
	}

	var searchParams = new URLSearchParams(window.location.search);
	var file = searchParams.get('file');
	var contents = loadContents(file);
	
	var messageHub = new FramesMessageHub();

	function saveContents(text) {
		console.log('Save called...');
		if (file) {
			var xhr = new XMLHttpRequest();
			xhr.open('PUT', '/services/v3/ide/workspaces'+file);
			xhr.onreadystatechange = function() {
				if (xhr.readyState === 4) {
					console.log('file saved: '+file);
				}
			}
			xhr.send(text);
			messageHub.post({data: file}, 'editor.file.saved');
		} else {
			console.error('file parameter is not present in the request');
		}
	}
			
	messageHub.subscribe(function(graph) {
		var schema = createSchema(graph);
		saveContents(schema);
	}, 'workbench.editor.save');
	
	
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
		
		// Table icon dimensions and position
		mxSwimlane.prototype.imageSize = 20;
		mxSwimlane.prototype.imageDx = 16;
		mxSwimlane.prototype.imageDy = 4;
		
		// Changes swimlane icon bounds
		mxSwimlane.prototype.getImageBounds = function(x, y, w, h) {
			return new mxRectangle(x+this.imageDx, y+this.imageDy, this.imageSize, this.imageSize);
		};
		
		// Defines an icon for creating new connections in the connection handler.
		// This will automatically disable the highlighting of the source vertex.
		mxConnectionHandler.prototype.connectImage = new mxImage('../resources/mxgraph/3.9.1/images/connector.gif', 16, 16);
		
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
		var graph = editor.graph;
		var model = graph.model;
		
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

		// Only tables are resizable
		graph.isCellResizable = function(cell) {
			return this.isSwimlane(cell);
		};
		
		// Only tables are movable
		graph.isCellMovable = function(cell) {
			return this.isSwimlane(cell);
		};

		// Sets the graph container and configures the editor
		editor.setGraphContainer(container);
		var config = mxUtils.load(
			'editors/config/keyhandler-minimal.xml').
				getDocumentElement();
		editor.configure(config);

		// Configures the automatic layout for the table columns
		editor.layoutSwimlanes = true;
		editor.createSwimlaneLayout = function() {
			var layout = new mxStackLayout(this.graph, false);
			layout.fill = true;
			layout.resizeParent = true;
			
			// Overrides the function to always return true
			layout.isVertexMovable = function(cell) {
				return true;
			};
			
			return layout;
		};
		
		// Text label changes will go into the name field of the user object
		graph.model.valueForCellChanged = function(cell, value) {
			if (value.name != null) {
				return mxGraphModel.prototype.valueForCellChanged.apply(this, arguments);
			}
			var old = cell.value.name;
			cell.value.name = value;
			return old;
		};
		
		// Columns are dynamically created HTML labels
		graph.isHtmlLabel = function(cell) {
			return !this.isSwimlane(cell) &&
				!this.model.isEdge(cell);
		};
		
		// Edges are not editable
		graph.isCellEditable = function(cell) {
			return !this.model.isEdge(cell);
		};
		
		// Returns the name field of the user object for the label
		graph.convertValueToString = function(cell) {
			if (cell.value != null && cell.value.name != null) {
				return cell.value.name;
			}

			return mxGraph.prototype.convertValueToString.apply(this, arguments); // "supercall"
		};
				
		// Returns the type as the tooltip for column cells
		graph.getTooltip = function(state) {
			if (this.isHtmlLabel(state.cell)) {
				return 'Type: '+state.cell.value.type;
			} else if (this.model.isEdge(state.cell)) {
				var source = this.model.getTerminal(state.cell, true);
				var parent = this.model.getParent(source);
				
				return parent.value.name+'.'+source.value.name;
			}
			
			return mxGraph.prototype.getTooltip.apply(this, arguments); // "supercall"
		};
		
		// Creates a dynamic HTML label for column fields
		graph.getLabel = function(cell) {
			if (this.isHtmlLabel(cell)) {
				var label = '';
				
				if (cell.value.primaryKey) {
					label += '<i title="Primary Key" class="fa fa-key" width="16" height="16" align="top"></i>&nbsp;';
				} else {
					label += '<img src="../resources/mxgraph/3.9.1/images/spacer.gif" width="9" height="1">&nbsp;';
				}
										
				if (cell.value.autoIncrement) {
					label += '<i title="Auto Increment" class="fa fa-plus" width="16" height="16" align="top"></i>&nbsp;';
				} else if (cell.value.unique) {
					label += '<i title="Unique" class="fa fa-check" width="16" height="16" align="top"></i>&nbsp;';
				} else {
					label += '<img src="../resources/mxgraph/3.9.1/images/spacer.gif" width="9" height="1">&nbsp;';
				}

				var suffix = ': '+mxUtils.htmlEntities(cell.value.type, false)+(cell.value.columnLength ? 
							'('+cell.value.columnLength+')' : '');
				suffix = cell.value.isSQL ? '' : suffix;
				return label+mxUtils.htmlEntities(cell.value.name, false)+suffix;
			}
			
			return mxGraph.prototype.getLabel.apply(this, arguments); // "supercall"
		};
		
		// Removes the source vertex if edges are removed
		graph.addListener(mxEvent.REMOVE_CELLS, function(sender, evt) {
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
		graph.isValidDropTarget = function(cell, cells, evt) {
			return this.isSwimlane(cell);
		};

		// Installs a popupmenu handler using local function (see below).
		graph.popupMenuHandler.factoryMethod = function(menu, cell, evt) {
			createPopupMenu(editor, graph, menu, cell, evt);
		};

		// Adds all required styles to the graph (see below)
		configureStylesheet(graph);

		// Adds sidebar icon for the table object
		var tableObject = new Table('TABLENAME');
		var table = new mxCell(tableObject, new mxGeometry(0, 0, 200, 28), 'table');
		
		table.setVertex(true);
		addSidebarIcon(graph, sidebar, 	table, 'table', 'Drag this to the diagram to create a new Table');
		
		// Adds sidebar icon for the column object
		var columnObject = new Column('COLUMNNAME');
		var column = new mxCell(columnObject, new mxGeometry(0, 0, 0, 26));
		
		column.setVertex(true);
		column.setConnectable(false);

		addSidebarIcon(graph, sidebar, 	column, 'columns', 'Drag this to a Table to create a new Column');
		
		// Adds sidebar icon for the view object
		var viewObject = new View('VIEWENAME');
		var view = new mxCell(viewObject, new mxGeometry(0, 0, 200, 28), 'table');
		
		view.setVertex(true);
		addSidebarIcon(graph, sidebar, 	view, 'th-large', 'Drag this to the diagram to create a new View');
		
		// Adds primary key field into table
		var firstColumn = column.clone();
		
		firstColumn.value.name = 'TABLENAME_ID';
		firstColumn.value.type = 'INTEGER';
		firstColumn.value.columnLength = 0;
		firstColumn.value.primaryKey = true;
		firstColumn.value.autoIncrement = true;
		
		table.insert(firstColumn);
		
		// Adds sql field into view
		var sqlColumn = column.clone();
		
		sqlColumn.value.name = 'SELECT ...';
		sqlColumn.value.isSQL = true;
		
		view.insert(sqlColumn);
		
		// Adds child columns for new connections between tables
		graph.addEdge = function(edge, parent, source, target, index) {
			// check whether the source is view
			if (source.value.type === 'VIEW') {
				showAlert('Drop', 'Source must be a Table not a View');
				return;
			}
			
			// Finds the primary key child of the target table
			var primaryKey = null;
			var childCount = this.model.getChildCount(target);
			
			for (var i=0; i < childCount; i++) {
				var child = this.model.getChildAt(target, i);
				
				if (child.value.primaryKey) {
					primaryKey = child;
					break;
				}
			}
			
			if (primaryKey === null) {
				showAlert('Drop', 'Target Table must have a Primary Key');
				return;
			}
		
			this.model.beginUpdate();
			try {
				var col1 = this.model.cloneCell(column);
				col1.value.name = primaryKey.value.name;
				col1.value.type = primaryKey.value.type;
				col1.value.columnLength = primaryKey.value.columnLength;
			
				this.addCell(col1, source);
				source = col1;				
				target = primaryKey;
				
				return mxGraph.prototype.addEdge.apply(this, arguments); // "supercall"
			} finally {
				this.model.endUpdate();
			}
		};

		// Creates a new DIV that is used as a toolbar and adds
		// toolbar buttons.
		var spacer = document.createElement('div');
		spacer.style.display = 'inline';
		spacer.style.padding = '8px';

		addToolbarButton(editor, toolbar, 'save', 'Save', 'save', true);

		// Defines a new export action
		editor.addAction('save', function(editor, cell) {
			var schema = createSchema(graph);
			saveContents(schema);
		});
		
		addToolbarButton(editor, toolbar, 'properties', 'Properties', 'list-ul', true);

		// Defines a new export action
		editor.addAction('properties', function(editor, cell) {

			if (!cell) {
				cell = graph.getSelectionCell();
			}
			
			if (graph.isHtmlLabel(cell)) {
				if (cell) {
					// assume column
					if (cell.value.isSQL) {
						// assume View's (the only) column
						showQueryProperties(graph, cell);
					} else {
						// assume Table's column
						showProperties(graph, cell);
					}
				} else {
					showAlert('Error', 'Select a column');
				}
			} else {
				// assume Table, View or Connector
				if (cell.value) {
					// assume Table or View
					showStructureProperties(graph, cell);
				} else {
					// assume connector
					showConnectorProperties(graph, cell);
				}
				
			}
		});

		addToolbarButton(editor, toolbar, 'delete', 'Delete', 'times', true);

		toolbar.appendChild(spacer.cloneNode(true));
		
		addToolbarButton(editor, toolbar, 'undo', '', 'undo', true);
		addToolbarButton(editor, toolbar, 'redo', '', 'repeat', true);
		
		toolbar.appendChild(spacer.cloneNode(true));
		
		addToolbarButton(editor, toolbar, 'show', 'Show', 'camera', true);
		addToolbarButton(editor, toolbar, 'print', 'Print', 'print', true);
		
		toolbar.appendChild(spacer.cloneNode(true));

		// Defines a create SQK action
		editor.addAction('showSql', function(editor, cell) {
			var sql = createSql(graph);
			
			if (sql.length > 0) {
				var textarea = document.createElement('textarea');
				textarea.style.width = '410px';
				textarea.style.height = '420px';
				
				textarea.value = sql;
				showModalWindow('SQL', textarea, 410, 440);
			} else {
				showAlert('Warning', 'Schema is empty');
			}
		});

		addToolbarButton(editor, toolbar, 'showSql', 'Show SQL', 'database', true);

		// Defines export XML action
		editor.addAction('export', function(editor, cell) {
			var textarea = document.createElement('textarea');
			textarea.style.width = '410px';
			textarea.style.height = '420px';
			var enc = new mxCodec(mxUtils.createXmlDocument());
			var node = enc.encode(editor.graph.getModel());
			textarea.value = mxUtils.getPrettyXml(node);
			showModalWindow('XML', textarea, 410, 440);
		});

		//addToolbarButton(editor, toolbar, 'export', 'Export XML', 'download', true);
		
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
};
		