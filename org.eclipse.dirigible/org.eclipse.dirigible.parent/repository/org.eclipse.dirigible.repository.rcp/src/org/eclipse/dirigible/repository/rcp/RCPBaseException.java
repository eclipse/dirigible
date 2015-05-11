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

package org.eclipse.dirigible.repository.rcp;

import org.eclipse.dirigible.repository.api.RepositoryException;

/**
 * Main exception for the DB repository implementation
 * 
 */
public class RCPBaseException extends RepositoryException {

	private static final long serialVersionUID = 116149128529374300L;

	public RCPBaseException() {
		super();
	}

	public RCPBaseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RCPBaseException(String arg0) {
		super(arg0);
	}

	public RCPBaseException(Throwable arg0) {
		super(arg0);
	}

}
