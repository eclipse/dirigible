/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OData2ODataMTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(OData2ODataMTransformer.class);

    @Inject
    private DBMetadataUtil dbMetadataUtil;

    public String[] transform(ODataDefinition model) throws SQLException {

        List<String> result = new ArrayList<>();

        for (ODataEntityDefinition entity : model.getEntities()) {
        	String entityName = entity.getName().replace(".", "");
        	String namespace = model.getNamespace();
        	String tableName = entity.getName().replace(".", "_").toUpperCase();
            StringBuilder buff = new StringBuilder();
            buff.append("{\n")
                    .append("    \"edmType\" : \"").append(entity.getAlias()).append("\",\n")
                    .append("    \"edmTypeFqn\" : \"").append(namespace).append(".").append(entityName).append("\",\n")
                    .append("    \"sqlTable\" : \"").append(tableName).append("\",\n");
            
            boolean isPretty = Boolean.parseBoolean(Configuration.get(DBMetadataUtil.DIRIGIBLE_GENERATE_PRETTY_NAMES, "true"));
            
            PersistenceTableModel tableMetadata = dbMetadataUtil.getTableMetadata(tableName);
            tableMetadata.getColumns().forEach(column -> {
				String columnValue = isPretty ? DBMetadataUtil.addCorrectFormatting(column.getName()) : column.getName();
				buff.append("\"").append(columnValue)
						.append("\" : \"").append(column.getName()).append("\",\n");
			});
            tableMetadata.getRelations().stream().forEach(relation -> {
				String fkValue = isPretty ? DBMetadataUtil.addCorrectFormatting(relation.getFkColumnName()) : relation.getFkColumnName();
				buff.append("\"_ref_").append(relation.getToTableName()).append("\":{ \" joinColumn \" : \"").append(
						fkValue).append("\"\n").append("}\n").append(",\n");
			});
            PersistenceTableColumnModel idColumn = tableMetadata.getColumns().stream().filter(PersistenceTableColumnModel::isPrimaryKey).findFirst().orElse(null);
            buff.append("    \"_pk_\" : \"").append(idColumn.getName()).append("\"}\n");
            
            result.add(buff.toString());
        }
        return result.toArray(new String[]{});

    }

}
