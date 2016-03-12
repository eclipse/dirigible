package org.eclipse.dirigible.runtime.listener;

public interface IListenerEventProcessorProvider {

	public String getTriggerType();

	public IListenerEventProcessor createListenerEventProcessor();
}
