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

}
