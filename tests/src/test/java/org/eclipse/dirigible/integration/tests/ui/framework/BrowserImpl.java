/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.ui.framework;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

public class BrowserImpl implements Browser {

    private static final String BROWSER = "chrome";
    private static final long SELENIDE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private static final String PATH_SEPARATOR = "/";

    static {
        Configuration.timeout = SELENIDE_TIMEOUT_MILLIS;
        Configuration.browser = BROWSER;
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--remote-allow-origins=*");
    }

    private final int localServerPort;

    public BrowserImpl(int localServerPort) {
        this.localServerPort = localServerPort;
    }

    @Override
    public void openPath(String path) {
        String url = createAppUrl(path);
        Selenide.open(url);
        maximizeBrowser();
    }

    private void maximizeBrowser() {
        WebDriverRunner.getWebDriver()
                       .manage()
                       .window()
                       .maximize();
    }

    private String createAppUrl(String path) {
        String absolutePath = path.startsWith(PATH_SEPARATOR) ? path : PATH_SEPARATOR + path;
        return "http://localhost:" + localServerPort + absolutePath;
    }

    @Override
    public void enterTextInElementByAttributePattern(HtmlElementType elementType, HtmlAttribute attribute, String pattern, String text) {
        SelenideElement element = getElementByAttributePattern(elementType, attribute, pattern);
        element.click();
        element.setValue(text);
    }

    private SelenideElement getElementByAttributePattern(HtmlElementType elementType, HtmlAttribute attribute,
            String attributeValuePattern) {
        By selector = constructCssSelectorByTypeAndAttribute(elementType, attribute, attributeValuePattern);
        return Selenide.$(selector);
    }

    @Override
    public void clickElementByAttributePatternAndText(HtmlElementType elementType, HtmlAttribute attribute, String pattern, String text) {
        SelenideElement element = getElementByAttributePatternAndText(elementType, attribute, pattern, text);

        clickElement(element);
    }

    private SelenideElement getElementByAttributePatternAndText(HtmlElementType elementType, HtmlAttribute attribute, String pattern,
            String text) {
        By selector = constructCssSelectorByTypeAndAttribute(elementType, attribute, pattern);
        ElementsCollection options = Selenide.$$(selector);

        return options.findBy(Condition.text(text));
    }

    private By constructCssSelectorByTypeAndAttribute(HtmlElementType elementType, HtmlAttribute attribute, String attributePattern) {
        String cssSelector = elementType.getType() + "[" + attribute.getAttribute() + "*=" + attributePattern + "]";
        return Selectors.byCssSelector(cssSelector);
    }

    private void clickElement(SelenideElement element) {
        element.shouldBe(Condition.visible, Condition.enabled)
               .scrollIntoView(false)
               .click();
    }

    @Override
    public void assertElementExistsByTypeAndText(HtmlElementType elementType, String text) {
        SelenideElement element = getElementByAttributeAndText(elementType, text);
        element.should(Condition.exist);
    }

    private SelenideElement getElementByAttributeAndText(HtmlElementType elementType, String text) {
        By selector = constructCssSelectorByType(elementType);
        ElementsCollection options = Selenide.$$(selector);

        return options.findBy(Condition.text(text));
    }

    private By constructCssSelectorByType(HtmlElementType elementType) {
        String cssSelector = elementType.getType();
        return Selectors.byCssSelector(cssSelector);
    }

    @Override
    public String getPageTitle() {
        return Selenide.title();
    }

    @Override
    public SelenideElement waitUntilExist(HtmlElementType elementType) {
        SelenideElement element = getElementByType(elementType);
        return element.should(Condition.exist);
    }

    private SelenideElement getElementByType(HtmlElementType elementType) {
        By cssSelector = Selectors.byCssSelector(elementType.getType());
        return Selenide.$(cssSelector);
    }

}
