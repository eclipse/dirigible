/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.service;

import javax.ws.rs.core.Response;

public interface IRestExceptionHandler<T extends Throwable> {

	/**
	 * To response.
	 *
	 * @param exception the exception
	 * @return the response
	 */
	Response toResponse(T exception);

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	Class<? extends AbstractExceptionHandler<T>> getType();

}