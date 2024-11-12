package org.eclipse.dirigible.integration.tests.ui.tests;

import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"spring-boot-admin-server", "spring-boot-admin-client"})
class SpringBootAdminIT extends UserInterfaceIntegrationTest {

    private static final String SPRING_ADMIN_BRAND_TITLE = "Eclipse Dirigible Admin";

    @Test
    void testSpringBootAdminStarts() {
        ide.openSpringBootAdmin();

        browser.assertElementExistsByTypeAndText(HtmlElementType.ANCHOR, SPRING_ADMIN_BRAND_TITLE);
    }
}
