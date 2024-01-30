/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Database
 * 
 */
import * as bytes from "@dirigible/io/bytes";
import { InputStream } from "@dirigible/io/streams";
import { numeric } from "@dirigible/utils/alphanumeric";
const DatabaseFacade = Java.type("org.eclipse.dirigible.components.api.db.DatabaseFacade");
const DatabaseResultSetHelper = Java.type("org.eclipse.dirigible.components.data.management.helpers.DatabaseResultSetHelper");
const JSqlDate = Java.type("java.sql.Date");
const JSqlTimestamp = Java.type("java.sql.Timestamp");
const JSqlTime = Java.type("java.sql.Time");

const SQLTypes = Object.freeze({
	"BOOLEAN": 16,
	"DATE": 91,
	"TIME": 92,
	"TIMESTAMP": 93,
	"DOUBLE": 8,
	"FLOAT": 6,
	"REAL": 7,
	"TINYINT": -6,
	"SMALLINT": 5,
	"INTEGER": 4,
	"BIGINT": -5,
	"VARCHAR": 12,
	"CHAR": 1,
	"CLOB": 2005,
	"BLOB": 2004,
	"VARBINARY": -3,
	"DECIMAL": 3,
	"ARRAY": 2003,
	"NVARCHAR": -9,
	"NCLOB": 2011,
	"BIT": -7
});

export function getDatabaseTypes(): void {
	throw new Error("Deprecated");
};

export function getDataSources(): Object {
	const datasources = DatabaseFacade.getDataSources();
	if (datasources) {
		return JSON.parse(datasources);
	}
	return datasources;
};

export function createDataSource(name: string, driver: string, url: string, username: string, password: string, properties: string): void {
	throw new Error("Deprecated");
};

export function getMetadata(datasourceName: string): Object {
	let metadata;
	if (datasourceName) {
		metadata = DatabaseFacade.getMetadata(datasourceName);
	} else {
		metadata = DatabaseFacade.getMetadata();
	}

	if (metadata) {
		return JSON.parse(metadata);
	}
	return metadata;
};

export function getProductName(datasourceName?: string): string {
	let productName;
	if (datasourceName) {
		productName = DatabaseFacade.getProductName(datasourceName);
	} else {
		productName = DatabaseFacade.getProductName();
	}
	return productName;
};
 

export function getConnection(datasourceName?: string): Connection {
	const connection = new Connection(datasourceName);

	return connection;
};

/**
 * Connection object
 */
export class Connection {
	public native;

	constructor(datasourceName? : string) {
		if(datasourceName) {
			this.native = DatabaseFacade.getConnection(datasourceName);
		} else {
			this.native = DatabaseFacade.getConnection();
		}
	}

	public prepareStatement(sql: string): PreparedStatement {
		const preparedStatement = new PreparedStatement();
		const native = this.native.prepareStatement(sql);
		preparedStatement.native = native;
		return preparedStatement;
	};

	public prepareCall(sql: string): CallableStatement {
		const callableStatement = new CallableStatement();
		const native = this.native.prepareCall(sql);
		callableStatement.native = native;
		return callableStatement;
	};

	public close(): void {
		if (!this.isClosed()) {
			this.native.close();
		}
	};

	public commit (): void {
		this.native.commit();
	};

	public getAutoCommit (): boolean {
		return this.native.getAutoCommit();
	};

	public getCatalog (): string {
		return this.native.getCatalog();
	};

	public getSchema (): string {
		return this.native.getSchema();
	};

	public getTransactionIsolation (): number {
		return this.native.getTransactionIsolation();
	};

	public isClosed (): boolean {
		return this.native.isClosed();
	};

	public isReadOnly (): boolean {
		return this.native.isReadOnly();
	};

	public isValid (): boolean {
		return this.native.isValid();
	};

	public rollback (): void {
		this.native.rollback();
	};

	public setAutoCommit (autoCommit: boolean): void {
		this.native.setAutoCommit(autoCommit);
	};

	public setCatalog (catalog: string): void {
		this.native.setCatalog(catalog);
	};

	public setReadOnly (readOnly: boolean): void {
		this.native.setReadOnly(readOnly);
	};

	public setSchema (schema: string): void {
		this.native.setSchema(schema);
	};

	public setTransactionIsolation (transactionIsolation: number): void {
		this.native.setTransactionIsolation(transactionIsolation);
	};

	public getMetaData (): any /*: DatabaseMetaData*/ {
		return this.native.getMetaData();
	}
}

/**
 * Statement object
 */
class PreparedStatement {
	public native;
	public internalStatement;

	constructor(internalStatement?: string) {
		this.internalStatement = internalStatement;
	}

	public close(): void {
		this.native.close();
	};

	public getResultSet(): ResultSet {
		const resultset = new ResultSet();
		const native = this.native.getResultSet();
		resultset.native = native;
		return resultset;
	};

	public execute(): boolean {
		return this.native.execute();
	};

	public executeQuery(): ResultSet {
		const resultset = new ResultSet();
		resultset.native = this.native.executeQuery();
		return resultset;
	};

	public executeUpdate(): number {
		return this.native.executeUpdate();
	};

	public setNull(index: number, sqlType: number): void {
		this.native.setNull(index, sqlType);
	};

	public setBinaryStream(parameterIndex: number, inputStream: InputStream, length?: number): void {
		if (length) {
			this.native.setBinaryStream(parameterIndex, inputStream, length);
		} else {
			this.native.setBinaryStream(parameterIndex, inputStream);
		}
	};

	public setBoolean(index: number, value?: boolean): void {
		if (value !== null && value !== undefined) {
			this.native.setBoolean(index, value);
		} else {
			this.setNull(index, SQLTypes.BOOLEAN);
		}
	};

	public setByte(index: number, value?: any /*: byte*/): void {
		if (value !== null && value !== undefined) {
			this.native.setByte(index, value);
		} else {
			this.setNull(index, SQLTypes.TINYINT);
		}
	};

	public setBlob(index: number, value?: Blob): void {
		if (value !== null && value !== undefined) {
			let blob = createBlobValue(this.native, value);
			this.native.setBlob(index, blob);
		} else {
			this.setNull(index, SQLTypes.BLOB);
		}
	};

	public setClob(index: number, value?: any /*: Clob*/): void {
		if (value !== null && value !== undefined) {
			let clob = createClobValue(this.native, value);
			this.native.setClob(index, clob);
		} else {
			this.setNull(index, SQLTypes.CLOB);
		}
	};

	public setNClob(index: number, value?: any /*: NClob*/): void {
		if (value !== null && value !== undefined) {
			let nclob = createNClobValue(this.native, value);
			this.native.setNClob(index, nclob);
		} else {
			this.setNull(index, SQLTypes.NCLOB);
		}
	};

	public setBytesNative(index: number, value?: any[] /*byte[]*/): void {
		if (value !== null && value !== undefined) {
			this.native.setBytes(index, value);
		} else {
			this.setNull(index, SQLTypes.VARBINARY);
		}
	};

	public setBytes(index: number, value?: any[] /*byte[]*/): void {
		if (value !== null && value !== undefined) {
			var data = bytes.toJavaBytes(value);
			this.native.setBytes(index, data);
		} else {
			this.setNull(index, SQLTypes.VARBINARY);
		}
	};

	public setDate(index: number, value?: string | Date): void {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let dateInstance = new JSqlDate(date.getTime());
			this.native.setDate(index, dateInstance);
		} else {
			this.setNull(index, SQLTypes.DATE);
		}
	};

	public setDouble(index: number, value?: number): void {
		if (value !== null && value !== undefined) {
			this.native.setDouble(index, value);
		} else {
			this.setNull(index, SQLTypes.DOUBLE);
		}
	};

	public setFloat(index: number, value: number) {
		if (value !== null && value !== undefined) {
			this.native.setFloat(index, value);
		} else {
			this.setNull(index, SQLTypes.FLOAT);
		}
	};

	public setInt(index: number, value?: number) {
		if (value !== null && value !== undefined) {
			this.native.setInt(index, value);
		} else {
			this.setNull(index, SQLTypes.INTEGER);
		}
	};

	public setLong(index: string | number, value?: number) {
		index = parseInt("" + index, 10); //Rhino things..
		if (value !== null && value !== undefined) {
			this.native.setLong(index, value);
		} else {
			this.setNull(index, SQLTypes.BIGINT);
		}
	};

	public setShort(index: number, value?: number) {
		if (value !== null && value !== undefined) {
			this.native.setShort(index, value);
		} else {
			this.setNull(index, SQLTypes.SMALLINT);
		}
	};

	public setString(index: number, value?: string) {
		if (value !== null && value !== undefined) {
			this.native.setString(index, value);
		} else {
			this.setNull(index, SQLTypes.VARCHAR);
		}
	};

	public setTime(index: number, value?: string | Date) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timeInstance = new JSqlTime(date.getTime());
			this.native.setTime(index, timeInstance);
		} else {
			this.setNull(index, SQLTypes.TIME);
		}
	};

	public setTimestamp(index: number, value?: string | Date) {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timestampInstance = new JSqlTimestamp(date.getTime());
			this.native.setTimestamp(index, timestampInstance);
		} else {
			this.setNull(index, SQLTypes.TIMESTAMP);
		}
	};

	public setBigDecimal(index: number, value?: number /*: BigDecimal*/) {
		if (value !== null && value !== undefined) {
			this.native.setBigDecimal(index, value);
		} else {
			this.setNull(index, SQLTypes.DECIMAL);
		}
	};

	public setNString(index: number, value?: string) {
		if (value !== null && value !== undefined) {
			this.native.setNString(index, value);
		} else {
			this.setNull(index, SQLTypes.NVARCHAR);
		}
	};

	public addBatch() {
		this.native.addBatch();
	};

	public executeBatch() {
		return this.native.executeBatch();
	};

	public getMetaData() {
		return this.native.getMetaData();
	};

	public getMoreResults() {
		return this.native.getMoreResults();
	};

	public getParameterMetaData() {
		return this.native.getParameterMetaData();
	};

	public getSQLWarning() {
		return this.native.getWarnings();
	};

	public isClosed() {
		return this.native.isClosed();
	};
}

class CallableStatement {
	public native;

	// constructor() {

	// }

	public getResultSet() {
		const resultset = new ResultSet();
		resultset.native = this.native.getResultSet();
		return resultset;
	};

	public executeQuery(): ResultSet {
		const resultset = new ResultSet();
		resultset.native = this.native.executeQuery();
		return resultset;
	};

	public executeUpdate(): number {
		return this.native.executeUpdate();
	};

	public registerOutParameter(parameterIndex: number, sqlType: keyof typeof SQLTypes | number) {
		this.native.registerOutParameter(parameterIndex, sqlType);
	};

	public registerOutParameterByScale(parameterIndex: number, sqlType: keyof typeof SQLTypes | number, scale: number) {
		this.native.registerOutParameter(parameterIndex, sqlType, scale);
	};

	public registerOutParameterByTypeName(parameterIndex: number, sqlType: keyof typeof SQLTypes | number, typeName: string) {
		this.native.registerOutParameter(parameterIndex, sqlType, typeName);
	};

	public wasNull() {
		return this.native.wasNull();
	};

	public getString(parameterIndex: number): string {
		return this.native.getString(parameterIndex);
	};

	public getBoolean(parameterIndex: number): boolean {
		return this.native.getBoolean(parameterIndex);
	};

	public getByte(parameterIndex: number): any /*: byte*/ {
		return this.native.getByte(parameterIndex);
	};

	public getShort(parameterIndex: number): number {
		return this.native.getShort(parameterIndex);
	};

	public getInt(parameterIndex: number): number {
		return this.native.getInt(parameterIndex);
	};

	public getLong(parameterIndex: number): number {
		return this.native.getLong(parameterIndex);
	};

	public getFloat(parameterIndex: number): number {
		return this.native.getFloat(parameterIndex);
	};

	public getDouble(parameterIndex: number): number {
		return this.native.getDouble(parameterIndex);
	};

	public getDate(parameterIndex: number): Date {
		return this.native.getDate(parameterIndex);
	};

	public getTime(parameterIndex: number): Date {
		return this.native.getTime(parameterIndex);
	};

	public getTimestamp(parameterIndex: number): Date {
		return this.native.getTimestamp(parameterIndex);
	};

	public getObject(parameterIndex: number): any {
		return this.native.getObject(parameterIndex);
	};

	public getBigDecimal(parameterIndex: number): number /*: sql.BigDecimal*/ {
		return this.native.getBigDecimal(parameterIndex);
	};

	public getRef(parameterIndex: number): any /*: sql.Ref*/ {
		return this.native.getRef(parameterIndex);
	};

	public getBytes(parameterIndex: number): any[] /*: byte[]*/ {
		let data = this.native.getBytes(parameterIndex);
		return bytes.toJavaScriptBytes(data);
	};

	public getBytesNative(parameterIndex: number): any[] /*: byte[]*/ {
		return this.native.getBytes(parameterIndex);
	};

	public getBlob(parameterIndex: number): any /*: sql.Blob*/ {
		let data = readBlobValue(this.native.getBlob(parameterIndex));
		return bytes.toJavaScriptBytes(data);
	};

	public getBlobNative(parameterIndex: number): any /*: sql.Blob*/ {
		return readBlobValue(this.native.getBlob(parameterIndex));
	};

	public getClob(parameterIndex: number): any /*: sql.Clob*/ {
		return readClobValue(this.native.getClob(parameterIndex));
	};

	public getNClob(parameterIndex: string | number): any /*: sql.NClob*/ {
		return readNClobValue(this.native.getNClob(parameterIndex));
	};

	public getNString(parameterIndex: string | number): string {
		return this.native.getNString(parameterIndex);
	};

	public getArray(parameterIndex: string | number): any /*: sql.Array*/ {
		return this.native.getArray(parameterIndex);
	};

	public getURL(parameterIndex: string | number): URL {
		return this.native.getURL(parameterIndex);
	};

	public getRowId(parameterIndex: string | number): any /*: sql.RowId*/ {
		return this.native.getRowId(parameterIndex);
	};

	public getSQLXML(parameterIndex: string | number): any /*: sql.SQLXML*/ {
		return this.native.getSQLXML(parameterIndex);
	};

	public setURL(parameterIndex: string, value: URL): void {
		this.native.setURL(parameterIndex, value);
	};

	public setNull(parameterIndex: string, sqlTypeStr: keyof typeof SQLTypes | number, typeName?: string): void {
		const sqlType: number = Number.isInteger(sqlTypeStr) ? sqlTypeStr : SQLTypes[sqlTypeStr];
		if (typeName !== undefined && typeName !== null) {
			this.native.setNull(parameterIndex, sqlType, typeName);
		} else {
			this.native.setNull(parameterIndex, sqlType);
		}
	};

	public setBoolean(parameterIndex: string, value?: boolean): void {
		if (value !== null && value !== undefined) {
			this.native.setBoolean(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.BOOLEAN);
		}
	};

	public setByte(parameterIndex: string, value?: any /*: byte*/): void {
		if (value !== null && value !== undefined) {
			this.native.setByte(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.BIT);
		}
	};

	public setShort(parameterIndex: string, value?: number): void {
		if (value !== null && value !== undefined) {
			this.native.setShort(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.SMALLINT);
		}
	};

	public setInt(parameterIndex: string, value?: number): void {
		if (value !== null && value !== undefined) {
			this.native.setInt(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.INTEGER);
		}
	};

	public setLong(parameterIndex: string, value?: number): void {
		if (value !== null && value !== undefined) {
			this.native.setLong(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.BIGINT);
		}
	};

	public setFloat(parameterIndex: string, value?: number): void {
		if (value !== null && value !== undefined) {
			this.native.setFloat(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.FLOAT);
		}
	};

	public setDouble(parameterIndex: string, value?: number): void {
		if (value !== null && value !== undefined) {
			this.native.setDouble(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.DOUBLE);
		}
	};

	public setBigDecimal(parameterIndex: string, value?: number /*: BigDecimal*/): void {
		if (value !== null && value !== undefined) {
			this.native.setBigDecimal(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.DECIMAL);
		}
	};

	public setString(parameterIndex: string, value?: string): void {
		if (value !== null && value !== undefined) {
			this.native.setString(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.VARCHAR);
		}
	};

	public setBytes(parameterIndex: string, value?: any[] /*byte[]*/): void {
		if (value !== null && value !== undefined) {
			this.native.setBytes(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.ARRAY);
		}
	};

	public setDate(parameterIndex: string, value?: string | Date): void {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let dateInstance = new JSqlDate(date.getTime());
			this.native.setDate(parameterIndex, dateInstance);
		} else {
			this.setNull(parameterIndex, SQLTypes.DATE);
		}
	};

	public setTime(parameterIndex: string, value?: string | Date): void {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timeInstance = new JSqlTime(date.getTime());
			this.native.setTime(parameterIndex, timeInstance);
		} else {
			this.setNull(parameterIndex, SQLTypes.TIME);
		}
	};

	public setTimestamp(parameterIndex: string, value?: string | Date): void {
		if (value !== null && value !== undefined) {
			let date = getDateValue(value);
			let timestampInstance = new JSqlTimestamp(date.getTime());
			this.native.setTimestamp(parameterIndex, timestampInstance);
		} else {
			this.setNull(parameterIndex, SQLTypes.TIMESTAMP);
		}
	};

	public setAsciiStream(parameterIndex: string, inputStream: InputStream, length: number): void {
		if (length) {
			this.native.setAsciiStream(parameterIndex, inputStream, length);
		} else {
			this.native.setAsciiStream(parameterIndex, inputStream);
		}
	};

	public setBinaryStream(parameterIndex: string, inputStream: InputStream, length: number): void {
		if (length) {
			this.native.setBinaryStream(parameterIndex, inputStream, length);
		} else {
			this.native.setBinaryStream(parameterIndex, inputStream);
		}
	};

	public setObject(parameterIndex: string, value: Object, targetSqlType: number, scale: number): void {
		if (scale !== undefined && scale !== null && targetSqlType !== undefined && targetSqlType !== null) {
			this.native.setObject(parameterIndex, value, targetSqlType, scale);
		} else if (targetSqlType !== undefined && targetSqlType !== null) {
			this.native.setObject(parameterIndex, value, targetSqlType);
		} else {
			this.native.setObject(parameterIndex, value);
		}
	};

	public setRowId(parameterIndex: string, value: number /*: RowId*/): void {
		this.native.setRowId(parameterIndex, value);
	};

	public setNString(parameterIndex: string, value: string): void {
		if (value !== null && value !== undefined) {
			this.native.setNString(parameterIndex, value);
		} else {
			this.setNull(parameterIndex, SQLTypes.NVARCHAR);
		}
	};

	public setSQLXML(parameterIndex: string, value: any /*: SQLXML*/): void {
		if (value !== null && value !== undefined) {
			this.native.setSQLXML(parameterIndex, value);
		} else {
			throw Error("Nullable SQLXML type not supported.");
		}
	};

	public setBlob(parameterIndex: string, value: any /*Blob */): void{
		if (value !== null && value !== undefined) {
			let blob = createBlobValue(this.native, value);
			this.native.setBlob(parameterIndex, blob);
		} else {
			this.setNull(parameterIndex, SQLTypes.BLOB);
		}
	};

	public setClob(parameterIndex: string, value: any /*: Clob*/): void {
		if (value !== null && value !== undefined) {
			let clob = createClobValue(this.native, value);
			this.native.setClob(parameterIndex, clob);
		} else {
			this.setNull(parameterIndex, SQLTypes.CLOB);
		}
	};

	public setNClob(parameterIndex: string, value: any /*: NClob*/): void {
		if (value !== null && value !== undefined) {
			let nclob = createNClobValue(this.native, value);
			this.native.setNClob(parameterIndex, nclob);
		} else {
			this.setNull(parameterIndex, SQLTypes.NCLOB);
		}
	};

	public execute(): boolean {
		return this.native.execute();
	};

	public getMoreResults(): boolean /* unsure about here */ {
		return this.native.getMoreResults();
	};

	public getParameterMetaData(): any /*: ParameterMetaData*/ {
		return this.native.getParameterMetaData();
	};

	public isClosed(): boolean {
		return this.native.isClosed();
	};


	public close(): void {
		this.native.close();
	};
}

/**
 * ResultSet object
 */
class ResultSet {
	public native;
	public internalResultset;

	constructor(internalResultset?) {
		this.internalResultset = internalResultset;
	}

	public toJson(limited: boolean): void {
		if (limited === undefined || limited === false) {
			limited = false;
		}
		DatabaseResultSetHelper.toJson(this.native, limited, false);
	};

	public close(): void {
		this.native.close();
	};

	public getBigDecimal(identifier: number): any /*: BigDecimal*/ {
		return this.native.getBigDecimal(identifier);
	};

	public getBoolean(identifier: number): boolean {
		return this.native.getBoolean(identifier);
	};

	public getByte(identifier: number): any /*: byte*/ {
		return this.native.getByte(identifier);
	};

	public getBytes(identifier: number): any[] /*: byte[]*/ {
		let data = this.native.getBytes(identifier);
		return bytes.toJavaScriptBytes(data);
	};

	public getBytesNative(identifier: number): any[] /*: byte[]*/ {
		return this.native.getBytes(identifier);
	};

	public getBlob(identifier: number): any /*: sql.Blob*/ {
		let data = readBlobValue(this.native.getBlob(identifier));
		return bytes.toJavaScriptBytes(data);
	};

	public getBlobNative(identifier: number): any /*: sql.Blob*/ {
		return readBlobValue(this.native.getBlob(identifier));
	};

	public getClob(identifier: number): any /*: sql.Clob*/ {
		return readClobValue(this.native.getClob(identifier));
	};

	public getNClob(columnIndex: number): any /*: sql.NClob*/ {
		return readNClobValue(this.native.getNClob(columnIndex));
	}

	public getDate(identifier: number): Date | null {
		const dateInstance = this.native.getDate(identifier);
		return dateInstance !== null && dateInstance !== undefined ? new Date(dateInstance.getTime()) : null;
	};

	public getDouble(identifier: number): number {
		return this.native.getDouble(identifier);
	};

	public getFloat(identifier: number): number {
		return this.native.getFloat(identifier);
	};

	public getInt(identifier: number): number {
		return this.native.getInt(identifier);
	};

	public getLong(identifier: number): number {
		return this.native.getLong(identifier);
	};

	public getShort(identifier: number): number {
		return this.native.getShort(identifier);
	};

	public getString(identifier: number): string {
		return this.native.getString(identifier);
	};

	public getTime(identifier: number): Date | null {
		const dateInstance = this.native.getTime(identifier);
		return dateInstance !== null && dateInstance !== undefined ? new Date(dateInstance.getTime()) : null;
	};

	public getTimestamp(identifier: number): Date | null {
		const dateInstance = this.native.getTimestamp(identifier);
		return dateInstance !== null && dateInstance !== undefined ? new Date(dateInstance.getTime()) : null;
	};

	public isAfterLast(): boolean {
		return this.native.isAfterLast();
	};

	public isBeforeFirst(): boolean {
		return this.native.isBeforeFirst();
	};

	public isClosed(): boolean {
		return this.native.isClosed();
	};

	public isFirst(): boolean {
		return this.native.isFirst();
	};

	public isLast(): boolean {
		return this.native.isLast();
	};

	public next(): boolean {
		return this.native.next();
	};

	public getMetaData(): any /*: ResultSetMetaData*/ {
		return this.native.getMetaData();
	}

	public getNString(columnIndex: number): string {
		return this.native.getNString(columnIndex);
	}
}

function isHanaDatabase(connection: Connection): boolean {
	let isHanaDatabase = false;
	let metadata = connection.getMetaData();
	if (metadata !== null && metadata !== undefined) {
		isHanaDatabase = metadata.getDatabaseProductName() === "HDB";
	}
	return isHanaDatabase;
}

function readBlobValue(value: any /*: Blob*/) {
	return value.getBytes(1, value.length());
}

function createBlobValue(native, value: any /*byte[] */): any /*: Blob*/ {
	try {
		let connection = native.getConnection();
		if (connection === null || connection === undefined) {
			throw new Error("Can't create new 'Blob' value as the connection is null");
		}
		let blob = null;
		if (isHanaDatabase(connection)) {
			let ps = null;
			try {
				ps = connection.prepareStatement("SELECT TO_BLOB (?) FROM DUMMY;");
				ps.setBytes(1, value);
				let rs = ps.executeQuery();
				if (rs.next()) {
					blob = rs.getBlob(1);
				}
			} finally {
				if (ps !== null && ps !== undefined) {
					ps.close();
				}
			}
		} else {
			blob = connection.createBlob();
			blob.setBytes(1, value);
		}
		return blob;
	} catch (e) {
		throw new Error(`Error occured during creation of 'Clob' value: ${e.message}`);
	}
}

function readClobValue(value: any /*: Clob*/): string {
	return value.getSubString(1, value.length());
}

function createClobValue(native, value: string): any /*: Clob*/ {
	try {
		let connection = native.getConnection();
		if (connection === null || connection === undefined) {
			throw new Error("Can't create new 'Clob' value as the connection is null");
		}
		let clob = null;
		if (isHanaDatabase(connection)) {
			let ps = null;
			try {
				ps = connection.prepareStatement("SELECT TO_CLOB (?) FROM DUMMY;");
				ps.setString(1, value);
				let rs = ps.executeQuery();
				if (rs.next()) {
					clob = rs.getClob(1);
				}
			} finally {
				if (ps !== null && ps !== undefined) {
					ps.close();
				}
			}
		} else {
			clob = connection.createClob();
			clob.setString(1, value);
		}
		return clob;
	} catch (e) {
		throw new Error(`Error occured during creation of 'Clob' value: ${e.message}`);
	}
}

function readNClobValue(value: any /*: NClob*/): string {
	return value.getSubString(1, value.length());
}

function createNClobValue(native, value: string): any /*: NClob*/ {
	try {
		let connection = native.getConnection();
		if (connection === null || connection === undefined) {
			throw new Error("Can't create new 'NClob' value as the connection is null");
		}
		let nclob = null;
		if (isHanaDatabase(connection)) {
			let ps = null;
			try {
				ps = connection.prepareStatement("SELECT TO_NCLOB (?) FROM DUMMY;");
				ps.setString(1, value);
				let rs = ps.executeQuery();
				if (rs.next()) {
					nclob = rs.getNClob(1);
				}
			} finally {
				if (ps !== null && ps !== undefined) {
					ps.close();
				}
			}
		} else {
			nclob = connection.createNClob();
			nclob.setString(1, value);
		}
		return nclob;
	} catch (e) {
		throw new Error(`Error occured during creation of 'NClob' value: ${e.message}`);
	}
}

function getDateValue(value: string | Date): Date {
	if (typeof value === "string") {
		return new Date(value);
	}
	return value;
}