function showModalWindow(title, content, width, height) {
	var background = document.createElement('div');
	background.style.position = 'absolute';
	background.style.left = '0px';
	background.style.top = '0px';
	background.style.right = '0px';
	background.style.bottom = '0px';
	background.style.background = 'black';
	mxUtils.setOpacity(background, 50);
	document.body.appendChild(background);
	
	if (mxClient.IS_QUIRKS) {
		new mxDivResizer(background);
	}
	
	var x = Math.max(0, document.body.scrollWidth/2-width/2);
	var y = Math.max(10, (document.body.scrollHeight ||
				document.documentElement.scrollHeight)/2-height*2/3);
	var wnd = new mxWindow(title, content, x, y, width, height, false, true);
	wnd.setClosable(true);
				
	// Fades the background out after after the window has been closed
	wnd.addListener(mxEvent.DESTROY, function(evt) {
		mxEffects.fadeOut(background, 50, true, 
			10, 30, true);
	});

	wnd.setVisible(true);
	
	return wnd;
}

function showProperties(graph, cell) {
	// Creates a form for the user object inside the cell
	var form = new mxForm('properties');

	// Adds a field for the columnname
	var nameField = form.addText('Name', cell.value.name);
	var dataNameField = form.addText('Data Name', cell.value.dataName ? cell.value.dataName : JSON.stringify(cell.value.name).replace(/\W/g, '').toUpperCase());
	var dataTypeField = form.addCombo('Data Type', false, 1);
	form.addOption(dataTypeField, "VARCHAR", "VARCHAR", cell.value.type === "VARCHAR");
	form.addOption(dataTypeField, "CHAR", "CHAR", cell.value.type === "CHAR");
	form.addOption(dataTypeField, "DATE", "DATE", cell.value.type === "DATE");
	form.addOption(dataTypeField, "TIME", "TIME", cell.value.type === "TIME");
	form.addOption(dataTypeField, "TIMESTAMP", "TIMESTAMP", cell.value.type === "TIMESTAMP");
	form.addOption(dataTypeField, "INTEGER", "INTEGER", cell.value.type === "INTEGER");
	form.addOption(dataTypeField, "TINYINT", "TINYINT", cell.value.type === "TINYINT");
	form.addOption(dataTypeField, "BIGINT", "BIGINT", cell.value.type === "BIGINT");
	form.addOption(dataTypeField, "SMALLINT", "SMALLINT", cell.value.type === "SMALLINT");
	form.addOption(dataTypeField, "REAL", "REAL", cell.value.type === "REAL");
	form.addOption(dataTypeField, "DOUBLE", "DOUBLE", cell.value.type === "DOUBLE");
	form.addOption(dataTypeField, "BOOLEAN", "BOOLEAN", cell.value.type === "BOOLEAN");
	form.addOption(dataTypeField, "BLOB", "BLOB", cell.value.type === "BLOB");
	form.addOption(dataTypeField, "DECIMAL", "DECIMAL", cell.value.type === "DECIMAL");
	form.addOption(dataTypeField, "BIT", "BIT", cell.value.type === "BIT");
	
	var dataLengthField = form.addText('Data Length', cell.value.dataLength);
	
	var primaryKeyField = form.addCheckbox('Data Primary Key', cell.value.dataPrimaryKey);
	var autoIncrementField = form.addCheckbox('Data Auto Increment', cell.value.dataAutoIncrement);
	var notNullField = form.addCheckbox('Data Not Null', cell.value.dataNotNull);
	var uniqueField = form.addCheckbox('Data Unique', cell.value.dataUnique);
	
	var precisionField = form.addText('Data Precision', cell.value.dataPrecision);
	var scaleField = form.addText('Data Scale', cell.value.dataScale);
	
	var defaultField = form.addText('Data Default', cell.value.DataDefaultValue || '');
	var useDefaultField = form.addCheckbox('Use Default', cell.value.dataDefaultValue !== null);		
	
	var widgetdataTypeField = form.addCombo('Widget Type', false, 1);
	form.addOption(widgetdataTypeField, "Text Box", "TEXTBOX", cell.value.widgetType === "TEXTBOX");
	form.addOption(widgetdataTypeField, "Text Area", "TEXTAREA", cell.value.widgetType === "TEXTAREA");
	form.addOption(widgetdataTypeField, "Date Picker", "DATEPICKER", cell.value.widgetType === "DATEPICKER");
	form.addOption(widgetdataTypeField, "Drop Down", "DROPDOWN", cell.value.widgetType === "DROPDOWN");
	form.addOption(widgetdataTypeField, "Lookup Dialog", "LOOKUPDIALOG", cell.value.widgetType === "LOOKUPDIALOG");
	
	var widgetdataLengthField = form.addText('Widget Length', cell.value.widgetLength);
	var widgetdataPatternField = form.addText('Widget Pattern', cell.value.widgetPattern);
	var widgetdataServiceField = form.addText('Widget Service', cell.value.widgetService);

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();
		
		clone.name = nameField.value;
		
		clone.dataName = dataNameField.value;
		clone.dataType = dataTypeField.value;

		if (useDefaultField.checked) {
			clone.defaultValue = defaultField.value;
		} else {
			clone.defaultValue = null;
		}
		
		clone.dataLength = dataLengthField.value;
		
		clone.dataPrimaryKey = primaryKeyField.checked;
		clone.dataAutoIncrement = autoIncrementField.checked;
		clone.dataNotNull = notNullField.checked;
		clone.dataUnique = uniqueField.checked;
		
		clone.dataPrecision = precisionField.value;
		clone.dataScale = scaleField.value;
		
		clone.widgetType = widgetdataTypeField.value;
		clone.widgetLength = widgetdataLengthField.value;
		clone.widgetPattern = widgetdataPatternField.value;
		clone.widgetService = widgetdataServiceField.value;
		
		graph.model.setValue(cell, clone);
	
		wnd.destroy();
	};
	
	// Defines the function to be executed when the
	// Cancel button is pressed in the dialog
	var cancelFunction = function() {
		wnd.destroy();
	};
	form.addButtons(okFunction, cancelFunction);

	var parent = graph.model.getParent(cell);
	var name = parent.value.name+'.'+cell.value.name;
	wnd = showModalWindow(name, form.table, 240, 380);
}

function showEntityProperties(graph, cell) {
	// Creates a form for the user object inside the cell
	var form = new mxForm('properties');

	// Adds a field for the entity name
	var nameField = form.addText('Name', cell.value.name);
	var dataNameField = form.addText('Data Name', cell.value.dataName ? cell.value.dataName : JSON.stringify(cell.value.name).replace(/\W/g, '').toUpperCase());
	var isPrimaryField = form.addCheckbox('Primary Entity', cell.value.isPrimary);
	var menuKeyField = form.addText('Menu Key', cell.value.menuKey ? cell.value.menuKey : JSON.stringify(cell.value.name).replace(/\W/g, '').toLowerCase());
	var menuLabelField = form.addText('Menu Label', cell.value.menuLabel ? cell.value.menuLabel : cell.value.name);
	var layoutdataTypeField = form.addCombo('Layout Type', false, 1);
	form.addOption(layoutdataTypeField, "Manage", "MANAGE", cell.value.layoutType === "MANAGE");
	form.addOption(layoutdataTypeField, "List", "LIST", cell.value.layoutType === "LIST");
	form.addOption(layoutdataTypeField, "Display", "DISPLAY", cell.value.layoutType === "DISPLAY");

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();
		
		clone.name = nameField.value;
		clone.dataName = dataNameField.value;
		clone.isPrimary = isPrimaryField.checked;
		clone.menuKey = menuKeyField.value;
		clone.menuLabel = menuLabelField.value;
		clone.layoutType = layoutdataTypeField.value;
		
		graph.model.setValue(cell, clone);
	
		wnd.destroy();
	};
	
	// Defines the function to be executed when the
	// Cancel button is pressed in the dialog
	var cancelFunction = function() {
		wnd.destroy();
	};
	form.addButtons(okFunction, cancelFunction);

	wnd = showModalWindow(cell.value.name, form.table, 240, 180);
}

function showConnectorProperties(graph, cell) {
	// Creates a form for the user object inside the cell
	var form = new mxForm('properties');

	// Adds a field for the columnname
	var name = cell.value ? cell.value.name : cell.source.value.name+':'+cell.target.value.name;
	var relationshipNameField = form.addText('Name', name);
	//nameField.readOnly = true;
	var relationshipdataTypeField = form.addCombo('Relationship', false, 1);
	form.addOption(relationshipdataTypeField, "Association", "ASSOCIATION", cell.source.value.relationshipType === "ASSOCIATION");
	form.addOption(relationshipdataTypeField, "Aggregation", "AGGREGATION", cell.source.value.relationshipType === "AGGREGATION");
	form.addOption(relationshipdataTypeField, "Composition", "COMPOSITION", cell.source.value.relationshipType === "COMPOSITION");
	var relationshipCardinalityField = form.addCombo('Cardinality', false, 1);
	form.addOption(relationshipCardinalityField, "one-to-one", "1_1", cell.source.value.relationshipCardinality === "1_1");
	form.addOption(relationshipCardinalityField, "one-to-many", "1_n", cell.source.value.relationshipCardinality === "1_n");

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.source.value.clone();
		
		//clone.name = nameField.value;
		clone.relationshipName = relationshipNameField.value;
		clone.relationshipType = relationshipdataTypeField.value;
		clone.relationshipCardinality = relationshipCardinalityField.value;
		
		graph.model.setValue(cell.source, clone);
		
		var connector = new Connector();
		connector.name = relationshipNameField.value;
		graph.model.setValue(cell, connector);
	
		wnd.destroy();
	};
	
	// Defines the function to be executed when the
	// Cancel button is pressed in the dialog
	var cancelFunction = function() {
		wnd.destroy();
	};
	form.addButtons(okFunction, cancelFunction);

	wnd = showModalWindow(name, form.table, 240, 110);
}

function showAlert(title, message) {
	var width = 410;
	var height = 20;
	
	var panel = document.createElement('div');
	panel.setAttribute('align', 'center');
	panel.setAttribute('height', '70px');
	
	var text = document.createElement('p');
	text.style.width = width+'px';
	text.style.height = height+'px';
	//text.disabled = 'true';
	//text.value = message;
	mxUtils.write(text, message);
	
	wnd = null;
	var button = document.createElement('button');
	mxUtils.write(button, "Close");
	mxEvent.addListener(button, 'click', function(evt) {
		wnd.destroy();
	});
	
	panel.appendChild(text);
	panel.appendChild(button);
	
	wnd = showModalWindow(title, panel, width, height+70);
};

function showPrompt(title, message, callback) {
	var width = 410;
	var height = 20;
	
	var panel = document.createElement('div');
	panel.setAttribute('align', 'center');
	panel.setAttribute('height', '70px');
	
	var text = document.createElement('input');
	text.style.width = width+'px';
	text.style.height = height+'px';
	//text.disabled = 'true';
	text.value = message;
	//mxUtils.write(text, message);
	
	wnd = null;
	
	var buttonOK = document.createElement('button');
	mxUtils.write(buttonOK, "OK");
	mxEvent.addListener(buttonOK, 'click', function(evt) {
		wnd.destroy();
		callback(text.value);
	});
	
	var buttonCancel = document.createElement('button');
	mxUtils.write(buttonCancel, "Cancel");
	mxEvent.addListener(buttonCancel, 'click', function(evt) {
		wnd.destroy();
	});
	
	panel.appendChild(text);
	panel.appendChild(buttonOK);
	panel.appendChild(buttonCancel);
	
	wnd = showModalWindow(title, panel, width, height+50);
};
		