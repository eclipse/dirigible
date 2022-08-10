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
package org.eclipse.dirigible.database.ds.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Data Structures Classpath Content Handler.
 */
public class DataStructuresClasspathContentHandler extends AbstractClasspathContentHandler {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DataStructuresClasspathContentHandler.class);

	/** The data structures synchronizer. */
	private DataStructuresSynchronizer dataStructuresSynchronizer = new DataStructuresSynchronizer();

	/**
	 * Checks if is valid.
	 *
	 * @param path the path
	 * @return true, if is valid
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#isValid(java.lang.String)
	 */
	@Override
	protected boolean isValid(String path) {

		try {
			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_SCHEMA)) {
				dataStructuresSynchronizer.registerPredeliveredSchema(path);
				return true;
			}

			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_TABLE)) {
				dataStructuresSynchronizer.registerPredeliveredTable(path);
				return true;
			}

			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_VIEW)) {
				dataStructuresSynchronizer.registerPredeliveredView(path);
				return true;
			}

			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_REPLACE)) {
				dataStructuresSynchronizer.registerPredeliveredReplace(path);
				return true;
			}

			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_APPEND)) {
				dataStructuresSynchronizer.registerPredeliveredAppend(path);
				return true;
			}

			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_DELETE)) {
				dataStructuresSynchronizer.registerPredeliveredDelete(path);
				return true;
			}

			if (path.endsWith(IDataStructureModel.FILE_EXTENSION_UPDATE)) {
				dataStructuresSynchronizer.registerPredeliveredUpdate(path);
				return true;
			}
		} catch (IOException e) {
			logger.error("Predelivered Data Structure at path [" + path + "] is not valid", e);
		}

		return false;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
