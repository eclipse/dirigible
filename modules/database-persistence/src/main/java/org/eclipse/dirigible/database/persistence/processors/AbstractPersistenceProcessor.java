/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EnumType;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.parser.PersistenceAnnotationsParser;
import org.eclipse.dirigible.database.sql.DataTypeUtils;

/**
 * The Abstract Persistence Processor.
 */
public abstract class AbstractPersistenceProcessor implements IPersistenceProcessor {

	private IEntityManagerInterceptor entityManagerInterceptor;

	/**
	 * Instantiates a new abstract persistence processor.
	 */
	public AbstractPersistenceProcessor() {
		this(null);
	}

	/**
	 * Instantiates a new abstract persistence processor.
	 *
	 * @param entityManagerInterceptor
	 *            the entity manager interceptor
	 */
	protected AbstractPersistenceProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		this.entityManagerInterceptor = entityManagerInterceptor;
	}

	/**
	 * Generate script.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
	protected abstract String generateScript(Connection connection, PersistenceTableModel tableModel);

	/**
	 * Sets the values from pojo.
	 *
	 * @param tableModel
	 *            the table model
	 * @param pojo
	 *            the pojo
	 * @param preparedStatement
	 *            the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	protected void setValuesFromPojo(PersistenceTableModel tableModel, Object pojo, PreparedStatement preparedStatement)
			throws SQLException, NoSuchFieldException, IllegalAccessException {
		int i = 1;
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (!shouldSetColumnValue(columnModel)) {
				continue;
			}
			if (columnModel.isIdentity()) {
				continue;
			}
			// Field field = pojo.getClass().getDeclaredField(columnModel.getField());
			Field field = getFieldFromClass(pojo.getClass(), columnModel.getField());
			String dataType = columnModel.getType();
			Object valueObject = null;
			boolean oldAccessible = setAccessible(field);
			try {
				valueObject = field.get(pojo);
			} finally {
				resetAccesible(field, oldAccessible);
			}
			try {
				if ((columnModel.getEnumerated() != null) && (valueObject != null)) {
					if (EnumType.valueOf(columnModel.getEnumerated()).equals(EnumType.ORDINAL)) {
						valueObject = ((Enum) valueObject).ordinal();
					} else {
						// EnumType.STRING
						valueObject = ((Enum) valueObject).name();
					}
				}
				setValue(preparedStatement, i++, dataType, valueObject);
			} catch (PersistenceException e) {
				throw new PersistenceException(format("Database type [{0}] not supported (Class: [{1}])", dataType, pojo.getClass()));
			}
		}
	}

	/**
	 * Should set column value.
	 *
	 * @param columnModel
	 *            the column model
	 * @return true, if successful
	 */
	protected boolean shouldSetColumnValue(PersistenceTableColumnModel columnModel) {
		return true;
	}

	/**
	 * Reset accesible.
	 *
	 * @param field
	 *            the field
	 * @param oldAccessible
	 *            the old accessible
	 */
	private void resetAccesible(Field field, boolean oldAccessible) {
		field.setAccessible(oldAccessible);
	}

	/**
	 * Sets the accessible.
	 *
	 * @param field
	 *            the field
	 * @return true, if successful
	 */
	private boolean setAccessible(Field field) {
		boolean oldAccessible = field.isAccessible();
		field.setAccessible(true);
		return oldAccessible;
	}

	/**
	 * Sets the value primary key.
	 *
	 * @param tableModel
	 *            the table model
	 * @param id
	 *            the id
	 * @param preparedStatement
	 *            the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	protected void setValuePrimaryKey(PersistenceTableModel tableModel, Object id, PreparedStatement preparedStatement)
			throws SQLException, NoSuchFieldException, IllegalAccessException {
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (columnModel.isPrimaryKey()) {
				String dataType = columnModel.getType();
				setValue(preparedStatement, 1, dataType, id);
				break;
			}
		}
	}

	/**
	 * Sets the value.
	 *
	 * @param preparedStatement
	 *            the prepared statement
	 * @param i
	 *            the i
	 * @param value
	 *            the value
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected void setValue(PreparedStatement preparedStatement, int i, Object value) throws SQLException {
		setValue(preparedStatement, i, DataTypeUtils.getDatabaseTypeNameByJavaType(value.getClass()), value);
	}

	/**
	 * Sets the value.
	 *
	 * @param preparedStatement
	 *            the prepared statement
	 * @param i
	 *            the i
	 * @param dataType
	 *            the data type
	 * @param value
	 *            the value
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected void setValue(PreparedStatement preparedStatement, int i, String dataType, Object value) throws SQLException {

		if (getEntityManagerInterceptor() != null) {
			value = getEntityManagerInterceptor().onGetValueBeforeUpdate(i, dataType, value);
		}

		if (value == null) {
			preparedStatement.setNull(i, DataTypeUtils.getSqlTypeByDataType(dataType));
		} else if (DataTypeUtils.isVarchar(dataType)) {
			preparedStatement.setString(i, (String) value);
		} else if (DataTypeUtils.isChar(dataType)) {
			preparedStatement.setString(i, (String) value);
		} else if (DataTypeUtils.isDate(dataType)) {
			preparedStatement.setDate(i, (Date) value);
		} else if (DataTypeUtils.isTime(dataType)) {
			preparedStatement.setTime(i, (Time) value);
		} else if (DataTypeUtils.isTimestamp(dataType)) {
			preparedStatement.setTimestamp(i, (Timestamp) value);
		} else if (DataTypeUtils.isInteger(dataType)) {
			preparedStatement.setInt(i, (Integer) value);
		} else if (DataTypeUtils.isTinyint(dataType)) {
			preparedStatement.setByte(i, (byte) value);
		} else if (DataTypeUtils.isSmallint(dataType)) {
			preparedStatement.setShort(i, (Short) value);
		} else if (DataTypeUtils.isBigint(dataType)) {
			preparedStatement.setLong(i, (Long) value);
		} else if (DataTypeUtils.isReal(dataType)) {
			preparedStatement.setFloat(i, (Float) value);
		} else if (DataTypeUtils.isDouble(dataType)) {
			preparedStatement.setDouble(i, (Double) value);
		} else if (DataTypeUtils.isDecimal(dataType)) {
			preparedStatement.setDouble(i, (Double) value);
		} else if (DataTypeUtils.isBoolean(dataType)) {
			preparedStatement.setBoolean(i, (Boolean) value);
		} else if (DataTypeUtils.isBlob(dataType)) {
			byte[] bytes = (byte[]) value;
			preparedStatement.setBinaryStream(i, new ByteArrayInputStream(bytes), bytes.length);
		}

		else {
			throw new PersistenceException(format("Database type [{0}] not supported", dataType));
		}
	}

	/**
	 * Sets the value to pojo.
	 *
	 * @param pojo
	 *            the pojo
	 * @param resultSet
	 *            the result set
	 * @param columnModel
	 *            the column model
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 */
	protected void setValueToPojo(Object pojo, ResultSet resultSet, PersistenceTableColumnModel columnModel)
			throws NoSuchFieldException, SQLException, IllegalAccessException, IOException {
		Object value = resultSet.getObject(columnModel.getName());
		setValueToPojo(pojo, value, columnModel);
	}

	/**
	 * Sets the value to pojo.
	 *
	 * @param pojo
	 *            the pojo
	 * @param value
	 *            the value
	 * @param columnModel
	 *            the column model
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 */
	protected void setValueToPojo(Object pojo, Object value, PersistenceTableColumnModel columnModel)
			throws NoSuchFieldException, SQLException, IllegalAccessException, IOException {
		Field field = getFieldFromClass(pojo.getClass(), columnModel.getField());
		boolean oldAccessible = setAccessible(field);
		try {
			if (columnModel.getEnumerated() != null) {
				if (EnumType.valueOf(columnModel.getEnumerated()).equals(EnumType.ORDINAL) && (value instanceof Integer)) {
					if (field.getType().isEnum()) {
						value = field.getType().getEnumConstants()[(Integer) value];
					} else {
						throw new IllegalStateException("The annotation @Enumerated is set to a field with a type, which is not an enum type.");
					}
				} else if (EnumType.valueOf(columnModel.getEnumerated()).equals(EnumType.STRING) && (value instanceof String)) {
					if (field.getType().isEnum()) {
						value = Enum.valueOf((Class<Enum>) field.getType(), (String) value);
					} else {
						throw new IllegalStateException("The annotation @Enumerated is set to a field with a type, which is not an enum type.");
					}
				} else if (value != null) {
					throw new IllegalStateException("The annotation @Enumerated is missused, the value is unknown.");
				}
			}
			if (field.getType().equals(byte.class) || field.getType().equals(Byte.class)) {
				if (value instanceof Integer) {
					value = ((Integer) value).byteValue();
				}
			}
			if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
				if (value instanceof Long) {
					value = ((Long) value).intValue();
				}
			}
			if (getEntityManagerInterceptor() != null) {
				value = getEntityManagerInterceptor().onSetValueAfterQuery(pojo, field, value);
			}
			if (value instanceof Blob) {
				value = IOUtils.toByteArray(((Blob) value).getBinaryStream());
			}
			field.set(pojo, value);
		} finally {
			resetAccesible(field, oldAccessible);
		}
	}

	/**
	 * Gets the value from pojo.
	 *
	 * @param pojo
	 *            the pojo
	 * @param columnModel
	 *            the column model
	 * @return the value from pojo
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws SQLException
	 *             the SQL exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	protected Object getValueFromPojo(Object pojo, PersistenceTableColumnModel columnModel)
			throws NoSuchFieldException, SQLException, IllegalAccessException {
		// Field field = pojo.getClass().getDeclaredField(columnModel.getField());
		Field field = getFieldFromClass(pojo.getClass(), columnModel.getField());
		boolean oldAccessible = setAccessible(field);
		try {
			return field.get(pojo);
		} finally {
			resetAccesible(field, oldAccessible);
		}
	}

	/**
	 * Gets the field from class.
	 *
	 * @param clazz
	 *            the clazz
	 * @param fieldName
	 *            the field name
	 * @return the field from class
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 */
	private Field getFieldFromClass(Class clazz, String fieldName) throws NoSuchFieldException {
		Field field = null;
		List<Field> fields = Arrays.asList(PersistenceAnnotationsParser.collectFields(clazz));
		for (Field next : fields) {
			if (next.getName().equals(fieldName)) {
				field = next;
				break;
			}
		}
		if (field == null) {
			throw new NoSuchFieldException(format("There is no a Field named [{0}] in the POJO of Class [{1}]", fieldName, clazz.getCanonicalName()));
		}
		return field;
	}

	/**
	 * Open prepared statement.
	 *
	 * @param connection
	 *            the connection
	 * @param sql
	 *            the sql
	 * @return the prepared statement
	 * @throws SQLException
	 *             the SQL exception
	 */
	protected PreparedStatement openPreparedStatement(Connection connection, String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	/**
	 * Close prepared statement.
	 *
	 * @param preparedStatement
	 *            the prepared statement
	 */
	protected void closePreparedStatement(PreparedStatement preparedStatement) {
		try {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Gets the primary key.
	 *
	 * @param tableModel
	 *            the table model
	 * @return the primary key
	 */
	protected String getPrimaryKey(PersistenceTableModel tableModel) {
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (columnModel.isPrimaryKey()) {
				return columnModel.getName();
			}
		}
		return null;
	}

	/**
	 * Gets the entity manager interceptor.
	 *
	 * @return the entity manager interceptor
	 */
	public IEntityManagerInterceptor getEntityManagerInterceptor() {
		return entityManagerInterceptor;
	}

	/**
	 * Sets the entity manager interceptor.
	 *
	 * @param entityManagerInterceptor
	 *            the new entity manager interceptor
	 */
	public void setEntityManagerInterceptor(IEntityManagerInterceptor entityManagerInterceptor) {
		this.entityManagerInterceptor = entityManagerInterceptor;
	}

}
