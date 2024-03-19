package org.eclipse.dirigible.components.listeners.service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.stereotype.Component;

@Component
class TenantPropertyManager {

    private static final String TENANT_ID_PARAM_NAME = "tenant_id";

    private final TenantContext tenantContext;

    TenantPropertyManager(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
    }

    void setCurrentTenant(Message message) throws JMSException {
        Tenant currentTenant = tenantContext.getCurrentTenant();
        message.setObjectProperty(TENANT_ID_PARAM_NAME, currentTenant.getId());
    }

    String getCurrentTenantId(Message message) throws JMSException {
        Object tenantId = message.getObjectProperty(TENANT_ID_PARAM_NAME);
        if (null == tenantId) {
            throw new IllegalArgumentException(
                    "There is no tenant id parameter with name [" + TENANT_ID_PARAM_NAME + "] in message: " + message);
        }
        if (tenantId instanceof String tenantIdString) {
            return tenantIdString;
        } else {
            throw new IllegalArgumentException(
                    "Invalid tenant id param [{" + tenantId + "}] with name [" + TENANT_ID_PARAM_NAME + "] in message: " + message);
        }
    }
}
