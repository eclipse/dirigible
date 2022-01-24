package org.eclipse.dirigible.api.v3.mail.api;

import java.util.Properties;

public interface IMailConfigurationProvider {

    /**
     * Gets the name of the provider.
     *
     * @return the name
     */
     String getName();

    /**
     * Get Properties for MailClient Instance
     *
     * @return MailClient instance
     */
    Properties getProperties();
}
