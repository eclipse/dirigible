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

public interface TenantProvisioningStep {

    /**
     * This step will be executed when there is a tenant in INITIAL status.<br>
     * Implement what is required to provision the passed tenant.
     *
     * @param tenant
     * @throws TenantProvisioningException in case of error.
     */
    void execute(Tenant tenant) throws TenantProvisioningException;

}
