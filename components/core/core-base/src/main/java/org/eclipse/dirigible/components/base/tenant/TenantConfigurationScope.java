/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.tenant;

import org.eclipse.dirigible.components.base.callable.CallableResultAndException;

/**
 * Gives work of a tenant scope that is not a request - a job fire, a process step - the tenant's
 * configuration overrides, which only the request filter loads otherwise. Declared here so the
 * engines that run such work need no dependency on the module that stores the overrides.
 */
public interface TenantConfigurationScope {

    /**
     * Runs work of the current tenant scope with the tenant's configuration overrides in the
     * thread-scoped configuration.
     *
     * @param <Result> the result type
     * @param <Exc> the exception type
     * @param work the work
     * @return the work's result
     * @throws Exc the work's exception
     */
    <Result, Exc extends Throwable> Result executeWithTenantOverrides(CallableResultAndException<Result, Exc> work) throws Exc;

}
