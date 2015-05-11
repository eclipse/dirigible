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

package org.eclipse.dirigible.ide.common;

import java.net.URI;

import org.eclipse.jface.dialogs.IInputValidator;

public class UriValidator implements IInputValidator {

	private static final long serialVersionUID = 553319995495098208L;

	/**
	 * Validates the String. Returns null for no error, or an error message
	 * 
	 * @param newText
	 *            the String to validate
	 * @return String
	 */
	public String isValid(String newText) {
		try {
			URI.create(newText);
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
