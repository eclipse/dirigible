/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.api;

public class DataStructuresException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public DataStructuresException() {
		super();
	}

	public DataStructuresException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataStructuresException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataStructuresException(String message) {
		super(message);
	}

	public DataStructuresException(Throwable cause) {
		super(cause);
	}

}
