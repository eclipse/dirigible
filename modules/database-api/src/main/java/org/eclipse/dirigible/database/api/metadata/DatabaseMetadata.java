/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.Filter;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseMetadata.
 */
public class DatabaseMetadata {

	/** The all procedures are callable. */
	private boolean allProceduresAreCallable;
	
	/** The all tables are selectable. */
	private boolean allTablesAreSelectable;
	
	/** The get URL. */
	private String getURL;
	
	/** The get user name. */
	private String getUserName;
	
	/** The is read only. */
	private boolean isReadOnly;
	
	/** The nulls are sorted high. */
	private boolean nullsAreSortedHigh;
	
	/** The nulls are sorted low. */
	private boolean nullsAreSortedLow;
	
	/** The nulls are sorted at start. */
	private boolean nullsAreSortedAtStart;
	
	/** The nulls are sorted at end. */
	private boolean nullsAreSortedAtEnd;
	
	/** The database product name. */
	private String databaseProductName;
	
	/** The database product version. */
	private String databaseProductVersion;
	
	/** The driver name. */
	private String driverName;
	
	/** The driver version. */
	private String driverVersion;
	
	/** The driver major version. */
	private int driverMajorVersion;
	
	/** The driver minor version. */
	private int driverMinorVersion;
	
	/** The uses local files. */
	private boolean usesLocalFiles;
	
	/** The uses local file per table. */
	private boolean usesLocalFilePerTable;
	
	/** The supports mixed case identifiers. */
	private boolean supportsMixedCaseIdentifiers;
	
	/** The stores upper case identifiers. */
	private boolean storesUpperCaseIdentifiers;
	
	/** The stores lower case identifiers. */
	private boolean storesLowerCaseIdentifiers;
	
	/** The stores mixed case identifiers. */
	private boolean storesMixedCaseIdentifiers;
	
	/** The supports mixed case quoted identifiers. */
	private boolean supportsMixedCaseQuotedIdentifiers;
	
	/** The stores upper case quoted identifiers. */
	private boolean storesUpperCaseQuotedIdentifiers;
	
	/** The stores lower case quoted identifiers. */
	private boolean storesLowerCaseQuotedIdentifiers;
	
	/** The stores mixed case quoted identifiers. */
	private boolean storesMixedCaseQuotedIdentifiers;
	
	/** The identifier quote string. */
	private String identifierQuoteString;
	
	/** The sql keywords. */
	private String sqlKeywords;
	
	/** The numeric functions. */
	private String numericFunctions;
	
	/** The string functions. */
	private String stringFunctions;
	
	/** The system functions. */
	private String systemFunctions;
	
	/** The time date functions. */
	private String timeDateFunctions;
	
	/** The search string escape. */
	private String searchStringEscape;
	
	/** The extra name characters. */
	private String extraNameCharacters;
	
	/** The supports alter table with add column. */
	private boolean supportsAlterTableWithAddColumn;
	
	/** The supports alter table with drop column. */
	private boolean supportsAlterTableWithDropColumn;
	
	/** The supports column aliasing. */
	private boolean supportsColumnAliasing;
	
	/** The null plus non null is null. */
	private boolean nullPlusNonNullIsNull;
	
	/** The supports convert. */
	private boolean supportsConvert;
	
	/** The supports table correlation names. */
	private boolean supportsTableCorrelationNames;
	
	/** The supports different table correlation names. */
	private boolean supportsDifferentTableCorrelationNames;
	
	/** The supports expressions in order by. */
	private boolean supportsExpressionsInOrderBy;
	
	/** The supports order by unrelated. */
	private boolean supportsOrderByUnrelated;
	
	/** The supports group by. */
	private boolean supportsGroupBy;
	
	/** The supports group by unrelated. */
	private boolean supportsGroupByUnrelated;
	
	/** The supports group by beyond select. */
	private boolean supportsGroupByBeyondSelect;
	
	/** The supports like escape clause. */
	private boolean supportsLikeEscapeClause;
	
	/** The supports multiple result sets. */
	private boolean supportsMultipleResultSets;
	
	/** The supports multiple transactions. */
	private boolean supportsMultipleTransactions;
	
	/** The supports non nullable columns. */
	private boolean supportsNonNullableColumns;
	
	/** The supports minimum SQL grammar. */
	private boolean supportsMinimumSQLGrammar;
	
	/** The supports core SQL grammar. */
	private boolean supportsCoreSQLGrammar;
	
	/** The supports extended SQL grammar. */
	private boolean supportsExtendedSQLGrammar;
	
	/** The supports ANSI 92 entry level SQL. */
	private boolean supportsANSI92EntryLevelSQL;
	
	/** The supports ANSI 92 intermediate SQL. */
	private boolean supportsANSI92IntermediateSQL;
	
	/** The supports ANSI 92 full SQL. */
	private boolean supportsANSI92FullSQL;
	
	/** The supports integrity enhancement facility. */
	private boolean supportsIntegrityEnhancementFacility;
	
	/** The supports outer joins. */
	private boolean supportsOuterJoins;
	
	/** The supports full outer joins. */
	private boolean supportsFullOuterJoins;
	
	/** The supports limited outer joins. */
	private boolean supportsLimitedOuterJoins;
	
	/** The schema term. */
	private String schemaTerm;
	
	/** The procedure term. */
	private String procedureTerm;
	
	/** The catalog term. */
	private String catalogTerm;
	
	/** The is catalog at start. */
	private boolean isCatalogAtStart;
	
	/** The get catalog separator. */
	private String getCatalogSeparator;
	
	/** The supports schemas in data manipulation. */
	private boolean supportsSchemasInDataManipulation;
	
	/** The supports schemas in procedure calls. */
	private boolean supportsSchemasInProcedureCalls;
	
	/** The supports schemas in table definitions. */
	private boolean supportsSchemasInTableDefinitions;
	
	/** The supports schemas in index definitions. */
	private boolean supportsSchemasInIndexDefinitions;
	
	/** The supports schemas in privilege definitions. */
	private boolean supportsSchemasInPrivilegeDefinitions;
	
	/** The supports catalogs in data manipulation. */
	private boolean supportsCatalogsInDataManipulation;
	
	/** The supports catalogs in procedure calls. */
	private boolean supportsCatalogsInProcedureCalls;
	
	/** The supports catalogs in table definitions. */
	private boolean supportsCatalogsInTableDefinitions;
	
	/** The supports catalogs in index definitions. */
	private boolean supportsCatalogsInIndexDefinitions;
	
	/** The supports catalogs in privilege definitions. */
	private boolean supportsCatalogsInPrivilegeDefinitions;
	
	/** The supports positioned delete. */
	private boolean supportsPositionedDelete;
	
	/** The supports positioned update. */
	private boolean supportsPositionedUpdate;
	
	/** The supports select for update. */
	private boolean supportsSelectForUpdate;
	
	/** The supports stored procedures. */
	private boolean supportsStoredProcedures;
	
	/** The supports subqueries in comparisons. */
	private boolean supportsSubqueriesInComparisons;
	
	/** The supports subqueries in exists. */
	private boolean supportsSubqueriesInExists;
	
	/** The supports subqueries in ins. */
	private boolean supportsSubqueriesInIns;
	
	/** The supports subqueries in quantifieds. */
	private boolean supportsSubqueriesInQuantifieds;
	
	/** The supports correlated subqueries. */
	private boolean supportsCorrelatedSubqueries;
	
	/** The supports union. */
	private boolean supportsUnion;
	
	/** The supports union all. */
	private boolean supportsUnionAll;
	
	/** The supports open cursors across commit. */
	private boolean supportsOpenCursorsAcrossCommit;
	
	/** The supports open cursors across rollback. */
	private boolean supportsOpenCursorsAcrossRollback;
	
	/** The supports open statements across commit. */
	private boolean supportsOpenStatementsAcrossCommit;
	
	/** The supports open statements across rollback. */
	private boolean supportsOpenStatementsAcrossRollback;
	
	/** The max binary literal length. */
	private int maxBinaryLiteralLength;
	
	/** The max char literal length. */
	private int maxCharLiteralLength;
	
	/** The max column name length. */
	private int maxColumnNameLength;
	
	/** The max columns in group by. */
	private int maxColumnsInGroupBy;
	
	/** The max columns in index. */
	private int maxColumnsInIndex;
	
	/** The max columns in order by. */
	private int maxColumnsInOrderBy;
	
	/** The max columns in select. */
	private int maxColumnsInSelect;
	
	/** The max columns in table. */
	private int maxColumnsInTable;
	
	/** The max connections. */
	private int maxConnections;
	
	/** The max cursor name length. */
	private int maxCursorNameLength;
	
	/** The max index length. */
	private int maxIndexLength;
	
	/** The max schema name length. */
	private int maxSchemaNameLength;
	
	/** The max procedure name length. */
	private int maxProcedureNameLength;
	
	/** The max catalog name length. */
	private int maxCatalogNameLength;
	
	/** The max row size. */
	private int maxRowSize;
	
	/** The max row size include blobs. */
	private boolean maxRowSizeIncludeBlobs;
	
	/** The max statement length. */
	private int maxStatementLength;
	
	/** The max statements. */
	private int maxStatements;
	
	/** The max table name length. */
	private int maxTableNameLength;
	
	/** The max tables in select. */
	private int maxTablesInSelect;
	
	/** The max user name length. */
	private int maxUserNameLength;
	
	/** The default transaction isolation. */
	private int defaultTransactionIsolation;
	
	/** The supports transactions. */
	private boolean supportsTransactions;
	
	/** The supports data definition and data manipulation transactions. */
	private boolean supportsDataDefinitionAndDataManipulationTransactions;
	
	/** The supports data manipulation transactions only. */
	private boolean supportsDataManipulationTransactionsOnly;
	
	/** The data definition causes transaction commit. */
	private boolean dataDefinitionCausesTransactionCommit;
	
	/** The data definition ignored in transactions. */
	private boolean dataDefinitionIgnoredInTransactions;
	
	/** The supports batch updates. */
	private boolean supportsBatchUpdates;
	
	/** The supports savepoints. */
	private boolean supportsSavepoints;
	
	/** The supports named parameters. */
	private boolean supportsNamedParameters;
	
	/** The supports multiple open results. */
	private boolean supportsMultipleOpenResults;
	
	/** The supports get generated keys. */
	private boolean supportsGetGeneratedKeys;
	
	/** The get result set holdability. */
	private int getResultSetHoldability;
	
	/** The get database major version. */
	private int getDatabaseMajorVersion;
	
	/** The get database minor version. */
	private int getDatabaseMinorVersion;
	
	/** The get JDBC major version. */
	private int getJDBCMajorVersion;
	
	/** The get JDBC minor version. */
	private int getJDBCMinorVersion;
	
	/** The get SQL state type. */
	private int getSQLStateType;
	
	/** The locators update copy. */
	private boolean locatorsUpdateCopy;
	
	/** The supports statement pooling. */
	private boolean supportsStatementPooling;
	
	/** The supports stored functions using call syntax. */
	private boolean supportsStoredFunctionsUsingCallSyntax;
	
	/** The auto commit failure closes all result sets. */
	private boolean autoCommitFailureClosesAllResultSets;
	
	/** The generated key always returned. */
	private boolean generatedKeyAlwaysReturned;
	
	/** The get max logical lob size. */
	private long getMaxLogicalLobSize;
	
	/** The supports ref cursors. */
	private boolean supportsRefCursors;

	/** The schemas. */
	private List<SchemaMetadata> schemas;

	/** The kind. */
	private String kind = "database";

	/**
	 * Instantiates a new database metadata.
	 *
	 * @param connection the connection
	 * @param catalogName the catalog name
	 * @param schemaNameFilter the schema name filter
	 * @param tableNameFilter the table name filter
	 * @throws SQLException the SQL exception
	 */
	public DatabaseMetadata(Connection connection, String catalogName, Filter<String> schemaNameFilter, Filter<String> tableNameFilter)
			throws SQLException {
		super();
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		this.allProceduresAreCallable = databaseMetaData.allProceduresAreCallable();
		this.allTablesAreSelectable = databaseMetaData.allTablesAreSelectable();
		this.getURL = databaseMetaData.getURL();
		this.getUserName = databaseMetaData.getUserName();
		this.isReadOnly = databaseMetaData.isReadOnly();
		this.nullsAreSortedHigh = databaseMetaData.nullsAreSortedHigh();
		this.nullsAreSortedLow = databaseMetaData.nullsAreSortedLow();
		this.nullsAreSortedAtStart = databaseMetaData.nullsAreSortedAtStart();
		this.nullsAreSortedAtEnd = databaseMetaData.nullsAreSortedAtEnd();
		this.databaseProductName = databaseMetaData.getDatabaseProductName();
		this.databaseProductVersion = databaseMetaData.getDatabaseProductVersion();
		this.driverName = databaseMetaData.getDriverName();
		this.driverVersion = databaseMetaData.getDriverVersion();
		this.driverMajorVersion = databaseMetaData.getDriverMajorVersion();
		this.driverMinorVersion = databaseMetaData.getDriverMinorVersion();
		this.usesLocalFiles = databaseMetaData.usesLocalFiles();
		this.usesLocalFilePerTable = databaseMetaData.usesLocalFilePerTable();
		this.supportsMixedCaseIdentifiers = databaseMetaData.supportsMixedCaseIdentifiers();
		this.storesUpperCaseIdentifiers = databaseMetaData.storesUpperCaseIdentifiers();
		this.storesLowerCaseIdentifiers = databaseMetaData.storesLowerCaseIdentifiers();
		this.storesMixedCaseIdentifiers = databaseMetaData.storesMixedCaseIdentifiers();
		this.supportsMixedCaseQuotedIdentifiers = databaseMetaData.supportsMixedCaseQuotedIdentifiers();
		this.storesUpperCaseQuotedIdentifiers = databaseMetaData.storesUpperCaseQuotedIdentifiers();
		this.storesLowerCaseQuotedIdentifiers = databaseMetaData.storesLowerCaseQuotedIdentifiers();
		this.storesMixedCaseQuotedIdentifiers = databaseMetaData.storesMixedCaseQuotedIdentifiers();
		this.identifierQuoteString = databaseMetaData.getIdentifierQuoteString();
		this.sqlKeywords = databaseMetaData.getSQLKeywords();
		this.numericFunctions = databaseMetaData.getNumericFunctions();
		this.stringFunctions = databaseMetaData.getStringFunctions();
		this.systemFunctions = databaseMetaData.getSystemFunctions();
		this.timeDateFunctions = databaseMetaData.getTimeDateFunctions();
		this.searchStringEscape = databaseMetaData.getSearchStringEscape();
		this.extraNameCharacters = databaseMetaData.getExtraNameCharacters();
		this.supportsAlterTableWithAddColumn = databaseMetaData.supportsAlterTableWithAddColumn();
		this.supportsAlterTableWithDropColumn = databaseMetaData.supportsAlterTableWithDropColumn();
		this.supportsColumnAliasing = databaseMetaData.supportsColumnAliasing();
		this.nullPlusNonNullIsNull = databaseMetaData.nullPlusNonNullIsNull();
		this.supportsConvert = databaseMetaData.supportsConvert();
		this.supportsTableCorrelationNames = databaseMetaData.supportsTableCorrelationNames();
		this.supportsDifferentTableCorrelationNames = databaseMetaData.supportsDifferentTableCorrelationNames();
		this.supportsExpressionsInOrderBy = databaseMetaData.supportsExpressionsInOrderBy();
		this.supportsOrderByUnrelated = databaseMetaData.supportsOrderByUnrelated();
		this.supportsGroupBy = databaseMetaData.supportsGroupBy();
		this.supportsGroupByUnrelated = databaseMetaData.supportsGroupByUnrelated();
		this.supportsGroupByBeyondSelect = databaseMetaData.supportsGroupByBeyondSelect();
		this.supportsLikeEscapeClause = databaseMetaData.supportsLikeEscapeClause();
		this.supportsMultipleResultSets = databaseMetaData.supportsMultipleResultSets();
		this.supportsMultipleTransactions = databaseMetaData.supportsMultipleTransactions();
		this.supportsNonNullableColumns = databaseMetaData.supportsNonNullableColumns();
		this.supportsMinimumSQLGrammar = databaseMetaData.supportsMinimumSQLGrammar();
		this.supportsCoreSQLGrammar = databaseMetaData.supportsCoreSQLGrammar();
		this.supportsExtendedSQLGrammar = databaseMetaData.supportsExtendedSQLGrammar();
		this.supportsANSI92EntryLevelSQL = databaseMetaData.supportsANSI92EntryLevelSQL();
		this.supportsANSI92IntermediateSQL = databaseMetaData.supportsANSI92IntermediateSQL();
		this.supportsANSI92FullSQL = databaseMetaData.supportsANSI92FullSQL();
		this.supportsIntegrityEnhancementFacility = databaseMetaData.supportsIntegrityEnhancementFacility();
		this.supportsOuterJoins = databaseMetaData.supportsOuterJoins();
		this.supportsFullOuterJoins = databaseMetaData.supportsFullOuterJoins();
		this.supportsLimitedOuterJoins = databaseMetaData.supportsLimitedOuterJoins();
		this.schemaTerm = databaseMetaData.getSchemaTerm();
		this.procedureTerm = databaseMetaData.getProcedureTerm();
		this.catalogTerm = databaseMetaData.getCatalogTerm();
		this.isCatalogAtStart = databaseMetaData.isCatalogAtStart();
		this.getCatalogSeparator = databaseMetaData.getCatalogSeparator();
		this.supportsSchemasInDataManipulation = databaseMetaData.supportsSchemasInDataManipulation();
		this.supportsSchemasInProcedureCalls = databaseMetaData.supportsSchemasInProcedureCalls();
		this.supportsSchemasInTableDefinitions = databaseMetaData.supportsSchemasInTableDefinitions();
		this.supportsSchemasInIndexDefinitions = databaseMetaData.supportsSchemasInIndexDefinitions();
		this.supportsSchemasInPrivilegeDefinitions = databaseMetaData.supportsSchemasInPrivilegeDefinitions();
		this.supportsCatalogsInDataManipulation = databaseMetaData.supportsCatalogsInDataManipulation();
		this.supportsCatalogsInProcedureCalls = databaseMetaData.supportsCatalogsInProcedureCalls();
		this.supportsCatalogsInTableDefinitions = databaseMetaData.supportsCatalogsInTableDefinitions();
		this.supportsCatalogsInIndexDefinitions = databaseMetaData.supportsCatalogsInIndexDefinitions();
		this.supportsCatalogsInPrivilegeDefinitions = databaseMetaData.supportsCatalogsInPrivilegeDefinitions();
		this.supportsPositionedDelete = databaseMetaData.supportsPositionedDelete();
		this.supportsPositionedUpdate = databaseMetaData.supportsPositionedUpdate();
		this.supportsSelectForUpdate = databaseMetaData.supportsSelectForUpdate();
		this.supportsStoredProcedures = databaseMetaData.supportsStoredProcedures();
		this.supportsSubqueriesInComparisons = databaseMetaData.supportsSubqueriesInComparisons();
		this.supportsSubqueriesInExists = databaseMetaData.supportsSubqueriesInExists();
		this.supportsSubqueriesInIns = databaseMetaData.supportsSubqueriesInIns();
		this.supportsSubqueriesInQuantifieds = databaseMetaData.supportsSubqueriesInQuantifieds();
		this.supportsCorrelatedSubqueries = databaseMetaData.supportsCorrelatedSubqueries();
		this.supportsUnion = databaseMetaData.supportsUnion();
		this.supportsUnionAll = databaseMetaData.supportsUnionAll();
		this.supportsOpenCursorsAcrossCommit = databaseMetaData.supportsOpenCursorsAcrossCommit();
		this.supportsOpenCursorsAcrossRollback = databaseMetaData.supportsOpenCursorsAcrossRollback();
		this.supportsOpenStatementsAcrossCommit = databaseMetaData.supportsOpenStatementsAcrossCommit();
		this.supportsOpenStatementsAcrossRollback = databaseMetaData.supportsOpenStatementsAcrossRollback();
		this.maxBinaryLiteralLength = databaseMetaData.getMaxBinaryLiteralLength();
		this.maxCharLiteralLength = databaseMetaData.getMaxCharLiteralLength();
		this.maxColumnNameLength = databaseMetaData.getMaxColumnNameLength();
		this.maxColumnsInGroupBy = databaseMetaData.getMaxColumnsInGroupBy();
		this.maxColumnsInIndex = databaseMetaData.getMaxColumnsInIndex();
		this.maxColumnsInOrderBy = databaseMetaData.getMaxColumnsInOrderBy();
		this.maxColumnsInSelect = databaseMetaData.getMaxColumnsInSelect();
		this.maxColumnsInTable = databaseMetaData.getMaxColumnsInTable();
		this.maxConnections = databaseMetaData.getMaxConnections();
		this.maxCursorNameLength = databaseMetaData.getMaxCursorNameLength();
		this.maxIndexLength = databaseMetaData.getMaxIndexLength();
		this.maxSchemaNameLength = databaseMetaData.getMaxSchemaNameLength();
		this.maxProcedureNameLength = databaseMetaData.getMaxProcedureNameLength();
		this.maxCatalogNameLength = databaseMetaData.getMaxCatalogNameLength();
		this.maxRowSize = databaseMetaData.getMaxRowSize();
		this.maxRowSizeIncludeBlobs = databaseMetaData.doesMaxRowSizeIncludeBlobs();
		this.maxStatementLength = databaseMetaData.getMaxStatementLength();
		this.maxStatements = databaseMetaData.getMaxStatements();
		this.maxTableNameLength = databaseMetaData.getMaxTableNameLength();
		this.maxTablesInSelect = databaseMetaData.getMaxTablesInSelect();
		this.maxUserNameLength = databaseMetaData.getMaxUserNameLength();
		this.defaultTransactionIsolation = databaseMetaData.getDefaultTransactionIsolation();
		this.supportsTransactions = databaseMetaData.supportsTransactions();
		this.supportsDataDefinitionAndDataManipulationTransactions = databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions();
		this.supportsDataManipulationTransactionsOnly = databaseMetaData.supportsDataManipulationTransactionsOnly();
		this.dataDefinitionCausesTransactionCommit = databaseMetaData.dataDefinitionCausesTransactionCommit();
		this.dataDefinitionIgnoredInTransactions = databaseMetaData.dataDefinitionIgnoredInTransactions();
		this.supportsBatchUpdates = databaseMetaData.supportsBatchUpdates();
		this.supportsSavepoints = databaseMetaData.supportsSavepoints();
		this.supportsNamedParameters = databaseMetaData.supportsNamedParameters();
		this.supportsMultipleOpenResults = databaseMetaData.supportsMultipleOpenResults();
		this.supportsGetGeneratedKeys = databaseMetaData.supportsGetGeneratedKeys();
		this.getResultSetHoldability = databaseMetaData.getResultSetHoldability();
		this.getDatabaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
		this.getDatabaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();
		this.getJDBCMajorVersion = databaseMetaData.getJDBCMajorVersion();
		this.getJDBCMinorVersion = databaseMetaData.getJDBCMinorVersion();
		this.getSQLStateType = databaseMetaData.getSQLStateType();
		this.locatorsUpdateCopy = databaseMetaData.locatorsUpdateCopy();
		this.supportsStatementPooling = databaseMetaData.supportsStatementPooling();
		this.supportsStoredFunctionsUsingCallSyntax = databaseMetaData.supportsStoredFunctionsUsingCallSyntax();
		this.autoCommitFailureClosesAllResultSets = databaseMetaData.autoCommitFailureClosesAllResultSets();
		this.generatedKeyAlwaysReturned = databaseMetaData.generatedKeyAlwaysReturned();
		this.getMaxLogicalLobSize = databaseMetaData.getMaxLogicalLobSize();
		this.supportsRefCursors = databaseMetaData.supportsRefCursors();

		this.schemas = DatabaseMetadataHelper.listSchemas(connection, catalogName, schemaNameFilter, tableNameFilter);
	}

	/**
	 * Checks if is all procedures are callable.
	 *
	 * @return true, if is all procedures are callable
	 */
	public boolean isAllProceduresAreCallable() {
		return allProceduresAreCallable;
	}

	/**
	 * Sets the all procedures are callable.
	 *
	 * @param allProceduresAreCallable the new all procedures are callable
	 */
	public void setAllProceduresAreCallable(boolean allProceduresAreCallable) {
		this.allProceduresAreCallable = allProceduresAreCallable;
	}

	/**
	 * Checks if is all tables are selectable.
	 *
	 * @return true, if is all tables are selectable
	 */
	public boolean isAllTablesAreSelectable() {
		return allTablesAreSelectable;
	}

	/**
	 * Sets the all tables are selectable.
	 *
	 * @param allTablesAreSelectable the new all tables are selectable
	 */
	public void setAllTablesAreSelectable(boolean allTablesAreSelectable) {
		this.allTablesAreSelectable = allTablesAreSelectable;
	}

	/**
	 * Gets the gets the URL.
	 *
	 * @return the gets the URL
	 */
	public String getGetURL() {
		return getURL;
	}

	/**
	 * Sets the gets the URL.
	 *
	 * @param getURL the new gets the URL
	 */
	public void setGetURL(String getURL) {
		this.getURL = getURL;
	}

	/**
	 * Gets the gets the user name.
	 *
	 * @return the gets the user name
	 */
	public String getGetUserName() {
		return getUserName;
	}

	/**
	 * Sets the gets the user name.
	 *
	 * @param getUserName the new gets the user name
	 */
	public void setGetUserName(String getUserName) {
		this.getUserName = getUserName;
	}

	/**
	 * Checks if is read only.
	 *
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * Sets the read only.
	 *
	 * @param isReadOnly the new read only
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * Checks if is nulls are sorted high.
	 *
	 * @return true, if is nulls are sorted high
	 */
	public boolean isNullsAreSortedHigh() {
		return nullsAreSortedHigh;
	}

	/**
	 * Sets the nulls are sorted high.
	 *
	 * @param nullsAreSortedHigh the new nulls are sorted high
	 */
	public void setNullsAreSortedHigh(boolean nullsAreSortedHigh) {
		this.nullsAreSortedHigh = nullsAreSortedHigh;
	}

	/**
	 * Checks if is nulls are sorted low.
	 *
	 * @return true, if is nulls are sorted low
	 */
	public boolean isNullsAreSortedLow() {
		return nullsAreSortedLow;
	}

	/**
	 * Sets the nulls are sorted low.
	 *
	 * @param nullsAreSortedLow the new nulls are sorted low
	 */
	public void setNullsAreSortedLow(boolean nullsAreSortedLow) {
		this.nullsAreSortedLow = nullsAreSortedLow;
	}

	/**
	 * Checks if is nulls are sorted at start.
	 *
	 * @return true, if is nulls are sorted at start
	 */
	public boolean isNullsAreSortedAtStart() {
		return nullsAreSortedAtStart;
	}

	/**
	 * Sets the nulls are sorted at start.
	 *
	 * @param nullsAreSortedAtStart the new nulls are sorted at start
	 */
	public void setNullsAreSortedAtStart(boolean nullsAreSortedAtStart) {
		this.nullsAreSortedAtStart = nullsAreSortedAtStart;
	}

	/**
	 * Checks if is nulls are sorted at end.
	 *
	 * @return true, if is nulls are sorted at end
	 */
	public boolean isNullsAreSortedAtEnd() {
		return nullsAreSortedAtEnd;
	}

	/**
	 * Sets the nulls are sorted at end.
	 *
	 * @param nullsAreSortedAtEnd the new nulls are sorted at end
	 */
	public void setNullsAreSortedAtEnd(boolean nullsAreSortedAtEnd) {
		this.nullsAreSortedAtEnd = nullsAreSortedAtEnd;
	}

	/**
	 * Gets the database product name.
	 *
	 * @return the database product name
	 */
	public String getDatabaseProductName() {
		return databaseProductName;
	}

	/**
	 * Sets the database product name.
	 *
	 * @param databaseProductName the new database product name
	 */
	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	/**
	 * Gets the database product version.
	 *
	 * @return the database product version
	 */
	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	/**
	 * Sets the database product version.
	 *
	 * @param databaseProductVersion the new database product version
	 */
	public void setDatabaseProductVersion(String databaseProductVersion) {
		this.databaseProductVersion = databaseProductVersion;
	}

	/**
	 * Gets the driver name.
	 *
	 * @return the driver name
	 */
	public String getDriverName() {
		return driverName;
	}

	/**
	 * Sets the driver name.
	 *
	 * @param driverName the new driver name
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 * Gets the driver version.
	 *
	 * @return the driver version
	 */
	public String getDriverVersion() {
		return driverVersion;
	}

	/**
	 * Sets the driver version.
	 *
	 * @param driverVersion the new driver version
	 */
	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}

	/**
	 * Gets the driver major version.
	 *
	 * @return the driver major version
	 */
	public int getDriverMajorVersion() {
		return driverMajorVersion;
	}

	/**
	 * Sets the driver major version.
	 *
	 * @param driverMajorVersion the new driver major version
	 */
	public void setDriverMajorVersion(int driverMajorVersion) {
		this.driverMajorVersion = driverMajorVersion;
	}

	/**
	 * Gets the driver minor version.
	 *
	 * @return the driver minor version
	 */
	public int getDriverMinorVersion() {
		return driverMinorVersion;
	}

	/**
	 * Sets the driver minor version.
	 *
	 * @param driverMinorVersion the new driver minor version
	 */
	public void setDriverMinorVersion(int driverMinorVersion) {
		this.driverMinorVersion = driverMinorVersion;
	}

	/**
	 * Checks if is uses local files.
	 *
	 * @return true, if is uses local files
	 */
	public boolean isUsesLocalFiles() {
		return usesLocalFiles;
	}

	/**
	 * Sets the uses local files.
	 *
	 * @param usesLocalFiles the new uses local files
	 */
	public void setUsesLocalFiles(boolean usesLocalFiles) {
		this.usesLocalFiles = usesLocalFiles;
	}

	/**
	 * Checks if is uses local file per table.
	 *
	 * @return true, if is uses local file per table
	 */
	public boolean isUsesLocalFilePerTable() {
		return usesLocalFilePerTable;
	}

	/**
	 * Sets the uses local file per table.
	 *
	 * @param usesLocalFilePerTable the new uses local file per table
	 */
	public void setUsesLocalFilePerTable(boolean usesLocalFilePerTable) {
		this.usesLocalFilePerTable = usesLocalFilePerTable;
	}

	/**
	 * Checks if is supports mixed case identifiers.
	 *
	 * @return true, if is supports mixed case identifiers
	 */
	public boolean isSupportsMixedCaseIdentifiers() {
		return supportsMixedCaseIdentifiers;
	}

	/**
	 * Sets the supports mixed case identifiers.
	 *
	 * @param supportsMixedCaseIdentifiers the new supports mixed case identifiers
	 */
	public void setSupportsMixedCaseIdentifiers(boolean supportsMixedCaseIdentifiers) {
		this.supportsMixedCaseIdentifiers = supportsMixedCaseIdentifiers;
	}

	/**
	 * Checks if is stores upper case identifiers.
	 *
	 * @return true, if is stores upper case identifiers
	 */
	public boolean isStoresUpperCaseIdentifiers() {
		return storesUpperCaseIdentifiers;
	}

	/**
	 * Sets the stores upper case identifiers.
	 *
	 * @param storesUpperCaseIdentifiers the new stores upper case identifiers
	 */
	public void setStoresUpperCaseIdentifiers(boolean storesUpperCaseIdentifiers) {
		this.storesUpperCaseIdentifiers = storesUpperCaseIdentifiers;
	}

	/**
	 * Checks if is stores lower case identifiers.
	 *
	 * @return true, if is stores lower case identifiers
	 */
	public boolean isStoresLowerCaseIdentifiers() {
		return storesLowerCaseIdentifiers;
	}

	/**
	 * Sets the stores lower case identifiers.
	 *
	 * @param storesLowerCaseIdentifiers the new stores lower case identifiers
	 */
	public void setStoresLowerCaseIdentifiers(boolean storesLowerCaseIdentifiers) {
		this.storesLowerCaseIdentifiers = storesLowerCaseIdentifiers;
	}

	/**
	 * Checks if is stores mixed case identifiers.
	 *
	 * @return true, if is stores mixed case identifiers
	 */
	public boolean isStoresMixedCaseIdentifiers() {
		return storesMixedCaseIdentifiers;
	}

	/**
	 * Sets the stores mixed case identifiers.
	 *
	 * @param storesMixedCaseIdentifiers the new stores mixed case identifiers
	 */
	public void setStoresMixedCaseIdentifiers(boolean storesMixedCaseIdentifiers) {
		this.storesMixedCaseIdentifiers = storesMixedCaseIdentifiers;
	}

	/**
	 * Checks if is supports mixed case quoted identifiers.
	 *
	 * @return true, if is supports mixed case quoted identifiers
	 */
	public boolean isSupportsMixedCaseQuotedIdentifiers() {
		return supportsMixedCaseQuotedIdentifiers;
	}

	/**
	 * Sets the supports mixed case quoted identifiers.
	 *
	 * @param supportsMixedCaseQuotedIdentifiers the new supports mixed case quoted identifiers
	 */
	public void setSupportsMixedCaseQuotedIdentifiers(boolean supportsMixedCaseQuotedIdentifiers) {
		this.supportsMixedCaseQuotedIdentifiers = supportsMixedCaseQuotedIdentifiers;
	}

	/**
	 * Checks if is stores upper case quoted identifiers.
	 *
	 * @return true, if is stores upper case quoted identifiers
	 */
	public boolean isStoresUpperCaseQuotedIdentifiers() {
		return storesUpperCaseQuotedIdentifiers;
	}

	/**
	 * Sets the stores upper case quoted identifiers.
	 *
	 * @param storesUpperCaseQuotedIdentifiers the new stores upper case quoted identifiers
	 */
	public void setStoresUpperCaseQuotedIdentifiers(boolean storesUpperCaseQuotedIdentifiers) {
		this.storesUpperCaseQuotedIdentifiers = storesUpperCaseQuotedIdentifiers;
	}

	/**
	 * Checks if is stores lower case quoted identifiers.
	 *
	 * @return true, if is stores lower case quoted identifiers
	 */
	public boolean isStoresLowerCaseQuotedIdentifiers() {
		return storesLowerCaseQuotedIdentifiers;
	}

	/**
	 * Sets the stores lower case quoted identifiers.
	 *
	 * @param storesLowerCaseQuotedIdentifiers the new stores lower case quoted identifiers
	 */
	public void setStoresLowerCaseQuotedIdentifiers(boolean storesLowerCaseQuotedIdentifiers) {
		this.storesLowerCaseQuotedIdentifiers = storesLowerCaseQuotedIdentifiers;
	}

	/**
	 * Checks if is stores mixed case quoted identifiers.
	 *
	 * @return true, if is stores mixed case quoted identifiers
	 */
	public boolean isStoresMixedCaseQuotedIdentifiers() {
		return storesMixedCaseQuotedIdentifiers;
	}

	/**
	 * Sets the stores mixed case quoted identifiers.
	 *
	 * @param storesMixedCaseQuotedIdentifiers the new stores mixed case quoted identifiers
	 */
	public void setStoresMixedCaseQuotedIdentifiers(boolean storesMixedCaseQuotedIdentifiers) {
		this.storesMixedCaseQuotedIdentifiers = storesMixedCaseQuotedIdentifiers;
	}

	/**
	 * Gets the identifier quote string.
	 *
	 * @return the identifier quote string
	 */
	public String getIdentifierQuoteString() {
		return identifierQuoteString;
	}

	/**
	 * Sets the identifier quote string.
	 *
	 * @param identifierQuoteString the new identifier quote string
	 */
	public void setIdentifierQuoteString(String identifierQuoteString) {
		this.identifierQuoteString = identifierQuoteString;
	}

	/**
	 * Gets the sql keywords.
	 *
	 * @return the sql keywords
	 */
	public String getSqlKeywords() {
		return sqlKeywords;
	}

	/**
	 * Sets the sql keywords.
	 *
	 * @param sqlKeywords the new sql keywords
	 */
	public void setSqlKeywords(String sqlKeywords) {
		this.sqlKeywords = sqlKeywords;
	}

	/**
	 * Gets the numeric functions.
	 *
	 * @return the numeric functions
	 */
	public String getNumericFunctions() {
		return numericFunctions;
	}

	/**
	 * Sets the numeric functions.
	 *
	 * @param numericFunctions the new numeric functions
	 */
	public void setNumericFunctions(String numericFunctions) {
		this.numericFunctions = numericFunctions;
	}

	/**
	 * Gets the string functions.
	 *
	 * @return the string functions
	 */
	public String getStringFunctions() {
		return stringFunctions;
	}

	/**
	 * Sets the string functions.
	 *
	 * @param stringFunctions the new string functions
	 */
	public void setStringFunctions(String stringFunctions) {
		this.stringFunctions = stringFunctions;
	}

	/**
	 * Gets the system functions.
	 *
	 * @return the system functions
	 */
	public String getSystemFunctions() {
		return systemFunctions;
	}

	/**
	 * Sets the system functions.
	 *
	 * @param systemFunctions the new system functions
	 */
	public void setSystemFunctions(String systemFunctions) {
		this.systemFunctions = systemFunctions;
	}

	/**
	 * Gets the time date functions.
	 *
	 * @return the time date functions
	 */
	public String getTimeDateFunctions() {
		return timeDateFunctions;
	}

	/**
	 * Sets the time date functions.
	 *
	 * @param timeDateFunctions the new time date functions
	 */
	public void setTimeDateFunctions(String timeDateFunctions) {
		this.timeDateFunctions = timeDateFunctions;
	}

	/**
	 * Gets the search string escape.
	 *
	 * @return the search string escape
	 */
	public String getSearchStringEscape() {
		return searchStringEscape;
	}

	/**
	 * Sets the search string escape.
	 *
	 * @param searchStringEscape the new search string escape
	 */
	public void setSearchStringEscape(String searchStringEscape) {
		this.searchStringEscape = searchStringEscape;
	}

	/**
	 * Gets the extra name characters.
	 *
	 * @return the extra name characters
	 */
	public String getExtraNameCharacters() {
		return extraNameCharacters;
	}

	/**
	 * Sets the extra name characters.
	 *
	 * @param extraNameCharacters the new extra name characters
	 */
	public void setExtraNameCharacters(String extraNameCharacters) {
		this.extraNameCharacters = extraNameCharacters;
	}

	/**
	 * Checks if is supports alter table with add column.
	 *
	 * @return true, if is supports alter table with add column
	 */
	public boolean isSupportsAlterTableWithAddColumn() {
		return supportsAlterTableWithAddColumn;
	}

	/**
	 * Sets the supports alter table with add column.
	 *
	 * @param supportsAlterTableWithAddColumn the new supports alter table with add column
	 */
	public void setSupportsAlterTableWithAddColumn(boolean supportsAlterTableWithAddColumn) {
		this.supportsAlterTableWithAddColumn = supportsAlterTableWithAddColumn;
	}

	/**
	 * Checks if is supports alter table with drop column.
	 *
	 * @return true, if is supports alter table with drop column
	 */
	public boolean isSupportsAlterTableWithDropColumn() {
		return supportsAlterTableWithDropColumn;
	}

	/**
	 * Sets the supports alter table with drop column.
	 *
	 * @param supportsAlterTableWithDropColumn the new supports alter table with drop column
	 */
	public void setSupportsAlterTableWithDropColumn(boolean supportsAlterTableWithDropColumn) {
		this.supportsAlterTableWithDropColumn = supportsAlterTableWithDropColumn;
	}

	/**
	 * Checks if is supports column aliasing.
	 *
	 * @return true, if is supports column aliasing
	 */
	public boolean isSupportsColumnAliasing() {
		return supportsColumnAliasing;
	}

	/**
	 * Sets the supports column aliasing.
	 *
	 * @param supportsColumnAliasing the new supports column aliasing
	 */
	public void setSupportsColumnAliasing(boolean supportsColumnAliasing) {
		this.supportsColumnAliasing = supportsColumnAliasing;
	}

	/**
	 * Checks if is null plus non null is null.
	 *
	 * @return true, if is null plus non null is null
	 */
	public boolean isNullPlusNonNullIsNull() {
		return nullPlusNonNullIsNull;
	}

	/**
	 * Sets the null plus non null is null.
	 *
	 * @param nullPlusNonNullIsNull the new null plus non null is null
	 */
	public void setNullPlusNonNullIsNull(boolean nullPlusNonNullIsNull) {
		this.nullPlusNonNullIsNull = nullPlusNonNullIsNull;
	}

	/**
	 * Checks if is supports convert.
	 *
	 * @return true, if is supports convert
	 */
	public boolean isSupportsConvert() {
		return supportsConvert;
	}

	/**
	 * Sets the supports convert.
	 *
	 * @param supportsConvert the new supports convert
	 */
	public void setSupportsConvert(boolean supportsConvert) {
		this.supportsConvert = supportsConvert;
	}

	/**
	 * Checks if is supports table correlation names.
	 *
	 * @return true, if is supports table correlation names
	 */
	public boolean isSupportsTableCorrelationNames() {
		return supportsTableCorrelationNames;
	}

	/**
	 * Sets the supports table correlation names.
	 *
	 * @param supportsTableCorrelationNames the new supports table correlation names
	 */
	public void setSupportsTableCorrelationNames(boolean supportsTableCorrelationNames) {
		this.supportsTableCorrelationNames = supportsTableCorrelationNames;
	}

	/**
	 * Checks if is supports different table correlation names.
	 *
	 * @return true, if is supports different table correlation names
	 */
	public boolean isSupportsDifferentTableCorrelationNames() {
		return supportsDifferentTableCorrelationNames;
	}

	/**
	 * Sets the supports different table correlation names.
	 *
	 * @param supportsDifferentTableCorrelationNames the new supports different table correlation names
	 */
	public void setSupportsDifferentTableCorrelationNames(boolean supportsDifferentTableCorrelationNames) {
		this.supportsDifferentTableCorrelationNames = supportsDifferentTableCorrelationNames;
	}

	/**
	 * Checks if is supports expressions in order by.
	 *
	 * @return true, if is supports expressions in order by
	 */
	public boolean isSupportsExpressionsInOrderBy() {
		return supportsExpressionsInOrderBy;
	}

	/**
	 * Sets the supports expressions in order by.
	 *
	 * @param supportsExpressionsInOrderBy the new supports expressions in order by
	 */
	public void setSupportsExpressionsInOrderBy(boolean supportsExpressionsInOrderBy) {
		this.supportsExpressionsInOrderBy = supportsExpressionsInOrderBy;
	}

	/**
	 * Checks if is supports order by unrelated.
	 *
	 * @return true, if is supports order by unrelated
	 */
	public boolean isSupportsOrderByUnrelated() {
		return supportsOrderByUnrelated;
	}

	/**
	 * Sets the supports order by unrelated.
	 *
	 * @param supportsOrderByUnrelated the new supports order by unrelated
	 */
	public void setSupportsOrderByUnrelated(boolean supportsOrderByUnrelated) {
		this.supportsOrderByUnrelated = supportsOrderByUnrelated;
	}

	/**
	 * Checks if is supports group by.
	 *
	 * @return true, if is supports group by
	 */
	public boolean isSupportsGroupBy() {
		return supportsGroupBy;
	}

	/**
	 * Sets the supports group by.
	 *
	 * @param supportsGroupBy the new supports group by
	 */
	public void setSupportsGroupBy(boolean supportsGroupBy) {
		this.supportsGroupBy = supportsGroupBy;
	}

	/**
	 * Checks if is supports group by unrelated.
	 *
	 * @return true, if is supports group by unrelated
	 */
	public boolean isSupportsGroupByUnrelated() {
		return supportsGroupByUnrelated;
	}

	/**
	 * Sets the supports group by unrelated.
	 *
	 * @param supportsGroupByUnrelated the new supports group by unrelated
	 */
	public void setSupportsGroupByUnrelated(boolean supportsGroupByUnrelated) {
		this.supportsGroupByUnrelated = supportsGroupByUnrelated;
	}

	/**
	 * Checks if is supports group by beyond select.
	 *
	 * @return true, if is supports group by beyond select
	 */
	public boolean isSupportsGroupByBeyondSelect() {
		return supportsGroupByBeyondSelect;
	}

	/**
	 * Sets the supports group by beyond select.
	 *
	 * @param supportsGroupByBeyondSelect the new supports group by beyond select
	 */
	public void setSupportsGroupByBeyondSelect(boolean supportsGroupByBeyondSelect) {
		this.supportsGroupByBeyondSelect = supportsGroupByBeyondSelect;
	}

	/**
	 * Checks if is supports like escape clause.
	 *
	 * @return true, if is supports like escape clause
	 */
	public boolean isSupportsLikeEscapeClause() {
		return supportsLikeEscapeClause;
	}

	/**
	 * Sets the supports like escape clause.
	 *
	 * @param supportsLikeEscapeClause the new supports like escape clause
	 */
	public void setSupportsLikeEscapeClause(boolean supportsLikeEscapeClause) {
		this.supportsLikeEscapeClause = supportsLikeEscapeClause;
	}

	/**
	 * Checks if is supports multiple result sets.
	 *
	 * @return true, if is supports multiple result sets
	 */
	public boolean isSupportsMultipleResultSets() {
		return supportsMultipleResultSets;
	}

	/**
	 * Sets the supports multiple result sets.
	 *
	 * @param supportsMultipleResultSets the new supports multiple result sets
	 */
	public void setSupportsMultipleResultSets(boolean supportsMultipleResultSets) {
		this.supportsMultipleResultSets = supportsMultipleResultSets;
	}

	/**
	 * Checks if is supports multiple transactions.
	 *
	 * @return true, if is supports multiple transactions
	 */
	public boolean isSupportsMultipleTransactions() {
		return supportsMultipleTransactions;
	}

	/**
	 * Sets the supports multiple transactions.
	 *
	 * @param supportsMultipleTransactions the new supports multiple transactions
	 */
	public void setSupportsMultipleTransactions(boolean supportsMultipleTransactions) {
		this.supportsMultipleTransactions = supportsMultipleTransactions;
	}

	/**
	 * Checks if is supports non nullable columns.
	 *
	 * @return true, if is supports non nullable columns
	 */
	public boolean isSupportsNonNullableColumns() {
		return supportsNonNullableColumns;
	}

	/**
	 * Sets the supports non nullable columns.
	 *
	 * @param supportsNonNullableColumns the new supports non nullable columns
	 */
	public void setSupportsNonNullableColumns(boolean supportsNonNullableColumns) {
		this.supportsNonNullableColumns = supportsNonNullableColumns;
	}

	/**
	 * Checks if is supports minimum SQL grammar.
	 *
	 * @return true, if is supports minimum SQL grammar
	 */
	public boolean isSupportsMinimumSQLGrammar() {
		return supportsMinimumSQLGrammar;
	}

	/**
	 * Sets the supports minimum SQL grammar.
	 *
	 * @param supportsMinimumSQLGrammar the new supports minimum SQL grammar
	 */
	public void setSupportsMinimumSQLGrammar(boolean supportsMinimumSQLGrammar) {
		this.supportsMinimumSQLGrammar = supportsMinimumSQLGrammar;
	}

	/**
	 * Checks if is supports core SQL grammar.
	 *
	 * @return true, if is supports core SQL grammar
	 */
	public boolean isSupportsCoreSQLGrammar() {
		return supportsCoreSQLGrammar;
	}

	/**
	 * Sets the supports core SQL grammar.
	 *
	 * @param supportsCoreSQLGrammar the new supports core SQL grammar
	 */
	public void setSupportsCoreSQLGrammar(boolean supportsCoreSQLGrammar) {
		this.supportsCoreSQLGrammar = supportsCoreSQLGrammar;
	}

	/**
	 * Checks if is supports extended SQL grammar.
	 *
	 * @return true, if is supports extended SQL grammar
	 */
	public boolean isSupportsExtendedSQLGrammar() {
		return supportsExtendedSQLGrammar;
	}

	/**
	 * Sets the supports extended SQL grammar.
	 *
	 * @param supportsExtendedSQLGrammar the new supports extended SQL grammar
	 */
	public void setSupportsExtendedSQLGrammar(boolean supportsExtendedSQLGrammar) {
		this.supportsExtendedSQLGrammar = supportsExtendedSQLGrammar;
	}

	/**
	 * Checks if is supports ANSI 92 entry level SQL.
	 *
	 * @return true, if is supports ANSI 92 entry level SQL
	 */
	public boolean isSupportsANSI92EntryLevelSQL() {
		return supportsANSI92EntryLevelSQL;
	}

	/**
	 * Sets the supports ANSI 92 entry level SQL.
	 *
	 * @param supportsANSI92EntryLevelSQL the new supports ANSI 92 entry level SQL
	 */
	public void setSupportsANSI92EntryLevelSQL(boolean supportsANSI92EntryLevelSQL) {
		this.supportsANSI92EntryLevelSQL = supportsANSI92EntryLevelSQL;
	}

	/**
	 * Checks if is supports ANSI 92 intermediate SQL.
	 *
	 * @return true, if is supports ANSI 92 intermediate SQL
	 */
	public boolean isSupportsANSI92IntermediateSQL() {
		return supportsANSI92IntermediateSQL;
	}

	/**
	 * Sets the supports ANSI 92 intermediate SQL.
	 *
	 * @param supportsANSI92IntermediateSQL the new supports ANSI 92 intermediate SQL
	 */
	public void setSupportsANSI92IntermediateSQL(boolean supportsANSI92IntermediateSQL) {
		this.supportsANSI92IntermediateSQL = supportsANSI92IntermediateSQL;
	}

	/**
	 * Checks if is supports ANSI 92 full SQL.
	 *
	 * @return true, if is supports ANSI 92 full SQL
	 */
	public boolean isSupportsANSI92FullSQL() {
		return supportsANSI92FullSQL;
	}

	/**
	 * Sets the supports ANSI 92 full SQL.
	 *
	 * @param supportsANSI92FullSQL the new supports ANSI 92 full SQL
	 */
	public void setSupportsANSI92FullSQL(boolean supportsANSI92FullSQL) {
		this.supportsANSI92FullSQL = supportsANSI92FullSQL;
	}

	/**
	 * Checks if is supports integrity enhancement facility.
	 *
	 * @return true, if is supports integrity enhancement facility
	 */
	public boolean isSupportsIntegrityEnhancementFacility() {
		return supportsIntegrityEnhancementFacility;
	}

	/**
	 * Sets the supports integrity enhancement facility.
	 *
	 * @param supportsIntegrityEnhancementFacility the new supports integrity enhancement facility
	 */
	public void setSupportsIntegrityEnhancementFacility(boolean supportsIntegrityEnhancementFacility) {
		this.supportsIntegrityEnhancementFacility = supportsIntegrityEnhancementFacility;
	}

	/**
	 * Checks if is supports outer joins.
	 *
	 * @return true, if is supports outer joins
	 */
	public boolean isSupportsOuterJoins() {
		return supportsOuterJoins;
	}

	/**
	 * Sets the supports outer joins.
	 *
	 * @param supportsOuterJoins the new supports outer joins
	 */
	public void setSupportsOuterJoins(boolean supportsOuterJoins) {
		this.supportsOuterJoins = supportsOuterJoins;
	}

	/**
	 * Checks if is supports full outer joins.
	 *
	 * @return true, if is supports full outer joins
	 */
	public boolean isSupportsFullOuterJoins() {
		return supportsFullOuterJoins;
	}

	/**
	 * Sets the supports full outer joins.
	 *
	 * @param supportsFullOuterJoins the new supports full outer joins
	 */
	public void setSupportsFullOuterJoins(boolean supportsFullOuterJoins) {
		this.supportsFullOuterJoins = supportsFullOuterJoins;
	}

	/**
	 * Checks if is supports limited outer joins.
	 *
	 * @return true, if is supports limited outer joins
	 */
	public boolean isSupportsLimitedOuterJoins() {
		return supportsLimitedOuterJoins;
	}

	/**
	 * Sets the supports limited outer joins.
	 *
	 * @param supportsLimitedOuterJoins the new supports limited outer joins
	 */
	public void setSupportsLimitedOuterJoins(boolean supportsLimitedOuterJoins) {
		this.supportsLimitedOuterJoins = supportsLimitedOuterJoins;
	}

	/**
	 * Gets the schema term.
	 *
	 * @return the schema term
	 */
	public String getSchemaTerm() {
		return schemaTerm;
	}

	/**
	 * Sets the schema term.
	 *
	 * @param schemaTerm the new schema term
	 */
	public void setSchemaTerm(String schemaTerm) {
		this.schemaTerm = schemaTerm;
	}

	/**
	 * Gets the procedure term.
	 *
	 * @return the procedure term
	 */
	public String getProcedureTerm() {
		return procedureTerm;
	}

	/**
	 * Sets the procedure term.
	 *
	 * @param procedureTerm the new procedure term
	 */
	public void setProcedureTerm(String procedureTerm) {
		this.procedureTerm = procedureTerm;
	}

	/**
	 * Gets the catalog term.
	 *
	 * @return the catalog term
	 */
	public String getCatalogTerm() {
		return catalogTerm;
	}

	/**
	 * Sets the catalog term.
	 *
	 * @param catalogTerm the new catalog term
	 */
	public void setCatalogTerm(String catalogTerm) {
		this.catalogTerm = catalogTerm;
	}

	/**
	 * Checks if is catalog at start.
	 *
	 * @return true, if is catalog at start
	 */
	public boolean isCatalogAtStart() {
		return isCatalogAtStart;
	}

	/**
	 * Sets the catalog at start.
	 *
	 * @param isCatalogAtStart the new catalog at start
	 */
	public void setCatalogAtStart(boolean isCatalogAtStart) {
		this.isCatalogAtStart = isCatalogAtStart;
	}

	/**
	 * Gets the gets the catalog separator.
	 *
	 * @return the gets the catalog separator
	 */
	public String getGetCatalogSeparator() {
		return getCatalogSeparator;
	}

	/**
	 * Sets the gets the catalog separator.
	 *
	 * @param getCatalogSeparator the new gets the catalog separator
	 */
	public void setGetCatalogSeparator(String getCatalogSeparator) {
		this.getCatalogSeparator = getCatalogSeparator;
	}

	/**
	 * Checks if is supports schemas in data manipulation.
	 *
	 * @return true, if is supports schemas in data manipulation
	 */
	public boolean isSupportsSchemasInDataManipulation() {
		return supportsSchemasInDataManipulation;
	}

	/**
	 * Sets the supports schemas in data manipulation.
	 *
	 * @param supportsSchemasInDataManipulation the new supports schemas in data manipulation
	 */
	public void setSupportsSchemasInDataManipulation(boolean supportsSchemasInDataManipulation) {
		this.supportsSchemasInDataManipulation = supportsSchemasInDataManipulation;
	}

	/**
	 * Checks if is supports schemas in procedure calls.
	 *
	 * @return true, if is supports schemas in procedure calls
	 */
	public boolean isSupportsSchemasInProcedureCalls() {
		return supportsSchemasInProcedureCalls;
	}

	/**
	 * Sets the supports schemas in procedure calls.
	 *
	 * @param supportsSchemasInProcedureCalls the new supports schemas in procedure calls
	 */
	public void setSupportsSchemasInProcedureCalls(boolean supportsSchemasInProcedureCalls) {
		this.supportsSchemasInProcedureCalls = supportsSchemasInProcedureCalls;
	}

	/**
	 * Checks if is supports schemas in table definitions.
	 *
	 * @return true, if is supports schemas in table definitions
	 */
	public boolean isSupportsSchemasInTableDefinitions() {
		return supportsSchemasInTableDefinitions;
	}

	/**
	 * Sets the supports schemas in table definitions.
	 *
	 * @param supportsSchemasInTableDefinitions the new supports schemas in table definitions
	 */
	public void setSupportsSchemasInTableDefinitions(boolean supportsSchemasInTableDefinitions) {
		this.supportsSchemasInTableDefinitions = supportsSchemasInTableDefinitions;
	}

	/**
	 * Checks if is supports schemas in index definitions.
	 *
	 * @return true, if is supports schemas in index definitions
	 */
	public boolean isSupportsSchemasInIndexDefinitions() {
		return supportsSchemasInIndexDefinitions;
	}

	/**
	 * Sets the supports schemas in index definitions.
	 *
	 * @param supportsSchemasInIndexDefinitions the new supports schemas in index definitions
	 */
	public void setSupportsSchemasInIndexDefinitions(boolean supportsSchemasInIndexDefinitions) {
		this.supportsSchemasInIndexDefinitions = supportsSchemasInIndexDefinitions;
	}

	/**
	 * Checks if is supports schemas in privilege definitions.
	 *
	 * @return true, if is supports schemas in privilege definitions
	 */
	public boolean isSupportsSchemasInPrivilegeDefinitions() {
		return supportsSchemasInPrivilegeDefinitions;
	}

	/**
	 * Sets the supports schemas in privilege definitions.
	 *
	 * @param supportsSchemasInPrivilegeDefinitions the new supports schemas in privilege definitions
	 */
	public void setSupportsSchemasInPrivilegeDefinitions(boolean supportsSchemasInPrivilegeDefinitions) {
		this.supportsSchemasInPrivilegeDefinitions = supportsSchemasInPrivilegeDefinitions;
	}

	/**
	 * Checks if is supports catalogs in data manipulation.
	 *
	 * @return true, if is supports catalogs in data manipulation
	 */
	public boolean isSupportsCatalogsInDataManipulation() {
		return supportsCatalogsInDataManipulation;
	}

	/**
	 * Sets the supports catalogs in data manipulation.
	 *
	 * @param supportsCatalogsInDataManipulation the new supports catalogs in data manipulation
	 */
	public void setSupportsCatalogsInDataManipulation(boolean supportsCatalogsInDataManipulation) {
		this.supportsCatalogsInDataManipulation = supportsCatalogsInDataManipulation;
	}

	/**
	 * Checks if is supports catalogs in procedure calls.
	 *
	 * @return true, if is supports catalogs in procedure calls
	 */
	public boolean isSupportsCatalogsInProcedureCalls() {
		return supportsCatalogsInProcedureCalls;
	}

	/**
	 * Sets the supports catalogs in procedure calls.
	 *
	 * @param supportsCatalogsInProcedureCalls the new supports catalogs in procedure calls
	 */
	public void setSupportsCatalogsInProcedureCalls(boolean supportsCatalogsInProcedureCalls) {
		this.supportsCatalogsInProcedureCalls = supportsCatalogsInProcedureCalls;
	}

	/**
	 * Checks if is supports catalogs in table definitions.
	 *
	 * @return true, if is supports catalogs in table definitions
	 */
	public boolean isSupportsCatalogsInTableDefinitions() {
		return supportsCatalogsInTableDefinitions;
	}

	/**
	 * Sets the supports catalogs in table definitions.
	 *
	 * @param supportsCatalogsInTableDefinitions the new supports catalogs in table definitions
	 */
	public void setSupportsCatalogsInTableDefinitions(boolean supportsCatalogsInTableDefinitions) {
		this.supportsCatalogsInTableDefinitions = supportsCatalogsInTableDefinitions;
	}

	/**
	 * Checks if is supports catalogs in index definitions.
	 *
	 * @return true, if is supports catalogs in index definitions
	 */
	public boolean isSupportsCatalogsInIndexDefinitions() {
		return supportsCatalogsInIndexDefinitions;
	}

	/**
	 * Sets the supports catalogs in index definitions.
	 *
	 * @param supportsCatalogsInIndexDefinitions the new supports catalogs in index definitions
	 */
	public void setSupportsCatalogsInIndexDefinitions(boolean supportsCatalogsInIndexDefinitions) {
		this.supportsCatalogsInIndexDefinitions = supportsCatalogsInIndexDefinitions;
	}

	/**
	 * Checks if is supports catalogs in privilege definitions.
	 *
	 * @return true, if is supports catalogs in privilege definitions
	 */
	public boolean isSupportsCatalogsInPrivilegeDefinitions() {
		return supportsCatalogsInPrivilegeDefinitions;
	}

	/**
	 * Sets the supports catalogs in privilege definitions.
	 *
	 * @param supportsCatalogsInPrivilegeDefinitions the new supports catalogs in privilege definitions
	 */
	public void setSupportsCatalogsInPrivilegeDefinitions(boolean supportsCatalogsInPrivilegeDefinitions) {
		this.supportsCatalogsInPrivilegeDefinitions = supportsCatalogsInPrivilegeDefinitions;
	}

	/**
	 * Checks if is supports positioned delete.
	 *
	 * @return true, if is supports positioned delete
	 */
	public boolean isSupportsPositionedDelete() {
		return supportsPositionedDelete;
	}

	/**
	 * Sets the supports positioned delete.
	 *
	 * @param supportsPositionedDelete the new supports positioned delete
	 */
	public void setSupportsPositionedDelete(boolean supportsPositionedDelete) {
		this.supportsPositionedDelete = supportsPositionedDelete;
	}

	/**
	 * Checks if is supports positioned update.
	 *
	 * @return true, if is supports positioned update
	 */
	public boolean isSupportsPositionedUpdate() {
		return supportsPositionedUpdate;
	}

	/**
	 * Sets the supports positioned update.
	 *
	 * @param supportsPositionedUpdate the new supports positioned update
	 */
	public void setSupportsPositionedUpdate(boolean supportsPositionedUpdate) {
		this.supportsPositionedUpdate = supportsPositionedUpdate;
	}

	/**
	 * Checks if is supports select for update.
	 *
	 * @return true, if is supports select for update
	 */
	public boolean isSupportsSelectForUpdate() {
		return supportsSelectForUpdate;
	}

	/**
	 * Sets the supports select for update.
	 *
	 * @param supportsSelectForUpdate the new supports select for update
	 */
	public void setSupportsSelectForUpdate(boolean supportsSelectForUpdate) {
		this.supportsSelectForUpdate = supportsSelectForUpdate;
	}

	/**
	 * Checks if is supports stored procedures.
	 *
	 * @return true, if is supports stored procedures
	 */
	public boolean isSupportsStoredProcedures() {
		return supportsStoredProcedures;
	}

	/**
	 * Sets the supports stored procedures.
	 *
	 * @param supportsStoredProcedures the new supports stored procedures
	 */
	public void setSupportsStoredProcedures(boolean supportsStoredProcedures) {
		this.supportsStoredProcedures = supportsStoredProcedures;
	}

	/**
	 * Checks if is supports subqueries in comparisons.
	 *
	 * @return true, if is supports subqueries in comparisons
	 */
	public boolean isSupportsSubqueriesInComparisons() {
		return supportsSubqueriesInComparisons;
	}

	/**
	 * Sets the supports subqueries in comparisons.
	 *
	 * @param supportsSubqueriesInComparisons the new supports subqueries in comparisons
	 */
	public void setSupportsSubqueriesInComparisons(boolean supportsSubqueriesInComparisons) {
		this.supportsSubqueriesInComparisons = supportsSubqueriesInComparisons;
	}

	/**
	 * Checks if is supports subqueries in exists.
	 *
	 * @return true, if is supports subqueries in exists
	 */
	public boolean isSupportsSubqueriesInExists() {
		return supportsSubqueriesInExists;
	}

	/**
	 * Sets the supports subqueries in exists.
	 *
	 * @param supportsSubqueriesInExists the new supports subqueries in exists
	 */
	public void setSupportsSubqueriesInExists(boolean supportsSubqueriesInExists) {
		this.supportsSubqueriesInExists = supportsSubqueriesInExists;
	}

	/**
	 * Checks if is supports subqueries in ins.
	 *
	 * @return true, if is supports subqueries in ins
	 */
	public boolean isSupportsSubqueriesInIns() {
		return supportsSubqueriesInIns;
	}

	/**
	 * Sets the supports subqueries in ins.
	 *
	 * @param supportsSubqueriesInIns the new supports subqueries in ins
	 */
	public void setSupportsSubqueriesInIns(boolean supportsSubqueriesInIns) {
		this.supportsSubqueriesInIns = supportsSubqueriesInIns;
	}

	/**
	 * Checks if is supports subqueries in quantifieds.
	 *
	 * @return true, if is supports subqueries in quantifieds
	 */
	public boolean isSupportsSubqueriesInQuantifieds() {
		return supportsSubqueriesInQuantifieds;
	}

	/**
	 * Sets the supports subqueries in quantifieds.
	 *
	 * @param supportsSubqueriesInQuantifieds the new supports subqueries in quantifieds
	 */
	public void setSupportsSubqueriesInQuantifieds(boolean supportsSubqueriesInQuantifieds) {
		this.supportsSubqueriesInQuantifieds = supportsSubqueriesInQuantifieds;
	}

	/**
	 * Checks if is supports correlated subqueries.
	 *
	 * @return true, if is supports correlated subqueries
	 */
	public boolean isSupportsCorrelatedSubqueries() {
		return supportsCorrelatedSubqueries;
	}

	/**
	 * Sets the supports correlated subqueries.
	 *
	 * @param supportsCorrelatedSubqueries the new supports correlated subqueries
	 */
	public void setSupportsCorrelatedSubqueries(boolean supportsCorrelatedSubqueries) {
		this.supportsCorrelatedSubqueries = supportsCorrelatedSubqueries;
	}

	/**
	 * Checks if is supports union.
	 *
	 * @return true, if is supports union
	 */
	public boolean isSupportsUnion() {
		return supportsUnion;
	}

	/**
	 * Sets the supports union.
	 *
	 * @param supportsUnion the new supports union
	 */
	public void setSupportsUnion(boolean supportsUnion) {
		this.supportsUnion = supportsUnion;
	}

	/**
	 * Checks if is supports union all.
	 *
	 * @return true, if is supports union all
	 */
	public boolean isSupportsUnionAll() {
		return supportsUnionAll;
	}

	/**
	 * Sets the supports union all.
	 *
	 * @param supportsUnionAll the new supports union all
	 */
	public void setSupportsUnionAll(boolean supportsUnionAll) {
		this.supportsUnionAll = supportsUnionAll;
	}

	/**
	 * Checks if is supports open cursors across commit.
	 *
	 * @return true, if is supports open cursors across commit
	 */
	public boolean isSupportsOpenCursorsAcrossCommit() {
		return supportsOpenCursorsAcrossCommit;
	}

	/**
	 * Sets the supports open cursors across commit.
	 *
	 * @param supportsOpenCursorsAcrossCommit the new supports open cursors across commit
	 */
	public void setSupportsOpenCursorsAcrossCommit(boolean supportsOpenCursorsAcrossCommit) {
		this.supportsOpenCursorsAcrossCommit = supportsOpenCursorsAcrossCommit;
	}

	/**
	 * Checks if is supports open cursors across rollback.
	 *
	 * @return true, if is supports open cursors across rollback
	 */
	public boolean isSupportsOpenCursorsAcrossRollback() {
		return supportsOpenCursorsAcrossRollback;
	}

	/**
	 * Sets the supports open cursors across rollback.
	 *
	 * @param supportsOpenCursorsAcrossRollback the new supports open cursors across rollback
	 */
	public void setSupportsOpenCursorsAcrossRollback(boolean supportsOpenCursorsAcrossRollback) {
		this.supportsOpenCursorsAcrossRollback = supportsOpenCursorsAcrossRollback;
	}

	/**
	 * Checks if is supports open statements across commit.
	 *
	 * @return true, if is supports open statements across commit
	 */
	public boolean isSupportsOpenStatementsAcrossCommit() {
		return supportsOpenStatementsAcrossCommit;
	}

	/**
	 * Sets the supports open statements across commit.
	 *
	 * @param supportsOpenStatementsAcrossCommit the new supports open statements across commit
	 */
	public void setSupportsOpenStatementsAcrossCommit(boolean supportsOpenStatementsAcrossCommit) {
		this.supportsOpenStatementsAcrossCommit = supportsOpenStatementsAcrossCommit;
	}

	/**
	 * Checks if is supports open statements across rollback.
	 *
	 * @return true, if is supports open statements across rollback
	 */
	public boolean isSupportsOpenStatementsAcrossRollback() {
		return supportsOpenStatementsAcrossRollback;
	}

	/**
	 * Sets the supports open statements across rollback.
	 *
	 * @param supportsOpenStatementsAcrossRollback the new supports open statements across rollback
	 */
	public void setSupportsOpenStatementsAcrossRollback(boolean supportsOpenStatementsAcrossRollback) {
		this.supportsOpenStatementsAcrossRollback = supportsOpenStatementsAcrossRollback;
	}

	/**
	 * Gets the max binary literal length.
	 *
	 * @return the max binary literal length
	 */
	public int getMaxBinaryLiteralLength() {
		return maxBinaryLiteralLength;
	}

	/**
	 * Sets the max binary literal length.
	 *
	 * @param maxBinaryLiteralLength the new max binary literal length
	 */
	public void setMaxBinaryLiteralLength(int maxBinaryLiteralLength) {
		this.maxBinaryLiteralLength = maxBinaryLiteralLength;
	}

	/**
	 * Gets the max char literal length.
	 *
	 * @return the max char literal length
	 */
	public int getMaxCharLiteralLength() {
		return maxCharLiteralLength;
	}

	/**
	 * Sets the max char literal length.
	 *
	 * @param maxCharLiteralLength the new max char literal length
	 */
	public void setMaxCharLiteralLength(int maxCharLiteralLength) {
		this.maxCharLiteralLength = maxCharLiteralLength;
	}

	/**
	 * Gets the max column name length.
	 *
	 * @return the max column name length
	 */
	public int getMaxColumnNameLength() {
		return maxColumnNameLength;
	}

	/**
	 * Sets the max column name length.
	 *
	 * @param maxColumnNameLength the new max column name length
	 */
	public void setMaxColumnNameLength(int maxColumnNameLength) {
		this.maxColumnNameLength = maxColumnNameLength;
	}

	/**
	 * Gets the max columns in group by.
	 *
	 * @return the max columns in group by
	 */
	public int getMaxColumnsInGroupBy() {
		return maxColumnsInGroupBy;
	}

	/**
	 * Sets the max columns in group by.
	 *
	 * @param maxColumnsInGroupBy the new max columns in group by
	 */
	public void setMaxColumnsInGroupBy(int maxColumnsInGroupBy) {
		this.maxColumnsInGroupBy = maxColumnsInGroupBy;
	}

	/**
	 * Gets the max columns in index.
	 *
	 * @return the max columns in index
	 */
	public int getMaxColumnsInIndex() {
		return maxColumnsInIndex;
	}

	/**
	 * Sets the max columns in index.
	 *
	 * @param maxColumnsInIndex the new max columns in index
	 */
	public void setMaxColumnsInIndex(int maxColumnsInIndex) {
		this.maxColumnsInIndex = maxColumnsInIndex;
	}

	/**
	 * Gets the max columns in order by.
	 *
	 * @return the max columns in order by
	 */
	public int getMaxColumnsInOrderBy() {
		return maxColumnsInOrderBy;
	}

	/**
	 * Sets the max columns in order by.
	 *
	 * @param maxColumnsInOrderBy the new max columns in order by
	 */
	public void setMaxColumnsInOrderBy(int maxColumnsInOrderBy) {
		this.maxColumnsInOrderBy = maxColumnsInOrderBy;
	}

	/**
	 * Gets the max columns in select.
	 *
	 * @return the max columns in select
	 */
	public int getMaxColumnsInSelect() {
		return maxColumnsInSelect;
	}

	/**
	 * Sets the max columns in select.
	 *
	 * @param maxColumnsInSelect the new max columns in select
	 */
	public void setMaxColumnsInSelect(int maxColumnsInSelect) {
		this.maxColumnsInSelect = maxColumnsInSelect;
	}

	/**
	 * Gets the max columns in table.
	 *
	 * @return the max columns in table
	 */
	public int getMaxColumnsInTable() {
		return maxColumnsInTable;
	}

	/**
	 * Sets the max columns in table.
	 *
	 * @param maxColumnsInTable the new max columns in table
	 */
	public void setMaxColumnsInTable(int maxColumnsInTable) {
		this.maxColumnsInTable = maxColumnsInTable;
	}

	/**
	 * Gets the max connections.
	 *
	 * @return the max connections
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * Sets the max connections.
	 *
	 * @param maxConnections the new max connections
	 */
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	/**
	 * Gets the max cursor name length.
	 *
	 * @return the max cursor name length
	 */
	public int getMaxCursorNameLength() {
		return maxCursorNameLength;
	}

	/**
	 * Sets the max cursor name length.
	 *
	 * @param maxCursorNameLength the new max cursor name length
	 */
	public void setMaxCursorNameLength(int maxCursorNameLength) {
		this.maxCursorNameLength = maxCursorNameLength;
	}

	/**
	 * Gets the max index length.
	 *
	 * @return the max index length
	 */
	public int getMaxIndexLength() {
		return maxIndexLength;
	}

	/**
	 * Sets the max index length.
	 *
	 * @param maxIndexLength the new max index length
	 */
	public void setMaxIndexLength(int maxIndexLength) {
		this.maxIndexLength = maxIndexLength;
	}

	/**
	 * Gets the max schema name length.
	 *
	 * @return the max schema name length
	 */
	public int getMaxSchemaNameLength() {
		return maxSchemaNameLength;
	}

	/**
	 * Sets the max schema name length.
	 *
	 * @param maxSchemaNameLength the new max schema name length
	 */
	public void setMaxSchemaNameLength(int maxSchemaNameLength) {
		this.maxSchemaNameLength = maxSchemaNameLength;
	}

	/**
	 * Gets the max procedure name length.
	 *
	 * @return the max procedure name length
	 */
	public int getMaxProcedureNameLength() {
		return maxProcedureNameLength;
	}

	/**
	 * Sets the max procedure name length.
	 *
	 * @param maxProcedureNameLength the new max procedure name length
	 */
	public void setMaxProcedureNameLength(int maxProcedureNameLength) {
		this.maxProcedureNameLength = maxProcedureNameLength;
	}

	/**
	 * Gets the max catalog name length.
	 *
	 * @return the max catalog name length
	 */
	public int getMaxCatalogNameLength() {
		return maxCatalogNameLength;
	}

	/**
	 * Sets the max catalog name length.
	 *
	 * @param maxCatalogNameLength the new max catalog name length
	 */
	public void setMaxCatalogNameLength(int maxCatalogNameLength) {
		this.maxCatalogNameLength = maxCatalogNameLength;
	}

	/**
	 * Gets the max row size.
	 *
	 * @return the max row size
	 */
	public int getMaxRowSize() {
		return maxRowSize;
	}

	/**
	 * Sets the max row size.
	 *
	 * @param maxRowSize the new max row size
	 */
	public void setMaxRowSize(int maxRowSize) {
		this.maxRowSize = maxRowSize;
	}

	/**
	 * Checks if is max row size include blobs.
	 *
	 * @return true, if is max row size include blobs
	 */
	public boolean isMaxRowSizeIncludeBlobs() {
		return maxRowSizeIncludeBlobs;
	}

	/**
	 * Sets the max row size include blobs.
	 *
	 * @param maxRowSizeIncludeBlobs the new max row size include blobs
	 */
	public void setMaxRowSizeIncludeBlobs(boolean maxRowSizeIncludeBlobs) {
		this.maxRowSizeIncludeBlobs = maxRowSizeIncludeBlobs;
	}

	/**
	 * Gets the max statement length.
	 *
	 * @return the max statement length
	 */
	public int getMaxStatementLength() {
		return maxStatementLength;
	}

	/**
	 * Sets the max statement length.
	 *
	 * @param maxStatementLength the new max statement length
	 */
	public void setMaxStatementLength(int maxStatementLength) {
		this.maxStatementLength = maxStatementLength;
	}

	/**
	 * Gets the max statements.
	 *
	 * @return the max statements
	 */
	public int getMaxStatements() {
		return maxStatements;
	}

	/**
	 * Sets the max statements.
	 *
	 * @param maxStatements the new max statements
	 */
	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}

	/**
	 * Gets the max table name length.
	 *
	 * @return the max table name length
	 */
	public int getMaxTableNameLength() {
		return maxTableNameLength;
	}

	/**
	 * Sets the max table name length.
	 *
	 * @param maxTableNameLength the new max table name length
	 */
	public void setMaxTableNameLength(int maxTableNameLength) {
		this.maxTableNameLength = maxTableNameLength;
	}

	/**
	 * Gets the max tables in select.
	 *
	 * @return the max tables in select
	 */
	public int getMaxTablesInSelect() {
		return maxTablesInSelect;
	}

	/**
	 * Sets the max tables in select.
	 *
	 * @param maxTablesInSelect the new max tables in select
	 */
	public void setMaxTablesInSelect(int maxTablesInSelect) {
		this.maxTablesInSelect = maxTablesInSelect;
	}

	/**
	 * Gets the max user name length.
	 *
	 * @return the max user name length
	 */
	public int getMaxUserNameLength() {
		return maxUserNameLength;
	}

	/**
	 * Sets the max user name length.
	 *
	 * @param maxUserNameLength the new max user name length
	 */
	public void setMaxUserNameLength(int maxUserNameLength) {
		this.maxUserNameLength = maxUserNameLength;
	}

	/**
	 * Gets the default transaction isolation.
	 *
	 * @return the default transaction isolation
	 */
	public int getDefaultTransactionIsolation() {
		return defaultTransactionIsolation;
	}

	/**
	 * Sets the default transaction isolation.
	 *
	 * @param defaultTransactionIsolation the new default transaction isolation
	 */
	public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
		this.defaultTransactionIsolation = defaultTransactionIsolation;
	}

	/**
	 * Checks if is supports transactions.
	 *
	 * @return true, if is supports transactions
	 */
	public boolean isSupportsTransactions() {
		return supportsTransactions;
	}

	/**
	 * Sets the supports transactions.
	 *
	 * @param supportsTransactions the new supports transactions
	 */
	public void setSupportsTransactions(boolean supportsTransactions) {
		this.supportsTransactions = supportsTransactions;
	}

	/**
	 * Checks if is supports data definition and data manipulation transactions.
	 *
	 * @return true, if is supports data definition and data manipulation transactions
	 */
	public boolean isSupportsDataDefinitionAndDataManipulationTransactions() {
		return supportsDataDefinitionAndDataManipulationTransactions;
	}

	/**
	 * Sets the supports data definition and data manipulation transactions.
	 *
	 * @param supportsDataDefinitionAndDataManipulationTransactions the new supports data definition and data manipulation transactions
	 */
	public void setSupportsDataDefinitionAndDataManipulationTransactions(boolean supportsDataDefinitionAndDataManipulationTransactions) {
		this.supportsDataDefinitionAndDataManipulationTransactions = supportsDataDefinitionAndDataManipulationTransactions;
	}

	/**
	 * Checks if is supports data manipulation transactions only.
	 *
	 * @return true, if is supports data manipulation transactions only
	 */
	public boolean isSupportsDataManipulationTransactionsOnly() {
		return supportsDataManipulationTransactionsOnly;
	}

	/**
	 * Sets the supports data manipulation transactions only.
	 *
	 * @param supportsDataManipulationTransactionsOnly the new supports data manipulation transactions only
	 */
	public void setSupportsDataManipulationTransactionsOnly(boolean supportsDataManipulationTransactionsOnly) {
		this.supportsDataManipulationTransactionsOnly = supportsDataManipulationTransactionsOnly;
	}

	/**
	 * Checks if is data definition causes transaction commit.
	 *
	 * @return true, if is data definition causes transaction commit
	 */
	public boolean isDataDefinitionCausesTransactionCommit() {
		return dataDefinitionCausesTransactionCommit;
	}

	/**
	 * Sets the data definition causes transaction commit.
	 *
	 * @param dataDefinitionCausesTransactionCommit the new data definition causes transaction commit
	 */
	public void setDataDefinitionCausesTransactionCommit(boolean dataDefinitionCausesTransactionCommit) {
		this.dataDefinitionCausesTransactionCommit = dataDefinitionCausesTransactionCommit;
	}

	/**
	 * Checks if is data definition ignored in transactions.
	 *
	 * @return true, if is data definition ignored in transactions
	 */
	public boolean isDataDefinitionIgnoredInTransactions() {
		return dataDefinitionIgnoredInTransactions;
	}

	/**
	 * Sets the data definition ignored in transactions.
	 *
	 * @param dataDefinitionIgnoredInTransactions the new data definition ignored in transactions
	 */
	public void setDataDefinitionIgnoredInTransactions(boolean dataDefinitionIgnoredInTransactions) {
		this.dataDefinitionIgnoredInTransactions = dataDefinitionIgnoredInTransactions;
	}

	/**
	 * Checks if is supports batch updates.
	 *
	 * @return true, if is supports batch updates
	 */
	public boolean isSupportsBatchUpdates() {
		return supportsBatchUpdates;
	}

	/**
	 * Sets the supports batch updates.
	 *
	 * @param supportsBatchUpdates the new supports batch updates
	 */
	public void setSupportsBatchUpdates(boolean supportsBatchUpdates) {
		this.supportsBatchUpdates = supportsBatchUpdates;
	}

	/**
	 * Checks if is supports savepoints.
	 *
	 * @return true, if is supports savepoints
	 */
	public boolean isSupportsSavepoints() {
		return supportsSavepoints;
	}

	/**
	 * Sets the supports savepoints.
	 *
	 * @param supportsSavepoints the new supports savepoints
	 */
	public void setSupportsSavepoints(boolean supportsSavepoints) {
		this.supportsSavepoints = supportsSavepoints;
	}

	/**
	 * Checks if is supports named parameters.
	 *
	 * @return true, if is supports named parameters
	 */
	public boolean isSupportsNamedParameters() {
		return supportsNamedParameters;
	}

	/**
	 * Sets the supports named parameters.
	 *
	 * @param supportsNamedParameters the new supports named parameters
	 */
	public void setSupportsNamedParameters(boolean supportsNamedParameters) {
		this.supportsNamedParameters = supportsNamedParameters;
	}

	/**
	 * Checks if is supports multiple open results.
	 *
	 * @return true, if is supports multiple open results
	 */
	public boolean isSupportsMultipleOpenResults() {
		return supportsMultipleOpenResults;
	}

	/**
	 * Sets the supports multiple open results.
	 *
	 * @param supportsMultipleOpenResults the new supports multiple open results
	 */
	public void setSupportsMultipleOpenResults(boolean supportsMultipleOpenResults) {
		this.supportsMultipleOpenResults = supportsMultipleOpenResults;
	}

	/**
	 * Checks if is supports get generated keys.
	 *
	 * @return true, if is supports get generated keys
	 */
	public boolean isSupportsGetGeneratedKeys() {
		return supportsGetGeneratedKeys;
	}

	/**
	 * Sets the supports get generated keys.
	 *
	 * @param supportsGetGeneratedKeys the new supports get generated keys
	 */
	public void setSupportsGetGeneratedKeys(boolean supportsGetGeneratedKeys) {
		this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
	}

	/**
	 * Gets the gets the result set holdability.
	 *
	 * @return the gets the result set holdability
	 */
	public int getGetResultSetHoldability() {
		return getResultSetHoldability;
	}

	/**
	 * Sets the gets the result set holdability.
	 *
	 * @param getResultSetHoldability the new gets the result set holdability
	 */
	public void setGetResultSetHoldability(int getResultSetHoldability) {
		this.getResultSetHoldability = getResultSetHoldability;
	}

	/**
	 * Gets the gets the database major version.
	 *
	 * @return the gets the database major version
	 */
	public int getGetDatabaseMajorVersion() {
		return getDatabaseMajorVersion;
	}

	/**
	 * Sets the gets the database major version.
	 *
	 * @param getDatabaseMajorVersion the new gets the database major version
	 */
	public void setGetDatabaseMajorVersion(int getDatabaseMajorVersion) {
		this.getDatabaseMajorVersion = getDatabaseMajorVersion;
	}

	/**
	 * Gets the gets the database minor version.
	 *
	 * @return the gets the database minor version
	 */
	public int getGetDatabaseMinorVersion() {
		return getDatabaseMinorVersion;
	}

	/**
	 * Sets the gets the database minor version.
	 *
	 * @param getDatabaseMinorVersion the new gets the database minor version
	 */
	public void setGetDatabaseMinorVersion(int getDatabaseMinorVersion) {
		this.getDatabaseMinorVersion = getDatabaseMinorVersion;
	}

	/**
	 * Gets the gets the JDBC major version.
	 *
	 * @return the gets the JDBC major version
	 */
	public int getGetJDBCMajorVersion() {
		return getJDBCMajorVersion;
	}

	/**
	 * Sets the gets the JDBC major version.
	 *
	 * @param getJDBCMajorVersion the new gets the JDBC major version
	 */
	public void setGetJDBCMajorVersion(int getJDBCMajorVersion) {
		this.getJDBCMajorVersion = getJDBCMajorVersion;
	}

	/**
	 * Gets the gets the JDBC minor version.
	 *
	 * @return the gets the JDBC minor version
	 */
	public int getGetJDBCMinorVersion() {
		return getJDBCMinorVersion;
	}

	/**
	 * Sets the gets the JDBC minor version.
	 *
	 * @param getJDBCMinorVersion the new gets the JDBC minor version
	 */
	public void setGetJDBCMinorVersion(int getJDBCMinorVersion) {
		this.getJDBCMinorVersion = getJDBCMinorVersion;
	}

	/**
	 * Gets the gets the SQL state type.
	 *
	 * @return the gets the SQL state type
	 */
	public int getGetSQLStateType() {
		return getSQLStateType;
	}

	/**
	 * Sets the gets the SQL state type.
	 *
	 * @param getSQLStateType the new gets the SQL state type
	 */
	public void setGetSQLStateType(int getSQLStateType) {
		this.getSQLStateType = getSQLStateType;
	}

	/**
	 * Checks if is locators update copy.
	 *
	 * @return true, if is locators update copy
	 */
	public boolean isLocatorsUpdateCopy() {
		return locatorsUpdateCopy;
	}

	/**
	 * Sets the locators update copy.
	 *
	 * @param locatorsUpdateCopy the new locators update copy
	 */
	public void setLocatorsUpdateCopy(boolean locatorsUpdateCopy) {
		this.locatorsUpdateCopy = locatorsUpdateCopy;
	}

	/**
	 * Checks if is supports statement pooling.
	 *
	 * @return true, if is supports statement pooling
	 */
	public boolean isSupportsStatementPooling() {
		return supportsStatementPooling;
	}

	/**
	 * Sets the supports statement pooling.
	 *
	 * @param supportsStatementPooling the new supports statement pooling
	 */
	public void setSupportsStatementPooling(boolean supportsStatementPooling) {
		this.supportsStatementPooling = supportsStatementPooling;
	}

	/**
	 * Checks if is supports stored functions using call syntax.
	 *
	 * @return true, if is supports stored functions using call syntax
	 */
	public boolean isSupportsStoredFunctionsUsingCallSyntax() {
		return supportsStoredFunctionsUsingCallSyntax;
	}

	/**
	 * Sets the supports stored functions using call syntax.
	 *
	 * @param supportsStoredFunctionsUsingCallSyntax the new supports stored functions using call syntax
	 */
	public void setSupportsStoredFunctionsUsingCallSyntax(boolean supportsStoredFunctionsUsingCallSyntax) {
		this.supportsStoredFunctionsUsingCallSyntax = supportsStoredFunctionsUsingCallSyntax;
	}

	/**
	 * Checks if is auto commit failure closes all result sets.
	 *
	 * @return true, if is auto commit failure closes all result sets
	 */
	public boolean isAutoCommitFailureClosesAllResultSets() {
		return autoCommitFailureClosesAllResultSets;
	}

	/**
	 * Sets the auto commit failure closes all result sets.
	 *
	 * @param autoCommitFailureClosesAllResultSets the new auto commit failure closes all result sets
	 */
	public void setAutoCommitFailureClosesAllResultSets(boolean autoCommitFailureClosesAllResultSets) {
		this.autoCommitFailureClosesAllResultSets = autoCommitFailureClosesAllResultSets;
	}

	/**
	 * Checks if is generated key always returned.
	 *
	 * @return true, if is generated key always returned
	 */
	public boolean isGeneratedKeyAlwaysReturned() {
		return generatedKeyAlwaysReturned;
	}

	/**
	 * Sets the generated key always returned.
	 *
	 * @param generatedKeyAlwaysReturned the new generated key always returned
	 */
	public void setGeneratedKeyAlwaysReturned(boolean generatedKeyAlwaysReturned) {
		this.generatedKeyAlwaysReturned = generatedKeyAlwaysReturned;
	}

	/**
	 * Gets the gets the max logical lob size.
	 *
	 * @return the gets the max logical lob size
	 */
	public long getGetMaxLogicalLobSize() {
		return getMaxLogicalLobSize;
	}

	/**
	 * Sets the gets the max logical lob size.
	 *
	 * @param getMaxLogicalLobSize the new gets the max logical lob size
	 */
	public void setGetMaxLogicalLobSize(long getMaxLogicalLobSize) {
		this.getMaxLogicalLobSize = getMaxLogicalLobSize;
	}

	/**
	 * Checks if is supports ref cursors.
	 *
	 * @return true, if is supports ref cursors
	 */
	public boolean isSupportsRefCursors() {
		return supportsRefCursors;
	}

	/**
	 * Sets the supports ref cursors.
	 *
	 * @param supportsRefCursors the new supports ref cursors
	 */
	public void setSupportsRefCursors(boolean supportsRefCursors) {
		this.supportsRefCursors = supportsRefCursors;
	}

	/**
	 * Gets the schemas.
	 *
	 * @return the schemas
	 */
	public List<SchemaMetadata> getSchemas() {
		return schemas;
	}

	// public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
	// return databaseMetaData.supportsTransactionIsolationLevel(level);
	// }
	//
	// public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
	// throws SQLException {
	// return databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern);
	// }
	//
	// public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern,
	// String columnNamePattern) throws SQLException {
	// return databaseMetaData.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
	// }
	//
	// public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
	// throws SQLException {
	// return databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types);
	// }
	//
	// public ResultSet getSchemas() throws SQLException {
	// return databaseMetaData.getSchemas();
	// }
	//
	// public ResultSet getCatalogs() throws SQLException {
	// return databaseMetaData.getCatalogs();
	// }
	//
	// public ResultSet getTableTypes() throws SQLException {
	// return databaseMetaData.getTableTypes();
	// }

	// public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String
	// columnNamePattern)
	// throws SQLException {
	// return databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	// }
	//
	// public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
	// throws SQLException {
	// return databaseMetaData.getColumnPrivileges(catalog, schema, table, columnNamePattern);
	// }
	//
	// public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
	// throws SQLException {
	// return databaseMetaData.getTablePrivileges(catalog, schemaPattern, tableNamePattern);
	// }
	//
	// public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
	// throws SQLException {
	// return databaseMetaData.getBestRowIdentifier(catalog, schema, table, scope, nullable);
	// }
	//
	// public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
	// return databaseMetaData.getVersionColumns(catalog, schema, table);
	// }
	//
	// public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
	// return databaseMetaData.getPrimaryKeys(catalog, schema, table);
	// }
	//
	// public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
	// return databaseMetaData.getImportedKeys(catalog, schema, table);
	// }
	//
	// public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
	// return databaseMetaData.getExportedKeys(catalog, schema, table);
	// }
	//
	// public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable,
	// String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
	// return databaseMetaData.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog,
	// foreignSchema, foreignTable);
	// }
	//
	// public ResultSet getTypeInfo() throws SQLException {
	// return databaseMetaData.getTypeInfo();
	// }
	//
	// public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
	// throws SQLException {
	// return databaseMetaData.getIndexInfo(catalog, schema, table, unique, approximate);
	// }
	//
	// public boolean supportsResultSetType(int type) throws SQLException {
	// return databaseMetaData.supportsResultSetType(type);
	// }
	//
	// public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
	// return databaseMetaData.supportsResultSetConcurrency(type, concurrency);
	// }
	//
	// public boolean ownUpdatesAreVisible(int type) throws SQLException {
	// return databaseMetaData.ownUpdatesAreVisible(type);
	// }
	//
	// public boolean ownDeletesAreVisible(int type) throws SQLException {
	// return databaseMetaData.ownDeletesAreVisible(type);
	// }
	//
	// public boolean ownInsertsAreVisible(int type) throws SQLException {
	// return databaseMetaData.ownInsertsAreVisible(type);
	// }
	//
	// public boolean othersUpdatesAreVisible(int type) throws SQLException {
	// return databaseMetaData.othersUpdatesAreVisible(type);
	// }
	//
	// public boolean othersDeletesAreVisible(int type) throws SQLException {
	// return databaseMetaData.othersDeletesAreVisible(type);
	// }
	//
	// public boolean othersInsertsAreVisible(int type) throws SQLException {
	// return databaseMetaData.othersInsertsAreVisible(type);
	// }
	//
	// public boolean updatesAreDetected(int type) throws SQLException {
	// return databaseMetaData.updatesAreDetected(type);
	// }
	//
	// public boolean deletesAreDetected(int type) throws SQLException {
	// return databaseMetaData.deletesAreDetected(type);
	// }
	//
	// public boolean insertsAreDetected(int type) throws SQLException {
	// return databaseMetaData.insertsAreDetected(type);
	// }
	//
	// public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
	// throws SQLException {
	// return databaseMetaData.getUDTs(catalog, schemaPattern, typeNamePattern, types);
	// }
	//
	// public Connection getConnection() throws SQLException {
	// return databaseMetaData.getConnection();
	// }
	//
	// public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException
	// {
	// return databaseMetaData.getSuperTypes(catalog, schemaPattern, typeNamePattern);
	// }
	//
	// public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws
	// SQLException {
	// return databaseMetaData.getSuperTables(catalog, schemaPattern, tableNamePattern);
	// }
	//
	// public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern,
	// String attributeNamePattern) throws SQLException {
	// return databaseMetaData.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern);
	// }
	//
	// public boolean supportsResultSetHoldability(int holdability) throws SQLException {
	// return databaseMetaData.supportsResultSetHoldability(holdability);
	// }
	//
	// public RowIdLifetime getRowIdLifetime() throws SQLException {
	// return databaseMetaData.getRowIdLifetime();
	// }
	//
	// public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
	// return databaseMetaData.getSchemas(catalog, schemaPattern);
	// }
	//
	// public ResultSet getClientInfoProperties() throws SQLException {
	// return databaseMetaData.getClientInfoProperties();
	// }
	//
	// public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
	// throws SQLException {
	// return databaseMetaData.getFunctions(catalog, schemaPattern, functionNamePattern);
	// }
	//
	// public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
	// String columnNamePattern) throws SQLException {
	// return databaseMetaData.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
	// }
	//
	// public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern,
	// String columnNamePattern) throws SQLException {
	// return databaseMetaData.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
	// }

	/**
	 * Gets the kind.
	 *
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * Sets the kind.
	 *
	 * @param kind the new kind
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

}
