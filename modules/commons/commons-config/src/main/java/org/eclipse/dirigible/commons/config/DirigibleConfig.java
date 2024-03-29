package org.eclipse.dirigible.commons.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public enum DirigibleConfig {
    DEFAULT_DATA_SOURCE_NAME("DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT", "DefaultDB"), //
    SYSTEM_DATA_SOURCE_NAME("DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM", "SystemDB"), //
    SYNCHRONIZER_FREQUENCY("DIRIGIBLE_SYNCHRONIZER_FREQUENCY", "10"), //
    TRIAL_ENABLED("DIRIGIBLE_TRIAL_ENABLED", Boolean.FALSE.toString()), //
    REPOSITORY_LOCAL_ROOT_FOLDER("DIRIGIBLE_REPOSITORY_LOCAL_ROOT_FOLDER", "target"), //
    SINGLE_TENANT_MODE_ENABLED("DIRIGIBLE_SINGLE_TENANT_MODE_ENABLED", Boolean.TRUE.toString()), //
    TENANT_SUBDOMAIN_REGEX("DIRIGIBLE_TENANT_SUBDOMAIN_REGEX", "^([^\\.]+)\\..+$"), //
    BASIC_ADMIN_USERNAME("DIRIGIBLE_BASIC_USERNAME", toBase64("admin")), //
    BASIC_ADMIN_PASS("DIRIGIBLE_BASIC_PASSWORD", toBase64("admin"));

    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleConfig.class);
    private final String key;
    private final String defaultValue;

    DirigibleConfig(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getFromBase64Value() {
        String val = getStringValue();
        return fromBase64(val);
    }

    public String getStringValue() {
        return Configuration.get(key, defaultValue);
    }

    private static String fromBase64(String string) {
        return new String(Base64.getDecoder()
                                .decode(string),
                StandardCharsets.UTF_8);
    }

    private static String toBase64(String string) {
        return Base64.getEncoder()
                     .encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    public String getKey() {
        return key;
    }

    public boolean getBooleanValue() {
        String configValue = getStringValue();
        return Boolean.valueOf(configValue);
    }

    public int getIntValue() {
        String stringValue = getStringValue();
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Configuration with key [{}] has invalid non integer value: {}. Returning the defalt value [{}]", key, stringValue,
                    defaultValue, ex);
        }
        return Integer.parseInt(defaultValue);
    }

}
