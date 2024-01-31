package org.eclipse.dirigible.integration.tests.ui.tests;

import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlAttribute;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getSelectedText;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewProjectIT extends UserInterfaceIntegrationTest {

    private Dirigible dirigible;
    private static final String NEW_TEST_NAME = "NewTest";
    private static final String NEW_PROJECT_PLACEHOLDER = "project";



    @BeforeEach
    void setUp() {
        this.dirigible = new Dirigible(browser);
    }

    @Test
    void testNewProjectExists() throws InterruptedException {
        createNewProject();

//        browser.assertElementExistsByTypeAndText(HtmlElementType.SPAN, NEW_TEST_NAME);
        // Find the element
        String spanText = $("body > div.dg-statusbar > div.dg-statusbar-message > span").getText();

        // Assert that the span text contains "NewTest"
        spanText.contains(NEW_TEST_NAME);
    }

    void createNewProject() throws InterruptedException {
        dirigible.openHomePage();

        browser.clickElementByTypeAndText(HtmlElementType.BUTTON, "File");
        browser.clickElementByTypeAndText(HtmlElementType.SPAN, "New");
        browser.clickElementByTypeAndText(HtmlElementType.SPAN, "Project");

        browser.enterTextInElementByAttributePattern(HtmlElementType.INPUT, HtmlAttribute.PLACEHOLDER, NEW_PROJECT_PLACEHOLDER, NEW_TEST_NAME);

        browser.clickElementByTypeAndText(HtmlElementType.BUTTON, "Create");

        TimeUnit.SECONDS.sleep(10);

    }
}
