/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.core.indexing.api;

public class IndexingException extends Exception {

	private static final long serialVersionUID = 5800180600419241248L;

	public IndexingException() {
		super();
	}

	public IndexingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IndexingException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexingException(String message) {
		super(message);
	}

	public IndexingException(Throwable cause) {
		super(cause);
	}

}
