/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.model;

/**
 * The factory for creation of the data structure models from source content
 */
public class DataStructureModelFactory {

	/**
	 * Creates a table model from the raw content
	 *
	 * @param content
	 *            the content
	 * @return the table model instance
	 * @throws EDataStructureModelFormatException
	 *             Format Exception
	 */
	public static TableModel createTableModel(String content) throws EDataStructureModelFormatException {
		TableModel tableModel = new TableModel(content);
		return tableModel;
	}

	/**
	 * Creates a view model from the raw content
	 *
	 * @param content
	 *            the content
	 * @return the view model instance
	 * @throws EDataStructureModelFormatException
	 *             Format Exception
	 */
	public static ViewModel createViewModel(String content) throws EDataStructureModelFormatException {
		ViewModel viewModel = new ViewModel(content);
		return viewModel;
	}

}
