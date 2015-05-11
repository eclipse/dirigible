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

package org.eclipse.dirigible.runtime.java.dynamic.compilation;

public class InMemoryCompilationException extends RuntimeException {

	private static final long serialVersionUID = -7902267851162966022L;

	public InMemoryCompilationException(
			InMemoryDiagnosticListener diagnosticListener) {
		this(diagnosticListener.getErrors());
	}
	
	public InMemoryCompilationException(String message){
		super(message);
	}
	
	public InMemoryCompilationException(Throwable cause){
		super(cause);
	}
}
