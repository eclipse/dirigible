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
package org.eclipse.dirigible.engine.odata2.sql.processor;

import org.apache.olingo.odata2.api.edm.*;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.core.edm.EdmDouble;
import org.apache.olingo.odata2.core.edm.EdmInt16;
import org.apache.olingo.odata2.core.edm.EdmInt32;
import org.apache.olingo.odata2.core.edm.EdmInt64;
import org.eclipse.dirigible.engine.odata2.sql.api.SQLProcessor;
import org.eclipse.dirigible.engine.odata2.sql.builder.EdmUtils;
import org.eclipse.dirigible.engine.odata2.sql.builder.SQLSelectBuilder;
import org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ResultSetReader {

    private final SQLProcessor callback;

    public ResultSetReader(SQLProcessor callback) {
        this.callback = callback;
    }

    protected Map<String, Object> getEntityDataFromResultSet(SQLSelectBuilder selectEntityQuery, final EdmStructuralType entityType,
                                                             Collection<EdmProperty> properties, final ResultSet resultSet) throws SQLException, ODataException {
        Map<String, Object> result = new HashMap<>();
        for (EdmProperty property : properties) {
            result.put(property.getName(), readProperty(entityType, property, selectEntityQuery, resultSet));
        }
        return result;
    }


    protected ResultSetEntity getResultSetEntity(SQLSelectBuilder selectEntityQuery, final EdmEntityType entityType,
                                                 Collection<EdmProperty> properties, final ResultSet resultSet) throws SQLException, ODataException {
        Map<String, Object> data = new HashMap<>();
        for (EdmProperty property : properties) {
            data.put(property.getName(), readProperty(entityType, property, selectEntityQuery, resultSet));
        }
        return new ResultSetEntity(entityType, data);
    }

    protected Object convertProperty(EdmProperty property, Object dbValue) throws EdmException {
        if (dbValue instanceof BigDecimal) {
            BigDecimal dec = (BigDecimal) dbValue;
            if (property.getType().equals(EdmInt32.getInstance())) {
                return dec.toBigInteger().intValue();
            } else if (property.getType().equals(EdmInt64.getInstance())) {
                return dec.toBigInteger().longValue();
            } else if (property.getType().equals(EdmInt16.getInstance())) {
                return dec.toBigInteger().shortValue();
            } else if (property.getType().equals(EdmDouble.getInstance())) {
                return dec.doubleValue();
            }
        }
        return dbValue;
    }

    protected Object readProperty(EdmStructuralType entityType, EdmProperty property, SQLSelectBuilder selectEntityQuery,
                                  ResultSet resultSet) throws SQLException, ODataException {
        Object propertyDbValue;
        if (property.isSimple()) {
            if (!selectEntityQuery.isTransientType(entityType, property)) {
                final String columnName = selectEntityQuery.getSQLTableColumnAlias(entityType, property);
                if ("Binary".equals(property.getType().getName())) {
                    propertyDbValue = resultSet.getBytes(columnName);
                } else {
                    propertyDbValue = resultSet.getObject(columnName);
                }



                propertyDbValue = callback.onCustomizePropertyValue(entityType, property, entityType, propertyDbValue);
                return convertProperty(property, propertyDbValue);
            } else {
                return null;
            }
        } else {
            EdmStructuralType complexProperty = (EdmStructuralType) property.getType();
            Map<String, Object> complexPropertyData = new HashMap<>();
            for (String pn : complexProperty.getPropertyNames()) {
                EdmProperty prop = (EdmProperty) complexProperty.getProperty(pn);
                final String columnName = selectEntityQuery.getSQLTableColumnAlias(complexProperty, prop);
                propertyDbValue = resultSet.getObject(columnName);
                propertyDbValue = callback.onCustomizePropertyValue(entityType, property, entityType, propertyDbValue);
                complexPropertyData.put(pn, convertProperty(prop, propertyDbValue));
            }
            return complexPropertyData;
        }
    }

    public void accumulateExpandedEntities(SQLSelectBuilder query, ResultSet resultSet, ExpandAccumulator accumulator,
                                           List<ArrayList<NavigationPropertySegment>> expandEntities) throws SQLException, ODataException {

        for (List<NavigationPropertySegment> expandContents : expandEntities) {
            List<ResultSetEntity> parents = new ArrayList<>();
            /*
             * The inner loop is for nested expands e.g. Owner/Address, where the parent has a higher index.
             * If the resultset contains an Address, the Owner would be empty. If the result set contains an owner, it is added ot the accumulation
             */
            for (NavigationPropertySegment expandContent : expandContents) {
                EdmEntityType expandType = expandContent.getTargetEntitySet().getEntityType();
                Map<String, Object> expandData = getEntityDataFromResultSet(query, expandType, EdmUtils.getProperties(expandType), resultSet);
                if (OData2Utils.isEmpty(expandType, expandData)) {
                    break;
                } else {
                    ResultSetEntity entity = new ResultSetEntity(expandType, expandData);
                    accumulator.addExpandEntity(entity, parents);
                    parents.add(entity);
                }
            }
        }
    }

    public static class ExpandAccumulator {
        private final ResultSetEntity entity;
        private final LinkedHashMap<String, List<ExpandAccumulator>> expandData;

        public ExpandAccumulator(EdmEntityType type) throws EdmException {
            this(new ResultSetEntity(type, new HashMap<>()));
        }

        public ExpandAccumulator(ResultSetEntity entity) {
            this.entity = entity;
            this.expandData = new LinkedHashMap<>();
        }

        public boolean isAccumulatorFor(ResultSetEntity entity) {
            return this.entity.equals(entity);
        }

        public ResultSetEntity getResultSetEntity() {
            return entity;
        }

        public boolean addExpandEntity(ResultSetEntity entity, List<ResultSetEntity> parents) {
            if (parents.isEmpty()) {
                String fqn = OData2Utils.fqn(entity.entityType);
                expandData.computeIfAbsent(fqn, k -> new ArrayList<>());
                List<ExpandAccumulator> accumulators = expandData.get(fqn);
                ExpandAccumulator last = lastAccumulator(accumulators);
                if (last == null || !last.isAccumulatorFor(entity)) {
                    for (ExpandAccumulator accumulator : accumulators) {
                        if (accumulator.isAccumulatorFor(entity)) {
                            return false; //do not add more than once the same entity
                        }
                    }
                    accumulators.add(new ExpandAccumulator(entity));
                }
                return true;
            } else {  //recursion on the parents
                ResultSetEntity firstParent = parents.get(0);
                ExpandAccumulator firstParentAccumulator = accumulatorFor(firstParent);
                ArrayList<ResultSetEntity> nextParents = new ArrayList<>(parents);
                nextParents.remove(0);//firstParent
                return firstParentAccumulator.addExpandEntity(entity, nextParents);
            }

        }

        ExpandAccumulator lastAccumulator(List<ExpandAccumulator> acc) {
            return (acc == null || acc.size() == 0) ? null : acc.get(acc.size() - 1);
        }

        ExpandAccumulator accumulatorFor(ResultSetEntity firstParent) {
            String fqn = OData2Utils.fqn(firstParent.entityType);
            if (expandData.get(fqn) != null) {
                List<ExpandAccumulator> accumulators = expandData.get(fqn);
                for (ExpandAccumulator acc : accumulators) {
                    if (acc.isAccumulatorFor(firstParent)) {
                        return acc;
                    }
                }
            }
            throw new IllegalStateException("Unsupported expand case");
        }

        public Map<String, Object> renderForExpand() {
            return renderForExpand(this);
        }

        public Map<String, Object> renderForExpand(ExpandAccumulator input) {
            Map<String, Object> result = new HashMap<>(input.getResultSetEntity().data);
            for (String key : input.expandData.keySet()) {
                List<ExpandAccumulator> accumulators = input.expandData.get(key);
                List<Map<String, Object>> expandData = new ArrayList<>();
                for (ExpandAccumulator acc : accumulators) {
                    expandData.add(renderForExpand(acc));
                }
                result.put(key, expandData);
            }
            return result;
        }
    }

    static class ResultSetEntity {
        final Map<String, Object> data;
        final Map<String, Object> keys;
        final EdmEntityType entityType;

        public ResultSetEntity(EdmEntityType type, Map<String, Object> data) throws EdmException {
            this.entityType = type;
            this.data = data;
            this.keys = new HashMap<>();
            Collection<EdmProperty> keyProperties = type.getKeyProperties();
            for (EdmProperty p : keyProperties) {
                String name = p.getName();
                keys.put(name, data.get(name));
            }
        }

        public boolean isEmpty() throws ODataException {
            return OData2Utils.isEmpty(entityType, data);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResultSetEntity that = (ResultSetEntity) o;
            return keys.equals(that.keys);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keys);
        }
    }

}
