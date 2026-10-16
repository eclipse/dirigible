/**
 * @module security/tenants
 * @package @aerokit/sdk/security
 * @name Tenants
 * @overview
 *
 * The Tenants class lists the tenants the current user holds a role in and runs work across them. It is built for a user who works
 * for many tenants at once - an accounting practice keeping the books of its clients, each one a tenant - and who has to run the same
 * operation (a period close, a statutory file) in each of them.
 *
 * ### Key Features:
 * - **Granted tenants**: the tenants the user's memberships grant and that can be entered now, with the roles held in each.
 * - **Runs across tenants**: the work runs once per tenant, in that tenant's data and with that tenant's roles - never the roles of the
 *   tenant the session has selected. A failure in one tenant is reported for it and the run carries on with the next.
 * - **Audit**: every visit is recorded in the cross-tenant audit trail, readable by each tenant's administrators.
 *
 * Available with the token-groups tenant resolution only (`DIRIGIBLE_TENANT_RESOLUTION_STRATEGY=TOKEN_GROUPS`); check
 * `Tenants.isAvailable()` first.
 *
 * ### Example Usage:
 * ```ts
 * import { Tenants } from "@aerokit/sdk/security";
 *
 * const results = Tenants.forEachGrantedTenant("period-close", (tenantId) => {
 *     // runs with the data and the roles of tenantId
 *     return closePeriod("2026-09");
 * });
 * results.filter(r => !r.succeeded).forEach(r => console.error(`${r.tenantId}: ${r.error}`));
 * ```
 */

const TenantsFacade = Java.type("org.eclipse.dirigible.components.api.security.TenantsFacade");

/**
 * A tenant the current user holds a role in.
 */
export interface GrantedTenant {
    /** The tenant id. */
    id: string;
    /** The tenant name. */
    name: string;
    /** The roles held in the tenant. */
    roles: string[];
}

/**
 * What one tenant's visit of a run came to.
 */
export interface CrossTenantResult<T> {
    /** The tenant visited. */
    tenantId: string;
    /** Whether the work returned normally. */
    succeeded: boolean;
    /** What the work returned; undefined on a failure. */
    result?: T;
    /** Why the work failed; undefined on a success. */
    error?: string;
}

/**
 * Lists the tenants the current user holds a role in and runs work across them.
 */
export class Tenants {

    /**
     * Whether runs across tenants are available on this instance.
     *
     * @returns True with the token-groups tenant resolution.
     */
    public static isAvailable(): boolean {
        return TenantsFacade.isAvailable();
    }

    /**
     * The tenants the current user holds a role in and can enter now.
     *
     * @returns The tenants, with the roles held in each.
     */
    public static getGrantedTenants(): GrantedTenant[] {
        return JSON.parse(TenantsFacade.getGrantedTenants());
    }

    /**
     * Runs the work once per tenant the current user can enter, in that tenant's data and with that tenant's roles.
     *
     * @param operation What the run does, recorded in the audit trail (e.g. 'period-close').
     * @param work The work, given the id of the tenant it runs in.
     * @returns One result per tenant.
     */
    public static forEachGrantedTenant<T>(operation: string, work: (tenantId: string) => T): CrossTenantResult<T>[] {
        const results = TenantsFacade.forEachGrantedTenant(operation, (tenantId: string) => work(tenantId));
        const mapped: CrossTenantResult<T>[] = [];
        for (let i = 0; i < results.size(); i++) {
            const r = results.get(i);
            mapped.push({
                tenantId: r.tenantId(),
                succeeded: r.succeeded(),
                result: r.succeeded() ? r.result() : undefined,
                error: r.error() ?? undefined
            });
        }
        return mapped;
    }
}
