package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.Filter;

public class DatabaseMetadata {

	private boolean allProceduresAreCallable;
	private boolean allTablesAreSelectable;
	private String getURL;
	private String getUserName;
	private boolean isReadOnly;
	private boolean nullsAreSortedHigh;
	private boolean nullsAreSortedLow;
	private boolean nullsAreSortedAtStart;
	private boolean nullsAreSortedAtEnd;
	private String databaseProductName;
	private String databaseProductVersion;
	private String driverName;
	private String driverVersion;
	private int driverMajorVersion;
	private int driverMinorVersion;
	private boolean usesLocalFiles;
	private boolean usesLocalFilePerTable;
	private boolean supportsMixedCaseIdentifiers;
	private boolean storesUpperCaseIdentifiers;
	private boolean storesLowerCaseIdentifiers;
	private boolean storesMixedCaseIdentifiers;
	private boolean supportsMixedCaseQuotedIdentifiers;
	private boolean storesUpperCaseQuotedIdentifiers;
	private boolean storesLowerCaseQuotedIdentifiers;
	private boolean storesMixedCaseQuotedIdentifiers;
	private String identifierQuoteString;
	private String sqlKeywords;
	private String numericFunctions;
	private String stringFunctions;
	private String systemFunctions;
	private String timeDateFunctions;
	private String searchStringEscape;
	private String extraNameCharacters;
	private boolean supportsAlterTableWithAddColumn;
	private boolean supportsAlterTableWithDropColumn;
	private boolean supportsColumnAliasing;
	private boolean nullPlusNonNullIsNull;
	private boolean supportsConvert;
	private boolean supportsTableCorrelationNames;
	private boolean supportsDifferentTableCorrelationNames;
	private boolean supportsExpressionsInOrderBy;
	private boolean supportsOrderByUnrelated;
	private boolean supportsGroupBy;
	private boolean supportsGroupByUnrelated;
	private boolean supportsGroupByBeyondSelect;
	private boolean supportsLikeEscapeClause;
	private boolean supportsMultipleResultSets;
	private boolean supportsMultipleTransactions;
	private boolean supportsNonNullableColumns;
	private boolean supportsMinimumSQLGrammar;
	private boolean supportsCoreSQLGrammar;
	private boolean supportsExtendedSQLGrammar;
	private boolean supportsANSI92EntryLevelSQL;
	private boolean supportsANSI92IntermediateSQL;
	private boolean supportsANSI92FullSQL;
	private boolean supportsIntegrityEnhancementFacility;
	private boolean supportsOuterJoins;
	private boolean supportsFullOuterJoins;
	private boolean supportsLimitedOuterJoins;
	private String schemaTerm;
	private String procedureTerm;
	private String catalogTerm;
	private boolean isCatalogAtStart;
	private String getCatalogSeparator;
	private boolean supportsSchemasInDataManipulation;
	private boolean supportsSchemasInProcedureCalls;
	private boolean supportsSchemasInTableDefinitions;
	private boolean supportsSchemasInIndexDefinitions;
	private boolean supportsSchemasInPrivilegeDefinitions;
	private boolean supportsCatalogsInDataManipulation;
	private boolean supportsCatalogsInProcedureCalls;
	private boolean supportsCatalogsInTableDefinitions;
	private boolean supportsCatalogsInIndexDefinitions;
	private boolean supportsCatalogsInPrivilegeDefinitions;
	private boolean supportsPositionedDelete;
	private boolean supportsPositionedUpdate;
	private boolean supportsSelectForUpdate;
	private boolean supportsStoredProcedures;
	private boolean supportsSubqueriesInComparisons;
	private boolean supportsSubqueriesInExists;
	private boolean supportsSubqueriesInIns;
	private boolean supportsSubqueriesInQuantifieds;
	private boolean supportsCorrelatedSubqueries;
	private boolean supportsUnion;
	private boolean supportsUnionAll;
	private boolean supportsOpenCursorsAcrossCommit;
	private boolean supportsOpenCursorsAcrossRollback;
	private boolean supportsOpenStatementsAcrossCommit;
	private boolean supportsOpenStatementsAcrossRollback;
	private int maxBinaryLiteralLength;
	private int maxCharLiteralLength;
	private int maxColumnNameLength;
	private int maxColumnsInGroupBy;
	private int maxColumnsInIndex;
	private int maxColumnsInOrderBy;
	private int maxColumnsInSelect;
	private int maxColumnsInTable;
	private int maxConnections;
	private int maxCursorNameLength;
	private int maxIndexLength;
	private int maxSchemaNameLength;
	private int maxProcedureNameLength;
	private int maxCatalogNameLength;
	private int maxRowSize;
	private boolean maxRowSizeIncludeBlobs;
	private int maxStatementLength;
	private int maxStatements;
	private int maxTableNameLength;
	private int maxTablesInSelect;
	private int maxUserNameLength;
	private int defaultTransactionIsolation;
	private boolean supportsTransactions;
	private boolean supportsDataDefinitionAndDataManipulationTransactions;
	private boolean supportsDataManipulationTransactionsOnly;
	private boolean dataDefinitionCausesTransactionCommit;
	private boolean dataDefinitionIgnoredInTransactions;
	private boolean supportsBatchUpdates;
	private boolean supportsSavepoints;
	private boolean supportsNamedParameters;
	private boolean supportsMultipleOpenResults;
	private boolean supportsGetGeneratedKeys;
	private int getResultSetHoldability;
	private int getDatabaseMajorVersion;
	private int getDatabaseMinorVersion;
	private int getJDBCMajorVersion;
	private int getJDBCMinorVersion;
	private int getSQLStateType;
	private boolean locatorsUpdateCopy;
	private boolean supportsStatementPooling;
	private boolean supportsStoredFunctionsUsingCallSyntax;
	private boolean autoCommitFailureClosesAllResultSets;
	private boolean generatedKeyAlwaysReturned;
	private long getMaxLogicalLobSize;
	private boolean supportsRefCursors;

	private List<SchemaMetadata> schemas;

	private String kind = "database";

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

	public boolean isAllProceduresAreCallable() {
		return allProceduresAreCallable;
	}

	public void setAllProceduresAreCallable(boolean allProceduresAreCallable) {
		this.allProceduresAreCallable = allProceduresAreCallable;
	}

	public boolean isAllTablesAreSelectable() {
		return allTablesAreSelectable;
	}

	public void setAllTablesAreSelectable(boolean allTablesAreSelectable) {
		this.allTablesAreSelectable = allTablesAreSelectable;
	}

	public String getGetURL() {
		return getURL;
	}

	public void setGetURL(String getURL) {
		this.getURL = getURL;
	}

	public String getGetUserName() {
		return getUserName;
	}

	public void setGetUserName(String getUserName) {
		this.getUserName = getUserName;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean isNullsAreSortedHigh() {
		return nullsAreSortedHigh;
	}

	public void setNullsAreSortedHigh(boolean nullsAreSortedHigh) {
		this.nullsAreSortedHigh = nullsAreSortedHigh;
	}

	public boolean isNullsAreSortedLow() {
		return nullsAreSortedLow;
	}

	public void setNullsAreSortedLow(boolean nullsAreSortedLow) {
		this.nullsAreSortedLow = nullsAreSortedLow;
	}

	public boolean isNullsAreSortedAtStart() {
		return nullsAreSortedAtStart;
	}

	public void setNullsAreSortedAtStart(boolean nullsAreSortedAtStart) {
		this.nullsAreSortedAtStart = nullsAreSortedAtStart;
	}

	public boolean isNullsAreSortedAtEnd() {
		return nullsAreSortedAtEnd;
	}

	public void setNullsAreSortedAtEnd(boolean nullsAreSortedAtEnd) {
		this.nullsAreSortedAtEnd = nullsAreSortedAtEnd;
	}

	public String getDatabaseProductName() {
		return databaseProductName;
	}

	public void setDatabaseProductName(String databaseProductName) {
		this.databaseProductName = databaseProductName;
	}

	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	public void setDatabaseProductVersion(String databaseProductVersion) {
		this.databaseProductVersion = databaseProductVersion;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}

	public int getDriverMajorVersion() {
		return driverMajorVersion;
	}

	public void setDriverMajorVersion(int driverMajorVersion) {
		this.driverMajorVersion = driverMajorVersion;
	}

	public int getDriverMinorVersion() {
		return driverMinorVersion;
	}

	public void setDriverMinorVersion(int driverMinorVersion) {
		this.driverMinorVersion = driverMinorVersion;
	}

	public boolean isUsesLocalFiles() {
		return usesLocalFiles;
	}

	public void setUsesLocalFiles(boolean usesLocalFiles) {
		this.usesLocalFiles = usesLocalFiles;
	}

	public boolean isUsesLocalFilePerTable() {
		return usesLocalFilePerTable;
	}

	public void setUsesLocalFilePerTable(boolean usesLocalFilePerTable) {
		this.usesLocalFilePerTable = usesLocalFilePerTable;
	}

	public boolean isSupportsMixedCaseIdentifiers() {
		return supportsMixedCaseIdentifiers;
	}

	public void setSupportsMixedCaseIdentifiers(boolean supportsMixedCaseIdentifiers) {
		this.supportsMixedCaseIdentifiers = supportsMixedCaseIdentifiers;
	}

	public boolean isStoresUpperCaseIdentifiers() {
		return storesUpperCaseIdentifiers;
	}

	public void setStoresUpperCaseIdentifiers(boolean storesUpperCaseIdentifiers) {
		this.storesUpperCaseIdentifiers = storesUpperCaseIdentifiers;
	}

	public boolean isStoresLowerCaseIdentifiers() {
		return storesLowerCaseIdentifiers;
	}

	public void setStoresLowerCaseIdentifiers(boolean storesLowerCaseIdentifiers) {
		this.storesLowerCaseIdentifiers = storesLowerCaseIdentifiers;
	}

	public boolean isStoresMixedCaseIdentifiers() {
		return storesMixedCaseIdentifiers;
	}

	public void setStoresMixedCaseIdentifiers(boolean storesMixedCaseIdentifiers) {
		this.storesMixedCaseIdentifiers = storesMixedCaseIdentifiers;
	}

	public boolean isSupportsMixedCaseQuotedIdentifiers() {
		return supportsMixedCaseQuotedIdentifiers;
	}

	public void setSupportsMixedCaseQuotedIdentifiers(boolean supportsMixedCaseQuotedIdentifiers) {
		this.supportsMixedCaseQuotedIdentifiers = supportsMixedCaseQuotedIdentifiers;
	}

	public boolean isStoresUpperCaseQuotedIdentifiers() {
		return storesUpperCaseQuotedIdentifiers;
	}

	public void setStoresUpperCaseQuotedIdentifiers(boolean storesUpperCaseQuotedIdentifiers) {
		this.storesUpperCaseQuotedIdentifiers = storesUpperCaseQuotedIdentifiers;
	}

	public boolean isStoresLowerCaseQuotedIdentifiers() {
		return storesLowerCaseQuotedIdentifiers;
	}

	public void setStoresLowerCaseQuotedIdentifiers(boolean storesLowerCaseQuotedIdentifiers) {
		this.storesLowerCaseQuotedIdentifiers = storesLowerCaseQuotedIdentifiers;
	}

	public boolean isStoresMixedCaseQuotedIdentifiers() {
		return storesMixedCaseQuotedIdentifiers;
	}

	public void setStoresMixedCaseQuotedIdentifiers(boolean storesMixedCaseQuotedIdentifiers) {
		this.storesMixedCaseQuotedIdentifiers = storesMixedCaseQuotedIdentifiers;
	}

	public String getIdentifierQuoteString() {
		return identifierQuoteString;
	}

	public void setIdentifierQuoteString(String identifierQuoteString) {
		this.identifierQuoteString = identifierQuoteString;
	}

	public String getSqlKeywords() {
		return sqlKeywords;
	}

	public void setSqlKeywords(String sqlKeywords) {
		this.sqlKeywords = sqlKeywords;
	}

	public String getNumericFunctions() {
		return numericFunctions;
	}

	public void setNumericFunctions(String numericFunctions) {
		this.numericFunctions = numericFunctions;
	}

	public String getStringFunctions() {
		return stringFunctions;
	}

	public void setStringFunctions(String stringFunctions) {
		this.stringFunctions = stringFunctions;
	}

	public String getSystemFunctions() {
		return systemFunctions;
	}

	public void setSystemFunctions(String systemFunctions) {
		this.systemFunctions = systemFunctions;
	}

	public String getTimeDateFunctions() {
		return timeDateFunctions;
	}

	public void setTimeDateFunctions(String timeDateFunctions) {
		this.timeDateFunctions = timeDateFunctions;
	}

	public String getSearchStringEscape() {
		return searchStringEscape;
	}

	public void setSearchStringEscape(String searchStringEscape) {
		this.searchStringEscape = searchStringEscape;
	}

	public String getExtraNameCharacters() {
		return extraNameCharacters;
	}

	public void setExtraNameCharacters(String extraNameCharacters) {
		this.extraNameCharacters = extraNameCharacters;
	}

	public boolean isSupportsAlterTableWithAddColumn() {
		return supportsAlterTableWithAddColumn;
	}

	public void setSupportsAlterTableWithAddColumn(boolean supportsAlterTableWithAddColumn) {
		this.supportsAlterTableWithAddColumn = supportsAlterTableWithAddColumn;
	}

	public boolean isSupportsAlterTableWithDropColumn() {
		return supportsAlterTableWithDropColumn;
	}

	public void setSupportsAlterTableWithDropColumn(boolean supportsAlterTableWithDropColumn) {
		this.supportsAlterTableWithDropColumn = supportsAlterTableWithDropColumn;
	}

	public boolean isSupportsColumnAliasing() {
		return supportsColumnAliasing;
	}

	public void setSupportsColumnAliasing(boolean supportsColumnAliasing) {
		this.supportsColumnAliasing = supportsColumnAliasing;
	}

	public boolean isNullPlusNonNullIsNull() {
		return nullPlusNonNullIsNull;
	}

	public void setNullPlusNonNullIsNull(boolean nullPlusNonNullIsNull) {
		this.nullPlusNonNullIsNull = nullPlusNonNullIsNull;
	}

	public boolean isSupportsConvert() {
		return supportsConvert;
	}

	public void setSupportsConvert(boolean supportsConvert) {
		this.supportsConvert = supportsConvert;
	}

	public boolean isSupportsTableCorrelationNames() {
		return supportsTableCorrelationNames;
	}

	public void setSupportsTableCorrelationNames(boolean supportsTableCorrelationNames) {
		this.supportsTableCorrelationNames = supportsTableCorrelationNames;
	}

	public boolean isSupportsDifferentTableCorrelationNames() {
		return supportsDifferentTableCorrelationNames;
	}

	public void setSupportsDifferentTableCorrelationNames(boolean supportsDifferentTableCorrelationNames) {
		this.supportsDifferentTableCorrelationNames = supportsDifferentTableCorrelationNames;
	}

	public boolean isSupportsExpressionsInOrderBy() {
		return supportsExpressionsInOrderBy;
	}

	public void setSupportsExpressionsInOrderBy(boolean supportsExpressionsInOrderBy) {
		this.supportsExpressionsInOrderBy = supportsExpressionsInOrderBy;
	}

	public boolean isSupportsOrderByUnrelated() {
		return supportsOrderByUnrelated;
	}

	public void setSupportsOrderByUnrelated(boolean supportsOrderByUnrelated) {
		this.supportsOrderByUnrelated = supportsOrderByUnrelated;
	}

	public boolean isSupportsGroupBy() {
		return supportsGroupBy;
	}

	public void setSupportsGroupBy(boolean supportsGroupBy) {
		this.supportsGroupBy = supportsGroupBy;
	}

	public boolean isSupportsGroupByUnrelated() {
		return supportsGroupByUnrelated;
	}

	public void setSupportsGroupByUnrelated(boolean supportsGroupByUnrelated) {
		this.supportsGroupByUnrelated = supportsGroupByUnrelated;
	}

	public boolean isSupportsGroupByBeyondSelect() {
		return supportsGroupByBeyondSelect;
	}

	public void setSupportsGroupByBeyondSelect(boolean supportsGroupByBeyondSelect) {
		this.supportsGroupByBeyondSelect = supportsGroupByBeyondSelect;
	}

	public boolean isSupportsLikeEscapeClause() {
		return supportsLikeEscapeClause;
	}

	public void setSupportsLikeEscapeClause(boolean supportsLikeEscapeClause) {
		this.supportsLikeEscapeClause = supportsLikeEscapeClause;
	}

	public boolean isSupportsMultipleResultSets() {
		return supportsMultipleResultSets;
	}

	public void setSupportsMultipleResultSets(boolean supportsMultipleResultSets) {
		this.supportsMultipleResultSets = supportsMultipleResultSets;
	}

	public boolean isSupportsMultipleTransactions() {
		return supportsMultipleTransactions;
	}

	public void setSupportsMultipleTransactions(boolean supportsMultipleTransactions) {
		this.supportsMultipleTransactions = supportsMultipleTransactions;
	}

	public boolean isSupportsNonNullableColumns() {
		return supportsNonNullableColumns;
	}

	public void setSupportsNonNullableColumns(boolean supportsNonNullableColumns) {
		this.supportsNonNullableColumns = supportsNonNullableColumns;
	}

	public boolean isSupportsMinimumSQLGrammar() {
		return supportsMinimumSQLGrammar;
	}

	public void setSupportsMinimumSQLGrammar(boolean supportsMinimumSQLGrammar) {
		this.supportsMinimumSQLGrammar = supportsMinimumSQLGrammar;
	}

	public boolean isSupportsCoreSQLGrammar() {
		return supportsCoreSQLGrammar;
	}

	public void setSupportsCoreSQLGrammar(boolean supportsCoreSQLGrammar) {
		this.supportsCoreSQLGrammar = supportsCoreSQLGrammar;
	}

	public boolean isSupportsExtendedSQLGrammar() {
		return supportsExtendedSQLGrammar;
	}

	public void setSupportsExtendedSQLGrammar(boolean supportsExtendedSQLGrammar) {
		this.supportsExtendedSQLGrammar = supportsExtendedSQLGrammar;
	}

	public boolean isSupportsANSI92EntryLevelSQL() {
		return supportsANSI92EntryLevelSQL;
	}

	public void setSupportsANSI92EntryLevelSQL(boolean supportsANSI92EntryLevelSQL) {
		this.supportsANSI92EntryLevelSQL = supportsANSI92EntryLevelSQL;
	}

	public boolean isSupportsANSI92IntermediateSQL() {
		return supportsANSI92IntermediateSQL;
	}

	public void setSupportsANSI92IntermediateSQL(boolean supportsANSI92IntermediateSQL) {
		this.supportsANSI92IntermediateSQL = supportsANSI92IntermediateSQL;
	}

	public boolean isSupportsANSI92FullSQL() {
		return supportsANSI92FullSQL;
	}

	public void setSupportsANSI92FullSQL(boolean supportsANSI92FullSQL) {
		this.supportsANSI92FullSQL = supportsANSI92FullSQL;
	}

	public boolean isSupportsIntegrityEnhancementFacility() {
		return supportsIntegrityEnhancementFacility;
	}

	public void setSupportsIntegrityEnhancementFacility(boolean supportsIntegrityEnhancementFacility) {
		this.supportsIntegrityEnhancementFacility = supportsIntegrityEnhancementFacility;
	}

	public boolean isSupportsOuterJoins() {
		return supportsOuterJoins;
	}

	public void setSupportsOuterJoins(boolean supportsOuterJoins) {
		this.supportsOuterJoins = supportsOuterJoins;
	}

	public boolean isSupportsFullOuterJoins() {
		return supportsFullOuterJoins;
	}

	public void setSupportsFullOuterJoins(boolean supportsFullOuterJoins) {
		this.supportsFullOuterJoins = supportsFullOuterJoins;
	}

	public boolean isSupportsLimitedOuterJoins() {
		return supportsLimitedOuterJoins;
	}

	public void setSupportsLimitedOuterJoins(boolean supportsLimitedOuterJoins) {
		this.supportsLimitedOuterJoins = supportsLimitedOuterJoins;
	}

	public String getSchemaTerm() {
		return schemaTerm;
	}

	public void setSchemaTerm(String schemaTerm) {
		this.schemaTerm = schemaTerm;
	}

	public String getProcedureTerm() {
		return procedureTerm;
	}

	public void setProcedureTerm(String procedureTerm) {
		this.procedureTerm = procedureTerm;
	}

	public String getCatalogTerm() {
		return catalogTerm;
	}

	public void setCatalogTerm(String catalogTerm) {
		this.catalogTerm = catalogTerm;
	}

	public boolean isCatalogAtStart() {
		return isCatalogAtStart;
	}

	public void setCatalogAtStart(boolean isCatalogAtStart) {
		this.isCatalogAtStart = isCatalogAtStart;
	}

	public String getGetCatalogSeparator() {
		return getCatalogSeparator;
	}

	public void setGetCatalogSeparator(String getCatalogSeparator) {
		this.getCatalogSeparator = getCatalogSeparator;
	}

	public boolean isSupportsSchemasInDataManipulation() {
		return supportsSchemasInDataManipulation;
	}

	public void setSupportsSchemasInDataManipulation(boolean supportsSchemasInDataManipulation) {
		this.supportsSchemasInDataManipulation = supportsSchemasInDataManipulation;
	}

	public boolean isSupportsSchemasInProcedureCalls() {
		return supportsSchemasInProcedureCalls;
	}

	public void setSupportsSchemasInProcedureCalls(boolean supportsSchemasInProcedureCalls) {
		this.supportsSchemasInProcedureCalls = supportsSchemasInProcedureCalls;
	}

	public boolean isSupportsSchemasInTableDefinitions() {
		return supportsSchemasInTableDefinitions;
	}

	public void setSupportsSchemasInTableDefinitions(boolean supportsSchemasInTableDefinitions) {
		this.supportsSchemasInTableDefinitions = supportsSchemasInTableDefinitions;
	}

	public boolean isSupportsSchemasInIndexDefinitions() {
		return supportsSchemasInIndexDefinitions;
	}

	public void setSupportsSchemasInIndexDefinitions(boolean supportsSchemasInIndexDefinitions) {
		this.supportsSchemasInIndexDefinitions = supportsSchemasInIndexDefinitions;
	}

	public boolean isSupportsSchemasInPrivilegeDefinitions() {
		return supportsSchemasInPrivilegeDefinitions;
	}

	public void setSupportsSchemasInPrivilegeDefinitions(boolean supportsSchemasInPrivilegeDefinitions) {
		this.supportsSchemasInPrivilegeDefinitions = supportsSchemasInPrivilegeDefinitions;
	}

	public boolean isSupportsCatalogsInDataManipulation() {
		return supportsCatalogsInDataManipulation;
	}

	public void setSupportsCatalogsInDataManipulation(boolean supportsCatalogsInDataManipulation) {
		this.supportsCatalogsInDataManipulation = supportsCatalogsInDataManipulation;
	}

	public boolean isSupportsCatalogsInProcedureCalls() {
		return supportsCatalogsInProcedureCalls;
	}

	public void setSupportsCatalogsInProcedureCalls(boolean supportsCatalogsInProcedureCalls) {
		this.supportsCatalogsInProcedureCalls = supportsCatalogsInProcedureCalls;
	}

	public boolean isSupportsCatalogsInTableDefinitions() {
		return supportsCatalogsInTableDefinitions;
	}

	public void setSupportsCatalogsInTableDefinitions(boolean supportsCatalogsInTableDefinitions) {
		this.supportsCatalogsInTableDefinitions = supportsCatalogsInTableDefinitions;
	}

	public boolean isSupportsCatalogsInIndexDefinitions() {
		return supportsCatalogsInIndexDefinitions;
	}

	public void setSupportsCatalogsInIndexDefinitions(boolean supportsCatalogsInIndexDefinitions) {
		this.supportsCatalogsInIndexDefinitions = supportsCatalogsInIndexDefinitions;
	}

	public boolean isSupportsCatalogsInPrivilegeDefinitions() {
		return supportsCatalogsInPrivilegeDefinitions;
	}

	public void setSupportsCatalogsInPrivilegeDefinitions(boolean supportsCatalogsInPrivilegeDefinitions) {
		this.supportsCatalogsInPrivilegeDefinitions = supportsCatalogsInPrivilegeDefinitions;
	}

	public boolean isSupportsPositionedDelete() {
		return supportsPositionedDelete;
	}

	public void setSupportsPositionedDelete(boolean supportsPositionedDelete) {
		this.supportsPositionedDelete = supportsPositionedDelete;
	}

	public boolean isSupportsPositionedUpdate() {
		return supportsPositionedUpdate;
	}

	public void setSupportsPositionedUpdate(boolean supportsPositionedUpdate) {
		this.supportsPositionedUpdate = supportsPositionedUpdate;
	}

	public boolean isSupportsSelectForUpdate() {
		return supportsSelectForUpdate;
	}

	public void setSupportsSelectForUpdate(boolean supportsSelectForUpdate) {
		this.supportsSelectForUpdate = supportsSelectForUpdate;
	}

	public boolean isSupportsStoredProcedures() {
		return supportsStoredProcedures;
	}

	public void setSupportsStoredProcedures(boolean supportsStoredProcedures) {
		this.supportsStoredProcedures = supportsStoredProcedures;
	}

	public boolean isSupportsSubqueriesInComparisons() {
		return supportsSubqueriesInComparisons;
	}

	public void setSupportsSubqueriesInComparisons(boolean supportsSubqueriesInComparisons) {
		this.supportsSubqueriesInComparisons = supportsSubqueriesInComparisons;
	}

	public boolean isSupportsSubqueriesInExists() {
		return supportsSubqueriesInExists;
	}

	public void setSupportsSubqueriesInExists(boolean supportsSubqueriesInExists) {
		this.supportsSubqueriesInExists = supportsSubqueriesInExists;
	}

	public boolean isSupportsSubqueriesInIns() {
		return supportsSubqueriesInIns;
	}

	public void setSupportsSubqueriesInIns(boolean supportsSubqueriesInIns) {
		this.supportsSubqueriesInIns = supportsSubqueriesInIns;
	}

	public boolean isSupportsSubqueriesInQuantifieds() {
		return supportsSubqueriesInQuantifieds;
	}

	public void setSupportsSubqueriesInQuantifieds(boolean supportsSubqueriesInQuantifieds) {
		this.supportsSubqueriesInQuantifieds = supportsSubqueriesInQuantifieds;
	}

	public boolean isSupportsCorrelatedSubqueries() {
		return supportsCorrelatedSubqueries;
	}

	public void setSupportsCorrelatedSubqueries(boolean supportsCorrelatedSubqueries) {
		this.supportsCorrelatedSubqueries = supportsCorrelatedSubqueries;
	}

	public boolean isSupportsUnion() {
		return supportsUnion;
	}

	public void setSupportsUnion(boolean supportsUnion) {
		this.supportsUnion = supportsUnion;
	}

	public boolean isSupportsUnionAll() {
		return supportsUnionAll;
	}

	public void setSupportsUnionAll(boolean supportsUnionAll) {
		this.supportsUnionAll = supportsUnionAll;
	}

	public boolean isSupportsOpenCursorsAcrossCommit() {
		return supportsOpenCursorsAcrossCommit;
	}

	public void setSupportsOpenCursorsAcrossCommit(boolean supportsOpenCursorsAcrossCommit) {
		this.supportsOpenCursorsAcrossCommit = supportsOpenCursorsAcrossCommit;
	}

	public boolean isSupportsOpenCursorsAcrossRollback() {
		return supportsOpenCursorsAcrossRollback;
	}

	public void setSupportsOpenCursorsAcrossRollback(boolean supportsOpenCursorsAcrossRollback) {
		this.supportsOpenCursorsAcrossRollback = supportsOpenCursorsAcrossRollback;
	}

	public boolean isSupportsOpenStatementsAcrossCommit() {
		return supportsOpenStatementsAcrossCommit;
	}

	public void setSupportsOpenStatementsAcrossCommit(boolean supportsOpenStatementsAcrossCommit) {
		this.supportsOpenStatementsAcrossCommit = supportsOpenStatementsAcrossCommit;
	}

	public boolean isSupportsOpenStatementsAcrossRollback() {
		return supportsOpenStatementsAcrossRollback;
	}

	public void setSupportsOpenStatementsAcrossRollback(boolean supportsOpenStatementsAcrossRollback) {
		this.supportsOpenStatementsAcrossRollback = supportsOpenStatementsAcrossRollback;
	}

	public int getMaxBinaryLiteralLength() {
		return maxBinaryLiteralLength;
	}

	public void setMaxBinaryLiteralLength(int maxBinaryLiteralLength) {
		this.maxBinaryLiteralLength = maxBinaryLiteralLength;
	}

	public int getMaxCharLiteralLength() {
		return maxCharLiteralLength;
	}

	public void setMaxCharLiteralLength(int maxCharLiteralLength) {
		this.maxCharLiteralLength = maxCharLiteralLength;
	}

	public int getMaxColumnNameLength() {
		return maxColumnNameLength;
	}

	public void setMaxColumnNameLength(int maxColumnNameLength) {
		this.maxColumnNameLength = maxColumnNameLength;
	}

	public int getMaxColumnsInGroupBy() {
		return maxColumnsInGroupBy;
	}

	public void setMaxColumnsInGroupBy(int maxColumnsInGroupBy) {
		this.maxColumnsInGroupBy = maxColumnsInGroupBy;
	}

	public int getMaxColumnsInIndex() {
		return maxColumnsInIndex;
	}

	public void setMaxColumnsInIndex(int maxColumnsInIndex) {
		this.maxColumnsInIndex = maxColumnsInIndex;
	}

	public int getMaxColumnsInOrderBy() {
		return maxColumnsInOrderBy;
	}

	public void setMaxColumnsInOrderBy(int maxColumnsInOrderBy) {
		this.maxColumnsInOrderBy = maxColumnsInOrderBy;
	}

	public int getMaxColumnsInSelect() {
		return maxColumnsInSelect;
	}

	public void setMaxColumnsInSelect(int maxColumnsInSelect) {
		this.maxColumnsInSelect = maxColumnsInSelect;
	}

	public int getMaxColumnsInTable() {
		return maxColumnsInTable;
	}

	public void setMaxColumnsInTable(int maxColumnsInTable) {
		this.maxColumnsInTable = maxColumnsInTable;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMaxCursorNameLength() {
		return maxCursorNameLength;
	}

	public void setMaxCursorNameLength(int maxCursorNameLength) {
		this.maxCursorNameLength = maxCursorNameLength;
	}

	public int getMaxIndexLength() {
		return maxIndexLength;
	}

	public void setMaxIndexLength(int maxIndexLength) {
		this.maxIndexLength = maxIndexLength;
	}

	public int getMaxSchemaNameLength() {
		return maxSchemaNameLength;
	}

	public void setMaxSchemaNameLength(int maxSchemaNameLength) {
		this.maxSchemaNameLength = maxSchemaNameLength;
	}

	public int getMaxProcedureNameLength() {
		return maxProcedureNameLength;
	}

	public void setMaxProcedureNameLength(int maxProcedureNameLength) {
		this.maxProcedureNameLength = maxProcedureNameLength;
	}

	public int getMaxCatalogNameLength() {
		return maxCatalogNameLength;
	}

	public void setMaxCatalogNameLength(int maxCatalogNameLength) {
		this.maxCatalogNameLength = maxCatalogNameLength;
	}

	public int getMaxRowSize() {
		return maxRowSize;
	}

	public void setMaxRowSize(int maxRowSize) {
		this.maxRowSize = maxRowSize;
	}

	public boolean isMaxRowSizeIncludeBlobs() {
		return maxRowSizeIncludeBlobs;
	}

	public void setMaxRowSizeIncludeBlobs(boolean maxRowSizeIncludeBlobs) {
		this.maxRowSizeIncludeBlobs = maxRowSizeIncludeBlobs;
	}

	public int getMaxStatementLength() {
		return maxStatementLength;
	}

	public void setMaxStatementLength(int maxStatementLength) {
		this.maxStatementLength = maxStatementLength;
	}

	public int getMaxStatements() {
		return maxStatements;
	}

	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}

	public int getMaxTableNameLength() {
		return maxTableNameLength;
	}

	public void setMaxTableNameLength(int maxTableNameLength) {
		this.maxTableNameLength = maxTableNameLength;
	}

	public int getMaxTablesInSelect() {
		return maxTablesInSelect;
	}

	public void setMaxTablesInSelect(int maxTablesInSelect) {
		this.maxTablesInSelect = maxTablesInSelect;
	}

	public int getMaxUserNameLength() {
		return maxUserNameLength;
	}

	public void setMaxUserNameLength(int maxUserNameLength) {
		this.maxUserNameLength = maxUserNameLength;
	}

	public int getDefaultTransactionIsolation() {
		return defaultTransactionIsolation;
	}

	public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
		this.defaultTransactionIsolation = defaultTransactionIsolation;
	}

	public boolean isSupportsTransactions() {
		return supportsTransactions;
	}

	public void setSupportsTransactions(boolean supportsTransactions) {
		this.supportsTransactions = supportsTransactions;
	}

	public boolean isSupportsDataDefinitionAndDataManipulationTransactions() {
		return supportsDataDefinitionAndDataManipulationTransactions;
	}

	public void setSupportsDataDefinitionAndDataManipulationTransactions(boolean supportsDataDefinitionAndDataManipulationTransactions) {
		this.supportsDataDefinitionAndDataManipulationTransactions = supportsDataDefinitionAndDataManipulationTransactions;
	}

	public boolean isSupportsDataManipulationTransactionsOnly() {
		return supportsDataManipulationTransactionsOnly;
	}

	public void setSupportsDataManipulationTransactionsOnly(boolean supportsDataManipulationTransactionsOnly) {
		this.supportsDataManipulationTransactionsOnly = supportsDataManipulationTransactionsOnly;
	}

	public boolean isDataDefinitionCausesTransactionCommit() {
		return dataDefinitionCausesTransactionCommit;
	}

	public void setDataDefinitionCausesTransactionCommit(boolean dataDefinitionCausesTransactionCommit) {
		this.dataDefinitionCausesTransactionCommit = dataDefinitionCausesTransactionCommit;
	}

	public boolean isDataDefinitionIgnoredInTransactions() {
		return dataDefinitionIgnoredInTransactions;
	}

	public void setDataDefinitionIgnoredInTransactions(boolean dataDefinitionIgnoredInTransactions) {
		this.dataDefinitionIgnoredInTransactions = dataDefinitionIgnoredInTransactions;
	}

	public boolean isSupportsBatchUpdates() {
		return supportsBatchUpdates;
	}

	public void setSupportsBatchUpdates(boolean supportsBatchUpdates) {
		this.supportsBatchUpdates = supportsBatchUpdates;
	}

	public boolean isSupportsSavepoints() {
		return supportsSavepoints;
	}

	public void setSupportsSavepoints(boolean supportsSavepoints) {
		this.supportsSavepoints = supportsSavepoints;
	}

	public boolean isSupportsNamedParameters() {
		return supportsNamedParameters;
	}

	public void setSupportsNamedParameters(boolean supportsNamedParameters) {
		this.supportsNamedParameters = supportsNamedParameters;
	}

	public boolean isSupportsMultipleOpenResults() {
		return supportsMultipleOpenResults;
	}

	public void setSupportsMultipleOpenResults(boolean supportsMultipleOpenResults) {
		this.supportsMultipleOpenResults = supportsMultipleOpenResults;
	}

	public boolean isSupportsGetGeneratedKeys() {
		return supportsGetGeneratedKeys;
	}

	public void setSupportsGetGeneratedKeys(boolean supportsGetGeneratedKeys) {
		this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
	}

	public int getGetResultSetHoldability() {
		return getResultSetHoldability;
	}

	public void setGetResultSetHoldability(int getResultSetHoldability) {
		this.getResultSetHoldability = getResultSetHoldability;
	}

	public int getGetDatabaseMajorVersion() {
		return getDatabaseMajorVersion;
	}

	public void setGetDatabaseMajorVersion(int getDatabaseMajorVersion) {
		this.getDatabaseMajorVersion = getDatabaseMajorVersion;
	}

	public int getGetDatabaseMinorVersion() {
		return getDatabaseMinorVersion;
	}

	public void setGetDatabaseMinorVersion(int getDatabaseMinorVersion) {
		this.getDatabaseMinorVersion = getDatabaseMinorVersion;
	}

	public int getGetJDBCMajorVersion() {
		return getJDBCMajorVersion;
	}

	public void setGetJDBCMajorVersion(int getJDBCMajorVersion) {
		this.getJDBCMajorVersion = getJDBCMajorVersion;
	}

	public int getGetJDBCMinorVersion() {
		return getJDBCMinorVersion;
	}

	public void setGetJDBCMinorVersion(int getJDBCMinorVersion) {
		this.getJDBCMinorVersion = getJDBCMinorVersion;
	}

	public int getGetSQLStateType() {
		return getSQLStateType;
	}

	public void setGetSQLStateType(int getSQLStateType) {
		this.getSQLStateType = getSQLStateType;
	}

	public boolean isLocatorsUpdateCopy() {
		return locatorsUpdateCopy;
	}

	public void setLocatorsUpdateCopy(boolean locatorsUpdateCopy) {
		this.locatorsUpdateCopy = locatorsUpdateCopy;
	}

	public boolean isSupportsStatementPooling() {
		return supportsStatementPooling;
	}

	public void setSupportsStatementPooling(boolean supportsStatementPooling) {
		this.supportsStatementPooling = supportsStatementPooling;
	}

	public boolean isSupportsStoredFunctionsUsingCallSyntax() {
		return supportsStoredFunctionsUsingCallSyntax;
	}

	public void setSupportsStoredFunctionsUsingCallSyntax(boolean supportsStoredFunctionsUsingCallSyntax) {
		this.supportsStoredFunctionsUsingCallSyntax = supportsStoredFunctionsUsingCallSyntax;
	}

	public boolean isAutoCommitFailureClosesAllResultSets() {
		return autoCommitFailureClosesAllResultSets;
	}

	public void setAutoCommitFailureClosesAllResultSets(boolean autoCommitFailureClosesAllResultSets) {
		this.autoCommitFailureClosesAllResultSets = autoCommitFailureClosesAllResultSets;
	}

	public boolean isGeneratedKeyAlwaysReturned() {
		return generatedKeyAlwaysReturned;
	}

	public void setGeneratedKeyAlwaysReturned(boolean generatedKeyAlwaysReturned) {
		this.generatedKeyAlwaysReturned = generatedKeyAlwaysReturned;
	}

	public long getGetMaxLogicalLobSize() {
		return getMaxLogicalLobSize;
	}

	public void setGetMaxLogicalLobSize(long getMaxLogicalLobSize) {
		this.getMaxLogicalLobSize = getMaxLogicalLobSize;
	}

	public boolean isSupportsRefCursors() {
		return supportsRefCursors;
	}

	public void setSupportsRefCursors(boolean supportsRefCursors) {
		this.supportsRefCursors = supportsRefCursors;
	}

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

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
