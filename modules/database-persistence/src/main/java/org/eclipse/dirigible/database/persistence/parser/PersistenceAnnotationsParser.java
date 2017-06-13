package org.eclipse.dirigible.database.persistence.parser;

import static java.text.MessageFormat.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Types;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.squle.DataTypeUtils;

public class PersistenceAnnotationsParser {
	
	public PersistenceTableModel parsePojo(Object pojo) throws PersistenceException {
		Class<? extends Object> clazz = pojo.getClass();
		PersistenceTableModel persistenceTableModel = parseTable(clazz);
		parseColumns(clazz, persistenceTableModel);
		return persistenceTableModel;
	}
	
	public PersistenceTableModel parsePojo(Class<? extends Object> clazz) throws PersistenceException {
		PersistenceTableModel persistenceTableModel = parseTable(clazz);
		parseColumns(clazz, persistenceTableModel);
		return persistenceTableModel;
	}

	private PersistenceTableModel parseTable(Class<? extends Object> clazz) {
		Annotation annotation = clazz.getAnnotation(Table.class);
		if (annotation == null) {
			throw new PersistenceException(format("No Table annotation found in Class {0}", clazz));
		}
		Table table = (Table) annotation;
		PersistenceTableModel persistenceModel = new PersistenceTableModel();
		persistenceModel.setClassName(clazz.getCanonicalName());
		if (table.schema() == null) {
			throw new PersistenceException(format("Table Name is mandatory, but it is not present in Class [{0}]", clazz.getCanonicalName()));
		}
		persistenceModel.setTableName(table.name());
		if (table.schema() != null) {
			persistenceModel.setSchemaName(table.schema());
		}
		return persistenceModel;
	}

	private void parseColumns(Class<? extends Object> clazz, PersistenceTableModel persistenceModel) {
		for (Field field : clazz.getDeclaredFields()) {
			Annotation annotation = field.getAnnotation(Column.class);
			if (annotation == null) {
				throw new PersistenceException(format("No Column annotation found in Class {0} and Field {1}", clazz, field.getName()));
			}
			Column column = (Column) annotation;
			if (column.name() == null) {
				throw new PersistenceException(format("Column Name is mandatory, but it is not present in Class [{0}] and Field [{1}]", clazz.getCanonicalName(), field.getName()));
			}
			annotation = field.getAnnotation(Id.class);
			boolean primaryKey = annotation != null;
			String type = column.columnDefinition();
			if (type == null) {
				type = DataTypeUtils.getDatabaseTypeNameByJavaType(field.getType().getClass());
			}
			int length = column.length();
			if (length == 0 && DataTypeUtils.getDatabaseTypeByJavaType(field.getType().getClass()) == Types.VARCHAR) {
				length = DataTypeUtils.VARCHAR_DEFAULT_LENGTH;
			}
			PersistenceTableColumnModel persistenceTableColumnModel = 
					new PersistenceTableColumnModel(
							field.getName(),
							column.name(), 
							type, 
							length,
							column.nullable(),
							primaryKey,
							column.precision(),
							column.scale());
			persistenceModel.getColumns().add(persistenceTableColumnModel);
		}
		
	}

}
