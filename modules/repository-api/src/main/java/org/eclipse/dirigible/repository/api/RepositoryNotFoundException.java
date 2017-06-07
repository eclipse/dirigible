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

public class RepositoryNotFoundException extends RepositoryReadException {


	public RepositoryNotFoundException() {
		super();
	}

	public RepositoryNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryNotFoundException(String message) {
		super(message);
	}

	public RepositoryNotFoundException(Throwable cause) {
		super(cause);
	}

}
