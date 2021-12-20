/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.engine.odata2.definition.ODataEntityDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class TableMetadataProvider {

    private static final Logger logger = LoggerFactory.getLogger(TableMetadataProvider.class);

    private DBMetadataUtil dbMetadataUtil = new DBMetadataUtil();

    public static final List<String> VIEW_TYPES = List.of(ISqlKeywords.METADATA_VIEW, ISqlKeywords.METADATA_CALC_VIEW);

    public PersistenceTableModel getPersistenceTableModel(ODataEntityDefinition oDataEntityDefinition, StringBuilder buff) throws SQLException {
        // tableMetadata
        // default - current logic on line 64 PersistenceTableModel tableMetadata = dbMetadataUtil.getTableMetadata(entity.getTable(), dbMetadataUtil.getOdataArtifactTypeSchema(entity.getTable()));

        // when we see that it is a synonym - go and find the target object, for which we call dbMetadataUtil.getTableMetadata(targetObjectName, targetObjectSchema);
        PersistenceTableModel tableMetadata = dbMetadataUtil.getTableMetadata(oDataEntityDefinition.getTable(), dbMetadataUtil.getOdataArtifactTypeSchema(oDataEntityDefinition.getTable()));

        // TODO place this is inside the TableMetadataProvider provider - see if it is a synonym and return the target object
        // Calcview and calcviewsynonym - return Calcview object but keep the calcviewsynonym name -?? is it a problem
        // take columns from target object but name from the synonym -?? is it a problem
        if (ISqlKeywords.METADATA_SYNONYM.equals(tableMetadata.getTableType())) {
            //remove and place in handle TableMetadataProvider
            HashMap<String, String> targetObjectMetadata = dbMetadataUtil.getSynonymTargetObjectMetadata(tableMetadata.getTableName(), tableMetadata.getSchemaName());

            if (targetObjectMetadata.isEmpty()) {
                logger.error("Failed to get details for synonym - " + tableMetadata.getTableName());
                return null;
            }

            if (!VIEW_TYPES.contains(targetObjectMetadata.get(ISqlKeywords.KEYWORD_TABLE_TYPE)) && !ISqlKeywords.METADATA_TABLE.equals(targetObjectMetadata.get(ISqlKeywords.KEYWORD_TABLE_TYPE))) {
                logger.error("Unsupported object type for {}", targetObjectMetadata.get(ISqlKeywords.KEYWORD_TABLE));
                return null;
            }

            tableMetadata = dbMetadataUtil.getTableMetadata(targetObjectMetadata.get(ISqlKeywords.KEYWORD_TABLE), targetObjectMetadata.get(ISqlKeywords.KEYWORD_SCHEMA));
        }


        return tableMetadata;
    }
}
