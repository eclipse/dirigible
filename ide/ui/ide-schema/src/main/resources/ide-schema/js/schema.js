function createSchema(graph) {
	var schema = [];
	schema.push('<schema>\n');
	schema.push(' <structures>\n');
	var parent = graph.getDefaultParent();
	var childCount = graph.model.getChildCount(parent);

	for (var i=0; i<childCount; i++) {
		var child = graph.model.getChildAt(parent, i);
		
		if (!graph.model.isEdge(child)) {
			schema.push('  <structure name="'+child.value.name+'" type="'+child.value.type.toLowerCase()+'">\n');
			
			var columnCount = graph.model.getChildCount(child);

			if (columnCount > 0) {
				for (var j=0; j<columnCount; j++) {
					var column = graph.model.getChildAt(child, j).value;
					
					if (column.isSQL) {
						schema.push('    <sql value="'+column.name+'"/>\n');
					} else {
						schema.push('    <column name="'+column.name+'" type="'+column.type+'"');
						if (column.columnLength !== null) {
							schema.push(' length="'+column.columnLength+'"');
						}
						if (column.notNull) {
							schema.push(' nullable="false"');
						}
						if (column.primaryKey) {
							schema.push(' primaryKey="true"');
						}
						if (column.autoIncrement) {
							schema.push(' identity="true"');
						}
						if (column.unique) {
							schema.push(' unique="true"');
						}
						if (column.defaultValue !== null) {
							schema.push(' defaultValue="'+column.defaultValue+'"');
						}
						if (column.precision !== null) {
							schema.push(' precision="'+column.precision+'"');
						}
						if (column.scale !== null) {
							schema.push(' scale="'+column.scale+'"');
						}
						schema.push('></column>\n');
					}
				}
			}
			schema.push('  </structure>\n');
		} else {
			schema.push('  <structure name="'+child.source.parent.value.name+'_' 
				+child.target.parent.value.name+'" type="foreignKey" ');
			schema.push('table="'+child.source.parent.value.name+'" ');
			schema.push('constraintName="'+child.source.parent.value.name+'_' 
				+ child.target.parent.value.name+'" ');
			schema.push('columns="'+child.source.value.name+'" '+
				'referencedTable="'+child.target.parent.value.name+'" '+
				'referencedColumns="'+child.target.value.name+'">\n');
			schema.push('  </structure>\n');
		}
	}
	schema.push(' </structures>\n');
	
	var enc = new mxCodec(mxUtils.createXmlDocument());
	var node = enc.encode(graph.getModel());
	var model = mxUtils.getXml(node);
	schema.push(' '+model);
	schema.push('\n</schema>');
	
	return schema.join('');
};