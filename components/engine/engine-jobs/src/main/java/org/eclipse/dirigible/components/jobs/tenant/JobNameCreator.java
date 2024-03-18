package org.eclipse.dirigible.components.jobs.tenant;

import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobNameCreator {

    private static final String TENANT_JOB_NAME_REGEX = ".+###(.+)";
    private static final Pattern TENANT_JOB_NAME_PATTERN = Pattern.compile(TENANT_JOB_NAME_REGEX);
    private final TenantContext tenantContext;

    JobNameCreator(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
    }

    public String toTenantName(String name) {
        if (tenantContext.isNotInitialized()) {
            return name;
        }
        Tenant currentTenant = tenantContext.getCurrentTenant();
        return currentTenant.isDefault() ? name : currentTenant.getId() + "###" + name;
    }

    public String fromTenantName(String jobName) {
        Matcher matcher = TENANT_JOB_NAME_PATTERN.matcher(jobName);
        return matcher.matches() ? matcher.group(1) : jobName;
    }
}
