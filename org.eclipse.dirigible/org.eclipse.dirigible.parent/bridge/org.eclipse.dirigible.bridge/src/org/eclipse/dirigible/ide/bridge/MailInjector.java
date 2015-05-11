/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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

public class MailInjector implements Injector {
	
	public static final String JAVA_COMP_ENV_JDBC_DEFAULT_MAIL_SESSION = "java:comp/env/mail/SAPInternalNWCloudSession"; //$NON-NLS-1$
	public static final String DEFAULT_MAIL_SESSION = "DEFAULT_MAIL_SESSION"; //$NON-NLS-1$
	
	private static final Logger logger = LoggerFactory.getLogger(MailInjector.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.ide.bridge.Injector#inject(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void inject(ServletConfig servletConfig, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		Session session = (Session) req.getSession().getAttribute(DEFAULT_MAIL_SESSION);
		if (session == null) {
			try {
				session = lookupMailSession();
				req.getSession().setAttribute(DEFAULT_MAIL_SESSION, session);
			} catch (NamingException e) {
				logger.error(DirigibleBridge.class.getCanonicalName(), e);
			}
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
		return (Session) ctx.lookup(JAVA_COMP_ENV_JDBC_DEFAULT_MAIL_SESSION);
	}


}
