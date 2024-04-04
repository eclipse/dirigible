package org.eclipse.dirigible.components.data.sources;

import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class TestConfig {

    @MockBean
    private TenantContext tenantContext;
}
