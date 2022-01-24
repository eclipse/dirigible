package org.eclipse.dirigible.mail.env;

import org.eclipse.dirigible.api.v3.mail.api.IMailConfigurationProvider;
import org.eclipse.dirigible.commons.config.Configuration;

import java.util.Properties;

public class EnvMailConfigProvider implements IMailConfigurationProvider {

    // Mail properties
    private static final String MAIL_USER = "mail.user";
    private static final String MAIL_PASSWORD = "mail.password";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";

    // SMTPS properties
    private static final String MAIL_SMTPS_HOST = "mail.smtps.host";
    private static final String MAIL_SMTPS_PORT = "mail.smtps.port";
    private static final String MAIL_SMTPS_AUTH = "mail.smtps.auth";

    // SMTP properties
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    private static final String DIRIGIBLE_MAIL_USERNAME = "DIRIGIBLE_MAIL_USERNAME";
    private static final String DIRIGIBLE_MAIL_PASSWORD = "DIRIGIBLE_MAIL_PASSWORD";

    private static final String DIRIGIBLE_MAIL_TRANSPORT_PROTOCOL = "DIRIGIBLE_MAIL_TRANSPORT_PROTOCOL";

    // SMTPS properties
    private static final String DIRIGIBLE_MAIL_SMTPS_HOST = "DIRIGIBLE_MAIL_SMTPS_HOST";
    private static final String DIRIGIBLE_MAIL_SMTPS_PORT = "DIRIGIBLE_MAIL_SMTPS_PORT";
    private static final String DIRIGIBLE_MAIL_SMTPS_AUTH = "DIRIGIBLE_MAIL_SMTPS_AUTH";

    // SMTP properties
    private static final String DIRIGIBLE_MAIL_SMTP_HOST = "DIRIGIBLE_MAIL_SMTP_HOST";
    private static final String DIRIGIBLE_MAIL_SMTP_PORT = "DIRIGIBLE_MAIL_SMTP_PORT";
    private static final String DIRIGIBLE_MAIL_SMTP_AUTH = "DIRIGIBLE_MAIL_SMTP_AUTH";

    // Default values
    private static final String DEFAULT_MAIL_TRANSPORT_PROTOCOL = "smtps";

    private static final String PROVIDER_NAME = "environment";

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();

        addValue(properties, MAIL_USER, DIRIGIBLE_MAIL_USERNAME);
        addValue(properties, MAIL_PASSWORD, DIRIGIBLE_MAIL_PASSWORD);

        addValue(properties, MAIL_TRANSPORT_PROTOCOL, DIRIGIBLE_MAIL_TRANSPORT_PROTOCOL, DEFAULT_MAIL_TRANSPORT_PROTOCOL);

        addValue(properties, MAIL_SMTPS_HOST, DIRIGIBLE_MAIL_SMTPS_HOST);
        addValue(properties, MAIL_SMTPS_PORT, DIRIGIBLE_MAIL_SMTPS_PORT);
        addValue(properties, MAIL_SMTPS_AUTH, DIRIGIBLE_MAIL_SMTPS_AUTH);

        addValue(properties, MAIL_SMTP_HOST, DIRIGIBLE_MAIL_SMTP_HOST);
        addValue(properties, MAIL_SMTP_PORT, DIRIGIBLE_MAIL_SMTP_PORT);
        addValue(properties, MAIL_SMTP_AUTH, DIRIGIBLE_MAIL_SMTP_AUTH);

        return properties;
    }

    private static void addValue(Properties properties, String key, String envKey) {
        addValue(properties, key, envKey, null);
    }

    private static void addValue(Properties properties, String key, String envKey, String defaultValue) {
        String value = Configuration.get(envKey);
        if (value != null) {
            properties.put(key, value);
        } else if (defaultValue != null) {
            properties.put(key, defaultValue);
        }
    }
}
