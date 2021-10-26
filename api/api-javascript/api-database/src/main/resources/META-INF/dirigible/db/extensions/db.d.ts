declare module "@dirigible/db" {
    module dao {
        function create(): DAO;
    }
    module database {
        function getDatabaseTypes(): string[];

        function getDataSources(databaseType: string): string[];

        function createDataSource(name: string, driver: string, url: string, username: string, password: string, properties: string);

        function getMetadata(databaseType: string, datasourceName: string): object;

        function getConnection(databaseType: string, datasourceName: string): Connection;
        function getConnection(): Connection;

        function getProductName(databaseType: string, datasourceName: string): string;

    }
    module procedure {
        function create(sql: string, databaseType, datasourceName);

        function execute(sql, parameters, databaseType, datasourceName): any;
    }
    module query {
        function execute(sql: string, parameters, databaseType, datasourceName): JSON | ResultSet
    }
    module sequence {
        function nextval(sequence, databaseType, datasourceName): any;

        function create(sequence, databaseType, datasourceName);

        function drop(sequence, databaseType, datasourceName);
    }
    module sql {
        function getDialect(connection: Connection): Dialect
    }
    module update {
        function execute(sql, parameters, databaseType, datasourceName);
    }


}

interface DAO {
    insert(entity): any;

    list(oQuerySetting?: string): [];

    find(id: string, expand?: string, select?: string): object;

    update(entity): DAO;

    remove(id: string);

    count(): number;

    dropTable();

}

interface Connection {
    prepareStatement(sql: string): PreparedStatement;

    prepareCall(sql: string): CallableStatement;

    close();

    commit();

    getAutoCommit();

    getCatalog(): string;

    getSchema(): string;

    getTransactionIsolation(): string;

    isClosed(): boolean;

    isReadOnly(): boolean;

    isValid(): boolean;

    rollback();

    setAutoCommit(autocommit);

    setCatalog(catalog);

    setSchema(schema);

    setReadOnly(value: boolean);

    setTransactionIsolation(transactionIsolation: string);
}

interface PreparedStatement {
    close();

    getResultSet(): ResultSet;

    execute();

    executeQuery(): ResultSet;

    executeUpdate();

    setNull(index, sqlType);

    setBoolean(index, value: boolean);

    setByte(index, value);

    setClob(index, value);

    setBlob(index, value);

    setBytesNative(index, value);

    setBytes(index, value);

    setDate(index, value);

    setDouble(index, value);

    setFloat(index, value);

    setInt(index, value);

    setLong(index, value);

    setShort(index, value);

    setString(index, value);

    setTime(index, value);

    setTimestamp(index, value);

    execute();

    addBatch();

    executeBatch();

    getMetaData();

    getMoreResults();

    getParameterData(): string;

    getSQLWarning(): string;

    isClosed(): boolean;

    setDecimal(index, value);

    setNClob(index, value);

    setNString(index, value);

}

interface CallableStatement {

    getResultSet(): ResultSet;

    execute();

    executeQuery(): ResultSet;

    executeUpdate();

    registerOurParameter(parameterindex: number, sqlType);

    registerOutParameterByScale(parameterindex: number, sqlType, scale);

    registerOutParameterByTypeName(parameterIndex, sqlType, typeName);

    wasNull(index, sqlType);

    getString(parameter): string;

    getBoolean(parameter): boolean;

    getByte(parameter);

    getShort(parameter);

    getInt(parameter);

    getLong(parameter);

    getFloat(parameter);

    getDouble(parameter);

    getBytes(parameter);

    getDate(parameter);

    getTime(parameter);

    getTimestamp(parameter);

    getObject(parameter);

    getBigDecimal(parameter);

    getRef(parameter);

    getBlob(parameter);

    getClob(parameter);

    getNClob(parameter);

    getNString(parameter);

    getArray(parameter): any[];

    getURL(parameter);

    getRowId(parameter);

    getSQLXML(parameter);

    setURL(parameter, value);

    setNull(parameter, sqlTypeStr, typeName);

    setBoolean(parameter, value);

    setByte(parameter, value);

    setShort(parameter, value);

    setInt(parameter, value);

    setLong(parameter, value);

    setFloat(parameter, value);

    setDouble(parameter, value);

    setBigDecimal(parameter, value);

    setBytes(parameter, value);

    setString(parameter, value);

    setDate(parameter, value);

    setTime(parameter, value);

    setTimestamp(parameter, value);

    setBlob(parameter, value);

    setBytesNative(index, value);

    setClob(parameter, value);

    setNClob(parameter, value);

    setNString(parameter, value);

    setAsciiStream(parameter, inputStream, length);

    setBinaryStream(parameter, inputStream, length);

    setObject(parameter, value, targetSqlType, scale);

    setRowId(parameter, value);

    setNString(parameter, value);

    execute();

    getMetaData();

    getMoreResults();

    getParameterMetaData(): string;

    getSQLWarning(): string;

    isClosed(): boolean;

    setDecimal(index, value);

}

interface ResultSet {
    toJson(input): JSON;

    close();

    getBlob(identifier);

    getBigDecimal(identifier);

    getBoolean(identifier);

    getByte(identifier);

    getBytesNative(identifier);

    getBytes(identifier);

    getClob(identifier);

    getDate(identifier);

    getDouble(identifier);

    getFloat(identifier);

    getInt(identifier);

    getLong(identifier);

    getShort(identifier);

    getString(identifier);

    getTime(identifier);

    getTimestamp(identifier);

    isAfterLast(): boolean;

    isBeforeFirst(): boolean;

    isCLosed(): boolean;

    isFirst(): boolean;

    isLast(): boolean;

    next();

    getMetadata();

    getNClob();

    getNString();

}

interface Dialect {
    parameters: string[];
    build: string;
    select: Select;
    insert: Insert;
    update: Update;
    delete: Delete;
    create: Create;
    view: CreateView;
    sequence: CreateSequence;
    drop: Drop;

    nextval(name): NextVal
}

interface Select {
    build;
    parameters;

    distinct();

    forUpdate();

    column(column);

    from(table: string, alias: string);

    join(table: string, on, alias);

    innerJoin(table: string, on, alias);

    outerJoin(table: string, on, alias);

    leftJoin(table: string, on, alias);

    rightJoin(table: string, on, alias);

    fullJoin(table: string, on, alias);

    where(condition);

    order(column, asc);

    group(column);

    limit(limit);

    offset(offset);

    having(having);

    union(select);

}

interface Insert {
    build;
    parameters;

    into(table);

    column(column);

    value(value);

    select();
}

interface Update {
    build;
    parameters;

    table(table);

    set(column, value);

    where(condition);

}

interface Delete {
    build;
    parameters;

    from(table);

    where(condition);
}

interface NextVal {
    name;
    build;
}

interface Create {
    table: CreateTable;
}

interface CreateTable {
    build;

    column(column, type, isPrimaryKey, isNullable, isUnique, args);

    columnVarchar(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnChar(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnDate(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnTime(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnTimestamp(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnInteger(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnTinyint(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnBigint(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnSmallint(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnReal(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnDouble(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnBoolean(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnBlob(column, length, isPrimaryKey, isNullable, isUnique, args);

    columnDecimal(column, length, isPrimaryKey, isNullable, isUnique, args);

    primaryKey(columns, name);

    foreignKey(name, columns, referencedTable, referencedColumns);

    unique(name, columns);

    check(name, expression);


}

interface CreateView {
    build;

    column(column);

    asSelect(select);
}

interface CreateSequence {
    build();
}

interface Drop {
    table(table): DropTable;

    view(view): DropView;

    sequence(sequence): DropSequence;
}

interface DropTable {
    build()
}

interface DropView {
    build();
}

interface DropSequence {
    build;
}