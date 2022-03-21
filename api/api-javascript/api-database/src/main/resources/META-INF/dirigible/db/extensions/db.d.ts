declare module "@dirigible/db" {
    module database {
        /**
         * Returns the list of the available databases in this instance
         */
        function getDatabaseTypes(): string[];

        /**
         * Returns the list of the available data-sources in this instance for the given databaseType. In case the databaseType is not present, the data-sources of the default database type are listed
         * @param databaseType
         */
        function getDataSources(databaseType: string): string[];

        /**
         * Creates a named dynamic datasource based on the provided parameters
         * @param name
         * @param driver
         * @param url
         * @param username
         * @param password
         * @param properties
         */
        function createDataSource(name: string, driver: string, url: string, username: string, password: string, properties: string);

        /**
         * Returns the metadata of the selected databaseType and datasourceName. In case the datasourceName parameter is omitted, then the default data-source for the selected database is taken. In case the databaseType is omitted, then the default data-source of the default database type is taken
         * @param databaseType
         * @param datasourceName
         */
        function getMetadata(databaseType: string, datasourceName: string): object;

        /**
         * Establishes a connection to the selected data-source. Both parameters are optional
         * @param databaseType
         * @param datasourceName
         */
        function getConnection(databaseType?: string, datasourceName?: string): Connection;

        /**
         * Returns ProductName
         * @param databaseType
         * @param datasourceName
         */
        function getProductName(databaseType: string, datasourceName: string): string;

    }
    module dao {
        /**
         * Creates new DAO instances from oConfiguraiton JS object, which can be either standard ORM definition or a standard dirigible table definition
         * @param oConfiguration
         * @param loggerName
         */
        function create(oConfiguration, loggerName?): DAO;
    }
    module procedure {
        /**
         * Creates a SQL Stored Procedure in the selected databaseType and datasourceName, throws Error, if issue occur
         * @param sql
         * @param databaseType
         * @param datasourceName
         */
        function create(sql: string, databaseType?, datasourceName?: string);

        /**
         * Execute SQL Stored Procedure in the selected databaseType and datasourceName with the provided parameters and returns the result, if any
         * @param sql
         * @param parameters
         * @param databaseType
         * @param datasourceName
         */
        function execute(sql, parameters?, databaseType?, datasourceName?): [][];
    }
    module query {
        /**
         * Executes a SQL query against the selected databaseType and datasourceName with the provided parameters
         * @param sql
         * @param parameters
         * @param databaseType
         * @param datasourceName
         */
        function execute(sql: string, parameters?, databaseType?, datasourceName?): ResultSet;
    }
    module sequence {
        /**
         * Increment the sequence with the given name and returns the value. Creates the sequence implicitly if it deos not exist.
         * @param sequence
         * @param databaseType
         * @param datasourceName
         */
        function nextval(sequence:string, databaseType?, datasourceName?): number;

        /**
         * Creates the sequence by the given name.
         * @param sequence
         * @param databaseType
         * @param datasourceName
         */
        function create(sequence, databaseType, datasourceName);

        /**
         * Remove the sequence by the given name.
         * @param sequence
         * @param databaseType
         * @param datasourceName
         */
        function drop(sequence, databaseType?, datasourceName?);
    }
    module sql {
        /**
         * Returns the dialect based on the provided connection if any or the default one otherwise
         * @param connection
         */
        function getDialect(connection?: Connection): Dialect;
    }
    module update {
        /**
         * Executes a SQL update against the selected databaseType and datasourceName with the provided parameters and returns the number of affected rows
         * @param sql
         * @param parameters
         * @param databaseType
         * @param datasourceName
         */
        function execute(sql, parameters?, databaseType?, datasourceName?):number;
    }

    interface DAO {
        /**
         * Creates a table for persisting entities
         */
        createTable();

        /**
         * inserts array or entity and returns id (or ids of array of entities was supplied as input)
         * @param entity
         */
        insert(entity): any;

        /**
         * Lists entities optionally constrained with the supplied query settings
         * @param oQuerySetting
         */
        list(oQuerySetting?: string): [];

        /**
         * returns an entity by its id(if any), optionally expanding inline the associations defined in expand and optionally constraining the entitiy properties to those specified in select
         * @param id
         * @param expand
         * @param select
         */
        find(id: string, expand?: string, select?: string): object;

        /**
         * updates a persistent entity and returns for its dao chaining
         * @param entity
         */
        update(entity): DAO;

        /**
         * delete entity by id, or array of ids, or delete all (if not argument is provided).
         * @param id
         */
        remove(id?: string);

        /**
         * returns the number of persisted entities
         */
        count(): number;

        /**
         * Drops the entities table
         */
        dropTable(dropIdSequence?);

        /**
         *
         * @param expansionPath
         * @param context
         */
        expand(expansionPath, context);

        /**
         * Returns if orm contains table
         */
        existsTable():boolean

    }

    interface Connection {
        /**
         * Creates a prepared statement by the given SQL script
         * @param sql
         */
        prepareStatement(sql: string): PreparedStatement;

        /**
         * Creates a callable statement by the given SQL script
         * @param sql
         */
        prepareCall(sql: string): CallableStatement;

        /**
         * Closes the Connection and returns it to the pool
         */
        close();

        /**
         * Commits the current transaction
         */
        commit();

        /**
         * Returns the value of the auto commit setting
         */
        getAutoCommit(): boolean;

        /**
         * Returns the Catalog name, which the Connection is related to
         */
        getCatalog(): string;

        /**
         * Returns the Schema name, which the Connection is related to
         */
        getSchema(): string;

        /**
         * Returns the value of the transaction isolation setting
         */
        getTransactionIsolation(): number;

        /**
         * Returns true if the Connection is already closed and false otherwise
         */
        isClosed(): boolean;

        /**
         * Returns true if the Connection is opened in a read only state and false otherwise
         */
        isReadOnly(): boolean;

        /**
         * Returns true if the Connection is still valid and false otherwise
         */
        isValid(): boolean;

        /**
         * Rolls the current transaction back
         */
        rollback();

        /**
         * Sets the value of the auto commit setting
         * @param autocommit
         */
        setAutoCommit(autocommit);

        /**
         * Sets the Catalog name, which the Connection is related to
         * @param catalog
         */
        setCatalog(catalog);

        /**
         * Sets the Schema name, which the Connection is related to
         * @param schema
         */
        setSchema(schema);

        /**
         * Sets the value of the read only state
         * @param value
         */
        setReadOnly(value: boolean);

        /**
         * Sets the value of the transaction isolation setting
         * @param transactionIsolation
         */
        setTransactionIsolation(transactionIsolation: string);
    }

    interface PreparedStatement {
        /**
         * Closes the Statement
         */
        close();

        getResultSet(): ResultSet;

        /**
         * Executes an SQL query, script, procedure, etc.
         */
        execute(): boolean;

        /**
         * Executes a query and returns a ResultSet
         */
        executeQuery(): ResultSet;

        /**
         * Executes an update SQL statement
         */
        executeUpdate();

        /**
         * Returns sqlTypes object
         */
        SQLTypes:object;

        /**
         * Sets a parameter as null
         * @param index
         * @param sqlType
         */
        setNull(index: number, sqlType);

        /**
         * Sets a parameter of type boolean
         * @param index
         * @param value
         */
        setBoolean(index: number, value: boolean);

        /**
         * Sets a parameter of type bytes
         * @param index
         * @param value
         */
        setByte(index:number, value);

        /**
         * Sets a parameter of type clob
         * @param index
         * @param value
         */
        setClob(index:number, value);

        /**
         * Sets a parameter of type blob
         * @param index
         * @param value
         */
        setBlob(index:number, value);

        /**
         * Sets a parameter of type BytesNative
         * @param index
         * @param value
         */
        setBytesNative(index, value);

        /**
         * Sets a parameter of type bytes
         * @param index
         * @param value
         */
        setBytes(index, value);

        /**
         * Sets a parameter of type Date
         * @param index
         * @param value
         */
        setDate(index, value);

        /**
         * Sets a parameter of type double
         * @param index
         * @param value
         */
        setDouble(index, value);

        /**
         * Sets a parameter of type float
         * @param index
         * @param value
         */
        setFloat(index, value);

        /**
         * Sets a parameter of type integer
         * @param index
         * @param value
         */
        setInt(index, value);

        /**
         * Sets a parameter of type long
         * @param index
         * @param value
         */
        setLong(index, value);

        /**
         * Sets a parameter of type short
         * @param index
         * @param value
         */
        setShort(index:number, value:number);

        /**
         * Sets a parameter of type string
         * @param index
         * @param value
         */
        setString(index:number, value:string);

        /**
         * Sets a parameter of type Time
         * @param index
         * @param value
         */
        setTime(index:number, value);

        /**
         * Sets a parameter of type timestamp
         * @param index
         * @param value
         */
        setTimestamp(index:number, value);

        /**
         * Adds a set of parameters to this PreparedStatement batch of commands
         */
        addBatch();

        /**
         * Submits a batch of commands to the database for execution and if all commands execute successfully, returns an array of update counts.
         */
        executeBatch():[];

        /**
         * Retrieves a metadata object that contains information about the columns of the object that will be returned when this PreparedStatement is executed
         */
        getMetaData(): object;

        /**
         * Returns true, if there are more ResultSet objects to be retrieved.
         */
        getMoreResults():boolean;

        /**
         * Retrieves the number, types and properties of this PreparedStatement parameters
         */
        getParameterMetaData(): object;

        /**
         * Retrieves the first warning reported
         */
        getSQLWarning(): object;

        /**
         * Returns true, if closed
         */
        isClosed(): boolean;

        /**
         * Sets a parameter of type BigDecimal
         * @param index
         * @param value
         */
        setBigDecimal(index:number, value);

        /**
         * Sets a parameter of type NClob
         * @param index
         * @param value
         */
        setNClob(index:number, value);

        /**
         * Sets a parameter of type NString
         * @param index
         * @param value
         */
        setNString(index:number, value);

    }

    interface CallableStatement {
        /**
         * Returns ResultSet object
         */
        getResultSet(): ResultSet;

        // /**
        //  * Executes an SQL query, script, procedure, etc.
        //  */
        // execute();

        /**
         * Executes a query and returns a ResultSet
         */
        executeQuery(): ResultSet;

        /**
         * Executes an update SQL statement
         */
        executeUpdate();

        /**
         * Register provided sqlType parameter
         * @param parameterindex
         * @param sqlType
         */
        registerOutParameter(parameterindex: number, sqlType);

        /**
         * Register provided sqlType parameter by scale
         * @param parameterindex
         * @param sqlType
         * @param scale
         */
        registerOutParameterByScale(parameterindex: number, sqlType, scale);

        /**
         * Register provided sqlType parameter by TypeName
         * @param parameterIndex
         * @param sqlType
         * @param typeName
         */
        registerOutParameterByTypeName(parameterIndex, sqlType, typeName);

        /**
         * Returns boolean value specifying whether the column just read contains null values.
         * @param index
         * @param sqlType
         */
        wasNull(index, sqlType):boolean;

        /**
         * Returns a value of type string
         * @param parameter
         */
        getString(parameter): string;

        /**
         * Returns a value of type boolean
         * @param parameter
         */
        getBoolean(parameter): boolean;

        /**
         * Returns a value of type Byte
         * @param parameter
         */
        getByte(parameter):number;

        /**
         * Returns a value of type Short
         * @param parameter
         */
        getShort(parameter):number;

        /**
         * Returns a value of type Int
         * @param parameter
         */
        getInt(parameter):number;

        /**
         * Returns a value of type Long
         * @param parameter
         */
        getLong(parameter):number;

        /**
         * Returns a value of type float
         * @param parameter
         */
        getFloat(parameter):number;

        /**
         * Returns a value of type double
         * @param parameter
         */
        getDouble(parameter):number;

        /**
         * Returns a value of type bytes
         * @param parameter
         */
        getBytes(parameter);

        /**
         * Returns a value of type Date
         * @param parameter
         */
        getDate(parameter):Date;

        /**
         * Returns a value of type Time
         * @param parameter
         */
        getTime(parameter):Date;

        /**
         * Returns a value of type Timestamp
         * @param parameter
         */
        getTimestamp(parameter):Date;

        /**
         * Returns object
         * @param parameter
         */
        getObject(parameter);

        /**
         * Returns a value of type bigdecimal
         * @param parameter
         */
        getBigDecimal(parameter):number;

        /**
         * Gets the value of a column specified by column index as a Java java.sql.Ref.
         * @param parameter
         */
        getRef(parameter);

        /**
         * Returns a value of type blob
         * @param parameter
         */
        getBlob(parameter);

        /**
         * Returns a value of type Clob
         * @param parameter
         */
        getClob(parameter);

        /**
         * Returns a value of type NClob
         * @param parameter
         */
        getNClob(parameter);

        /**
         * Returns a value of type NString
         * @param parameter
         */
        getNString(parameter):string;

        /**
         * Returns a value of type Array
         * @param parameter
         */
        getArray(parameter): any[];

        /**
         * Returns a url
         * @param parameter
         */
        getURL(parameter):string;

        /**
         * Returns a row ID
         * @param parameter
         */
        getRowId(parameter);

        /**
         *
         * @param parameter
         */
        getSQLXML(parameter);

        setURL(parameter, value);

        /**
         * Set nulls
         * @param parameter
         * @param sqlTypeStr
         * @param typeName
         */
        setNull(parameter, sqlTypeStr, typeName);

        /**
         * Sets a parameter of type boolean
         * @param parameter
         * @param value
         */
        setBoolean(parameter, value);

        /**
         * Sets a parameter of type Byte
         * @param parameter
         * @param value
         */
        setByte(parameter, value);

        /**
         * Sets a parameter of type Byte
         * @param parameter
         * @param value
         */
        setShort(parameter, value);

        /**
         * Sets a parameter of type Int.
         * @param parameter
         * @param value
         */
        setInt(parameter, value);

        /**
         * Sets a parameter of type Long
         * @param parameter
         * @param value
         */
        setLong(parameter, value);

        /**
         * Sets a parameter of type Float
         * @param parameter
         * @param value
         */
        setFloat(parameter, value);

        /**
         * Sets a parameter of type Double
         * @param parameter
         * @param value
         */
        setDouble(parameter, value);

        /**
         * Sets a parameter of type BigDecimal
         * @param parameter
         * @param value
         */
        setBigDecimal(parameter, value);

        /**
         * Sets parameter of type Byte.
         * @param parameter
         * @param value
         */
        setBytes(parameter, value);

        /**
         * Sets a parameter of type String
         * @param parameter
         * @param value
         */
        setString(parameter, value);

        /**
         * Sets a parameter of type Date
         * @param parameter
         * @param value
         */
        setDate(parameter, value);

        /**
         * Sets a parameter of type Time
         * @param parameter
         * @param value
         */
        setTime(parameter, value);

        /**
         * Sets a parameter of type Timestamp
         * @param parameter
         * @param value
         */
        setTimestamp(parameter, value);

        /**
         * Set a parameter of type Blob
         * @param parameter
         * @param value
         */
        setBlob(parameter, value);

        /**
         * Sets a parameter of type bites native
         * @param index
         * @param value
         */
        setBytesNative(index, value);

        /**
         * Sets a parameter of type Clob
         * @param parameter
         * @param value
         */
        setClob(parameter, value);

        /**
         * Sets a parameter of type NClob
         * @param parameter
         * @param value
         */
        setNClob(parameter, value);

        /**
         * Sets a parameter of type NString
         * @param parameter
         * @param value
         */
        setNString(parameter, value);

        /**
         * Sets a parameter of type ASCIStream
         * @param parameter
         * @param inputStream
         * @param length
         */
        setAsciiStream(parameter, inputStream, length);

        /**
         * Sets a parameter of type BinaryStream
         * @param parameter
         * @param inputStream
         * @param length
         */
        setBinaryStream(parameter, inputStream, length);

        /**
         * Sets object
         * @param parameter
         * @param value
         * @param targetSqlType
         * @param scale
         */
        setObject(parameter, value, targetSqlType, scale);

        setRowId(parameter, value);

        setNString(parameter, value);

        /**
         * Retrieves a metadata object that contains information about the columns of the object that will be returned when this PreparedStatement is executed
         */
        getMetaData(): object;

        /**
         * Returns true, if there are more ResultSet objects to be retrieved.
         */
        getMoreResults(): boolean;

        /**
         * Retrieves the number, types and properties of this PreparedStatement parameters
         */
        getParameterMetaData(): object;

        /**
         * Retrieves the first warning reported
         */
        getSQLWarning(): object;

        /**
         * Returns true, if closed
         */
        isClosed(): boolean;

        setBigDecimal(index, value);

    }

    interface ResultSet {
        /**
         * Returns the result set as stringfied JSON, limited = true will return only the first 100 records
         * @param input
         */
        toJson(input): string;

        /**
         * Closes the ResultSet
         */
        close();

        /**
         * Returns a value of type Blob
         * @param identifier
         */
        getBlob(identifier);

        /**
         * Returns a value of type BigDecimal
         * @param identifier
         */
        getBigDecimal(identifier);

        /**
         * Returns a value of type boolean
         * @param identifier
         */
        getBoolean(identifier): boolean;

        /**
         * Returns a value of type byte
         * @param identifier
         */
        getByte(identifier);

        /**
         * Returns a value of type bytesNative
         * @param identifier
         */
        getBytesNative(identifier);

        /**
         * Returns a value of type bytes
         * @param identifier
         */
        getBytes(identifier);

        /**
         * Returns a value of type Clob
         * @param identifier
         */
        getClob(identifier);

        /**
         * Returns a value of type date
         * @param identifier
         */
        getDate(identifier): Date;

        /**
         * Returns a value of type double
         * @param identifier
         */
        getDouble(identifier);

        /**
         * Returns a value of type float
         * @param identifier
         */
        getFloat(identifier): number;

        /**
         * Returns a value of type integer
         * @param identifier
         */
        getInt(identifier): number;

        /**
         * Returns a value of type long
         * @param identifier
         */
        getLong(identifier): number;

        /**
         * Returns a value of type short
         * @param identifier
         */
        getShort(identifier): number;

        /**
         * Returns a value of type string
         * @param identifier
         */
        getString(identifier): string;

        /**
         * Returns a value of type time
         * @param identifier
         */
        getTime(identifier): Date;

        /**
         * Returns a value of type timestamp
         * @param identifier
         */
        getTimestamp(identifier);

        /**
         * Returns true if the ResultSet is iterated at the end and false otherwise
         */
        isAfterLast(): boolean;

        /**
         * Returns true if the ResultSet is iterated at the beginning and false otherwise
         */
        isBeforeFirst(): boolean;

        /**
         * Returns true if the ResultSet is already closed and false otherwise
         */
        isCLosed(): boolean;

        /**
         * Returns true if the ResultSet is iterated at the first row and false otherwise
         */
        isFirst(): boolean;

        /**
         * Returns true if the ResultSet is iterated at the last row and false otherwise
         */
        isLast(): boolean;

        /**
         * Iterates the ResultSet to the next row and returns true if it is successful. Returns false if no more rows remain.
         */
        next();

        /**
         * Returs Metadata for ResultSet
         */
        getMetadata();

        /**
         * Returns NClob
         */
        getNClob();

        /**
         * Returns NString
         */
        getNString();

    }

    interface Dialect {
        /**
         * Returns a Select SQL builder
         */
        select(): Select;

        /**
         * Returns an Insert SQL builder
         */
        insert(): Insert;

        /**
         * Returns an Update SQL builder
         */
        update(): Update;

        /**
         * Returns a Delete SQL builder
         */
        delete(): Delete;

        /**
         * Returns a Create SQL builder
         */
        create():Create

        /**
         * Returns a Drop SQL builder
         */
        drop():Drop

        /**
         * Returns a Nextval SQL builder by a given name
         * @param name
         */
        nextval(name): NextVal;
    }

    interface Select {
        /**
         * Generate and returns the Select SQL statement as a string
         */
        build():string;

        /**
         * Sets the distinct flag and returns the current Select SQL builder
         */
        distinct():Select;

        /**
         * Sets the forUpdate flag and returns the current Select SQL builder
         */
        forUpdate():Select;

        /**
         * Adds a column with the given name and returns the current Select SQL builder. Use * for all
         * @param name
         */
        column(name:string):Select;

        /**
         * Adds a table with the given table name and alias and returns the current Select SQL builder
         * @param table
         * @param alias
         */

        from(table: string, alias?: string):Select;

        /**
         * Adds a join clause and returns the current Select SQL builder
         * @param table
         * @param on
         * @param alias
         */
        join(table: string, on, alias?):Select;

        /**
         * Adds a join clause and returns the current Select SQL builder
         * @param table
         * @param on
         * @param alias
         */
        innerJoin(table: string, on, alias?):Select;

        /**
         * Adds a join clause and returns the current Select SQL builder
         * @param table
         * @param on
         * @param alias
         */
        outerJoin(table: string, on, alias?):Select;

        /**
         * Adds a join clause and returns the current Select SQL builder
         * @param table
         * @param on
         * @param alias
         */
        leftJoin(table: string, on, alias?):Select;

        /**
         * Adds a join clause and returns the current Select SQL builder
         * @param table
         * @param on
         * @param alias
         */
        rightJoin(table: string, on, alias?):Select;

        /**
         * Adds a join clause and returns the current Select SQL builder
         * @param table
         * @param on
         * @param alias
         */
        fullJoin(table: string, on, alias):Select;

        /**
         * Adds a where clause with the given condition and returns the current Select SQL builder
         * @param condition
         */
        where(condition):Select;

        /**
         * Adds an order clause with the given column and optionally the ascending or descending order and returns the current Select SQL builder
         * @param column
         * @param asc
         */
        order(column, asc):Select;

        /**
         * Adds a group by clause and returns the current Select SQL builder
         * @param column
         */
        group(column):Select;

        /**
         * Sets the limit number and returns the current Select SQL builder
         * @param limit
         */
        limit(limit):Select;

        /**
         * Sets the offset number and returns the current Select SQL builder
         * @param offset
         */
        offset(offset):Select;

        /**
         * Adds an having clause and returns the current Select SQL builder
         * @param having
         */
        having(having):Select;

        /**
         * Adds an union clause and returns the current Select SQL builder
         * @param select
         */
        union(select):Select;

    }

    interface Insert {
        /**
         * Generate and returns the Insert SQL statement as a string
         */
        build():String;

        /**
         * Sets the table name and returns the current Insert SQL builder
         * @param table
         */
        into(table):Insert;

        /**
         * Adds a column name and returns the current Insert SQL builder
         * @param column
         */
        column(column):Insert;

        /**
         * Adds a value param and returns the current Insert SQL builder. Use ? for prepared statements afterwards.
         * @param param
         */
        value(param):Insert;

        /**
         * Sets the select statement if needed and returns the current Insert SQL builder
         * @param statement
         */
        select(statement):Insert;
    }

    interface Update {
        /**
         * Generate and returns the Update SQL statement as a string
         */
        build():String;
        parameters;

        /**
         * Sets the table name and returns the current Update SQL builder
         * @param table
         */
        table(table):Update;

        /**
         * Adds a column - value pair and returns the current Update SQL builder
         * @param column
         * @param value
         */
        set(column, value):Update;

        /**
         * Adds a where clause with the given condition and returns the current Update SQL builder
         * @param condition
         */
        where(condition):Update;

    }

    interface Delete {
        /**
         * Generate and returns the Delete SQL statement as a string
         */
        build():string;
        parameters;

        /**
         * Sets the table name and returns the current Delete SQL builder
         * @param table
         */
        from(table):Delete;

        /**
         * Adds a where clause with the given condition and returns the current Delete SQL builder
         * @param condition
         */
        where(condition):Delete;
    }

    interface NextVal {
        name;

        /**
         * Generate and returns the Nextval SQL statement as a string
         */
        build():String;
    }

    interface Create {
        /**
         * Returns a CreateTable SQL builder
         * @param name
         */
        table(name:string): CreateTable;

        /**
         * Returns a CreateView SQL builder
         * @param name
         */
        view(name:string):CreateView;

        /**
         * Returns a CreateSequence SQL builder
         * @param name
         */
        sequence(name):CreateSequence;
    }

    interface CreateTable {
        /**
         * Generate and returns the CreateTable SQL statement as a string
         */
        build():string;

        /**
         * Adds a column definition and returns the current CreateTable SQL builder
         * @param column
         * @param type
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        column(column, type, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a VARCHAR column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnVarchar(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a CHAR column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnChar(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a DATE column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnDate(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a TIME column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnTime(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a TIMESTAMP column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnTimestamp(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a INTEGER column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnInteger(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a TINYINT column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnTinyint(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a BIGINT column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnBigint(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a SMALLINT column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnSmallint(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a REAL column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnReal(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a DOUBLE PRECISION column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnDouble(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?);

        /**
         * Adds a BOOLEAN column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnBoolean(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a BLOB column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnBlob(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Adds a DECIMAL column definition and returns the current CreateTable SQL builder
         * @param column
         * @param length
         * @param isPrimaryKey
         * @param isNullable
         * @param isUnique
         * @param args
         */
        columnDecimal(column, length, isPrimaryKey?:boolean, isNullable?:boolean, isUnique?:boolean, args?):CreateTable;

        /**
         * Sets a primary key definition and returns the current CreateTable SQL builder
         * @param columns
         * @param name
         */
        primaryKey(columns, name):CreateTable;

        /**
         * Adds a foreign key definition and returns the current CreateTable SQL builder
         * @param name
         * @param columns
         * @param referencedTable
         * @param referencedColumns
         */
        foreignKey(name, columns, referencedTable, referencedColumns):CreateTable;

        /**
         * Adds an unique index definition and returns the current CreateTable SQL builder
         * @param name
         * @param columns
         */
        unique(name, columns):CreateTable;

        /**
         * Adds a check definition and returns the current CreateTable SQL builder
         * @param name
         * @param expression
         */
        check(name, expression):CreateTable;


    }

    interface CreateView {
        /**
         * Generate and returns the VieweTable SQL statement as a string
         */
        build():string;

        /**
         * Adds a column definition and returns the current VieweTable SQL builder
         * @param name
         */
        column(name):ViewTable;

        /**
         * Sets the select definition and returns the current VieweTable SQL builder
         * @param select
         */
        asSelect(select):ViewTable;
    }

    interface ViewTable{}

    interface CreateSequence {
        /**
         * Generate and returns the Sequence SQL statement as a string
         */
        build():string;
    }

    interface Drop {
        /**
         * Returns a DropTable SQL builder
         * @param name
         */
        table(name:string): DropTable;

        /**
         * Returns a DropView SQL builder
         * @param name
         */
        view(name): DropView;

        /**
         * Returns a DropSequence SQL builder
         * @param name
         */
        sequence(name): DropSequence;
    }

    interface DropTable {
        /**
         * Generate and returns the DropTable SQL statement as a string
         */
        build():string;
    }

    interface DropView {
        /**
         * Generate and returns the DropView SQL statement as a string
         */
        build():string;
    }

    interface DropSequence {
        /**
         * Generate and returns the DropSequence SQL statement as a string
         */
        build():string;
    }
}
