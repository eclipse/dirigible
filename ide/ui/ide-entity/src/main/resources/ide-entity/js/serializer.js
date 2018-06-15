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
			model.push('  <entity name="'+_.escape(child.value.name)+
				'" dataName="'+_.escape(child.value.dataName)+
				'" dataQuery="'+_.escape(child.value.dataQuery)+
				'" type="'+_.escape(child.value.type ? child.value.type : 'primary')+
				'" menuKey="'+_.escape(child.value.menuKey)+
				'" menuLabel="'+_.escape(child.value.menuLabel)+
				'" menuIndex="'+_.escape(child.value.menuIndex)+
				'" layoutType="'+_.escape(child.value.layoutType)+
				'" perspectiveName="'+_.escape(child.value.perspectiveName)+
				'" perspectiveIcon="'+_.escape(child.value.perspectiveIcon)+
				'" perspectiveOrder="'+_.escape(child.value.perspectiveOrder)+
				'">\n');
			
			var propertyCount = graph.model.getChildCount(child);
			if (propertyCount > 0) {
				for (var j=0; j<propertyCount; j++) {
					var property = graph.model.getChildAt(child, j).value;
					
					property.dataName = property.dataName ? property.dataName : JSON.stringify(property.name).replace(/\W/g, '').toUpperCase();
					
					model.push('    <property name="'+_.escape(property.name)+
						'" dataName="'+_.escape(property.dataName)+
						'" dataType="'+_.escape(property.dataType)+'"');
					if (property.dataLength !== null) {
						model.push(' dataLength="'+_.escape(property.dataLength)+'"');
					}
					if (property.dataNotNull) {
						model.push(' dataNullable="false"');
					}
					if (property.dataPrimaryKey) {
						model.push(' dataPrimaryKey="true"');
					}
					if (property.dataAutoIncrement) {
						model.push(' dataAutoIncrement="true"');
					}
					if (property.dataUnique) {
						model.push(' dataUnique="true"');
					}
					if (property.dataDefaultValue !== null) {
						model.push(' dataDefaultValue="'+_.escape(property.dataDefaultValue)+'"');
					}
					if (property.dataPrecision !== null) {
						model.push(' dataPrecision="'+_.escape(property.dataPrecision)+'"');
					}
					if (property.dataScale !== null) {
						model.push(' dataScale="'+_.escape(property.dataScale)+'"');
					}
					if (property.relationshipType !== null) {
						model.push(' relationshipType="'+_.escape(property.relationshipType ? property.relationshipType : 'ASSOCIATION')+'"');
					}
					if (property.relationshipCardinality !== null) {
						model.push(' relationshipCardinality="'+_.escape(property.relationshipCardinality ? property.relationshipCardinality : '1_n')+'"');
					}
					if (property.relationshipName !== null) {
						model.push(' relationshipName="'+_.escape(property.relationshipName)+'"');
					}
					if (property.widgetType !== null) {
						model.push(' widgetType="'+_.escape(property.widgetType)+'"');
					}
					if (property.widgetLength !== null) {
						model.push(' widgetLength="'+_.escape(property.widgetLength)+'"');
					}
					if (property.widgetLabel !== null) {
						model.push(' widgetLabel="'+_.escape(property.widgetLabel)+'"');
					}
					if (property.widgetShortLabel !== null) {
						model.push(' widgetShortLabel="'+_.escape(property.widgetShortLabel)+'"');
					}
					if (property.widgetPattern !== null) {
						model.push(' widgetPattern="'+_.escape(property.widgetPattern)+'"');
					}
					if (property.widgetFormat !== null) {
						model.push(' widgetFormat="'+_.escape(property.widgetFormat)+'"');
					}
					if (property.widgetSection !== null) {
						model.push(' widgetSection="'+_.escape(property.widgetSection)+'"');
					}
					if (property.widgetService !== null) {
						model.push(' widgetService="'+_.escape(property.widgetService)+'"');
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
			model.push('  <relation name="'+_.escape(child.source.parent.value.name)+'_' 
				+_.escape(child.target.parent.value.name)+'" type="relation" ');
			model.push('entity="'+_.escape(child.source.parent.value.name)+'" ');
			model.push('relationName="'+_.escape(relationName)+'" ');
			model.push('property="'+_.escape(child.source.value.name)+'" '+
				'referenced="'+_.escape(child.target.parent.value.name)+'" '+
				'referencedProperty="'+_.escape(child.target.value.name)+'">\n');
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
	var compositions = {};
	
	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		if (graph.model.isEdge(child)) {
			// Relationship Properties
			var relationName = child.name ? child.name : child.source.parent.value.name+'_'+ child.target.parent.value.name;
			child.source.value.relationshipName = relationName;
			child.source.value.relationshipEntityName = child.target.parent.value.name;
			child.source.value.widgetDropDownKey = child.source.value.widgetDropDownKey ? child.source.value.widgetDropDownKey : child.target.value.name;
			child.source.value.widgetDropDownValue = child.source.value.widgetDropDownValue ? child.source.value.widgetDropDownValue : child.target.value.name;
			
			if (child.source.value.relationshipType === 'COMPOSITION') {
				var composition = {};
				composition.entityName = child.source.parent.value.name;
				composition.entityProperty = child.source.value.name;
				composition.localProperty = child.target.value.name;
				if (!compositions[child.target.parent.value.name]) {
					compositions[child.target.parent.value.name] = [];
				}
				compositions[child.target.parent.value.name].push(composition);
			}
		}
	}

	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		if (!graph.model.isEdge(child)) {
			var entity = {};
			entity.name = child.value.name;
			entity.dataName = child.value.dataName ? child.value.dataName : JSON.stringify(child.value.name).replace(/\W/g, '').toUpperCase();
			entity.dataQuery = child.value.dataQuery;
			entity.type = child.value.type ? child.value.type : "primary";
			entity.menuKey = child.value.menuKey ? child.value.menuKey : JSON.stringify(child.value.name).replace(/\W/g, '').toLowerCase();
			entity.menuLabel = child.value.menuLabel ? child.value.menuLabel : child.value.name;
			entity.menuIndex = child.value.menuIndex ? child.value.menuIndex : 100;
			entity.layoutType = child.value.layoutType;
			entity.perspectiveName = child.value.perspectiveName;
			entity.perspectiveIcon = child.value.perspectiveIcon;
			entity.perspectiveOrder = child.value.perspectiveOrder;
			entity.properties = [];
			
			if (compositions[entity.name]) {
				entity.compositions = compositions[entity.name];
			}
			
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
					property.dataPrimaryKey = childProperty.dataPrimaryKey ? childProperty.dataPrimaryKey : "false";
					property.dataAutoIncrement = childProperty.dataAutoIncrement ? childProperty.dataAutoIncrement : "false";
					property.dataNotNull = childProperty.dataNotNull ? childProperty.dataNotNull : "false";
					property.dataUnique = childProperty.dataUnique ? childProperty.dataUnique : "false";
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
					property.widgetIsMajor = childProperty.widgetIsMajor ? childProperty.widgetIsMajor : "false";
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