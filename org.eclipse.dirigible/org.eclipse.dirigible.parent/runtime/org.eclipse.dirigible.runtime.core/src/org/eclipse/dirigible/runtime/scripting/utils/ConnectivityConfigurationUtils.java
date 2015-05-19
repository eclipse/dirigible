package org.eclipse.dirigible.runtime.scripting.utils;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.runtime.scripting.IConnectivityService;

public class ConnectivityConfigurationUtils implements IConnectivityService {

	@Override
	public Object getConnectivityConfiguration() {
		return System.getProperties().get(ICommonConstants.CONNECTIVITY_CONFIGURATION);
	}
	
}
