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
package org.eclipse.dirigible.database.transfer.callbacks;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.transfer.api.DataTransferConfiguration;
import org.eclipse.dirigible.database.transfer.api.IDataTransferCallbackHandler;
import org.eclipse.dirigible.database.transfer.manager.DataTransferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriterDataTransferCallbackHandler implements IDataTransferCallbackHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(WriterDataTransferCallbackHandler.class);
	
	private Writer writer;
	
	private String identifier;
	
	public WriterDataTransferCallbackHandler(Writer writer, String identifier) {
		this.writer = writer;
		this.identifier = identifier;
	}
	
	private void write(String s) {
		try {
			String message = String.format("[%s] %s", identifier, s);
			this.writer.write(message + "\n");
			this.writer.flush();
			logger.info(message);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public void transferStarted(DataTransferConfiguration configuration) {
		write("Transfer has been started...");
	}

	@Override
	public void transferFinished(int count) {
		write("Transfer has been finished successfully for tables count: " + count);
	}

	@Override
	public void transferFailed(String error) {
		write("Transfer failed with error: " + error);
	}

	@Override
	public void metadataLoadingStarted() {
		write("Loading of metadata has been started...");
		
	}

	@Override
	public void metadataLoadingFinished(int count) {
		write("Loading of metadata has been finished successfully - tables count is: " + count);
	}

	@Override
	public void sortingStarted(List<PersistenceTableModel> tables) {
		write("Topological sorting of tables via dependencies has been started...");
	}

	@Override
	public void sortingFinished(List<PersistenceTableModel> result) {
		StringBuffer buffer = new StringBuffer();
		for (PersistenceTableModel model : result) {
			buffer.append(model.getTableName() + ", ");
		}
		write("Loading of metadata has been finished successfully - tables count is: " + buffer.substring(0, buffer.length()-2));
	}

	@Override
	public void dataTransferStarted() {
		write("Data transfer has been started...");
		
	}

	@Override
	public void dataTransferFinished() {
		write("Data transfer has been finished successfully.");
		
	}

	@Override
	public void tableTransferStarted(String table) {
		write("Data transfer has been started for table: " + table);
		
	}

	@Override
	public void tableTransferFinished(String table, int transferedRecords) {
		write("Data transfer has been finished successfully for table: " + table + " with records count: " + transferedRecords);
		
	}

	@Override
	public void tableTransferFailed(String table, String error) {
		write("Data transfer has been started for table: " + table);
	}

	@Override
	public void recordTransferFinished(String tableName, int i) {
		//
	}

	@Override
	public void tableSelectSQL(String selectSQL) {
		logger.debug("Table select SQL script is: " + selectSQL);
		
	}

	@Override
	public void tableInsertSQL(String insertSQL) {
		logger.debug("Table select SQL script is: " + insertSQL);
	}

}
