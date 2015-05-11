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

package org.eclipse.dirigible.ide.workspace.ui.shared;

public class ValidationStatus implements IValidationStatus {

	private static enum Type {
		OK, ERROR, WARNING
	}

	private final Type type;

	private final String message;

	private ValidationStatus(Type type, String message) {
		this.type = type;
		this.message = message;
	}

	public boolean isOK() {
		return type == Type.OK;
	}

	public boolean hasErrors() {
		return (type == Type.ERROR);
	}

	public boolean hasWarnings() {
		return (type == Type.WARNING);
	}

	public String getMessage() {
		return message;
	}

	public static ValidationStatus createOk() {
		return new ValidationStatus(Type.OK, null);
	}

	public static ValidationStatus createWarning(String message) {
		return new ValidationStatus(Type.WARNING, message);
	}

	public static ValidationStatus createError(String message) {
		return new ValidationStatus(Type.ERROR, message);
	}

}
