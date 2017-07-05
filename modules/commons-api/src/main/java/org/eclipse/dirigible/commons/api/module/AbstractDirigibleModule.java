package org.eclipse.dirigible.commons.api.module;

import org.eclipse.dirigible.commons.api.logging.LoggingHelper;

import com.google.inject.AbstractModule;

public abstract class AbstractDirigibleModule extends AbstractModule {
	
	protected LoggingHelper loggingHelper;
	
	public abstract String getName();
	
	public void setLoggingHelper(LoggingHelper loggingHelper) {
		this.loggingHelper = loggingHelper;
	}

}
