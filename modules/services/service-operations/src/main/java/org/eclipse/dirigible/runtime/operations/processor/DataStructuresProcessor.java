/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.service.DataStructuresCoreService;

public class DataStructuresProcessor {
	
	@Inject
	private DataStructuresCoreService dataStructuresCoreService;
	
	public String list() throws DataStructuresException {
		
		List<DataStructureModel> dataStructures = dataStructuresCoreService.getDataStructures();
		
        return GsonHelper.GSON.toJson(dataStructures);
	}


}
