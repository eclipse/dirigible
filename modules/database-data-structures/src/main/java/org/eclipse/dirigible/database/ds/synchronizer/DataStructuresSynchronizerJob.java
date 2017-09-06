package org.eclipse.dirigible.database.ds.synchronizer;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizerJob;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizer;

public class DataStructuresSynchronizerJob extends AbstractSynchronizerJob {
	
	private DataStructuresSynchronizer dataStructureSynchronizer = StaticInjector.getInjector().getInstance(DataStructuresSynchronizer.class);
	
	@Override
	public ISynchronizer getSynchronizer() {
		return dataStructureSynchronizer;
	}

}
