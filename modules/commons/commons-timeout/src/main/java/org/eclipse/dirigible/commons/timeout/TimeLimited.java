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
package org.eclipse.dirigible.commons.timeout;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeLimited {
	
	private static final Logger logger = LoggerFactory.getLogger(TimeLimited.class);
	
	private static final String DIRIGIBLE_JOB_DEFAULT_TIMEOUT = "DIRIGIBLE_JOB_DEFAULT_TIMEOUT";
	
	private static final String DEFAULT_TIMEOUT = "3";

	public static synchronized void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
		runWithTimeout(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		}, timeout, timeUnit);
	}

	private static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<T> future = executor.submit(callable);
		executor.shutdown();
		try {
			return future.get(timeout, timeUnit);
		} catch (TimeoutException e) {
			future.cancel(true);
			throw e;
		} catch (ExecutionException e) {
			Throwable t = e.getCause();
			if (t instanceof Error) {
				throw (Error) t;
			} else if (t instanceof Exception) {
				throw (Exception) t;
			} else {
				throw new IllegalStateException(t);
			}
		}
	}
	
	public static final int getTimeout() {
		String defaultTimeout = Configuration.get(DIRIGIBLE_JOB_DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
		try {
			return Integer.parseInt(defaultTimeout);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
			return Integer.parseInt(DEFAULT_TIMEOUT);
		}
	}
	
	public static final int getTimeoutInMillis() {
		return getTimeout() * 1000 * 60;
	}

}
