package org.eclipse.dirigible.runtime.mail;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.EnvUtils;
import org.eclipse.dirigible.runtime.scripting.IMailService;

public class MailServiceFactory {

	public static final String DEFAULT_MAIL_SERVICE = ICommonConstants.INIT_PARAM_DEFAULT_MAIL_SERVICE;
	public static final String DEFAULT_MAIL_SERVICE_PROVIDED = ICommonConstants.INIT_PARAM_DEFAULT_MAIL_SERVICE_PROVIDED;
	public static final String DEFAULT_MAIL_SERVICE_BUILTIN = ICommonConstants.INIT_PARAM_DEFAULT_MAIL_SERVICE_BUILTIN;

	public static IMailService createMailService(HttpServletRequest request) {
		IMailService mailService = null;
		if (request != null) {
			// String serviceSource = (String) request.getSession().getAttribute(DEFAULT_MAIL_SERVICE);
			String serviceSource = EnvUtils.getEnv(DEFAULT_MAIL_SERVICE);
			if (DEFAULT_MAIL_SERVICE_PROVIDED.equals(serviceSource)) {
				mailService = new MailSenderProvidedService(request);
			} else {
				mailService = new MailSenderService(request);
			}
		}
		return mailService;
	}

}
