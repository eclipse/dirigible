function createModel(graph) {
	var model = [];
	model.push('<model>\n');
	model.push(' <entities>\n');
	var parent = graph.getDefaultParent();
	var childCount = graph.model.getChildCount(parent);

	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		
		if (!graph.model.isEdge(child)) {
			child.value.dataName = child.value.dataName ? child.value.dataName : JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase();
			child.value.menuKey = child.value.menuKey ? child.value.menuKey : JSON.stringify(child.value.name).replace(/\W/g, '').toLowerCase();
			child.value.menuLabel = child.value.menuLabel ? child.value.menuLabel : child.value.name;
			model.push('  <entity name="'+child.value.name+'" dataName="'+child.value.dataName+'" isPrimary="'+child.value.isPrimary+'"'
				+' menuKey="'+child.value.menuKey+'" menuLabel="'+child.value.menuLabel+'" menuIndex="'+child.value.menuIndex+'" layoutType="'+child.value.layoutType+'"'
				+'>\n');
			
			var propertyCount = graph.model.getChildCount(child);

			if (propertyCount > 0) {
				for (var j=0; j<propertyCount; j++) {
					var property = graph.model.getChildAt(child, j).value;
					
					property.dataName = property.dataName ? property.dataName : JSON.stringify(property.name).replace(/\W/g, '').toUpperCase();
					
					model.push('    <property name="'+property.name+'" dataName="'+property.dataName+'" dataType="'+property.dataType+'"');
					if (property.dataLength !== null) {
						model.push(' dataLength="'+property.dataLength+'"');
					}
					if (property.dataNotNull) {
						model.push(' dataNullable="false"');
					}
					if (property.dataPrimaryKey) {
						model.push(' dataPrimaryKey="true"');
					}
					if (property.dataAutoIncrement) {
						model.push(' dataIdentity="true"');
					}
					if (property.dataUnique) {
						model.push(' dataUnique="true"');
					}
					if (property.dataDefaultValue !== null) {
						model.push(' dataDefaultValue="'+property.dataDefaultValue+'"');
					}
					if (property.dataPrecision !== null) {
						model.push(' dataPrecision="'+property.dataPrecision+'"');
					}
					if (property.dataScale !== null) {
						model.push(' dataScale="'+property.dataScale+'"');
					}
					if (property.relationshipType !== null) {
						model.push(' relationshipType="'+property.relationshipType ? property.relationshipType : 'ASSOCIATION'+'"');
					}
					if (property.relationshipCardinality !== null) {
						model.push(' relationshipCardinality="'+property.relationshipCardinality ? property.relationshipCardinality : '1_n'+'"');
					}
					if (property.relationshipName !== null) {
						model.push(' relationshipName="'+property.relationshipName+'"');
					}
					if (property.widgetType !== null) {
						model.push(' widgetType="'+property.widgetType+'"');
					}
					if (property.widgetLength !== null) {
						model.push(' widgetLength="'+property.widgetLength+'"');
					}
					if (property.widgetLabel !== null) {
						model.push(' widgetLabel="'+property.widgetLabel+'"');
					}
					if (property.widgetShortLabel !== null) {
						model.push(' widgetShortLabel="'+property.widgetShortLabel+'"');
					}
					if (property.widgetPattern !== null) {
						model.push(' widgetPattern="'+property.widgetPattern+'"');
					}
					if (property.widgetFormat !== null) {
						model.push(' widgetFormat="'+property.widgetFormat+'"');
					}
					if (property.widgetSection !== null) {
						model.push(' widgetSection="'+property.widgetSection+'"');
					}
					if (property.widgetService !== null) {
						model.push(' widgetService="'+property.widgetService+'"');
					}
					if (property.widgetIsMajor) {
						model.push(' widgetIsMajor="true"');
					}
					
					model.push('></property>\n');
				}
			}
			model.push('  </entity>\n');
		} else {
			var relationName = child.name ? child.name : child.source.parent.value.name+'_'+ child.target.parent.value.name;
			model.push('  <relation name="'+child.source.parent.value.name+'_' 
				+child.target.parent.value.name+'" type="relation" ');
			model.push('entity="'+child.source.parent.value.name+'" ');
			model.push('relationName="'+relationName+'" ');
			model.push('property="'+child.source.value.name+'" '+
				'referenced="'+child.target.parent.value.name+'" '+
				'referencedProperty="'+child.target.value.name+'">\n');
			model.push('  </relation>\n');
		}
	}
	model.push(' </entities>\n');
	
	var enc = new mxCodec(mxUtils.createXmlDocument());
	var node = enc.encode(graph.getModel());
	var mxGraph = mxUtils.getXml(node);
	model.push(' '+mxGraph);
	model.push('\n</model>');
	
	return model.join('');
}

function createModelJson(graph) {
	var root = {};
	root.model = {};
	root.model.entities = [];
	var parent = graph.getDefaultParent();
	var childCount = graph.model.getChildCount(parent);
	
	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		if (graph.model.isEdge(child)) {
			// Relationship Properties
			var relationName = child.name ? child.name : child.source.parent.value.name+'_'+ child.target.parent.value.name;
			child.source.value.relationshipName = relationName;
			child.source.value.relationshipEntityName = child.target.parent.value.name;
			child.source.value.widgetDropDownKey = child.source.value.widgetDropDownKey ? child.source.value.widgetDropDownKey : child.target.value.name;
			child.source.value.widgetDropDownValue = child.source.value.widgetDropDownValue ? child.source.value.widgetDropDownValue : child.target.value.name;
		}
	}



	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		if (!graph.model.isEdge(child)) {
			var entity = {};
			entity.name = child.value.name;
			entity.dataName = child.value.dataName ? child.value.dataName : JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase();
			entity.isPrimary = child.value.isPrimary;
			entity.menuKey = child.value.menuKey ? child.value.menuKey : JSON.stringify(child.value.name).replace(/\W/g, '').toLowerCase();
			entity.menuLabel = child.value.menuLabel ? child.value.menuLabel : child.value.name;
			entity.menuIndex = child.value.menuIndex ? child.value.menuIndex : 100;
			entity.layoutType = child.value.layoutType;
			entity.properties = [];
			
			var propertyCount = graph.model.getChildCount(child);
			if (propertyCount > 0) {
				for (var j=0; j<propertyCount; j++) {
					var childProperty = graph.model.getChildAt(child, j).value;
					var property = {};
					
					// General
					property.name = childProperty.name;
					
					// Data Properties
					property.dataName = childProperty.dataName ? childProperty.dataName : JSON.stringify(childProperty.name).replace(/\W/g, '').toUpperCase();
					property.dataType = childProperty.dataType;
					property.dataLength = childProperty.dataLength;
					property.dataDefaultValue = childProperty.dataDefaultValue;
					property.dataPrimaryKey = childProperty.dataPrimaryKey ? childProperty.dataPrimaryKey : false;
					property.dataAutoIncrement = childProperty.dataAutoIncrement ? childProperty.dataAutoIncrement : false;
					property.dataNotNull = childProperty.dataNotNull ? childProperty.dataNotNull : false;
					property.dataUnique = childProperty.dataUnique ? childProperty.dataUnique : false;
					property.dataPrecision = childProperty.dataPrecision;
					property.dataScale = childProperty.dataScale;
					
					// Relationship Properties
					property.relationshipType = childProperty.relationshipType;
					property.relationshipCardinality = childProperty.relationshipCardinality;
					property.relationshipName = childProperty.relationshipName;
					property.relationshipEntityName = childProperty.relationshipEntityName;
					
					// Widget Properties
					property.widgetType = childProperty.widgetType;
					property.widgetLength = childProperty.widgetLength;
					property.widgetLabel = childProperty.widgetLabel;
					property.widgetShortLabel = childProperty.widgetShortLabel;
					property.widgetPattern = childProperty.widgetPattern;
					property.widgetFormat = childProperty.widgetFormat;
					property.widgetSection = childProperty.widgetSection;
					property.widgetService = childProperty.widgetService;
					property.widgetIsMajor = childProperty.widgetIsMajor ? childProperty.widgetIsMajor : false;
					property.widgetDropDownKey = childProperty.widgetDropDownKey;
					property.widgetDropDownValue = childProperty.widgetDropDownValue;
					
					entity.properties.push(property);
				}
			}

			root.model.entities.push(entity);
		}
	}
	
	var serialized = JSON.stringify(root, null, 4);
	
	return serialized;
}