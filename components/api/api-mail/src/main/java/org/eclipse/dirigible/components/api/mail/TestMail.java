package org.eclipse.dirigible.components.api.mail;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestMail {

    public static void main(String[] args) throws MessagingException, IOException, ClassNotFoundException {
        // Class<?> providerClass = Class.forName("com.sun.mail.smtp.SMTPTransport");
        // Class<?> serviceClass = providerClass.asSubclass(jakarta.mail.Transport.class);
        // System.out.println(serviceClass);

        Properties sysProps = System.getProperties();
        System.out.println("System properties: " + sysProps);

        System.out.println("Running");
        Properties properties = new Properties();

        properties.setProperty("mail.smtp.port", "2525");
        properties.setProperty("mail.password", "06ec8a05a2ee6a");
        properties.setProperty("mail.user", "6eeaaee24cfdec");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        properties.setProperty("mail.transport.protocol", "smtp");

        MailClient client = new MailClient(properties);
        String[] cc = {};
        String[] bcc = {};
        String[] to = {"iliyan.velichkov@codbex.com"};
        Map<String, String> parts = Map.of("contentType", "type/html", "text", "Content!!!", "type", "text");
        client.send("from@example.com", to, cc, bcc, "Test email", List.of(parts));

        System.out.println("Done");
    }

    public static void main2(String[] args) throws NoSuchProviderException {
        String to = "iliyan.velichkov@codbex.com";
        String from = "from@example.com";
        String host = "sandbox.smtp.mailtrap.io"; // your SMTP host
        String username = "6eeaaee24cfdec"; // your SMTP username
        String password = "06ec8a05a2ee6a"; // your SMTP password

        // Get system properties
        // Properties properties = System.getProperties();
        Properties properties = new Properties();

        // Setup mail server
        // properties.setProperty("mail.smtp.host", host);
        // properties.setProperty("mail.smtp.port", "2525"); // or the port your SMTP server uses
        // properties.setProperty("mail.smtp.auth", "true");
        // properties.setProperty("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
        // properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.port", "2525");
        properties.setProperty("mail.password", "06ec8a05a2ee6a");
        properties.setProperty("mail.user", "6eeaaee24cfdec");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        properties.setProperty("mail.transport.protocol", "smtp");

        // Get the Session object.
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Transport transport = session.getTransport();
        System.out.println("Transport:" + transport.getClass());
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field
            message.setFrom(new InternetAddress(from));

            // Set To: header field
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Set the actual message
            message.setText("This is the actual message");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }
}

