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
	
	private static final String SEVERITY_INFO = "INFO";
	
	private static final String SEVERITY_ERROR = "ERROR";
	
	private static final String SEVERITY_WARNING = "WARNING";
	
	
	private Writer writer;
	
	private String identifier;
	
	public WriterDataTransferCallbackHandler(Writer writer, String identifier) {
		this.writer = writer;
		this.identifier = identifier;
	}
	
	private void write(String s, String severity) {
		try {
			String message = String.format("[%s][%s] %s", identifier, severity, s);
			this.writer.write(message);
			this.writer.write("\n");
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
		write("Transfer has been started...", SEVERITY_INFO);
	}

	@Override
	public void transferFinished(int count) {
		write("Transfer has been finished successfully for tables count: " + count, SEVERITY_INFO);
	}

	@Override
	public void transferFailed(String error) {
		write("Transfer failed with error: " + error, SEVERITY_ERROR);
	}

	@Override
	public void metadataLoadingStarted() {
		write("Loading of metadata has been started...", SEVERITY_INFO);
		
	}

	@Override
	public void metadataLoadingFinished(int count) {
		write("Loading of metadata has been finished successfully - tables count is: " + count, SEVERITY_INFO);
	}

	@Override
	public void sortingStarted(List<PersistenceTableModel> tables) {
		write("Topological sorting of tables via dependencies has been started...", SEVERITY_INFO);
	}

	@Override
	public void sortingFinished(List<PersistenceTableModel> result) {
		StringBuffer buffer = new StringBuffer();
		for (PersistenceTableModel model : result) {
			buffer.append(model.getTableName() + ", ");
		}
		write("Loading of metadata has been finished successfully - tables count is: " + buffer.substring(0, buffer.length()-2), SEVERITY_INFO);
	}

	@Override
	public void dataTransferStarted() {
		write("Data transfer has been started...", SEVERITY_INFO);
		
	}

	@Override
	public void dataTransferFinished() {
		write("Data transfer has been finished successfully.", SEVERITY_INFO);
		
	}

	@Override
	public void tableTransferStarted(String table) {
		write("Data transfer has been started for table: " + table, SEVERITY_INFO);
		
	}

	@Override
	public void tableTransferFinished(String table, int transferedRecords) {
		write("Data transfer has been finished successfully for table: " + table + " with records count: " + transferedRecords, SEVERITY_INFO);
		
	}

	@Override
	public void tableTransferFailed(String table, String error) {
		write("Data transfer has been failed for table: " + table + " with error: " + error, SEVERITY_ERROR);
	}

	@Override
	public void recordTransferFinished(String tableName, int i) {
		//
	}

	@Override
	public void tableSelectSQL(String selectSQL) {
		logger.debug("Table select SQL script is: " + selectSQL, SEVERITY_INFO);
		
	}

	@Override
	public void tableInsertSQL(String insertSQL) {
		logger.debug("Table select SQL script is: " + insertSQL, SEVERITY_INFO);
	}

	@Override
	public void tableSkipped(String table, String reason) {
		logger.debug("Table " + table + " has been skipped due to: " + reason, SEVERITY_WARNING);
		
	}

}
