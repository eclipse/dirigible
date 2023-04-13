declare module "@dirigible/mail" {
    interface MailClient {
        /**
         * Send mail to MailRecipients
         * @param from
         * @param recipients
         * @param subject
         * @param text
         * @param sybType
         */
        send(from: string, recipients: { to: string[] | string, cc?: string[] | string, bcc?: string[] | string} | string, subject: string, text: string, sybType);

        toJavaProperties(properties);

        parseRecipients(recipients, type);
    }

    module client {
        /**
         * Get mail client with the provided MailClientOptions, if no options are provided, the default mail client configuration will be used
         * @param options
         */
        function getClient(options): MailClient;

        /**
         * Send mail using the default mail client configuration to MailRecipients
         * @param from
         * @param recipients
         * @param subject
         * @param text
         * @param sybType
         */
        function send(from: string, recipients: { to: string[] | string, cc?: string[] | string, bcc?: string[] | string} | string , subject: string, text: string, sybType);
    }

}
