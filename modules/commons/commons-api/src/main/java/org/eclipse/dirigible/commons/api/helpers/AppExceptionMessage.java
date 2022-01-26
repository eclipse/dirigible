/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.helpers;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Error message that is returned if an application error occur.
 */
@XmlRootElement
public class AppExceptionMessage {

	private String message;

	private int status;

	/**
	 * Needed for serialization / de-serialization.
	 */
	public AppExceptionMessage() {
	}

	/**
	 * Default constructor.
	 *
	 * @param status
	 *            the response server status.
	 * @param message
	 *            the actual error message.
	 */
	public AppExceptionMessage(Status status, String message) {
		this.status = status.getStatusCode();
		this.message = message;
	}

	/**
	 * Gets the message.
	 *
	 * @return the error message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the error message.
	 *
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Sets the server response status.
	 *
	 * @return the server response status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the server response status.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
