function createModel(graph) {
	var model = [];
	model.push('<model>\n');
	model.push(' <entities>\n');
	var parent = graph.getDefaultParent();
	var childCount = graph.model.getChildCount(parent);

	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		
		if (!graph.model.isEdge(child)) {
			model.push('  <entity name="'+child.value.name+'" type="'+child.value.type.toLowerCase()+'">\n');
			
			var propertyCount = graph.model.getChildCount(child);

			if (propertyCount > 0) {
				for (var j=0; j<propertyCount; j++) {
					var property = graph.model.getChildAt(child, j).value;
					
					model.push('    <property name="'+property.name+'" type="'+property.type+'"');
					if (property.propertyLength !== null) {
						model.push(' length="'+property.propertyLength+'"');
					}
					if (property.notNull) {
						model.push(' nullable="false"');
					}
					if (property.primaryKey) {
						model.push(' primaryKey="true"');
					}
					if (property.autoIncrement) {
						model.push(' identity="true"');
					}
					if (property.unique) {
						model.push(' unique="true"');
					}
					if (property.defaultValue !== null) {
						model.push(' defaultValue="'+property.defaultValue+'"');
					}
					if (property.precision !== null) {
						model.push(' precision="'+property.precision+'"');
					}
					if (property.scale !== null) {
						model.push(' scale="'+property.scale+'"');
					}
					model.push('></property>\n');
				}
			}
			model.push('  </entity>\n');
		} else {
			model.push('  <relation name="'+child.source.parent.value.name+'_' 
				+child.target.parent.value.name+'" type="relation" ');
			model.push('entity="'+child.source.parent.value.name+'" ');
			model.push('relationName="'+child.source.parent.value.name+'_' 
				+ child.target.parent.value.name+'" ');
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