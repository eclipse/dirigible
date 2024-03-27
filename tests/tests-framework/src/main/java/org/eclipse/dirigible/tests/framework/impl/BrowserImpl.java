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
package org.eclipse.dirigible.tests.framework.impl;

import com.codeborne.selenide.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.CallableResultAndNoException;
import org.eclipse.dirigible.tests.framework.HtmlAttribute;
import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Lazy
@Component
class BrowserImpl implements Browser {

    private static final String BROWSER = "chrome";
    private static final long SELENIDE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10);
    private static final String PATH_SEPARATOR = "/";

    static {
        Configuration.timeout = SELENIDE_TIMEOUT_MILLIS;
        Configuration.browser = BROWSER;
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--remote-allow-origins=*");
    }

    private final String protocol;
    private final int port;
    private final String host;

    @Autowired
    BrowserImpl(@LocalServerPort int port) {
        this(Protocol.HTTP, "localhost", port);
    }

    BrowserImpl(Protocol protocol, String host, int port) {
        this.protocol = protocol.name();
        this.host = host;
        this.port = port;
    }

    enum Protocol {
        HTTP, HTTPS

    }

    @Override
    public void openPath(String path) {
        String url = createAppUrl(path);
        Selenide.open(url);
        maximizeBrowser();
    }

    private String createAppUrl(String path) {
        if (StringUtils.isBlank(path)) {
            return createAppUrlByAbsolutePath("");
        }
        String absolutePath = path.startsWith(PATH_SEPARATOR) ? path : PATH_SEPARATOR + path;
        return createAppUrlByAbsolutePath(absolutePath);
    }

    private void maximizeBrowser() {
        WebDriverRunner.getWebDriver()
                       .manage()
                       .window()
                       .maximize();
    }

    private String createAppUrlByAbsolutePath(String absolutePath) {
        return protocol + "://" + host + ":" + port + absolutePath;
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

    private By constructCssSelectorByTypeAndAttribute(HtmlElementType elementType, HtmlAttribute attribute, String attributePattern) {
        String cssSelector = elementType.getType() + "[" + attribute.getAttribute() + "*='" + attributePattern + "']";
        return Selectors.byCssSelector(cssSelector);
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

    private void clickElement(SelenideElement element) {
        element.shouldBe(Condition.visible, Condition.enabled)
               .scrollIntoView(false)
               .click();
    }

    @Override
    public void clickElementByAttributeValue(HtmlElementType htmlElementType, HtmlAttribute htmlAttribute, String attributeValue) {
        SelenideElement element = getElementByAttributePattern(htmlElementType, htmlAttribute, attributeValue);

        clickElement(element);
    }

    @Override
    public void doubleClickOnElementContainingText(HtmlElementType elementType, String text) {
        String textPattern = Pattern.quote(text);

        boolean executed =
                handleElementInAllFrames(() -> getElementByAttributeAndTextPattern(elementType, textPattern), e -> e.doubleClick());

        assertThat(executed).withFailMessage("Element of type [" + elementType + "] with text pattern [" + textPattern + "] was not found.")
                            .isTrue();
    }

    @Override
    public void clickElementByAttributePattern(HtmlElementType elementType, HtmlAttribute attribute, String pattern) {
        boolean executed = handleElementInAllFrames(() -> getElementByAttributePattern(elementType, attribute, pattern), e -> e.click());
        assertThat(executed)
                            .withFailMessage("Element of type [" + elementType + "] with attribute [" + attribute + "] with pattern ["
                                    + pattern + "] was not found.")
                            .isTrue();
    }

    private boolean handleElementInAllFrames(CallableResultAndNoException<SelenideElement> elementResolver,
            Consumer<SelenideElement> elementHandler) {
        Selenide.switchTo()
                .defaultContent();
        SelenideElement element = elementResolver.call();
        if (element.exists()) {
            elementHandler.accept(element);
            return true;
        }
        try {
            ElementsCollection iframes = getElements(HtmlElementType.IFRAME);

            for (SelenideElement iframe : iframes) {
                Selenide.switchTo()
                        .frame(iframe);
                SelenideElement el = elementResolver.call();
                if (el.exists()) {
                    elementHandler.accept(el);
                    return true;
                }

                // without this, the frame cannot be switched in the next iteration
                Selenide.switchTo()
                        .defaultContent();
            }
            return false;
        } finally {
            Selenide.switchTo()
                    .defaultContent();
        }
    }

    private ElementsCollection getElements(HtmlElementType elementType) {
        By selector = constructCssSelectorByType(elementType);
        return Selenide.$$(selector);
    }

    private By constructCssSelectorByType(HtmlElementType elementType) {
        String cssSelector = elementType.getType();
        return Selectors.byCssSelector(cssSelector);
    }

    @Override
    public void assertElementExistsByTypeAndTextPattern(HtmlElementType htmlElementType, String textPattern) {
        SelenideElement element = getElementByAttributeAndTextPattern(htmlElementType, textPattern);
        element.should(Condition.exist);
    }

    @Override
    public void reload() {
        Selenide.refresh();
    }

    private SelenideElement getElementByAttributeAndTextPattern(HtmlElementType htmlElementType, String textPattern) {
        ElementsCollection elements = getElements(htmlElementType);
        return elements.findBy(Condition.matchText(textPattern));
    }

    @Override
    public void assertElementExistsByTypeAndText(HtmlElementType elementType, String text) {
        boolean executed = handleElementInAllFrames(() -> getElementByAttributeAndText(elementType, text), e -> e.should(Condition.exist));
        assertThat(executed).withFailMessage("Element of type [" + elementType + "] with text [" + text + "] was not found.")
                            .isTrue();
    }

    private SelenideElement getElementByAttributeAndText(HtmlElementType elementType, String text) {
        By selector = constructCssSelectorByType(elementType);
        ElementsCollection options = Selenide.$$(selector);

        return options.findBy(Condition.text(text));
    }

    @Override
    public String getPageTitle() {
        return Selenide.title();
    }

}
