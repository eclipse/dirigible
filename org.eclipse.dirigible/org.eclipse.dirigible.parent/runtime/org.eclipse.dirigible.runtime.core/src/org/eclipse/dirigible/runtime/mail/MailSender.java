/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IMailService;

public class MailSender implements IMailService {

	private static final String MAIL_SERVICE_IS_NOT_AVAILABLE = "Mail Service is not available";

	private HttpServletRequest request;

	public MailSender(HttpServletRequest request) {
		this.request = request;
	}

	private static final Logger logger = Logger.getLogger(MailSender.class.getCanonicalName());

	@Override
	public String sendMail(String from, String to, String subject, String content) {
		try {
			Session smtpSession = (Session) System.getProperties().get(ICommonConstants.MAIL_SESSION);
			if (smtpSession == null) {
				smtpSession = (Session) this.request.getSession().getAttribute(ICommonConstants.MAIL_SESSION);
			}
			if (smtpSession == null) {
				throw new Exception(MAIL_SERVICE_IS_NOT_AVAILABLE);
			}
			Transport transport = smtpSession.getTransport();
			transport.connect();

			MimeMessage mimeMessage = createMimeMessage(smtpSession, from, to, subject, content);
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			logger.error(this.getClass().getCanonicalName() + "#sendMail()", e); //$NON-NLS-1$
			// e.printStackTrace();
			return e.getMessage();
			// throw new Exception(e);
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
