/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.utils;

import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * The Class UuidFacade.
 */
public class UuidFacade {

	/**
	 * Random.
	 *
	 * @return the string
	 */
	public static final String random() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Validate.
	 *
	 * @param uuid the uuid
	 * @return true, if successful
	 */
	public static final boolean validate(String uuid) {
		try {
			UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}

}
