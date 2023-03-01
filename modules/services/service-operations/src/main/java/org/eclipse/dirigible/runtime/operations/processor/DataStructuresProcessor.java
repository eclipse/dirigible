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
package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.service.DataStructuresCoreService;

/**
 * The Class DataStructuresProcessor.
 */
public class DataStructuresProcessor {
	
	/** The data structures core service. */
	private DataStructuresCoreService dataStructuresCoreService = new DataStructuresCoreService();
	
	/**
	 * List.
	 *
	 * @return the string
	 * @throws DataStructuresException the data structures exception
	 */
	public String list() throws DataStructuresException {
		
		List<DataStructureModel> dataStructures = dataStructuresCoreService.getDataStructures();
		
        return GsonHelper.toJson(dataStructures);
	}


}
