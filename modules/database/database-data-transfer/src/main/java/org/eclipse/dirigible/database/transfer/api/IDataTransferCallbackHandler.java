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
package org.eclipse.dirigible.database.transfer.api;

import java.util.List;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;

public interface IDataTransferCallbackHandler {
	
	String getIdentifier();
	
	void setIdentifier(String identifier);

	void transferStarted(DataTransferConfiguration configuration);

	void transferFinished(int count);

	void transferFailed(String error);

	void metadataLoadingStarted();
	
	void metadataLoadingError(String error);

	void metadataLoadingFinished(int count);

	void sortingStarted(List<PersistenceTableModel> tables);

	void sortingFinished(List<PersistenceTableModel> result);

	void dataTransferStarted();

	void dataTransferFinished();
	
	void tableTransferStarted(String table);
	
	void tableTransferFinished(String table, int transferedRecords);
	
	void tableTransferFailed(String table, String error);

	void recordTransferFinished(String tableName, int i);

	void tableSelectSQL(String selectSQL);

	void tableInsertSQL(String insertSQL);
	
	void tableSkipped(String table, String reason);
	
	public void stopTransfer();

	boolean isStopped();

}
