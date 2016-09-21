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

import java.io.IOException;

import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailInjector implements IInjector {

	static final String DEFAULT_MAIL_SESSION = "MailSession"; //$NON-NLS-1$

	static final String PROVIDED_MAIL_SESSION = "MailSessionProvided"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(MailInjector.class);

	@Override
	public void injectOnRequest(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Session session = (Session) req.getAttribute(DEFAULT_MAIL_SESSION);
		if (session == null) {
			try {
				session = lookupMailSession();
				if (session != null) {
					req.setAttribute(DEFAULT_MAIL_SESSION, session);
				} else {
					logger.warn(InitParametersInjector.INIT_PARAM_JNDI_MAIL_SESSION + " not present");
				}
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}

		MailSenderProvided mailSenderProvided = (MailSenderProvided) req.getAttribute(PROVIDED_MAIL_SESSION);
		if (mailSenderProvided == null) {
			mailSenderProvided = new MailSenderProvided(session);
			req.setAttribute(PROVIDED_MAIL_SESSION, mailSenderProvided);
		}

	}

	@Override
	public void injectOnStart(ServletConfig servletConfig) throws ServletException, IOException {

		Session session = (Session) System.getProperties().get(DEFAULT_MAIL_SESSION);
		if (session == null) {
			try {
				session = lookupMailSession();
				if (session != null) {
					System.getProperties().put(DEFAULT_MAIL_SESSION, session);
				} else {
					logger.warn(InitParametersInjector.INIT_PARAM_JNDI_MAIL_SESSION + " not present");
				}
			} catch (Exception e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
		}

		MailSenderProvided mailSenderProvided = (MailSenderProvided) System.getProperties().get(PROVIDED_MAIL_SESSION);
		if (mailSenderProvided == null) {
			mailSenderProvided = new MailSenderProvided(session);
			System.getProperties().put(PROVIDED_MAIL_SESSION, mailSenderProvided);
		}

	}

	/**
	 * Retrieve the MailSession from the target server environment
	 *
	 * @return
	 * @throws NamingException
	 */
	private Session lookupMailSession() throws NamingException {
		final InitialContext ctx = new InitialContext();
		String key = InitParametersInjector.get(InitParametersInjector.INIT_PARAM_JNDI_MAIL_SESSION);
		if (key != null) {
			return (Session) ctx.lookup(key);
		}
		return null;
	}

}
