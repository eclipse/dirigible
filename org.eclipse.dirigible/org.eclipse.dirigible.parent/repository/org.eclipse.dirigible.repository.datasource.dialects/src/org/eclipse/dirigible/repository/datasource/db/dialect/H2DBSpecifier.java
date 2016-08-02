package org.eclipse.dirigible.repository.datasource.db.dialect;

public class H2DBSpecifier extends RDBGenericDialectSpecifier {
	
	private static final String PRODUCT_NAME = "H2";
	
	private static final String H2_TIMESTAMP = "TIMESTAMP";
	private static final String H2_CLOB = "CLOB";
	private static final String H2_BLOB = "BLOB";
	private static final String H2_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
	private static final String H2_BIG_VARCHAR = "VARCHAR(1000)";
	private static final String H2_KEY_VARCHAR = "VARCHAR(4000)";

	private static final String LIMIT_D_D = "LIMIT %d OFFSET %d";
	
	@Override
	public boolean isDialectForName(String productName) {
		return PRODUCT_NAME.equalsIgnoreCase(productName);
	}
	
	@Override
	public String specify(String sql) {
		if(sql==null || sql.length()<1)
			return sql;
		return sql.replace(DIALECT_TIMESTAMP, H2_TIMESTAMP)
				  	.replace(DIALECT_CLOB, H2_CLOB)
					.replace(DIALECT_BLOB, H2_BLOB)
					.replace(DIALECT_CURRENT_TIMESTAMP, H2_CURRENT_TIMESTAMP)
					.replace(DIALECT_BIG_VARCHAR, H2_BIG_VARCHAR)
					.replace(DIALECT_KEY_VARCHAR, H2_KEY_VARCHAR);
	}
	
	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return String.format(LIMIT_D_D, offset, limit);
	}
	
	@Override
	public String getAlterAddOpen() {
		return " ADD( ";
	}

	@Override
	public String getAlterAddClose() {
		return ")";
	}
	
}
