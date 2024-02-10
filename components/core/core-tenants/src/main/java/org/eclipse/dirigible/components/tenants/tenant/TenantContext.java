package org.eclipse.dirigible.components.tenants.tenant;

public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<Long> currentTenantId = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static Long getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void setCurrentTenantId(Long tenantId) {
        currentTenantId.set(tenantId);
    }

    public static void clear() {
        currentTenant.set(null);
        currentTenantId.set(null);
    }
}
