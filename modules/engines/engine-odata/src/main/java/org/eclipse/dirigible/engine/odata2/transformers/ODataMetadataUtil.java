/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import org.eclipse.dirigible.engine.odata2.definition.ODataAssociationDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;

public class ODataMetadataUtil {

    public static ODataEntityDefinition getEntity(ODataDefinition model, String name, String navigation) {
        for (ODataEntityDefinition entity : model.getEntities()) {
            if (name.equals(entity.getName())) {
                return entity;
            }
        }
        throw new IllegalArgumentException(String.format("There is no entity with name: %s, referenced by the navigation: %s", name, navigation));
    }

    public static ODataEntityDefinition getEntityByTableName(ODataDefinition model, String tableName) {
        for (ODataEntityDefinition entity : model.getEntities()) {
            if (tableName.equals(entity.getTable())) {
                return entity;
            }
        }
        throw new IllegalArgumentException(String.format("There is no table with name: %s defined in the model", tableName));
    }

    public static String getTableNameByEntity(ODataDefinition model, String entityName) {
        for (ODataEntityDefinition entity : model.getEntities()) {
            if (entityName.equals(entity.getName())) {
                return entity.getTable();
            }
        }
        throw new IllegalArgumentException(String.format("There is no entity with name: %s defined in the model", entityName));
    }

    public static ODataAssociationDefinition getAssociation(ODataDefinition model, String name, String navigation) {
        for (ODataAssociationDefinition association : model.getAssociations()) {
            if (name.equals(association.getName())) {
                return association;
            }
        }
        throw new IllegalArgumentException(String.format("There is no association with name: %s, referenced by the navigation: %s", name, navigation));
    }

}