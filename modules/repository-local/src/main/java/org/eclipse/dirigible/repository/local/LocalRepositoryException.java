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

package org.eclipse.dirigible.repository.local;

import org.eclipse.dirigible.repository.api.RepositoryException;

/**
 * Main exception for the DB repository implementation
 * 
 */
public class LocalRepositoryException extends RepositoryException {

	private static final long serialVersionUID = 116149128529374300L;

	public LocalRepositoryException() {
		super();
	}

	public LocalRepositoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public LocalRepositoryException(String arg0) {
		super(arg0);
	}

	public LocalRepositoryException(Throwable arg0) {
		super(arg0);
	}

}
