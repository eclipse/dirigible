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
package org.eclipse.dirigible.components.tenants.tenant;

import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantResult;

class TenantResultImpl<Result> implements TenantResult<Result> {

    private final Tenant tenant;
    private final Result result;

    TenantResultImpl(Tenant tenant, Result result) {
        this.tenant = tenant;
        this.result = result;
    }

    @Override
    public Tenant getTenant() {
        return tenant;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "TenantResultImpl [tenant=" + tenant + ", result=" + result + "]";
    }

}
