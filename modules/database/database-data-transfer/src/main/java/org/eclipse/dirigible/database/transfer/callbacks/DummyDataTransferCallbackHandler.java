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

import java.util.List;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.transfer.api.DataTransferConfiguration;
import org.eclipse.dirigible.database.transfer.api.IDataTransferCallbackHandler;

public class DummyDataTransferCallbackHandler implements IDataTransferCallbackHandler {

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIdentifier(String identifier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferStarted(DataTransferConfiguration configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferFinished(int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferFailed(String error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void metadataLoadingStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void metadataLoadingFinished(int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sortingStarted(List<PersistenceTableModel> tables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sortingFinished(List<PersistenceTableModel> result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dataTransferStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dataTransferFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableTransferStarted(String table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableTransferFinished(String table, int transferedRecords) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableTransferFailed(String table, String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recordTransferFinished(String tableName, int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableSelectSQL(String selectSQL) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableInsertSQL(String insertSQL) {
		// TODO Auto-generated method stub
		
	}

}
