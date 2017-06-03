package org.eclipse.dirigible.api.v3.auth;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.TestModeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacade implements IScriptingFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);
	
	private static final String GUEST = "guest";
	
	private static volatile String TEST = "test";
	
	public static final String getName() {
		String userName = null;
		try {
			userName = HttpRequestFacade.getRemoteUser();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (userName != null) {
			return userName;
		}
		if (Configuration.isTestModeEnabled()) {
			return TEST;
		}
		return GUEST;
	}
	
	public static final void setName(String userName) throws TestModeException {
		if (Configuration.isTestModeEnabled()) {
			TEST = userName;
			logger.warn(format("User name set programmatically {0}", userName));
		} else {
			throw new TestModeException("Setting the user name programmatically is supported only when the test mode is enabled");
		}
	}

}
