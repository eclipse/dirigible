/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.template.service.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * The outputs a regeneration removes because the template no longer writes them there (#7889).
 */
class ModelTemplateAdaptersTest {

    private static final String REPORT_FILE_TEMPLATE = "template-application-ui-harmonia-java/template/template-report-file.js";

    @Test
    void aReportsCasePreservedPageFolderIsRetired() {
        Map<String, Object> parameters =
                Map.of("genFolderName", "OverdueInvoices", "javaGenFolderName", "overdueinvoices", "name", "OverdueInvoices");
        assertEquals(List.of("OverdueInvoices/reports/OverdueInvoices"),
                ModelTemplateAdapters.retiredOutputs(REPORT_FILE_TEMPLATE, parameters));
    }

    @Test
    void aReportWhoseFolderNeedsNoSanitizingRetiresNothing() {
        Map<String, Object> parameters = Map.of("genFolderName", "revenue", "javaGenFolderName", "revenue", "name", "Revenue");
        assertTrue(ModelTemplateAdapters.retiredOutputs(REPORT_FILE_TEMPLATE, parameters)
                                        .isEmpty());
    }

    @Test
    void anotherTemplateRetiresNothing() {
        Map<String, Object> parameters = Map.of("genFolderName", "Sales", "javaGenFolderName", "sales", "name", "Sales");
        assertTrue(ModelTemplateAdapters.retiredOutputs("template-application-ui-harmonia-java/template/template.js", parameters)
                                        .isEmpty());
    }
}
