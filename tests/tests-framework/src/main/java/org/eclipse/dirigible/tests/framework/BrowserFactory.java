package org.eclipse.dirigible.tests.framework;

public interface BrowserFactory {

    Browser createByTenantSubdomain(String tenantSubdomain);

    Browser create();
}
