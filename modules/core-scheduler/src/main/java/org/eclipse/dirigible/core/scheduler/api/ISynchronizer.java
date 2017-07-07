package org.eclipse.dirigible.core.scheduler.api;

public interface ISynchronizer {
	
	public void synchronize();
	
	public void cleanup() throws SynchronizationException;

}
