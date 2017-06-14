package org.eclipse.dirigible.database.persistence.processors;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.squle.DataTypeUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public abstract class AbstractPersistenceProcessor implements IPersistenceProcessor {
	
	protected abstract String generateScript(Connection connection, PersistenceTableModel tableModel);

	protected void setValuesFromPojo(PersistenceTableModel tableModel, Object pojo, PreparedStatement preparedStatement)
			throws SQLException, NoSuchFieldException, IllegalAccessException {
		int i=1;
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			Field field = pojo.getClass().getDeclaredField(columnModel.getField());
			String dataType = columnModel.getType();
			Object valueObject = null;
			boolean oldAccessible = setAccessible(field);
			try {
				valueObject = field.get(pojo);
			} finally {
				resetAccesible(field, oldAccessible);
			}
			try {
				setValue(preparedStatement, i++, dataType, valueObject);
			} catch (PersistenceException e) {
				throw new PersistenceException(format("Database type [{0}] not supported (Class: [{1}])", dataType, pojo.getClass()));
			}
		}
	}

	private void resetAccesible(Field field, boolean oldAccessible) {
		field.setAccessible(oldAccessible);
	}

	private boolean setAccessible(Field field) {
		boolean oldAccessible = field.isAccessible();
		field.setAccessible(true);
		return oldAccessible;
	}
	
//	protected void setValuesFromJson(PersistenceTableModel tableModel, String json, PreparedStatement preparedStatement)
//			throws SQLException, NoSuchFieldException, IllegalAccessException, PersistenceException {
//		int i=1;
//		JsonElement jsonElement = new JsonParser().parse(json);
//		if (!(jsonElement instanceof JsonObject)) {
//			throw new PersistenceException(format("Invalid json object [{0}]", json));
//		}
//		JsonObject jsonObject = (JsonObject) jsonElement;
//		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
//			String dataType = columnModel.getType();
//			String fieldName = columnModel.getField();
//			i = setValueFromJson(json, preparedStatement, i, jsonObject, dataType, fieldName);
//		}
//	}
//
//	private int setValueFromJson(String json, PreparedStatement preparedStatement, int i, JsonObject jsonObject, String dataType, String fieldName) throws SQLException {
//		
//		if (DataTypeUtils.isVarchar(dataType)) {
//			preparedStatement.setString(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsString());
//		} else if (DataTypeUtils.isChar(dataType)) {
//			preparedStatement.setString(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsString());
//		} else if (DataTypeUtils.isDate(dataType)) {
//			preparedStatement.setDate(i++, new Date(jsonObject.getAsJsonPrimitive(fieldName).getAsLong()));
//		} else if (DataTypeUtils.isTime(dataType)) {
//			preparedStatement.setTime(i++, new Time(jsonObject.getAsJsonPrimitive(fieldName).getAsLong()));
//		} else if (DataTypeUtils.isTimestamp(dataType)) {
//			preparedStatement.setTimestamp(i++, new Timestamp(jsonObject.getAsJsonPrimitive(fieldName).getAsLong()));
//		} else if (DataTypeUtils.isInteger(dataType)) {
//			preparedStatement.setInt(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsInt());
//		} else if (DataTypeUtils.isBigint(dataType)) {
//			preparedStatement.setLong(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsLong());
//		} else if (DataTypeUtils.isReal(dataType)) {
//			preparedStatement.setFloat(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsFloat());
//		} else if (DataTypeUtils.isDouble(dataType)) {
//			preparedStatement.setDouble(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsDouble());
//		} else if (DataTypeUtils.isBoolean(dataType)) {
//			preparedStatement.setBoolean(i++, jsonObject.getAsJsonPrimitive(fieldName).getAsBoolean());
//		} else if (DataTypeUtils.isBlob(dataType)) {
//			JsonArray jsonArray = jsonObject.getAsJsonPrimitive(fieldName).getAsJsonArray();
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			for (JsonElement element : jsonArray) {
//				if (!(element instanceof JsonPrimitive)) {
//					throw new PersistenceException(format("Invalid element in the array of integers provided [{0}] not supported (Json: [{1}])", dataType, json));
//				}
//				JsonPrimitive primitive = (JsonPrimitive) element;
//				baos.write(primitive.getAsInt());
//			}
//			byte[] bytes = baos.toByteArray();
//			preparedStatement.setBinaryStream(i++, new ByteArrayInputStream(bytes), bytes.length);
//		}
//
//		else {
//			throw new PersistenceException(format("Database type [{0}] not supported (Json: [{1}])", dataType, json));
//		}
//		return i;
//	}
	
	
	
	
	
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
	
	protected void setValue(PreparedStatement preparedStatement, int i, String dataType, Object value) throws SQLException {
		
		if (DataTypeUtils.isVarchar(dataType)) {
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
		} else if (DataTypeUtils.isBigint(dataType)) {
			preparedStatement.setLong(i, (Long) value);
		} else if (DataTypeUtils.isReal(dataType)) {
			preparedStatement.setFloat(i, (Float) value);
		} else if (DataTypeUtils.isDouble(dataType)) {
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
	
	protected void setValueToPojo(Object pojo, ResultSet resultSet, PersistenceTableColumnModel columnModel)
			throws NoSuchFieldException, SQLException, IllegalAccessException {
		Field field = pojo.getClass().getDeclaredField(columnModel.getField());
		Object value = resultSet.getObject(columnModel.getName());
		boolean oldAccessible = setAccessible(field);
		field.set(pojo, value);
		resetAccesible(field, oldAccessible);
	}
	
	protected PreparedStatement openPreparedStatement(Connection connection, String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}
	
	protected void closePreparedStatement(PreparedStatement preparedStatement) {
		try {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}
	
}
