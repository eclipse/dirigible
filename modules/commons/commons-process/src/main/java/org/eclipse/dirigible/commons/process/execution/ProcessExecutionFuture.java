/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.process.execution;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

import java.util.concurrent.CompletableFuture;

/**
 * The Class ProcessExecutionFuture.
 */
public class ProcessExecutionFuture extends CompletableFuture<Integer> implements ExecuteResultHandler {

	/**
	 * On process complete.
	 *
	 * @param i the i
	 */
	@Override
	public void onProcessComplete(int i) {
		complete(i);
	}

	/**
	 * On process failed.
	 *
	 * @param e the e
	 */
	@Override
	public void onProcessFailed(ExecuteException e) {
		completeExceptionally(e);
	}
}
