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

package org.eclipse.dirigible.repository.api;

/**
 * The {@link RepositoryException} is thrown in situations when a user is trying
 * to use any of the repository API in a way that is invalid or not expected.<br>
 * One such example is when passing <code>null</code> to a method when it does
 * not expect it. This would likely result in a {@link IllegalArgumentException}
 * or a {@link NullPointerException} depending on the implementation's code
 * conventions. The first exception type is preferred though.
 * 
 */
public class RepositoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RepositoryException() {
		super();
	}

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(Throwable ex) {
		super(ex);
	}

	public RepositoryException(String message, Throwable ex) {
		super(message, ex);
	}

}
