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
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataAssociationDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OData2ODataXTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(OData2ODataXTransformer.class);

    @Inject
    private DBMetadataUtil dbMetadataUtil;

    public String[] transform(ODataDefinition model) throws SQLException {
    	String[] result = new String[2];
        StringBuilder buff = new StringBuilder();
        buff.append("<Schema Namespace=\"").append(model.getNamespace()).append("\"\n")
                .append("    xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n");

        StringBuilder associations = new StringBuilder();
        StringBuilder entitySets = new StringBuilder();
        StringBuilder associationsSets = new StringBuilder();
        for (ODataEntityDefinition entity : model.getEntities()) {
            PersistenceTableModel tableMetadata = dbMetadataUtil.getTableMetadata(entity.getTable());
            List<PersistenceTableColumnModel> idColumns = tableMetadata.getColumns().stream().filter(PersistenceTableColumnModel::isPrimaryKey).collect(Collectors.toList());

            if (idColumns == null || idColumns.isEmpty()) {
            	logger.error("Table {} not available for entity {}, so it will be skipped.", entity.getTable(), entity.getName());
            	continue;
            }
            
            boolean isPretty = Boolean.parseBoolean(Configuration.get(DBMetadataUtil.DIRIGIBLE_GENERATE_PRETTY_NAMES, "true"));
            
            
            
			buff.append("    <EntityType Name=\"").append(entity.getName()).append("Type").append("\">\n").append("        <Key>\n");
			idColumns.forEach(column -> {
				String nameValue = isPretty ? DBMetadataUtil.addCorrectFormatting(column.getName()) : column.getName();
				buff.append("            <PropertyRef Name=\"").append(nameValue).append("\" />\n");
			});                    
            buff.append("        </Key>\n");
            tableMetadata.getColumns().forEach(column -> {
				String columnValue = isPretty ? DBMetadataUtil.addCorrectFormatting(column.getName()) : column.getName();
				buff.append("        <Property Name=\"").append(columnValue).append("\"")
						.append(" Nullable=\"").append(column.isNullable()).append("\"").append(" Type=\"").append(column.getType()).append("\"/>\n");
			});
            
            entity.getNavigations().forEach(relation -> {
            	ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
				String fromRole = association.getFrom().getEntity();
				String toRole = association.getTo().getEntity();
				buff.append("        <NavigationProperty Name=\"").append(relation.getName()).append("\"")
						.append(" Relationship=\"").append(model.getNamespace()).append(".").append(relation.getAssociation()).append("Type\"")
						.append(" FromRole=\"").append(fromRole).append("Principal").append("\"")
						.append(" ToRole=\"").append(toRole).append("Dependent").append("\"/>\n"
				);
			});
            
            // keep associations for later use
            entity.getNavigations().forEach(relation -> {
            	ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
				String fromRole = association.getFrom().getEntity();
				String toRole = association.getTo().getEntity();
				String fromMultiplicity = association.getFrom().getMultiplicity();
				String toMultiplicity = association.getTo().getMultiplicity();
				associations.append("    <Association Name=\"").append(relation.getAssociation()).append("Type\">\n")
						.append("        <End Type=\"").append(model.getNamespace()).append(".").append(fromRole).append("Type\"")
						.append(" Role=\"").append(fromRole).append("Principal").append("\" Multiplicity=\"").append(fromMultiplicity).append("\"/>\n")
						.append("        <End Type=\"").append(model.getNamespace()).append(".").append(toRole).append("Type\"")
						.append(" Role=\"").append(toRole).append("Dependent").append("\" Multiplicity=\"").append(toMultiplicity).append("\"/>\n")
						.append("    </Association>\n"
				);
			});
            
            // keep entity sets for later use
            entitySets.append("        <EntitySet Name=\"").append(entity.getAlias())
            		.append("\" EntityType=\"").append(model.getNamespace()).append(".").append(entity.getName()).append("Type\" />\n");
            
            // keep associations sets for later use
            entity.getNavigations().forEach(relation -> {
            	ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
				String fromRole = association.getFrom().getEntity();
				String toRole = association.getTo().getEntity();
				String fromSet = entity.getAlias();
				ODataEntityDefinition toSetEntity = ODataMetadataUtil.getEntity(model, toRole, relation.getName());
				String toSet = toSetEntity.getAlias();
				associationsSets.append("        <AssociationSet Name=\"").append(relation.getAssociation()).append("\"")
						.append(" Association=\"").append(model.getNamespace()).append(".").append(relation.getAssociation()).append("Type\">\n")
						.append("            <End Role=\"").append(fromRole).append("Principal").append("\"")
						.append(" EntitySet=\"").append(fromSet).append("\"/>\n")
						.append("            <End Role=\"").append(toRole).append("Dependent").append("\"")
						.append(" EntitySet=\"").append(toSet).append("\"/>\n")
						.append("        </AssociationSet>\n"
				);
			});

            buff.append("    </EntityType>\n");
        }
        
        buff.append(associations.toString());

        StringBuilder container = new StringBuilder();
//        buff.append("    <EntityContainer Name=\"").append(FilenameUtils.getBaseName(model.getLocation())).append("EntityContainer\" m:IsDefaultEntityContainer=\"true\">\n");
        container.append(entitySets.toString());
        container.append(associationsSets.toString());
//        buff.append("    </EntityContainer>\n");

        buff.append("</Schema>\n");
        result[0] = buff.toString();
        result[1] = container.toString();
        return result;
    }

}
