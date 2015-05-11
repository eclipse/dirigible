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

package org.eclipse.dirigible.runtime.logger;

import org.slf4j.LoggerFactory;

import org.eclipse.dirigible.repository.logging.Logger;

public class LoggerStub extends Logger {

	public static Logger getLogger(String name) {
		return new LoggerStub(LoggerFactory.getLogger(name), java.util.logging.Logger.getLogger(name));
	}

	public static Logger getLogger(Class<?> clazz) {
		return new LoggerStub(LoggerFactory.getLogger(clazz), java.util.logging.Logger.getLogger(clazz
				.getCanonicalName()));
	}
	
	protected LoggerStub(org.slf4j.Logger logger1,
			java.util.logging.Logger logger2) {
		super(logger1, logger2);
		super.printInSystemOutput = true;
	}
}
