/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener;

public class ListenersException extends Exception {

	private static final long serialVersionUID = 4325456556365693528L;

	public ListenersException(String arg0) {
		super(arg0);
	}

	public ListenersException(Throwable arg0) {
		super(arg0);
	}

	public ListenersException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
