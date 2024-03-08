package org.eclipse.dirigible.components.tenants.tenant;

import java.util.Objects;
import org.eclipse.dirigible.components.base.tenant.Tenant;

class TenantImpl implements Tenant {

    private static final String DEFAULT_TENANT_ID = "defaultTenant";
    private static final String DEFAULT_TENANT_NAME = "The default tenant";
    private static final String DEFAULT_TENANT_SUBDOMAIN = "default";

    private static final Tenant DEFAULT_TENANT = new TenantImpl(DEFAULT_TENANT_ID, DEFAULT_TENANT_NAME, DEFAULT_TENANT_SUBDOMAIN);

    private final String id;
    private final String name;
    private final String subdomain;
    private final boolean defaultTenant;

    TenantImpl(String id, String name, String subdomain) {
        this.id = id;
        this.name = name;
        this.subdomain = subdomain;
        this.defaultTenant = Objects.equals(id, DEFAULT_TENANT_ID);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isDefault() {
        return defaultTenant;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSubdomain() {
        return subdomain;
    }

    @Override
    public String toString() {
        return "TenantImpl [id=" + id + ", name=" + name + ", subdomain=" + subdomain + ", defaultTenant=" + defaultTenant + "]";
    }

    static Tenant getDefaultTenant() {
        return DEFAULT_TENANT;
    }

    static Tenant createFromEntity(org.eclipse.dirigible.components.tenants.domain.Tenant tenant) {
        return new TenantImpl(tenant.getId(), tenant.getName(), tenant.getSubdomain());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TenantImpl other = (TenantImpl) obj;
        return Objects.equals(id, other.id);
    }
}
