/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.ds.model.util;

import java.util.List;

import org.eclipse.dirigible.database.ds.model.DataStructureSchemaModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableColumnModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableConstraintForeignKeyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableIndexModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableIndexModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;

/**
 * The Class DatabaseModelUtils.
 */
public class DatabaseModelUtils {

    /**
     * Generate hashed name.
     *
     * @param values the values
     * @return the string
     */
    public static String generateHashedName(List<String> values){
        StringBuilder hashedName = new StringBuilder("");
        for(String val : values){
            hashedName.append(val);
        }
        return String.valueOf(hashedName.toString().hashCode());
    }
    
    /**
     * Table model to structure.
     *
     * @param model the model
     * @return the data structure table model
     */
    public static DataStructureTableModel tableModelToStructure(PersistenceTableModel model) {
	    if (model != null) {
			DataStructureTableModel tableModel = new DataStructureTableModel();
			tableModel.setName(model.getTableName());
			tableModel.setType(IDataStructureModel.TYPE_TABLE);
			for (PersistenceTableColumnModel column : model.getColumns()) {
				tableModel.getColumns().add(new DataStructureTableColumnModel(column.getName(), column.getType(), column.getLength() + "", column.isNullable(), column.isPrimaryKey(), null,
						column.getScale() + "", column.isUnique()));
			}
			for (PersistenceTableRelationModel relation : model.getRelations()) {
				tableModel.getConstraints().getForeignKeys().add(new DataStructureTableConstraintForeignKeyModel(relation.getToTableName(), new String[] {relation.getFkColumnName()}));
			}
			for (PersistenceTableIndexModel index : model.getIndices()) {
				tableModel.getIndexes().add(new DataStructureTableIndexModel(index.getName(), index.getType(), index.getUnique(), index.getColumns()));
			}
			return tableModel;
		}
	    return null;
    }

	/**
	 * Table models to schema.
	 *
	 * @param schemaName the schema name
	 * @param models the models
	 * @return the data structure schema model
	 */
	public static DataStructureSchemaModel tableModelsToSchema(String schemaName, List<PersistenceTableModel> models) {
		DataStructureSchemaModel schemaModel = new DataStructureSchemaModel();
		schemaModel.setName(schemaName);
		for (PersistenceTableModel model : models) {
			schemaModel.getTables().add(tableModelToStructure(model));
		}
		return schemaModel;
	}
}
