package org.eclipse.dirigible.database.squle.dialects;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.dialects.derby.DerbySquleDialect;

public class SquleDialectFactory {
	
	public static final ISquleDialect getDialect(Connection connection) throws SQLException {
		String productName = connection.getMetaData().getDatabaseProductName();
		return databaseTypeMappings.get(productName);
	}
	
	public static final ISquleDialect DATABASE_TYPE_DERBY = new DerbySquleDialect();
//	public static final ISquleDialect DATABASE_TYPE_H2 = "h2";
//	public static final ISquleDialect DATABASE_TYPE_HSQL = "hsql";
//	public static final ISquleDialect DATABASE_TYPE_MYSQL = "mysql";
//	public static final ISquleDialect DATABASE_TYPE_ORACLE = "oracle";
//	public static final ISquleDialect DATABASE_TYPE_POSTGRES = "postgres";
//	public static final ISquleDialect DATABASE_TYPE_MSSQL = "mssql";
//	public static final ISquleDialect DATABASE_TYPE_DB2 = "db2";
//	public static final ISquleDialect DATABASE_TYPE_HANA = "hana";
//	public static final ISquleDialect DATABASE_TYPE_SYBASE = "sybase";
	
	
	// Lifted from Activiti
	protected static Map<String, ISquleDialect> databaseTypeMappings = getDefaultDatabaseTypeMappings();
	
	protected static Map<String, ISquleDialect> getDefaultDatabaseTypeMappings() {
		Map<String, ISquleDialect> databaseTypeMappings = Collections.synchronizedMap(new HashMap<String, ISquleDialect>());
	    databaseTypeMappings.put("Apache Derby", DATABASE_TYPE_DERBY);
//	    databaseTypeMappings.setProperty("H2", DATABASE_TYPE_H2);
//	    databaseTypeMappings.setProperty("HSQL Database Engine", DATABASE_TYPE_HSQL);
//	    databaseTypeMappings.setProperty("MySQL", DATABASE_TYPE_MYSQL);
//	    databaseTypeMappings.setProperty("Oracle", DATABASE_TYPE_ORACLE);
//	    databaseTypeMappings.setProperty("PostgreSQL", DATABASE_TYPE_POSTGRES);
//	    databaseTypeMappings.setProperty("Microsoft SQL Server", DATABASE_TYPE_MSSQL);
//	    databaseTypeMappings.setProperty(DATABASE_TYPE_DB2,DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/NT",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/NT64",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2 UDP",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/LINUX",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/LINUX390",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/LINUXX8664",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/LINUXZ64",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/LINUXPPC64",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/LINUXPPC64LE",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/400 SQL",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/6000",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2 UDB iSeries",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/AIX64",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/HPUX",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/HP64",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/SUN",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/SUN64",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/PTX",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2/2",DATABASE_TYPE_DB2);
//	    databaseTypeMappings.setProperty("DB2 UDB AS400", DATABASE_TYPE_DB2);
//		databaseTypeMappings.setProperty("HDB", DATABASE_TYPE_HANA);
//		databaseTypeMappings.setProperty("Adaptive Server Enterprise", DATABASE_TYPE_SYBASE);
	    return databaseTypeMappings;
	  }


}
