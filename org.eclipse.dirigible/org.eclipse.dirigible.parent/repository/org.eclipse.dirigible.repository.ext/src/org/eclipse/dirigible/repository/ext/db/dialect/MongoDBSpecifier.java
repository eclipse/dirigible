package org.eclipse.dirigible.repository.ext.db.dialect;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("javadoc")
public class MongoDBSpecifier implements IDialectSpecifier {

	@Override
	public String specify(String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSpecificType(String commonType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createTopAndStart(int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSchemaFilterSupported() {
		return false;
	}

	@Override
	public String getSchemaFilterScript() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlterAddOpen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlterAddOpenEach() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlterAddClose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlterAddCloseEach() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCatalogForSchema() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * @Override
	 * public JsonObject getLayout() {
	 * MongodbJdbcConnection nativeConn = this.dbConnection.getClass().getMethod("getMongoDb");
	 * JsonObject root = new JsonObject();
	 * root.addProperty("label", "container node");
	 * JsonArray array = new JsonArray();
	 * JsonObject child1 = new JsonObject();
	 * child1.addProperty("label", "child1");
	 * JsonObject child2 = new JsonObject();
	 * child2.addProperty("label", "child2");
	 * array.add(child1);
	 * array.add(child2);
	 * root.add("nodes", array);
	 * return root;
	 * }
	 */
}
