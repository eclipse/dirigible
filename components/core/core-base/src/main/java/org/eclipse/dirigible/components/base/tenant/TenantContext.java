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
package org.eclipse.dirigible.components.base.tenant;

import java.util.List;

public interface TenantContext {

    /**
     * Checks if is not initialized.
     *
     * @return true, if is not initialized
     */
    boolean isNotInitialized();

    /**
     * Checks if is initialized.
     *
     * @return true, if is initialized
     */
    boolean isInitialized();

    /**
     * Gets the current tenant.
     *
     * @return the current tenant
     */
    Tenant getCurrentTenant();

    /**
     * This method will execute callable.call() method on behalf of the specified tenant.
     *
     * @param tenant the tenant
     * @param callable the callable
     * @param <Result> result type
     * @return the result
     * @throws Exception the exception which is thrown by the passed callable
     */
    <Result> Result executeWithPossibleException(Tenant tenant, CallableResultAndException<Result> callable) throws Exception;

    /**
     * This method will execute callable.call() method on behalf of the specified tenant.
     *
     * @param tenant the tenant
     * @param callable the callable
     * @param <Result> result type
     * @return the result
     */
    <Result> Result execute(Tenant tenant, CallableResultAndNoException<Result> callable);

    /**
     * This method will execute callable.call() method on behalf of the specified tenant id.
     *
     * @param tenantId the tenant id
     * @param callable the callable
     * @param <Result> result type
     * @return the result
     * @throws TenantNotFoundException in case the provided tenant id doesn't exist
     */
    <Result> Result execute(String tenantId, CallableResultAndNoException<Result> callable) throws TenantNotFoundException;

    /**
     * This method will execute callable.call() for each provisioned tenant.
     *
     * @param <Result> result type
     * @param callable the callable
     * @return the results of the tenant executions
     */
    <Result> List<TenantResult<Result>> executeForEachTenant(CallableResultAndNoException<Result> callable);

}
