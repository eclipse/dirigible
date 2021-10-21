declare module "@dirigible/mail" {
    interface MailClient {
        send(from: string, recepients: string [], subject: string, text: string, sybType);

        toJavaProperties(properties);

        parseRecipients(recipients, type);
    }

    module client {
        function getClient(options): MailClient;

        function send(from: string, recepients: string [], subject: string, text: string, sybType);
    }

}