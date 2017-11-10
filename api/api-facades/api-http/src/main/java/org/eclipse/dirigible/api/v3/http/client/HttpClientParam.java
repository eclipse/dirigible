/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.http.client;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpClientParam.
 */
public class HttpClientParam {

	/** The name. */
	private String name;

	/** The value. */
	private String value;

	/**
	 * Instantiates a new http client param.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public HttpClientParam(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
