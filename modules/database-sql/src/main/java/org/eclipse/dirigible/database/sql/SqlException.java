/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql;

public class SqlException extends RuntimeException {

	private static final long serialVersionUID = 4878658205810743068L;

	public SqlException() {
		super();
	}

	public SqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SqlException(String message, Throwable cause) {
		super(message, cause);
	}

	public SqlException(String message) {
		super(message);
	}

	public SqlException(Throwable cause) {
		super(cause);
	}

	
}
