package org.eclipse.dirigible.graalium.core.graal.configuration;

/**
 * The Class Configuration.
 */
public class Configuration {

    /**
     * Gets the.
     *
     * @param environmentVariableName the environment variable name
     * @param defaultValue the default value
     * @return the string
     */
    public static String get(String environmentVariableName, String defaultValue) {
        String maybeEnvironmentVariableValue = System.getenv(environmentVariableName);
        return maybeEnvironmentVariableValue != null ? maybeEnvironmentVariableValue : defaultValue;
    }
}
