package org.eclipse.dirigible.database.persistence.processors;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.squle.DataType;

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
			if (DataType.VARCHAR.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setString(i++, (String) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.CHAR.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setString(i++, (String) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.DATE.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setDate(i++, (Date) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.TIME.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setTime(i++, (Time) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.TIMESTAMP.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setTimestamp(i++, (Timestamp) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.INTEGER.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setInt(i++, (int) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.BIGINT.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setLong(i++, (long) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.REAL.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setFloat(i++, (float) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.DOUBLE.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setDouble(i++, (double) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.BOOLEAN.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				preparedStatement.setBoolean(i++, (boolean) field.get(pojo));
				resetAccesible(field, oldAccessible);
			} else if (DataType.BLOB.toString().equals(columnModel.getType())) {
				boolean oldAccessible = setAccessible(field);
				byte[] bytes = (byte[]) field.get(pojo);
				preparedStatement.setBinaryStream(i++, new ByteArrayInputStream(bytes), bytes.length);
				resetAccesible(field, oldAccessible);
			}
			
			else {
				throw new PersistenceException(format("Database type [{0}] not supported (Class: [{1}])", columnModel.getType(), pojo.getClass()));
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
	
	protected void setValuesFromJson(PersistenceTableModel tableModel, String json, PreparedStatement preparedStatement)
			throws SQLException, NoSuchFieldException, IllegalAccessException, PersistenceException {
		int i=1;
		JsonElement jsonElement = new JsonParser().parse(json);
		if (!(jsonElement instanceof JsonObject)) {
			throw new PersistenceException(format("Invalid json object [{0}]", json));
		}
		JsonObject jsonObject = (JsonObject) jsonElement;
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (DataType.VARCHAR.toString().equals(columnModel.getType())) {
				preparedStatement.setString(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsString());
			} else if (DataType.CHAR.toString().equals(columnModel.getType())) {
				preparedStatement.setString(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsString());
			} else if (DataType.DATE.toString().equals(columnModel.getType())) {
				preparedStatement.setDate(i++, new Date(jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsLong()));
			} else if (DataType.TIME.toString().equals(columnModel.getType())) {
				preparedStatement.setTime(i++, new Time(jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsLong()));
			} else if (DataType.TIMESTAMP.toString().equals(columnModel.getType())) {
				preparedStatement.setTimestamp(i++, new Timestamp(jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsLong()));
			} else if (DataType.INTEGER.toString().equals(columnModel.getType())) {
				preparedStatement.setInt(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsInt());
			} else if (DataType.BIGINT.toString().equals(columnModel.getType())) {
				preparedStatement.setLong(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsLong());
			} else if (DataType.REAL.toString().equals(columnModel.getType())) {
				preparedStatement.setFloat(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsFloat());
			} else if (DataType.DOUBLE.toString().equals(columnModel.getType())) {
				preparedStatement.setDouble(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsDouble());
			} else if (DataType.BOOLEAN.toString().equals(columnModel.getType())) {
				preparedStatement.setBoolean(i++, jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsBoolean());
			} else if (DataType.BLOB.toString().equals(columnModel.getType())) {
				JsonArray jsonArray = jsonObject.getAsJsonPrimitive(columnModel.getField()).getAsJsonArray();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for (JsonElement element : jsonArray) {
					if (!(element instanceof JsonPrimitive)) {
						throw new PersistenceException(format("Invalid element in the array of integers provided [{0}] not supported (Json: [{1}])", columnModel.getType(), json));
					}
					JsonPrimitive primitive = (JsonPrimitive) element;
					baos.write(primitive.getAsInt());
				}
				byte[] bytes = baos.toByteArray();
				preparedStatement.setBinaryStream(i++, new ByteArrayInputStream(bytes), bytes.length);
			}

			else {
				throw new PersistenceException(format("Database type [{0}] not supported (Json: [{1}])", columnModel.getType(), json));
			}
		}
	}
}
