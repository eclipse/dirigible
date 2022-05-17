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
package org.eclipse.dirigible.engine.odata2.sql.binding;

import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.edm.provider.Mapping;
import org.eclipse.dirigible.database.sql.ISqlKeywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class EdmTableBinding extends Mapping {

    public enum DataStructureType { TABLE, CALC_VIEW };
    
    private static final String NO_PROPERTY_FOUND = "No sql binding configuration found in the mapping configuration for property %s."
            + " Did you map this property in the %s mapping?";

    private static final String PROPERTY_WRONG_CONFIGURATION = "Sql binding configuration in the mapping configuration for property %s is wrongly configured.";
    
    private static final String JOIN_COLUMN_UNSUPPORTED_CONFIGURATION = PROPERTY_WRONG_CONFIGURATION + " The value %s is not of expected type List and String.";

    private Map<String, Object> bindingData;
    private String targetFqn;

    public EdmTableBinding(Map<String, Object> bindingData) {
        this.bindingData = bindingData;
        this.targetFqn = readEdmEntityFqn();
    }

    public String getEdmFullyQualifedName() {
        return targetFqn;
    }

    public String getTableName() {
        return readMandatoryConfig("sqlTable", String.class);
    }

    public List<String> getMappingTableName(EdmStructuralType target) throws EdmException {
        return getRefProperties(target, "manyToManyMappingTable", "mappingTableName");
    }

    public List<String> getMappingTableJoinColumn(EdmStructuralType target) throws EdmException {
        return getRefProperties(target, "manyToManyMappingTable", "mappingTableJoinColumn");
    }

    public List<String> getJoinColumnTo(EdmStructuralType target) throws EdmException {
        return getRefProperties(target, "joinColumn", "");
    }

    @SuppressWarnings("unchecked")
    public List<String> getRefProperties(EdmStructuralType target, String property, String secondaryProperty) throws EdmException {
        String ref = "_ref_" + target.getName();
        Map<String, Object> refKeys = readMandatoryConfig(ref, Map.class);
        if (refKeys.containsKey(property)) {
            Object joinColumn = refKeys.get(property);
            if (joinColumn instanceof List) {
                return (List<String>)joinColumn;
            } else if (refKeys.get(property) instanceof String) {
                return Arrays.asList(String.valueOf(refKeys.get(property)));
            } else if (refKeys.get(property) instanceof Map) {
                return Arrays.asList(((Map<String, String>) refKeys.get(property)).get(secondaryProperty));
            } else {
                throw new IllegalArgumentException(format(format(JOIN_COLUMN_UNSUPPORTED_CONFIGURATION, ref, joinColumn)));
            }
        } else {
            throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, ref + "->" + property, targetFqn));
        }
    }

    public boolean hasMappingTable(EdmStructuralType target) throws EdmException {
        String ref = "_ref_" + target.getName();
        Map<String, Object> refKeys = readMandatoryConfig(ref, Map.class);
        return refKeys.containsKey("manyToManyMappingTable");
    }

    public boolean isPropertyMapped(EdmProperty p) {
        try {
            return this.isPropertyMapped(p.getName());
        } catch (EdmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isPropertyMapped(String propertyName) {
        if (bindingData.containsKey(propertyName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAggregationTypeExplicit() {
        String key = "aggregationType";
        if(isPropertyMapped(key)) {
            String aggregationType = readMandatoryConfig(key, String.class);
            return "explicit".equals(aggregationType);
        }

        return false;
    }

    public boolean isColumnContainedInAggregationProp(String columnName) {
        String key = "aggregationProps";
        if(isPropertyMapped(key)) {
            Map<String, String> aggregationProps = readMandatoryConfig(key, Map.class);
            return aggregationProps.containsKey(columnName);
        }

        return false;
    }

    public String getColumnAggregationType(String columnName) {
        Map<String, String> aggregationProps = readMandatoryConfig("aggregationProps", Map.class);
        return aggregationProps.get(columnName);
    }

    private <T> boolean isOfType(String key, Class<T> clazz) {
        if (bindingData.containsKey(key)) {
            Object property = bindingData.get(key);
            if (clazz.isInstance(property)) {
                return true;
            } else {
                return false;
            }
        }

        throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, key, targetFqn));
    }

    private <T> T readMandatoryConfig(String key, Class<T> clazz) {
        if (bindingData.containsKey(key)) {
            Object property = bindingData.get(key);
            if (clazz.isInstance(property)) {
                return clazz.cast(property);
            }
        }
        throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, key, targetFqn));
    }

    public boolean hasJoinColumnTo(EdmStructuralType target) throws EdmException {
        if (target instanceof EdmEntityType || target instanceof EdmComplexType) {
            String jc = "_ref_" + target.getName();
            if (bindingData.containsKey(jc)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getJoinColumnTo(EdmEntitySet target) throws EdmException {
        return getJoinColumnTo(target.getEntityType());
    }

    public String getColumnName(String propertyName) {
        if (bindingData.containsKey(propertyName)) {
            if (isOfType(propertyName, String.class)) {
                return String.valueOf(bindingData.get(propertyName));
            } else if (isOfType(propertyName, Map.class)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> value = (Map<String, Object>) bindingData.get(propertyName);
                return String.valueOf(value.get("name"));
            } else {
                throw new IllegalArgumentException(format(PROPERTY_WRONG_CONFIGURATION, propertyName));
            }

        } else {
            throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, propertyName, targetFqn));
        }
    }

    public String getColumnName(EdmProperty property) {
        try {
            return getColumnName(property.getName());
        } catch (EdmException e) {
            throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, property, targetFqn));
        }
    }

    protected String readEdmEntityFqn() {
        return readMandatoryConfig("edmTypeFqn", String.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ColumnInfo getColumnInfo(String propertyName) {
        if (bindingData.containsKey(propertyName)) {
            if (isOfType(propertyName, String.class)) {
                return new ColumnInfo(String.valueOf(bindingData.get(propertyName)));
            } else if (isOfType(propertyName, Map.class)) {
                Map<String, Object> value = (Map) bindingData.get(propertyName);
                String name = String.valueOf(value.get("name"));
                String sqlType = String.valueOf(value.get("sqlType"));
                return new ColumnInfo(name, sqlType); //TODO read this from the database metadata and perform a conversion there
            } else {
                throw new IllegalArgumentException(format(PROPERTY_WRONG_CONFIGURATION, propertyName));
            }
        } else {
            throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, propertyName, targetFqn));
        }

    }

    public ColumnInfo getColumnInfo(EdmProperty property) {
        try {
            return getColumnInfo(property.getName());
        } catch (EdmException e) {
            throw new IllegalArgumentException(format(NO_PROPERTY_FOUND, property, targetFqn));
        }
    }

    public List<String> getParameters() {
        List<String> parameters = new ArrayList<>();
        String key = "_parameters_";
        if (bindingData.containsKey(key)) {
            parameters = (List<String>) bindingData.get(key);
        }
        return parameters;
    }

    public DataStructureType getDataStructureType() {
        DataStructureType dataStructureType = DataStructureType.TABLE;
        String key = "dataStructureType";
        if (bindingData.containsKey(key)) {
            dataStructureType = DataStructureType.valueOf((String) bindingData.get(key));
        }
        return dataStructureType;
    }

    public String getPrimaryKey() throws EdmException {
        return readMandatoryConfig("_pk_", String.class);
    }

    public static class ColumnInfo {
        private final String columnName;
        private final String jdbcType;

        public ColumnInfo(final String columnName, final String jdbcType) {
            this.columnName = columnName;
            this.jdbcType = jdbcType;
        }

        public ColumnInfo(final String columnName) {
            this(columnName, (String) null);
        }

        public String getColumnName() {
            return columnName;
        }

        public String getJdbcType() {
            return jdbcType;
        }
    }
}
