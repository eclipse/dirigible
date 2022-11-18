/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.transfer.callback;

import java.util.List;

import org.eclipse.dirigible.components.data.transfer.domain.DataTransferConfiguration;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;

/**
 * The Class DummyDataTransferCallbackHandler.
 */
public class DummyDataTransferCallbackHandler implements DataTransferCallbackHandler {

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the identifier.
	 *
	 * @param identifier the new identifier
	 */
	@Override
	public void setIdentifier(String identifier) {
		// TODO Auto-generated method stub

	}

	/**
	 * Transfer started.
	 *
	 * @param configuration the configuration
	 */
	@Override
	public void transferStarted(DataTransferConfiguration configuration) {
		// TODO Auto-generated method stub

	}

	/**
	 * Transfer finished.
	 *
	 * @param count the count
	 */
	@Override
	public void transferFinished(int count) {
		// TODO Auto-generated method stub

	}

	/**
	 * Transfer failed.
	 *
	 * @param error the error
	 */
	@Override
	public void transferFailed(String error) {
		// TODO Auto-generated method stub

	}

	/**
	 * Metadata loading started.
	 */
	@Override
	public void metadataLoadingStarted() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Metadata loading error.
	 *
	 * @param error the error
	 */
	@Override
	public void metadataLoadingError(String error) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Metadata loading finished.
	 *
	 * @param count the count
	 */
	@Override
	public void metadataLoadingFinished(int count) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Sorting started.
	 *
	 * @param tables the tables
	 */
	@Override
	public void sortingStarted(List<PersistenceTableModel> tables) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Sorting finished.
	 *
	 * @param result the result
	 */
	@Override
	public void sortingFinished(List<PersistenceTableModel> result) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Data transfer started.
	 */
	@Override
	public void dataTransferStarted() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Data transfer finished.
	 */
	@Override
	public void dataTransferFinished() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Table transfer started.
	 *
	 * @param table the table
	 */
	@Override
	public void tableTransferStarted(String table) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Table transfer finished.
	 *
	 * @param table the table
	 * @param transferedRecords the transfered records
	 */
	@Override
	public void tableTransferFinished(String table, int transferedRecords) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Table transfer failed.
	 *
	 * @param table the table
	 * @param error the error
	 */
	@Override
	public void tableTransferFailed(String table, String error) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Record transfer finished.
	 *
	 * @param tableName the table name
	 * @param i the i
	 */
	@Override
	public void recordTransferFinished(String tableName, int i) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Table select SQL.
	 *
	 * @param selectSQL the select SQL
	 */
	@Override
	public void tableSelectSQL(String selectSQL) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Table insert SQL.
	 *
	 * @param insertSQL the insert SQL
	 */
	@Override
	public void tableInsertSQL(String insertSQL) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Table skipped.
	 *
	 * @param table the table
	 * @param reason the reason
	 */
	@Override
	public void tableSkipped(String table, String reason) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Stop transfer.
	 */
	@Override
	public void stopTransfer() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Checks if is stopped.
	 *
	 * @return true, if is stopped
	 */
	@Override
	public boolean isStopped() {
		return false;
	}

}
