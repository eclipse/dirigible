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
	var typeField = form.addCombo('Type', false, 1);
	form.addOption(typeField, "VARCHAR", "VARCHAR", cell.value.type === "VARCHAR");
	form.addOption(typeField, "CHAR", "CHAR", cell.value.type === "CHAR");
	form.addOption(typeField, "DATE", "DATE", cell.value.type === "DATE");
	form.addOption(typeField, "TIME", "TIME", cell.value.type === "TIME");
	form.addOption(typeField, "TIMESTAMP", "TIMESTAMP", cell.value.type === "TIMESTAMP");
	form.addOption(typeField, "INTEGER", "INTEGER", cell.value.type === "INTEGER");
	form.addOption(typeField, "TINYINT", "TINYINT", cell.value.type === "TINYINT");
	form.addOption(typeField, "BIGINT", "BIGINT", cell.value.type === "BIGINT");
	form.addOption(typeField, "SMALLINT", "SMALLINT", cell.value.type === "SMALLINT");
	form.addOption(typeField, "REAL", "REAL", cell.value.type === "REAL");
	form.addOption(typeField, "DOUBLE", "DOUBLE", cell.value.type === "DOUBLE");
	form.addOption(typeField, "BOOLEAN", "BOOLEAN", cell.value.type === "BOOLEAN");
	form.addOption(typeField, "BLOB", "BLOB", cell.value.type === "BLOB");
	form.addOption(typeField, "DECIMAL", "DECIMAL", cell.value.type === "DECIMAL");
	form.addOption(typeField, "BIT", "BIT", cell.value.type === "BIT");
	
	var lengthField = form.addText('Length', cell.value.columnLength);
	
	var primaryKeyField = form.addCheckbox('Primary Key', cell.value.primaryKey);
	var autoIncrementField = form.addCheckbox('Auto Increment', cell.value.autoIncrement);
	var notNullField = form.addCheckbox('Not Null', cell.value.notNull);
	var uniqueField = form.addCheckbox('Unique', cell.value.unique);
	
	var precisionField = form.addText('Precision', cell.value.precision);
	var scaleField = form.addText('Scale', cell.value.scale);
	
	var defaultField = form.addText('Default', cell.value.defaultValue || '');
	var useDefaultField = form.addCheckbox('Use Default', cell.value.defaultValue !== null);		
	

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();
		
		clone.name = nameField.value;
		clone.type = typeField.value;

		if (useDefaultField.checked) {
			clone.defaultValue = defaultField.value;
		} else {
			clone.defaultValue = null;
		}
		
		clone.columnLength = lengthField.value;
		
		clone.primaryKey = primaryKeyField.checked;
		clone.autoIncrement = autoIncrementField.checked;
		clone.notNull = notNullField.checked;
		clone.unique = uniqueField.checked;
		
		clone.precision = precisionField.value;
		clone.scale = scaleField.value;
		
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
	wnd = showModalWindow(name, form.table, 240, 310);
}

function showQueryProperties(graph, cell) {
	// Creates a form for the user object inside the cell
	var form = new mxForm('properties');

	// Adds a field for the columnname
	var nameField = form.addTextarea('SQL', cell.value.name, 5);

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();
		
		clone.name = nameField.value;
		
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
	wnd = showModalWindow(name, form.table, 240, 110);
}

function showStructureProperties(graph, cell) {
	// Creates a form for the user object inside the cell
	var form = new mxForm('properties');

	// Adds a field for the table or view name
	var name = cell.value.name;
	var nameField = form.addText('Name', name);

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();
		
		clone.name = nameField.value;
		
		graph.model.setValue(cell, clone);
	
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

function showConnectorProperties(graph, cell) {
	// Creates a form for the user object inside the cell
	var form = new mxForm('properties');

	// Adds a field for the columnname
	var name = cell.source.value.name+':'+cell.target.value.name;
	var nameField = form.addText('Name', name);
	nameField.readOnly = true;

	var wnd = null;

	// Defines the function to be executed when the
	// OK button is pressed in the dialog
	var okFunction = function() {
		var clone = cell.value.clone();
		
		clone.name = nameField.value;
		
		graph.model.setValue(cell, clone);
	
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
		