package org.eclipse.dirigible.components.base.tenant;

public interface TenantResult<Result> {

    Tenant getTenant();

    Result getResult();

}
