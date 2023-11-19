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

import org.eclipse.dirigible.integration.tests.ui.framework.Browser;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlAttribute;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlElementType;

public class Dirigible {

    private static final String ROOT_PATH = "/";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private static final String USERNAME_FIELD_ID = "username";
    private static final String PASSWORD_FIELD_ID = "password";
    private static final String SUBMIT_TYPE = "submit";

    private static final String SIGN_IN_BUTTON_TEXT = "Sign in";

    private final Browser browser;

    public Dirigible(Browser browser) {
        this.browser = browser;
    }

    public void openHomePage() {
        browser.openPath(ROOT_PATH);
        login();
    }

    private void login() {
        browser.enterTextInElementByAttributePattern(HtmlElementType.INPUT, HtmlAttribute.ID, USERNAME_FIELD_ID, USERNAME);
        browser.enterTextInElementByAttributePattern(HtmlElementType.INPUT, HtmlAttribute.ID, PASSWORD_FIELD_ID, PASSWORD);
        browser.clickElementByAttributePatternAndText(HtmlElementType.BUTTON, HtmlAttribute.TYPE, SUBMIT_TYPE, SIGN_IN_BUTTON_TEXT);
    }
}
