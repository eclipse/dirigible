/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.engine.odata2.api.ITableMetadataProvider;
import org.eclipse.dirigible.engine.odata2.definition.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OData2ODataXTransformer {

    private static final Logger logger = LoggerFactory.getLogger(OData2ODataXTransformer.class);

    public static final List<String> TABLE_TYPES = List.of(ISqlKeywords.METADATA_TABLE, ISqlKeywords.METADATA_BASE_TABLE);
    public static final List<String> VIEW_TYPES = List.of(ISqlKeywords.METADATA_VIEW, ISqlKeywords.METADATA_CALC_VIEW);

    private final ODataPropertyNameEscaper propertyNameEscaper;
    private final ITableMetadataProvider tableMetadataProvider;

    public OData2ODataXTransformer(ITableMetadataProvider tableMetadataProvider) {
        this(tableMetadataProvider, new DefaultPropertyNameEscaper());
    }

    public OData2ODataXTransformer(ITableMetadataProvider tableMetadataProvider, ODataPropertyNameEscaper propertyNameEscaper) {
        this.tableMetadataProvider = tableMetadataProvider;
        this.propertyNameEscaper = propertyNameEscaper;
    }

    public String[] transform(ODataDefinition model) throws SQLException {
        String[] result = new String[2];
        StringBuilder buff = new StringBuilder();
        buff.append("<Schema Namespace=\"").append(model.getNamespace()).append("\"\n")
                .append("\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n");

        StringBuilder associations = new StringBuilder();
        StringBuilder entitySets = new StringBuilder();
        StringBuilder associationsSets = new StringBuilder();
        for (ODataEntityDefinition entity : model.getEntities()) {
            PersistenceTableModel tableMetadata = tableMetadataProvider.getPersistenceTableModel(entity);

            if (tableMetadata == null) {
                continue;
            }

            List<PersistenceTableColumnModel> idColumns = tableMetadata.getColumns().stream().filter(PersistenceTableColumnModel::isPrimaryKey).collect(Collectors.toList());

            if (tableMetadata.getTableType() == null || (idColumns.isEmpty() && ISqlKeywords.METADATA_TABLE.equals(tableMetadata.getTableType()))) {
                logger.error("Table {} not available for entity {}, so it will be skipped.", entity.getTable(), entity.getName());
                continue;
            }
            if (!VIEW_TYPES.contains(tableMetadata.getTableType()) && !TABLE_TYPES.contains(tableMetadata.getTableType())) {
                logger.error("Unsupported database type {} for entity object {}", tableMetadata.getTableType(), entity.getTable());
                continue;
            }

            boolean isPretty = Boolean.parseBoolean(Configuration.get(DBMetadataUtil.DIRIGIBLE_GENERATE_PRETTY_NAMES, "true"));

            List<ODataParameter> entityParameters = entity.getParameters();
            List<ODataProperty> entityProperties = entity.getProperties();

            if (tableMetadata.getTableType().equals(ISqlKeywords.METADATA_TABLE)) {
                ODataMetadataUtil.validateODataPropertyName(tableMetadata.getColumns(), entityProperties, entity.getName());
            }

            buff.append("\t<EntityType Name=\"").append(entity.getName()).append("Type").append("\"");
            entity.getAnnotationsEntityType().forEach((key, value) -> buff.append(" ").append(key).append("=\"").append(value).append("\""));
            buff.append(">\n");

            List<PersistenceTableColumnModel> entityOrigKeys = checkIfViewHasExposedOriginalKeysFromTable(tableMetadata, entity);
            buff.append("\t\t<Key>\n");

            // Keys are explicit defined only on VIEW artifact
            if (VIEW_TYPES.contains(tableMetadata.getTableType())) {
                entityParameters.forEach(parameter -> buff.append("\t\t\t<PropertyRef Name=\"").append(propertyNameEscaper.escape(parameter.getName())).append("\" />\n"));

                if (entityOrigKeys.size() > 0) {
                    entityOrigKeys.forEach(key -> {
                        String columnValue = DBMetadataUtil.getPropertyNameFromDbColumnName(key.getName(), entityProperties, isPretty);
                        buff.append("\t\t\t<PropertyRef Name=\"").append(propertyNameEscaper.escape(columnValue)).append("\" />\n");
                    });
                } else {
                    // Local key was generated
                    entity.getKeys().forEach(key -> buff.append("\t\t\t<PropertyRef Name=\"").append(propertyNameEscaper.escape(key)).append("\" />\n"));
                }
            } else {
                idColumns.forEach(column -> {
                    String nameValue = DBMetadataUtil.getPropertyNameFromDbColumnName(column.getName(), entityProperties, isPretty);
                    buff.append("\t\t\t<PropertyRef Name=\"").append(propertyNameEscaper.escape(nameValue)).append("\" />\n");
                });
            }
            buff.append("\t\t</Key>\n");

            // Add keys and parameters as property
            if (VIEW_TYPES.contains(tableMetadata.getTableType())) {
                if (entityOrigKeys.size() == 0) {
                    // Local key was generated
                    entity.getKeys().forEach(key -> buff.append("\t\t<Property Name=\"").append(key).append("\"").append(" Type=\"").append("Edm.String").append("\"").append(" Nullable=\"").append("false").append("\" MaxLength=\"2147483647\"").append(" sap:filterable=\"false\"").append("/>\n"));
                }

                entityParameters.forEach(parameter -> {
                    buff.append("\t\t<Property Name=\"").append(parameter.getName()).append("\"")
                            .append(" Nullable=\"").append(parameter.isNullable()).append("\"").append(" Type=\"").append(parameter.getType() != null ? parameter.getType() : "null").append("\"");
                    buff.append("/>\n");
                });
            }

            // Expose all Db columns in case no entity props are defined
            if (entityProperties.isEmpty() && entityParameters.isEmpty()) {
                tableMetadata.getColumns().forEach(column -> {
                    String columnValue = DBMetadataUtil.getPropertyNameFromDbColumnName(column.getName(), entityProperties, isPretty);
                    buff.append("\t\t<Property Name=\"").append(propertyNameEscaper.escape(columnValue)).append("\"")
                            .append(" Nullable=\"").append(DBMetadataUtil.isNullable(column, entityProperties)).append("\"").append(" Type=\"").append(DBMetadataUtil.getType(column, entityProperties)).append("\"");
                    buff.append("/>\n");
                });
            } else {
                // In case entity props are defined expose only them
                PersistenceTableModel finalTableMetadata = tableMetadata;
                entityProperties.forEach(prop -> {
                    List<PersistenceTableColumnModel> dbColumn = finalTableMetadata.getColumns().stream().filter(el -> el.getName().equals(prop.getColumn())).collect(Collectors.toList());
                    if (dbColumn.size() > 0) {
                        String columnValue = DBMetadataUtil.getPropertyNameFromDbColumnName(dbColumn.get(0).getName(), entityProperties, isPretty);
                        buff.append("\t\t<Property Name=\"").append(columnValue).append("\"")
                                .append(" Nullable=\"").append(prop.isNullable()).append("\"").append(" Type=\"").append(prop.getType() != null ? prop.getType() : dbColumn.get(0).getType()).append("\"");
                        prop.getAnnotationsProperty().forEach((key, value) -> buff.append(" ").append(key).append("=\"").append(value).append("\""));
                        buff.append("/>\n");
                    } else {
                        throw new OData2TransformerException(String.format("There is inconsistency for entity '%s'. Odata column definitions for %s do not match the DB table column definition.", entity.getName(), prop.getName()));
                    }
                });
            }

            entity.getNavigations().forEach(relation -> {
                ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
                String fromRole = association.getFrom().getEntity();
                String toRole = association.getTo().getEntity();
                buff.append("\t\t<NavigationProperty Name=\"").append(relation.getName()).append("\"")
                        .append(" Relationship=\"").append(model.getNamespace()).append(".").append(relation.getAssociation()).append("Type\"")
                        .append(" FromRole=\"").append(fromRole).append("Principal").append("\"")
                        .append(" ToRole=\"").append(toRole).append("Dependent").append("\"");
                relation.getAnnotationsNavigationProperty().forEach((key, value) -> buff.append(" ").append(key).append("=\"").append(value).append("\""));
                buff.append("/>\n");
            });

            // Keep associations for later use
            entity.getNavigations().forEach(relation -> {
                ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
                String fromRole = association.getFrom().getEntity();
                String toRole = association.getTo().getEntity();
                String fromMultiplicity = association.getFrom().getMultiplicity();
                ODataMetadataUtil.validateMultiplicity(fromMultiplicity);
                String toMultiplicity = association.getTo().getMultiplicity();
                ODataMetadataUtil.validateMultiplicity(toMultiplicity);
                associations.append("\t<Association Name=\"").append(relation.getAssociation()).append("Type\">\n")
                        .append("\t\t<End Type=\"").append(model.getNamespace()).append(".").append(fromRole).append("Type\"")
                        .append(" Role=\"").append(fromRole).append("Principal").append("\" Multiplicity=\"").append(fromMultiplicity).append("\"/>\n")
                        .append("\t\t<End Type=\"").append(model.getNamespace()).append(".").append(toRole).append("Type\"")
                        .append(" Role=\"").append(toRole).append("Dependent").append("\" Multiplicity=\"").append(toMultiplicity).append("\"/>\n")
                        .append(" \t</Association>\n"
                        );
            });

            // Keep entity sets for later use
            entitySets.append("\t\t<EntitySet Name=\"").append(entity.getAlias())
                    .append("\" EntityType=\"").append(model.getNamespace()).append(".").append(entity.getName()).append("Type\"");
            entity.getAnnotationsEntitySet().forEach((key, value) -> entitySets.append(" ").append(key).append("=\"").append(value).append("\""));
            entitySets.append("/>\n");

            // Keep associations sets for later use
            entity.getNavigations().forEach(relation -> {
                ODataAssociationDefinition association = ODataMetadataUtil.getAssociation(model, relation.getAssociation(), relation.getName());
                String fromRole = association.getFrom().getEntity();
                String toRole = association.getTo().getEntity();
                String fromSet = entity.getAlias();
                ODataEntityDefinition toSetEntity = ODataMetadataUtil.getEntity(model, toRole, relation.getName());
                String toSet = toSetEntity.getAlias();
                associationsSets.append("\t<AssociationSet Name=\"").append(relation.getAssociation()).append("\"")
                        .append(" Association=\"").append(model.getNamespace()).append(".").append(relation.getAssociation()).append("Type\"");
                association.getAnnotationsAssociationSet().forEach((key, value) -> associationsSets.append(" ").append(key).append("=\"").append(value).append("\""));
                associationsSets.append(">\n");
                associationsSets.append("\t\t\t<End Role=\"").append(fromRole).append("Principal").append("\"")
                        .append(" EntitySet=\"").append(fromSet).append("\"/>\n")
                        .append(" \t\t\t<End Role=\"").append(toRole).append("Dependent").append("\"")
                        .append(" EntitySet=\"").append(toSet).append("\"/>\n")
                        .append("\t\t\t</AssociationSet>\n"
                        );
            });
            buff.append("\t</EntityType>\n");
        }
        buff.append(associations);

        StringBuilder container = new StringBuilder();
        container.append(entitySets);
        container.append(associationsSets);

        buff.append("</Schema>\n");
        result[0] = buff.toString();
        result[1] = container.toString();
        return result;
    }

    private List<PersistenceTableColumnModel> checkIfViewHasExposedOriginalKeysFromTable(PersistenceTableModel tableMetadata, ODataEntityDefinition entity) {
        return tableMetadata.getColumns().stream().filter(el -> entity.getKeys().stream().anyMatch(x -> x.equals(el.getName()))).collect(Collectors.toList());
    }
}
