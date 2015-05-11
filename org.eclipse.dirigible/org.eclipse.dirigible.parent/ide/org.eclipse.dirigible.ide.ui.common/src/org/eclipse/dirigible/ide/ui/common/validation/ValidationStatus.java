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

package org.eclipse.dirigible.ide.ui.common.validation;

public class ValidationStatus implements IValidationStatus {

	private static final String OK_STATUS = Messages.ValidationStatus_OK_STATUS;

	private static final String WARNING_STATUS = Messages.ValidationStatus_WARNING_STATUS;

	private static final String ERROR_STATUS = Messages.ValidationStatus_ERROR_STATUS;

	private static enum Type {
		OK, ERROR, WARNING
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

	private final Type type;

	private final String message;

	private ValidationStatus(Type type, String message) {
		this.type = type;
		this.message = message;
	}

	public boolean hasErrors() {
		return (type == Type.ERROR);
	}

	public boolean hasWarnings() {
		return (type == Type.WARNING);
	}

	@Override
	public boolean isOK() {
		return !hasWarnings() && !hasErrors();
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		if (hasErrors()) {
			return ERROR_STATUS + getMessage();
		} else if (hasWarnings()) {
			return WARNING_STATUS + getMessage();
		} else {
			return OK_STATUS;
		}
	}

	/**
	 * Creates and returns a new ValidationStatus instance with status and
	 * message based on parameters.
	 * 
	 * @param validationStatus_1
	 * @param validationStatus_2
	 * @return a newly created status based on the status of the parameters.
	 */
	public static IValidationStatus getValidationStatus(
			IValidationStatus validationStatus_1,
			IValidationStatus validationStatus_2) {
		// check errors
		if (validationStatus_1.hasErrors()) {
			if (validationStatus_2.hasErrors()) {
				return ValidationStatus
						.createError(validationStatus_1.getMessage() + " \n" //$NON-NLS-1$
								+ validationStatus_2.getMessage());
			} else {
				return validationStatus_1;
			}
		}

		if (validationStatus_2.hasErrors())
			return validationStatus_2;

		// check warnings
		if (validationStatus_1.hasWarnings()) {
			if (validationStatus_2.hasWarnings()) {
				return ValidationStatus
						.createWarning(validationStatus_1.getMessage() + " \n" //$NON-NLS-1$
								+ validationStatus_2.getMessage());
			} else {
				return validationStatus_1;
			}
		} else {
			return validationStatus_2;
		}
	}

}
