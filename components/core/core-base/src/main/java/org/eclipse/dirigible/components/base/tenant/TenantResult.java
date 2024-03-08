package org.eclipse.dirigible.components.base.tenant;

/**
 * A result of an execution for a tenant.
 *
 * @param <Result> result type
 */
public interface TenantResult<Result> {

    Tenant getTenant();

    Result getResult();

}
