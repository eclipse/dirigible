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
