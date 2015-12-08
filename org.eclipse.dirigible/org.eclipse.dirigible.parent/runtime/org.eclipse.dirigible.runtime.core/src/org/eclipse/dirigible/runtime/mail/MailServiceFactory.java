package org.eclipse.dirigible.runtime.mail;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.runtime.scripting.IMailService;

public class MailServiceFactory {

	public static final String DEFAULT_MAIL_SERVICE = "mailSender"; //$NON-NLS-1$
	public static final String DEFAULT_MAIL_SERVICE_PROVIDED = "provided"; //$NON-NLS-1$
	public static final String DEFAULT_MAIL_SERVICE_BUILTIN = "built-in"; //$NON-NLS-1$

	public static IMailService createMailService(HttpServletRequest request) {
		IMailService mailService = null;
		if (request != null) {
			String serviceSource = (String) request.getSession().getAttribute(DEFAULT_MAIL_SERVICE);
			if (DEFAULT_MAIL_SERVICE_PROVIDED.equals(serviceSource)) {
				mailService = new MailSenderProvidedService(request);
			} else {
				mailService = new MailSenderService(request);
			}
		}
		return mailService;
	}

}
