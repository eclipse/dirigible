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
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.engine.odata2.api.ITableMetadataProvider;
import org.eclipse.dirigible.engine.odata2.definition.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class OData2ODataMTransformer {

    private static final Logger logger = LoggerFactory.getLogger(OData2ODataMTransformer.class);
    private final ODataPropertyNameEscaper propertyNameEscaper;

    private DBMetadataUtil dbMetadataUtil = new DBMetadataUtil();

    private final ITableMetadataProvider metadataProvider;

    public OData2ODataMTransformer(ODataPropertyNameEscaper propertyNameEscaper){
        this(propertyNameEscaper, new DefaultTableMetadataProvider());
    }

    public OData2ODataMTransformer(ODataPropertyNameEscaper propertyNameEscaper, ITableMetadataProvider metadataProvider){
        this.propertyNameEscaper = propertyNameEscaper;
        this.metadataProvider = metadataProvider;
    }

    public OData2ODataMTransformer(){
        this(new DefaultPropertyNameEscaper(), new DefaultTableMetadataProvider());
    }

    public String[] transform(ODataDefinition model) throws SQLException {

        List<String> result = new ArrayList<>();

        for (ODataEntityDefinition entity : model.getEntities()) {
            PersistenceTableModel tableMetadata = metadataProvider.getPersistenceTableModel(entity);

            StringBuilder buff = new StringBuilder();
            buff.append("{\n")
                    .append("\t\"edmType\": \"").append(entity.getName()).append("Type").append("\",\n")
                    .append("\t\"edmTypeFqn\": \"").append(model.getNamespace()).append(".").append(entity.getName()).append("Type").append("\",\n")
                    .append("\t\"sqlTable\": \"").append(entity.getTable()).append("\",\n")
                    .append("\t\"dataStructureType\": \"").append(tableMetadata.getTableType()).append("\",\n");

            boolean isPretty = Boolean.parseBoolean(Configuration.get(DBMetadataUtil.DIRIGIBLE_GENERATE_PRETTY_NAMES, "true"));

            if (tableMetadata.getTableType() == null) {
                logger.error("Table {} not available for entity {}, so it will be skipped.", entity.getTable(), entity.getName());
                continue;
            }

            List<PersistenceTableColumnModel> idColumns = tableMetadata.getColumns().stream().filter(PersistenceTableColumnModel::isPrimaryKey).collect(Collectors.toList());
            if (idColumns.isEmpty() && ISqlKeywords.METADATA_TABLE.equals(tableMetadata.getTableType())) {
                logger.error("Table {} doesn't have primary keys {}, so it will be skipped.", entity.getTable(), entity.getName());
                continue;
            }

            List<ODataProperty> entityProperties = entity.getProperties();
            ODataMetadataUtil.validateODataPropertyName(tableMetadata.getColumns(), entityProperties, entity.getName());
            // Expose all Db columns in case no entity props are defined
            if (entityProperties.isEmpty()) {
                tableMetadata.getColumns().forEach(column -> {
                    String columnValue = DBMetadataUtil.getPropertyNameFromDbColumnName(column.getName(), entityProperties, isPretty);
                    buff.append("\t\"").append(propertyNameEscaper.escape(columnValue)).append("\": \"").append(column.getName()).append("\",\n");
                });
            } else {
                // In case entity props are defined expose only them
                entityProperties.forEach(prop -> {
                    List<PersistenceTableColumnModel> dbColumnName = tableMetadata.getColumns().stream().filter(x -> x.getName().equals(prop.getColumn())).collect(Collectors.toList());
                    buff.append("\t\"").append(propertyNameEscaper.escape(prop.getName())).append("\": \"").append(dbColumnName.get(0).getName()).append("\",\n");
                });
            }

            List<ODataParameter> entityParameters = entity.getParameters();
            if(!entityParameters.isEmpty()) {
                List<String> parameterNames = new ArrayList<>();
                entityParameters.forEach(parameter -> {
                    parameterNames.add("\"" + parameter.getName() + "\"");
                    buff.append("\t\"").append(propertyNameEscaper.escape(parameter.getName())).append("\": \"").append(parameter.getName()).append("\",\n");
                });
                buff.append("\t\"_parameters_\" : [").append(String.join(",", parameterNames)).append("],\n");
            }


            //Process FK relations from DB if they exist
            Map<String, List<PersistenceTableRelationModel>> groupRelationsByToTableName = tableMetadata.getRelations().stream()
                    .collect(Collectors.groupingBy(PersistenceTableRelationModel::getToTableName));
            //In case there is FK on DB side, but the entity and it's navigation are not defined in the .odata file -> then the relation will not be exposed
            List<Map.Entry<String, List<PersistenceTableRelationModel>>> relationsThatExistInOdataFile
                    = groupRelationsByToTableName.entrySet().stream().filter(x -> {
                for (ODataEntityDefinition e : model.getEntities()) {
                    if (x.getKey().equals(e.getTable())) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());

            final List<String> assembleRefFromFK = relationsThatExistInOdataFile.stream().map(rel -> {
                String fkElement = rel.getValue().stream().map(x -> "\"" + x.getFkColumnName() + "\"").collect(Collectors.joining(","));
                ODataEntityDefinition toSetEntity = ODataMetadataUtil.getEntityByTableName(model, rel.getKey());
                String dependentEntity = toSetEntity.getName();
                return assembleOdataMRefSection(dependentEntity, fkElement, null, null);
            }).collect(Collectors.toList());
            if (!assembleRefFromFK.isEmpty()) {
                buff.append(String.join(",\n", assembleRefFromFK)).append(",\n");
            }


            // Process Associations from .odata file
            List<ODataAssociationDefinition> assWhereEntityIsFROMRole = model.getAssociations().stream().filter(ass -> ass.getFrom().getEntity().equals(entity.getName())).collect(Collectors.toList());
            List<ODataAssociationDefinition> assWhereEntityIsTORole = model.getAssociations().stream().filter(ass -> ass.getTo().getEntity().equals(entity.getName())).collect(Collectors.toList());

            for (ODataAssociationDefinition oDataAssociationDefinition : assWhereEntityIsFROMRole) {
                validateAssociationProperties(oDataAssociationDefinition, model);
            }
            List<String> assembleRefNav = assWhereEntityIsFROMRole.stream().map(association -> {
                String fromRoleEntity = association.getFrom().getEntity();
                String toRoleEntity = association.getTo().getEntity();
                ODataEntityDefinition toSetEntity = ODataMetadataUtil.getEntity(model, toRoleEntity, association.getName());
                String dependentEntity = toSetEntity.getName();
                String fkElement = association.getFrom().getProperties().stream().map(x -> "\"" + ODataMetadataUtil.getEntityPropertyColumnByPropertyName(model, fromRoleEntity, x) + "\"").collect(Collectors.joining(","));
                String mappingTableName = association.getFrom().getMappingTableDefinition().getMappingTableName();
                String mappingTableJoinColumn = association.getFrom().getMappingTableDefinition().getMappingTableJoinColumn();
                return checkRefSectionConsistency(buff, entity, groupRelationsByToTableName, assembleRefFromFK,
                        toSetEntity, dependentEntity, fkElement, mappingTableName, mappingTableJoinColumn);
            }).filter(Objects::nonNull).collect(Collectors.toList());
            if (!assembleRefNav.isEmpty()) {
                buff.append(String.join(",\n", assembleRefNav)).append(",\n");
            }

            for (ODataAssociationDefinition oDataAssociationDefinition : assWhereEntityIsTORole) {
                validateAssociationProperties(oDataAssociationDefinition, model);
            }
            assembleRefNav = assWhereEntityIsTORole.stream().map(association -> {
                String fromRoleEntity = association.getFrom().getEntity();
                String toRoleEntity = association.getTo().getEntity();
                ODataEntityDefinition fromSetEntity = ODataMetadataUtil.getEntity(model, fromRoleEntity, association.getName());
                String principleEntity = fromSetEntity.getName();
                String fkElement = association.getTo().getProperties().stream().map(x -> "\"" + ODataMetadataUtil.getEntityPropertyColumnByPropertyName(model, toRoleEntity, x) + "\"").collect(Collectors.joining(","));
                String mappingTableName = association.getTo().getMappingTableDefinition().getMappingTableName();
                String mappingTableJoinColumn = association.getTo().getMappingTableDefinition().getMappingTableJoinColumn();
                return checkRefSectionConsistency(buff, entity, groupRelationsByToTableName, assembleRefFromFK,
                        fromSetEntity, principleEntity, fkElement, mappingTableName, mappingTableJoinColumn);
            }).filter(Objects::nonNull).collect(Collectors.toList());
            if (!assembleRefNav.isEmpty()) {
                buff.append(String.join(",\n", assembleRefNav)).append(",\n");
            }

            if(entity.getKeyGenerated() != null && !entity.getKeyGenerated().isEmpty()) {
                buff.append("\t\"keyGenerated\": \"").append(entity.getKeyGenerated()).append("\",\n");
            }

            if("aggregate".equals(entity.getAnnotationsEntityType().get("sap:semantics"))) {
                buff.append("\t\"aggregationType\" : ");
                if(!entity.getAggregationsTypeAndColumn().isEmpty()) {
                    buff.append("\"derived\",\n");
                    Map<String, String> aggregationsTypeAndColumn = entity.getAggregationsTypeAndColumn();
                    String aggregationProps = aggregationsTypeAndColumn.keySet().stream()
                            .map(key -> "\t\t\"" + key + "\": \"" + aggregationsTypeAndColumn.get(key) + "\"")
                            .collect(Collectors.joining(",\n", "\t\"aggregationProps\" : {\n", "\n\t},\n"));
                    buff.append(aggregationProps);
                } else {
                    buff.append("\"explicit\",\n");
                }
            }

            String[] pks = idColumns.stream().map(PersistenceTableColumnModel::getName).collect(Collectors.toList()).toArray(new String[]{});
            buff.append("\t\"_pk_\" : \"").append(String.join(",", pks)).append("\"");
            buff.append("\n}");

            result.add(buff.toString());
        }
        return result.toArray(new String[]{});
    }

    private String checkRefSectionConsistency(StringBuilder buff, ODataEntityDefinition entity, Map<String,
            List<PersistenceTableRelationModel>> groupRelationsByToTableName, List<String> assembleRefFromFK,
                                              ODataEntityDefinition toSetEntity, String principleEntity, String fkElement,
                                              String mappingTableName, String mappingTableJoinColumn) {
        String refSection = assembleOdataMRefSection(principleEntity, fkElement, mappingTableName, mappingTableJoinColumn);
        if (groupRelationsByToTableName.get(toSetEntity.getTable()) != null) {
            List<String> match = assembleRefFromFK.stream().filter(el -> el.equals(refSection)).collect(Collectors.toList());
            if (match.isEmpty()) {
                throw new OData2TransformerException(String.format("There is inconsistency in odata file from table %s to table %s on joinColumns: %s", entity.getTable(), principleEntity, fkElement));
            }
        }
        if (!buff.toString().contains("_ref_" + principleEntity + "Type")) {
            return refSection;
        }
        return null;
    }

    private void validateAssociationProperties(ODataAssociationDefinition association, ODataDefinition model) {
        validateAssociationProperty(association.getFrom(), model, association);
        validateAssociationProperty(association.getTo(), model, association);
    }

    private void validateAssociationProperty(ODataAssociationEndDefinition assEndDefinition, ODataDefinition model, ODataAssociationDefinition association) {
        ODataEntityDefinition entity = ODataMetadataUtil.getEntity(model, assEndDefinition.getEntity(), association.getName());
        if (!entity.getProperties().isEmpty()) {
            ArrayList<String> invalidProps = new ArrayList<>();
            assEndDefinition.getProperties().forEach(assProp -> {
                List<ODataProperty> consistentProps = entity.getProperties().stream().filter(prop -> prop.getName().equals(assProp)).collect(Collectors.toList());
                if (consistentProps.isEmpty()) {
                    invalidProps.add(assProp);
                }
            });
            if (!invalidProps.isEmpty()) {
                throw new OData2TransformerException(String.format("There is inconsistency for entity '%s'. OData entity properties definitions for %s do not match the association properties definition.", assEndDefinition.getEntity(), invalidProps.stream().map(String::valueOf).collect(Collectors.joining(","))));
            }
            if (assEndDefinition.getProperties().size() > entity.getProperties().size()) {
                throw new OData2TransformerException(String.format("There is inconsistency for entity '%s'. The number of defined OData properties do not match the number of the association properties definition", assEndDefinition.getEntity()));
            }
        }
    }

    private String assembleOdataMRefSection(String dependentEntity, String fkElements, String mappingTableName,
                                            String mappingTableJoinColumn) {
        String refSection = "\t\"_ref_" + dependentEntity + "Type" + "\": {\n\t\t\"joinColumn\" : " + "[\n\t\t\t" +
                fkElements + "\n\t\t]";

        if(mappingTableName != null && !mappingTableName.isEmpty()
                && mappingTableJoinColumn != null && !mappingTableJoinColumn.isEmpty()) {
            refSection = refSection + "," + "\n\t\t\"manyToManyMappingTable\" : {" + "\n\t\t\t\"mappingTableName\" : "
                    + "\"" + mappingTableName + "\"," + "\n\t\t\t\"mappingTableJoinColumn\" : "
                    + "\"" + mappingTableJoinColumn + "\"" + "\n\t\t}";
        }

        refSection = refSection + "\n\t}";

        return refSection;
    }


}


