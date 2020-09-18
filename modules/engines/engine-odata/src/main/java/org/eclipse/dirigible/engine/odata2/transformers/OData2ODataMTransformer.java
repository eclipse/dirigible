/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataAssociationDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OData2ODataMTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(OData2ODataMTransformer.class);

    @Inject
    private DBMetadataUtil dbMetadataUtil;

    public String[] transform(ODataDefinition model) throws SQLException {

        List<String> result = new ArrayList<>();

        Map<String, String> tableToEntity = new HashMap<String, String>();
        for (ODataEntityDefinition entity : model.getEntities()) {
        	tableToEntity.put(entity.getTable(), entity.getName());
        }
        
        for (ODataEntityDefinition entity : model.getEntities()) {
            StringBuilder buff = new StringBuilder();
            buff.append("{\n")
                    .append("  \"edmType\": \"").append(entity.getName()).append("Type").append("\",\n")
                    .append("  \"edmTypeFqn\": \"").append(model.getNamespace()).append(".").append(entity.getName()).append("Type").append("\",\n")
                    .append("  \"sqlTable\": \"").append(entity.getTable()).append("\",\n");
            
            boolean isPretty = Boolean.parseBoolean(Configuration.get(DBMetadataUtil.DIRIGIBLE_GENERATE_PRETTY_NAMES, "true"));
            
            PersistenceTableModel tableMetadata = dbMetadataUtil.getTableMetadata(entity.getTable());
            List<PersistenceTableColumnModel> idColumns = tableMetadata.getColumns().stream().filter(PersistenceTableColumnModel::isPrimaryKey).collect(Collectors.toList());
            
            if (idColumns == null || idColumns.isEmpty()) {
            	logger.error("Table {} not available for entity {}, so it will be skipped.", entity.getTable(), entity.getName());
            	continue;
            }
            
            List<ODataProperty> entityProperties = entity.getProperties();
            tableMetadata.getColumns().forEach(column -> {
				String columnValue = DBMetadataUtil.getColumnToProperty(column.getName(), entityProperties, isPretty);
				buff.append("  \"").append(columnValue)
						.append("\": \"").append(column.getName()).append("\",\n");
			});
            
            tableMetadata.getRelations().stream().forEach(relation -> {
				String fkValue = isPretty ? DBMetadataUtil.addCorrectFormatting(relation.getFkColumnName()) : relation.getFkColumnName();
				String toEntityName = tableToEntity.get(relation.getToTableName());
				buff.append("  \"_ref_").append(toEntityName).append("Type").append("\": {\"joinColumn\": \"")
					.append(fkValue).append("\"\n").append("}\n").append(",\n");
			});
            
            entity.getNavigations().forEach(relation -> {
            	ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
    			String toRole = association.getTo().getEntity();
    			String fromRoleProperty = association.getFrom().getProperty();
    			ODataEntityDefinition toSetEntity = ODataMetadataUtil.getEntity(model, toRole, relation.getName());
    			String dependentEntity = toSetEntity.getName();
				buff.append("  \"_ref_").append(dependentEntity).append("Type").append("\": {\"joinColumn\": \"")
					.append(fromRoleProperty).append("\"\n").append("}\n").append(",\n");
			});
            
            String[] pks = idColumns.stream().map(PersistenceTableColumnModel::getName).collect(Collectors.toList()).toArray(new String[]{});
            buff.append("  \"_pk_\": \"").append(String.join(",", pks)).append("\"}\n");
            
            result.add(buff.toString());
        }
        return result.toArray(new String[]{});

    }

}
