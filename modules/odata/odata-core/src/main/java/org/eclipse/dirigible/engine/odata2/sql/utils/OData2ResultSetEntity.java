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
package org.eclipse.dirigible.engine.odata2.sql.utils;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;

import java.util.*;
import java.util.stream.Collectors;

import static org.eclipse.dirigible.engine.odata2.sql.utils.OData2Utils.fqn;

/**
 * Holder object for reading result sets
 */
public class OData2ResultSetEntity {
    private final Map<String, Object> entitiyPropertiesData;
    private Map<String, LinkedHashSet<ExpandedEntity>> expandData;

    public OData2ResultSetEntity(Map<String, Object> entitiyPropertiesData) {
        this.entitiyPropertiesData = (entitiyPropertiesData == null? new HashMap<>() : entitiyPropertiesData);
        this.expandData = new HashMap<>();
    }

    public Map<String, Object> getEntitiyPropertiesData() {
        return entitiyPropertiesData;
    }

    public Map<String, List<Object>> getExpandData() {
        Map<String, List<Object>> result = new HashMap<>();
        expandData.keySet().forEach(key -> {
            List<Object> expandedEntities = expandData.get(key).stream().map(e -> (Object)e.navigationEntityData).collect(Collectors.toList());
            result.put(key, expandedEntities);
        });
        return result;
    }

    public void addExpandedEntityProperties(EdmEntityType navigationEntity, Map<String, Object> navigationEntityData) throws EdmException {
        String fqnNavigationEntity = fqn(navigationEntity);
        if (!expandData.containsKey(fqnNavigationEntity)) {
            expandData.put(fqnNavigationEntity, new LinkedHashSet<>());
        }
        this.expandData.get(fqnNavigationEntity).add(new ExpandedEntity(navigationEntity, navigationEntityData));
    }


    @Override
    public String toString() {
        return "OData2ResultSetEntity [" + entitiyPropertiesData + "]"; 
    }

    /**
     * We use this class to have only one instance of each Expanded entity. This is important when we have a query with multiple expands.
     * In this case the result set will have a single entity multiple times (the cartesian product of the 2 entities) as each entity is joined
     */
    static class ExpandedEntity {
        Map<String, Object> keyPropertyValues = new HashMap<>();
        Map<String, Object> navigationEntityData;
        public ExpandedEntity(EdmEntityType navigationEntity, Map<String, Object> navigationEntityData) throws EdmException {
            this.navigationEntityData = navigationEntityData;
            Collection<EdmProperty> keyProperties = navigationEntity.getKeyProperties();
            for (EdmProperty p: keyProperties){
                String name = p.getName();
                keyPropertyValues.put(name, navigationEntityData.get(name));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExpandedEntity that = (ExpandedEntity) o;
            return keyPropertyValues.equals(that.keyPropertyValues);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keyPropertyValues);
        }
    }

}
