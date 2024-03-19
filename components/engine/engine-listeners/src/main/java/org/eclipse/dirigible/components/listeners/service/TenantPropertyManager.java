package org.eclipse.dirigible.components.listeners.service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class TenantPropertyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantPropertyManager.class);
    private static final String TENANT_ID_PARAM_NAME = "tenant_id";

    private final TenantContext tenantContext;
    private final Tenant defualtTenant;

    TenantPropertyManager(TenantContext tenantContext, @DefaultTenant Tenant defualtTenant) {
        this.tenantContext = tenantContext;
        this.defualtTenant = defualtTenant;
    }

    void setCurrentTenant(Message message) throws JMSException {
        String tenantId = getCurrentTenantId();
        LOGGER.debug("Will set tenant id [{}].", tenantId);
        message.setObjectProperty(TENANT_ID_PARAM_NAME, getCurrentTenantId());
    }

    private String getCurrentTenantId() {
        return tenantContext.isNotInitialized() ? defualtTenant.getId()
                : tenantContext.getCurrentTenant()
                               .getId();
    }

    String getCurrentTenantId(Message message) throws JMSException {
        Object tenantId = message.getObjectProperty(TENANT_ID_PARAM_NAME);
        if (null == tenantId) {
            throw new IllegalArgumentException("Tenant id parameter [" + TENANT_ID_PARAM_NAME + "] cannot be null in message: " + message);
        }
        if (tenantId instanceof String tenantIdString) {
            return tenantIdString;
        } else {
            throw new IllegalArgumentException(
                    "Invalid tenant id param [{" + tenantId + "}] with name [" + TENANT_ID_PARAM_NAME + "] in message: " + message);
        }
    }
}
