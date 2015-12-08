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

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.scripting.IMailService;

public class MailSenderProvidedService implements IMailService {

	private static final String MAIL_SERVICE_IS_NOT_AVAILABLE = "Mail Service (Provided) is not available";

	private HttpServletRequest request;

	MailSenderProvidedService(HttpServletRequest request) {
		this.request = request;
	}

	private static final Logger logger = Logger.getLogger(MailSenderProvidedService.class.getCanonicalName());

	@Override
	public String sendMail(String from, String to, String subject, String content) {
		try {
			Object providedMailSender = System.getProperties().get(ICommonConstants.MAIL_SESSION_PROVIDED);
			if ((providedMailSender == null) && (this.request != null)) {
				providedMailSender = this.request.getSession().getAttribute(ICommonConstants.MAIL_SESSION_PROVIDED);
			}
			if (providedMailSender == null) {
				throw new Exception(MAIL_SERVICE_IS_NOT_AVAILABLE);
			}

			Method method = providedMailSender.getClass().getMethod("sendMail", String.class, String.class, String.class, String.class);
			return (String) method.invoke(providedMailSender, from, to, subject, content);

		} catch (Exception e) {
			logger.error(this.getClass().getCanonicalName() + "#sendMail()", e); //$NON-NLS-1$
			return e.getMessage();
		}
	}

}
