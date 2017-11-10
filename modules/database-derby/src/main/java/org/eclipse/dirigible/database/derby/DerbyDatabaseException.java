/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.derby;

import org.eclipse.dirigible.database.api.DatabaseException;

public class DerbyDatabaseException extends DatabaseException {

	private static final long serialVersionUID = -2161860568272479874L;

	public DerbyDatabaseException() {
		super();
	}

	public DerbyDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DerbyDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DerbyDatabaseException(String message) {
		super(message);
	}

	public DerbyDatabaseException(Throwable cause) {
		super(cause);
	}

}
