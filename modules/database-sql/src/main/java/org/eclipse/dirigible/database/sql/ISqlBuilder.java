/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql;

// TODO: Auto-generated Javadoc
/**
 * The Interface ISqlBuilder.
 */
public interface ISqlBuilder extends ISqlKeywords {

	/**
	 * Generate.
	 *
	 * @return the string
	 */
	public String generate();

	/**
	 * Builds the.
	 *
	 * @return the string
	 */
	public String build();

}
