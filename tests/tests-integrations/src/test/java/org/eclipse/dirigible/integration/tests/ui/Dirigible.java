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
package org.eclipse.dirigible.integration.tests.ui;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.HtmlAttribute;
import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.eclipse.dirigible.tests.util.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Lazy
@Component
public class Dirigible {

    private static final Logger LOGGER = LoggerFactory.getLogger(Dirigible.class);

    private static final String LOGIN_PAGE_TITLE = "Please sign in";

    private static final String ROOT_PATH = "/";

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";

    private static final String USERNAME_FIELD_ID = "username";
    private static final String PASSWORD_FIELD_ID = "password";
    private static final String SUBMIT_TYPE = "submit";

    private static final String SIGN_IN_BUTTON_TEXT = "Sign in";

    private final Browser browser;
    private final String username;
    private final String password;

    @Autowired
    public Dirigible(Browser browser) {
        this(browser, //
                new String(Base64.getDecoder()
                                 .decode(Configuration.get(Configuration.BASIC_USERNAME, "YWRtaW4="))), // admin
                new String(Base64.getDecoder()
                                 .decode(Configuration.get(Configuration.BASIC_PASSWORD, "YWRtaW4="))));// admin
    }

    public Dirigible(Browser browser, String username, String password) {
        this.browser = browser;
        this.username = username;
        this.password = password;
    }

    public void openHomePage() {
        browser.openPath(ROOT_PATH);
        login(false);

        SleepUtil.sleepMillis(500);
        browser.reload();
    }

    private boolean isLoginPageOpened() {
        String pageTitle = browser.getPageTitle();
        return LOGIN_PAGE_TITLE.equals(pageTitle);
    }

    public void login() {
        login(true);
    }

    public void login(boolean forceLogin) {
        if (!forceLogin && !isLoginPageOpened()) {
            LOGGER.info("Already logged in");
            return;
        }
        LOGGER.info("Logging...");
        browser.enterTextInElementByAttributePattern(HtmlElementType.INPUT, HtmlAttribute.ID, USERNAME_FIELD_ID, username);
        browser.enterTextInElementByAttributePattern(HtmlElementType.INPUT, HtmlAttribute.ID, PASSWORD_FIELD_ID, password);
        browser.clickElementByAttributePatternAndText(HtmlElementType.BUTTON, HtmlAttribute.TYPE, SUBMIT_TYPE, SIGN_IN_BUTTON_TEXT);
    }

    public DirigibleWorkbench openWorkbench() {
        browser.clickElementByAttributeValue(HtmlElementType.ANCHOR, HtmlAttribute.TITLE, "Workbench");
        return new DirigibleWorkbench(browser);
    }

}
