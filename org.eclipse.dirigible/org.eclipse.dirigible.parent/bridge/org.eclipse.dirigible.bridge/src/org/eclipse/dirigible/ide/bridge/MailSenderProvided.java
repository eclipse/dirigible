/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.bridge;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

public class MailSenderProvided {

	private static final String MAIL_SERVICE_IS_NOT_AVAILABLE = "Mail Service is not available";

	private Session session;

	public MailSenderProvided(Session session) {
		this.session = session;
	}

	private static final Logger logger = Logger.getLogger(MailSenderProvided.class.getCanonicalName());

	public String sendMail(String from, String to, String subject, String content) {
		try {

			Transport transport = this.session.getTransport();
			transport.connect();

			MimeMessage mimeMessage = createMimeMessage(this.session, from, to, subject, content);
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			logger.severe(String.format("Exception occurred in class %s in method %s with message %s", this.getClass().getCanonicalName(), //$NON-NLS-1$
					"#sendMail()", e.getMessage()));
			e.printStackTrace();
			return e.getMessage();
		}
		return ""; //$NON-NLS-1$
	}

	private static MimeMessage createMimeMessage(Session smtpSession, String from, String to, String subjectText, String mailText)
			throws MessagingException {

		MimeMessage mimeMessage = new MimeMessage(smtpSession);
		InternetAddress[] fromAddress = InternetAddress.parse(from);
		InternetAddress[] toAddresses = InternetAddress.parse(to);
		mimeMessage.setFrom(fromAddress[0]);
		mimeMessage.setRecipients(RecipientType.TO, toAddresses);
		mimeMessage.setSubject(subjectText, "UTF-8"); //$NON-NLS-1$

		MimeMultipart multiPart = new MimeMultipart("alternative"); //$NON-NLS-1$
		MimeBodyPart part = new MimeBodyPart();
		part.setText(mailText, "utf-8", "plain"); //$NON-NLS-1$ //$NON-NLS-2$
		multiPart.addBodyPart(part);
		mimeMessage.setContent(multiPart);

		return mimeMessage;
	}

}
