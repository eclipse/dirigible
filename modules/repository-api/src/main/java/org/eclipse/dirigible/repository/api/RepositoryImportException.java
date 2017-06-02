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

public class RepositoryImportException extends RepositoryException {

	private static final long serialVersionUID = -163847774919514248L;

	public RepositoryImportException() {
		super();
	}

	public RepositoryImportException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryImportException(String message) {
		super(message);
	}

	public RepositoryImportException(Throwable cause) {
		super(cause);
	}

}
