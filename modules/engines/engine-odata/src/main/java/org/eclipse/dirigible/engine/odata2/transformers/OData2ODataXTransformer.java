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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OData2ODataXTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(OData2ODataXTransformer.class);

    @Inject
    private DBMetadataUtil dbMetadataUtil;

    public String transform(ODataDefinition model) throws SQLException {
        StringBuilder buff = new StringBuilder();
        buff.append("<Schema Namespace=\"").append(model.getNamespace()).append("\"\n")
                .append("    xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n");

        for (ODataEntityDefinition entity : model.getEntities()) {
            String tableName = entity.getName().replace(".", "_").toUpperCase();
            String entityName = entity.getName().replace(".", "");
            PersistenceTableModel tableMetadata = dbMetadataUtil.getTableMetadata(tableName);
            PersistenceTableColumnModel idColumn = tableMetadata.getColumns().stream().filter(PersistenceTableColumnModel::isPrimaryKey).findFirst().orElse(null);

            boolean isPretty = Boolean.parseBoolean(Configuration.get(DBMetadataUtil.DIRIGIBLE_GENERATE_PRETTY_NAMES, "true"));
            
            String nameValue = isPretty ? DBMetadataUtil.addCorrectFormatting(idColumn.getName()) : idColumn.getName();
			buff.append("    <EntityType Name=\"").append(entityName).append("\">\n")
                    .append("        <Key>\n")
                    .append("            <PropertyRef Name=\"").append(nameValue).append("\" />\n")
                    .append("        </Key>\n");
            tableMetadata.getColumns().forEach(column -> {
				String columnValue = isPretty ? DBMetadataUtil.addCorrectFormatting(column.getName()) : column.getName();
				buff.append("        <Property Name=\"").append(columnValue).append("\"")
						.append(" Nullable=\"").append(column.isNullable()).append("\"").append(" Type=\"").append(column.getType()).append("\"/>\n");
			});

            buff.append("    </EntityType>\n");
        }

        buff.append("    <EntityContainer Name=\"").append(FilenameUtils.getBaseName(model.getLocation())).append("EntityContainer\" m:IsDefaultEntityContainer=\"true\">\n");
        for (ODataEntityDefinition entity : model.getEntities()) {
        	String entityName = entity.getName().replace(".", "");
            buff.append("        <EntitySet Name=\"").append(entity.getAlias()).append("\" EntityType=\"").append(model.getNamespace()).append(".").append(entityName).append("\" />\n");
                
        }
        buff.append("    </EntityContainer>\n");

        buff.append("</Schema>\n");
        return buff.toString();
    }
}
