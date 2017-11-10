/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.managed;

import org.eclipse.dirigible.database.api.DatabaseException;

public class ManagedDatabaseException extends DatabaseException {

	private static final long serialVersionUID = -2161860568272479874L;

	public ManagedDatabaseException() {
		super();
	}

	public ManagedDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ManagedDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagedDatabaseException(String message) {
		super(message);
	}

	public ManagedDatabaseException(Throwable cause) {
		super(cause);
	}

}
