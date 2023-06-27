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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class MongoDBStatement implements Statement {
	
	protected MongoDBConnection conn;
	protected boolean isClosed = false;
	
	public MongoDBStatement(MongoDBConnection conn){
		this.conn = conn;
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

	/**
	 * Input string: the document specification as defined in https://docs.mongodb.org/manual/reference/command/find/#dbcmd.find
	 */
	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		MongoDatabase db = this.conn.getMongoDb();
		BsonDocument filterDocument = null;
		if(sql==null || sql.length()<1)//that is a call to find() in terms of mongodb queries
			filterDocument = new BsonDocument();
		else
			filterDocument = BsonDocument.parse(sql);	
		/*
		 *  TODO: With 3.2 use https://docs.mongodb.org/manual/reference/command/find/#dbcmd.find instead
		 * 	Document response = this.conn.getMongoDb().runCommand(filterDocument); //MongoDB 3.2 only. Won't work on 3.0
		 */
		String collectionName = filterDocument.containsKey("find")? filterDocument.getString("find").getValue():null;
		if(collectionName==null)
			collectionName = this.conn.getCollectionName();//fallback if any
		if(collectionName==null)
			throw new IllegalArgumentException("Specifying a collection is mandatory for query operations");
		BsonDocument filter = filterDocument.containsKey("filter")? filterDocument.getDocument("filter"): null;
		
		FindIterable<Document> searchHits = null;
		if(filter==null)
			searchHits = db.getCollection(collectionName).find();
		else 
			searchHits = db.getCollection(collectionName).find(filter);
		if(filterDocument.containsKey("batchSize"))
			searchHits.batchSize(filterDocument.getInt32("batchSize").getValue());
		if(filterDocument.containsKey("limit"))
			searchHits.limit(filterDocument.getInt32("limit").getValue());
		if(filterDocument.containsKey("sort"))
			searchHits.sort(filterDocument.getDocument("sort"));
		return new MongoDBResultSet(this, searchHits);
	}

	/**
	 * https://docs.mongodb.org/manual/reference/command/update/#dbcmd.update
	 */
	@Override
	public int executeUpdate(String sql) throws SQLException {
		BsonDocument updateDocument = null;
		if(sql==null || sql.length()<1)
			throw new IllegalArgumentException();
		else
			updateDocument = BsonDocument.parse(sql);
		
		Document response = this.conn.getMongoDb().runCommand(updateDocument);
		int updatedDocuments = 0;
		if(response!=null && response.get("ok")!=null){
			updatedDocuments = response.getInteger("nModified");
			//TODO operation atomicity concerns? /errors/
		}
		return updatedDocuments;
	}

	@Override
	public void close() throws SQLException {
		this.isClosed = true;
		this.conn.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return ResultSet.FETCH_FORWARD;
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.conn;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.isClosed;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
