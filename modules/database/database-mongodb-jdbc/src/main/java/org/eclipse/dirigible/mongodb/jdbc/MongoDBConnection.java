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
package org.eclipse.dirigible.mongodb.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.eclipse.dirigible.mongodb.jdbc.util.SingleColumnMongoIteratorResultSet;
import org.eclipse.dirigible.mongodb.jdbc.util.SingleColumnStaticResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection implements Connection {
	
	private static final Logger LOG = LoggerFactory.getLogger(MongoDBConnection.class);

	private Properties info;
	private MongoClientURI uri;
	
	private String dbName;
	private String collectionName;
	private boolean isClosed = true;
	private boolean isReadonly = false;
	
	private MongoClient client;
	private MongoClientOptions clientOptions;
	MongoDatabase db;
	MongoCollection<Document> collection;
	
	private MongoDBDatabaseMetadata metadata;
	
	public MongoDBConnection(String url, Properties info) {
		String dbUrl = url.replace("jdbc:", "");
		MongoClientURI uri = new MongoClientURI(dbUrl);
		this.uri = uri;
		this.dbName = this.uri.getDatabase();
		this.collectionName = this.uri.getCollection();
		
		this.client = new MongoClient(this.uri);
		this.isClosed = false;
		
		this.info = info;
		if(this.info == null)
			this.info = new Properties();
		this.clientOptions = this.client.getMongoClientOptions();
		this.info.putAll(this.mongoClientOptionsAsProperties(this.clientOptions, this.info));
		
		//retrieve these from connected client
		this.dbName = this.uri.getDatabase();
		if(this.dbName!=null)
			this.db = this.client.getDatabase(this.dbName);
		if(this.collectionName!=null)
			this.collection = this.db.getCollection(this.collectionName);
		
		LOG.debug("Connected with client properties: "+this.info.toString());
	}
	
	MongoDatabase getMongoDb() {
		return this.db;
	}
	
	String getCollectionName(){
		return this.collectionName;
	}

	private Properties mongoClientOptionsAsProperties(MongoClientOptions ops, Properties props){
		//TODO: write complex object properties too?
		if(ops.getDescription()!=null)
			props.setProperty("description", ops.getDescription());
		if(ops.getRequiredReplicaSetName()!=null)
			props.setProperty("requiredReplicaSetName", ops.getRequiredReplicaSetName());
		props.setProperty("connectionsPerHost", ""+ops.getConnectionsPerHost());
		props.setProperty("connectTimeout", ""+ops.getConnectTimeout());
		props.setProperty("heartbeatConnectTimeout", ""+ops.getHeartbeatConnectTimeout());
		props.setProperty("heartbeatFrequency", ""+ops.getHeartbeatFrequency());
		props.setProperty("heartbeatSocketTimeout", ""+ops.getHeartbeatSocketTimeout());
		props.setProperty("localThreshold", ""+ops.getLocalThreshold());
		props.setProperty("maxConnectionIdleTime", ""+ops.getMaxConnectionIdleTime());
		props.setProperty("maxConnectionLifeTime", ""+ops.getMaxConnectionLifeTime());
		props.setProperty("maxWaitTime", ""+ops.getMaxWaitTime());
		props.setProperty("minConnectionsPerHost", ""+ops.getMinConnectionsPerHost());
		props.setProperty("minHeartbeatFrequency", ""+ops.getMinHeartbeatFrequency());
		props.setProperty("serverSelectionTimeout", ""+ops.getServerSelectionTimeout());
		props.setProperty("socketTimeout", ""+ops.getSocketTimeout());
		return props;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (isWrapperFor(iface)) {
	        return (T) this;
	    }
	    throw new SQLException("No wrapper for " + iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		 return iface != null && iface.isAssignableFrom(getClass());
	}

	@Override
	public Statement createStatement() throws SQLException {
		return new MongoDBStatement(this);
	}
	
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return new MongoDBPreparedStatement(this, sql);
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		 throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		 throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		 throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public String nativeSQL(String sql) throws SQLException {
		//TODO: currently works only ofr queries
		BsonDocument filterDocument = null;
		if(sql==null || sql.length()<1)//that is a call to find() in terms of mongodb queries
			filterDocument = new BsonDocument();
		else
			filterDocument = BsonDocument.parse(sql);
		return filterDocument.toJson();
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		//silently ignore
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	@Override
	public void commit() throws SQLException {
		//silently ignore
	}

	@Override
	public void rollback() throws SQLException {
		//silently ignore
	}

	@Override
	public void close() throws SQLException {
		this.client.close();
		this.isClosed = true;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.isClosed;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if(metadata==null){
			metadata = new MongoDBDatabaseMetadata();
			Document response = db.runCommand(BsonDocument.parse("{ buildInfo: 1 }"));
			metadata.setDatabaseProductName("MongoDB");
			metadata.setDatabaseProductVersion(response.getString("version"));
			metadata.setDriverName("Java Driver");
			metadata.setURL(this.uri.getURI());
		}
		metadata.setIsReadOnly(client.isLocked());
		ResultSet schemasRS = new SingleColumnStaticResultSet(Arrays.asList(new String[]{"default"}).iterator());
		metadata.setSchemas(schemasRS);
		metadata.setTables(new SingleColumnMongoIteratorResultSet(this.db.listCollectionNames()));
		
		return metadata;
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		this.isReadonly = readOnly;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return this.isReadonly;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		//silently ignore
	}

	@Override
	public String getCatalog() throws SQLException {
		return null;
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		//silently ignore
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return Connection.TRANSACTION_NONE;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void clearWarnings() throws SQLException {
		//silently ignore
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public int getHoldability() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		//silenty ignore
	}
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public Clob createClob() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public Blob createBlob() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public NClob createNClob() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public boolean isValid(int timeout) throws SQLException {
		Document response = this.db.runCommand(new BsonDocument("ping", new BsonInt32(1)));
		response.getDouble("ok");
		return response != null && response.getDouble("ok")==1.0;
	}
	
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		//silently ingore
	}
	
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		// silently ingore
	}
	
	@Override
	public String getClientInfo(String name) throws SQLException {
		return this.info.getProperty(name);
	}
	
	@Override
	public Properties getClientInfo() throws SQLException {
		return this.info;
	}
	
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
	@Override
	public void setSchema(String schema) throws SQLException {
		if(schema==null || schema.length()<1)
			throw new IllegalArgumentException();
		this.collection = this.db.getCollection(schema);
	}
	
	@Override
	public String getSchema() throws SQLException {
		return this.collectionName;
	}
	
	@Override
	public void abort(Executor executor) throws SQLException {
		executor.execute(new AsyncAbort(this));
	}
	
	private class AsyncAbort implements Runnable{
		MongoDBConnection mongodbJdbcConnection;
		public AsyncAbort(MongoDBConnection mongodbJdbcConnection) {
			this.mongodbJdbcConnection = mongodbJdbcConnection;
		}

		@Override
		public void run() {
			try {
				if(!this.mongodbJdbcConnection.isClosed()){
					this.mongodbJdbcConnection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	@Override
	public int getNetworkTimeout() throws SQLException {
		return this.clientOptions.getConnectTimeout();
	}

}
