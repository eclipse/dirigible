/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.persistence.parser;

import static java.text.MessageFormat.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.sql.DataType;
import org.eclipse.dirigible.database.sql.DataTypeUtils;

public class PersistenceAnnotationsParser {

	private static final Map<Class, PersistenceTableModel> MODELS_CACHE = Collections.synchronizedMap(new HashMap<Class, PersistenceTableModel>());

	public PersistenceTableModel parsePojo(Object pojo) throws PersistenceException {
		Class<? extends Object> clazz = pojo.getClass();
		PersistenceTableModel persistenceTableModel = parseTable(clazz);
		return persistenceTableModel;
	}

	public PersistenceTableModel parsePojo(Class<? extends Object> clazz) throws PersistenceException {
		PersistenceTableModel persistenceTableModel = parseTable(clazz);
		return persistenceTableModel;
	}

	private PersistenceTableModel parseTable(Class<? extends Object> clazz) {

		PersistenceTableModel persistenceTableModel = MODELS_CACHE.get(clazz);

		if (persistenceTableModel != null) {
			return persistenceTableModel;
		}

		Annotation annotation = clazz.getAnnotation(Table.class);
		if (annotation == null) {
			throw new PersistenceException(format("No Table annotation found in Class {0}", clazz));
		}
		Table table = (Table) annotation;
		persistenceTableModel = new PersistenceTableModel();
		persistenceTableModel.setClassName(clazz.getCanonicalName());
		if (table.schema() == null) {
			throw new PersistenceException(format("Table Name is mandatory, but it is not present in Class [{0}]", clazz.getCanonicalName()));
		}
		persistenceTableModel.setTableName(table.name());
		if (table.schema() != null) {
			persistenceTableModel.setSchemaName(table.schema());
		}

		parseColumns(clazz, persistenceTableModel);

		MODELS_CACHE.put(clazz, persistenceTableModel);

		return persistenceTableModel;
	}

	private void parseColumns(Class<? extends Object> clazz, PersistenceTableModel persistenceModel) {
		for (Field field : clazz.getDeclaredFields()) {
			boolean isTransient = Modifier.isTransient(field.getModifiers());
			if (isTransient) {
				continue;
			}
			// @Column
			Annotation annotationColumn = field.getAnnotation(Column.class);
			if (annotationColumn == null) {
				throw new PersistenceException(format("No Column annotation found in Class {0} and Field {1}", clazz, field.getName()));
			}
			Column column = (Column) annotationColumn;
			if (column.name() == null) {
				throw new PersistenceException(format("Column Name is mandatory, but it is not present in Class [{0}] and Field [{1}]",
						clazz.getCanonicalName(), field.getName()));
			}
			String type = column.columnDefinition();
			if (type == null) {
				type = DataTypeUtils.getDatabaseTypeNameByJavaType(field.getType().getClass());
			}
			int length = column.length();
			if ((length == 0) && (DataTypeUtils.getDatabaseTypeByJavaType(field.getType().getClass()) == Types.VARCHAR)) {
				length = DataTypeUtils.VARCHAR_DEFAULT_LENGTH;
			}
			boolean unique = column.unique();
			// @Id
			Annotation annotationId = field.getAnnotation(Id.class);
			boolean primaryKey = annotationId != null;
			// @GeneratedValue
			String generated = null;
			Annotation annotationGeneratedValue = field.getAnnotation(GeneratedValue.class);
			if (annotationGeneratedValue != null) {
				GeneratedValue generatedValue = (GeneratedValue) annotationGeneratedValue;
				if ((generatedValue.strategy() == null) || GenerationType.AUTO.equals(generatedValue.strategy())) {
					generated = GenerationType.TABLE.name();
				} else {
					if (!GenerationType.SEQUENCE.equals(generatedValue.strategy()) && !GenerationType.TABLE.equals(generatedValue.strategy())) {
						throw new IllegalArgumentException(format("Generation Type: [{0}] not supported.", generatedValue.strategy().name()));
					}
					generated = generatedValue.strategy().name();
				}
			}

			// @Enumerated
			String enumerated = null;
			Annotation annotationEnumerated = field.getAnnotation(Enumerated.class);
			if (annotationEnumerated != null) {
				Enumerated enumeratedAnnotation = (Enumerated) annotationEnumerated;
				EnumType enumType = enumeratedAnnotation.value();
				enumerated = enumType.name();
				if (EnumType.ORDINAL.equals(enumType)) {
					type = DataType.INTEGER.name();
				} else {
					type = DataType.VARCHAR.name();
					length = DataTypeUtils.VARCHAR_DEFAULT_LENGTH;
				}
			}

			PersistenceTableColumnModel persistenceTableColumnModel = new PersistenceTableColumnModel(field.getName(), column.name(), type, length,
					column.nullable(), primaryKey, column.precision(), column.scale(), generated, unique, enumerated);
			persistenceModel.getColumns().add(persistenceTableColumnModel);
		}

	}

}
