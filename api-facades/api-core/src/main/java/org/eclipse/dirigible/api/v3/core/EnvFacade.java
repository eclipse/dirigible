package org.eclipse.dirigible.api.v3.core;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvFacade implements IScriptingFacade {

	private static final Logger logger = LoggerFactory.getLogger(EnvFacade.class);

	public static final String get(String name) {
		logger.debug("API - EnvFacade.get() -> begin");
		String value = System.getenv(name);
		logger.debug("API - EnvFacade.get() -> end");
		return value;
	}

	public static final String list() {
		logger.debug("API - EnvFacade.get() -> begin");
		String value = GsonHelper.GSON.toJson(System.getenv());
		logger.debug("API - EnvFacade.get() -> end");
		return value;
	}

}
