package org.eclipse.dirigible.runtime.core.version;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;

public class VersionProcessor {
	
	public String version() throws IOException {
		Version version = new Version();
		final Properties properties = new Properties();
		properties.load(VersionProcessor.class.getResourceAsStream("/dirigible.properties"));
		version.setProductName(properties.getProperty("DIRIGIBLE_PRODUCT_NAME"));
		version.setProductVersion(properties.getProperty("DIRIGIBLE_PRODUCT_VERSION"));
		version.setProductType(properties.getProperty("DIRIGIBLE_PRODUCT_TYPE"));
		version.setInstanceName(properties.getProperty("DIRIGIBLE_INSTANCE_NAME"));
		version.getModules().addAll(DirigibleModulesInstallerModule.getModules());
		return GsonHelper.GSON.toJson(version);
	}

}
