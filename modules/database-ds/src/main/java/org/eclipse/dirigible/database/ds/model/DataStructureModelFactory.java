/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.ds.model;

import com.google.gson.Gson;

/**
 * The factory for creation of the data structure models from source content
 */
public class DataStructureModelFactory {
	
	private static final Gson gson = new Gson();
	
	/**
	 * Creates a table model from the raw content
	 *
	 * @param content
	 *            the table definition
	 * @return the table model instance
	 * @throws DataStructureModelException
	 *             in case of failure
	 */
	public static DataStructureTableModel createTableModel(String content) throws DataStructureModelException {
		DataStructureTableModel tableModel = gson.fromJson(content, DataStructureTableModel.class);
		return tableModel;
	}

	/**
	 * Creates a view model from the raw content
	 *
	 * @param content
	 *            the view definition
	 * @return the view model instance
	 * @throws DataStructureModelException
	 *             in case of failure
	 */
	public static DataStructureViewModel createViewModel(String content) throws DataStructureModelException {
		DataStructureViewModel viewModel = gson.fromJson(content, DataStructureViewModel.class);
		return viewModel;
	}

}
