/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.aggregation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * The branding values are written as string literals, whatever a tenant administrator put in them.
 */
class BrandingScriptTest {

    @AfterEach
    void resetBranding() {
        Configuration.remove(DirigibleConfig.BRANDING_NAME.getKey());
    }

    @Test
    void writes_the_defaults_as_string_literals() {
        String script = BrandingScript.render();

        assertTrue(script.contains("name: \"Dirigible\","), script);
        assertTrue(script.contains("theme: \"blimpkit-auto\","), script);
    }

    @Test
    void keeps_a_hostile_value_inside_its_string() {
        Configuration.set(DirigibleConfig.BRANDING_NAME.getKey(), "x'; alert(1); \"</script>\n");

        String script = BrandingScript.render();

        assertTrue(script.contains("name: \"x'; alert(1); \\\"\\u003c/script>\\n\","), script);
    }
}
