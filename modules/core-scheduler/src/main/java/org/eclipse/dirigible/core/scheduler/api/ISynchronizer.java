package org.eclipse.dirigible.core.scheduler.api;

public interface ISynchronizer {
	
	public void synchronize();
	
	public void synchronizePredelivered() throws SynchronizationException;
	
	public void synchronizeRegistry() throws SynchronizationException;
	
	public void cleanup() throws SynchronizationException;

}
