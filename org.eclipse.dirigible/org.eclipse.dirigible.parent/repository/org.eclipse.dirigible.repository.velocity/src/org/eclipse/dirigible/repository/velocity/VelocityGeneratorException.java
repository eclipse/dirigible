/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.velocity;

public class VelocityGeneratorException extends Exception {

	private static final long serialVersionUID = 5822190560203627957L;

	public VelocityGeneratorException() {
	}

	public VelocityGeneratorException(String arg0) {
		super(arg0);
	}

	public VelocityGeneratorException(Throwable arg0) {
		super(arg0);
	}

	public VelocityGeneratorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
