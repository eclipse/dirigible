/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
function addToolbarButton(editor, toolbar, action, label, image, isTransparent) {
	var button = document.createElement('button');
	button.title = label;
	// button.style.fontSize = '10';
	if (image !== null) {
		var img = document.createElement('i');
		img.setAttribute('class', 'fa fa-'+image+' fa-2x');
		// img.style.width = '16px';
		// img.style.height = '16px';
		img.style.verticalAlign = 'middle';
		img.style.marginRight = '2px';
		button.appendChild(img);
	}
	if (isTransparent) {
		//button.style.background = 'black';
		//button.style.color = 'white';
		//button.style.border = 'none';
		button.setAttribute('class', 'button');
		button.setAttribute('style', 'background: transparent; color: white; border: none');
	}
	mxEvent.addListener(button, 'click', function(evt) {
		editor.execute(action);
	});
	//mxUtils.write(button, label);
	toolbar.appendChild(button);
}

function addSidebarIcon(graph, sidebar, prototype, image, hint, $scope) {
	// Function that is executed when the image is dropped on
	// the graph. The cell argument points to the cell under
	// the mousepointer if there is one.
	var funct = function(graph, evt, cell) {
		graph.stopEditing(false);

		var pt = graph.getPointForEvent(evt);
		
		var parent = graph.getDefaultParent();
		var model = graph.getModel();

		var isEntity = graph.isSwimlane(prototype);
		var name = null;				

		if (!isEntity) {
			parent = cell;
			var pstate = graph.getView().getState(parent);
			
			if (parent === null || pstate === null) {
				showAlert('Drop', 'Drop target must be an entity', $scope);
				return;
			}
			
			pt.x -= pstate.x;
			pt.y -= pstate.y;

			var columnCount = graph.model.getChildCount(parent)+1;
			//showPrompt('Enter name for new property', 'property'+columnCount, createNode);
			createNode('property'+columnCount);
		} else {
			var entitiesCount = 0;
			var childCount = graph.model.getChildCount(parent);
			
			for (var i=0; i<childCount; i++) {
				if (!graph.model.isEdge(graph.model.getChildAt(parent, i))) {
					entitiesCount++;
				}
			}
			//showPrompt('Enter name for new entity', 'Entity'+(entitiesCount+1), createNode);
			createNode('Entity'+(entitiesCount+1));
		}
		
		function createNode(name) {
			if (name !== null) {
				var v1 = model.cloneCell(prototype);

				model.beginUpdate();
				try {
					v1.value.name = name;
					v1.geometry.x = pt.x;
					v1.geometry.y = pt.y;
					
					graph.addCell(v1, parent);
					
					if (isEntity) {
						v1.geometry.alternateBounds = new mxRectangle(0, 0, v1.geometry.width, v1.geometry.height);
						if (!v1.children[0].value.isSQL) {
							v1.children[0].value.name = name.toLowerCase()+'Id';
						}
						v1.value.type = 'Entity';
					}
				} finally {
					model.endUpdate();
				}
				
				graph.setSelectionCell(v1);
			}
		}

	};
	
	var img = document.createElement('i');
	img.setAttribute('class', 'fa fa-'+image+' fa-2x');
	//img.setAttribute('style', 'color: #337ab7');
	//img.color = '#337ab7';
	img.title = hint;
	sidebar.appendChild(img);
	  					
	// Creates the image which is used as the drag icon (preview)
	var dragImage = img.cloneNode(true);
	var ds = mxUtils.makeDraggable(img, graph, funct, dragImage);

	// Adds highlight of target entities for properties
	ds.highlightDropTargets = true;
	ds.getDropTarget = function(graph, x, y) {
		if (graph.isSwimlane(prototype)) {
			return null;
		} 
		var cell = graph.getCellAt(x, y);
		
		if (graph.isSwimlane(cell)) {
			return cell;
		} 
		var parent = graph.getModel().getParent(cell);
		
		if (graph.isSwimlane(parent)) {
			return parent;
		}
	};
}

function configureStylesheet(graph) {
	var style = new Object();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
	style[mxConstants.STYLE_FONTCOLOR] = '#609dd2'; // 'var(--modeler-entity-color)';
	style[mxConstants.STYLE_FONTSIZE] = '11';
	style[mxConstants.STYLE_FONTSTYLE] = 0;
	style[mxConstants.STYLE_SPACING_LEFT] = '4';
	style[mxConstants.STYLE_IMAGE_WIDTH] = '48';
	style[mxConstants.STYLE_IMAGE_HEIGHT] = '48';
	graph.getStylesheet().putDefaultVertexStyle(style);

	style = new Object();
	style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_SWIMLANE;
	style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
	style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
	style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
	//style[mxConstants.STYLE_GRADIENTCOLOR] = 'var(--modeler-entity-fill)';
	style[mxConstants.STYLE_FILLCOLOR] = '#609dd2'; // 'var(--modeler-entity-header-background)';
	style[mxConstants.STYLE_SWIMLANE_FILLCOLOR] = '#ffffff'; // 'var(--modeler-entity-background)';
	style[mxConstants.STYLE_STROKECOLOR] = '#88b5dd'; // 'var(--modeler-entity-border)';//'#337ab7';
	style[mxConstants.STYLE_FONTCOLOR] = '#fff'; // 'var(--modeler-entity-header-color)';
	
    style[mxConstants.STYLE_FILLCOLOR] = '#609dd2';
	style[mxConstants.STYLE_SWIMLANE_FILLCOLOR] = '#ffffff';
	style[mxConstants.STYLE_STROKECOLOR] = '#88b5dd';
	style[mxConstants.STYLE_FONTCOLOR] = '#fff';
	
	style[mxConstants.STYLE_STROKEWIDTH] = '2';
	style[mxConstants.STYLE_STARTSIZE] = '28';
	style[mxConstants.STYLE_VERTICAL_ALIGN] = 'middle';
	style[mxConstants.STYLE_FONTSIZE] = '12';
	style[mxConstants.STYLE_FONTSTYLE] = 1;
	style[mxConstants.STYLE_ROUNDED] = true;
	style[mxConstants.STYLE_ARCSIZE] = 4;
	// Looks better without opacity if shadow is enabled
	style[mxConstants.STYLE_OPACITY] = '80';
	style[mxConstants.STYLE_SHADOW] = 1;
	graph.getStylesheet().putCellStyle('entity', style);

	style = graph.stylesheet.getDefaultEdgeStyle();
	style[mxConstants.STYLE_LABEL_BACKGROUNDCOLOR] = '#FFFFFF';
	style[mxConstants.STYLE_STROKECOLOR] = '#88b5dd'; // var(--modeler-entity-border)';//'#337ab7';
	style[mxConstants.STYLE_STROKEWIDTH] = '2';
	style[mxConstants.STYLE_ROUNDED] = true;
	style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
}

// Function to create the entries in the popupmenu
function createPopupMenu(editor, graph, menu, cell, evt) {
	if (cell !== null) {
			menu.addItem('Properties', 'list-ul', function() {
				editor.execute('properties', cell);
			});
	
		menu.addItem('Copy', 'copy', function() {
			editor.execute('copy', cell);
		});
		
	}
	
	menu.addItem('Paste', 'paste', function() {
			editor.execute('paste', cell);
	});

	menu.addItem('Undo', 'undo', function() {
		editor.execute('undo', cell);
	});
	
	menu.addItem('Redo', 'repeat', function() {
		editor.execute('redo', cell);
	});
	
	if (cell !== null) {

		menu.addItem('Delete', 'times', function() {
			editor.execute('delete', cell);
		});
	
	}
}
