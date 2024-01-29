package org.eclipse.dirigible.integration.tests.ui.tests;

import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlAttribute;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewProjectIT extends UserInterfaceIntegrationTest {

    private Dirigible dirigible;
    private static final String NEW_TEST_NAME = "NewTest";


    @BeforeEach
    void setUp() {
        this.dirigible = new Dirigible(browser);
    }

    @Test
    void testNewProjectExist() throws InterruptedException {
        createNewProject();

        browser.assertElementExistsByTypeAndText(HtmlElementType.ANCHOR, NEW_TEST_NAME);

    }

    void createNewProject() throws InterruptedException {
        dirigible.openHomePage();

        browser.clickElementByTypeAndText(HtmlElementType.BUTTON, "File");
        browser.clickElementByTypeAndText(HtmlElementType.SPAN, "New");
        browser.clickElementByTypeAndText(HtmlElementType.SPAN, "Project");

        browser.enterTextInElementByAttributePattern(HtmlElementType.INPUT, HtmlAttribute.ID, "pgfi1", NEW_TEST_NAME);

        // String buttonText =
        // $(".fd-dialog__decisive-button.fd-button.fd-button--emphasized").getAttribute("text");
        // browser.clickElementByTypeAndText(HtmlElementType.BUTTON, buttonText);
        $(".fd-dialog__decisive-button.fd-button.fd-button--emphasized").click();

        TimeUnit.SECONDS.sleep(10);
    }
}
