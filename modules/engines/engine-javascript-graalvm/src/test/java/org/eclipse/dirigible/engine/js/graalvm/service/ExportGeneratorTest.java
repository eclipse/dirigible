/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.service;

import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.engine.js.graalvm.processor.generation.ExportGenerator;
import org.eclipse.dirigible.engine.js.graalvm.processor.GraalVMJavascriptEngineExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ExportGeneratorTest extends AbstractDirigibleTest {

    private static final Logger logger = LoggerFactory.getLogger(GraalVMCustomTest.class);

    private GraalVMJavascriptEngineExecutor graalVMJavascriptEngineExecutor;

    private ExportGenerator generator;

    @Before
    public void setUp() throws Exception {
        this.graalVMJavascriptEngineExecutor = new GraalVMJavascriptEngineExecutor();
        this.generator = new ExportGenerator(this.graalVMJavascriptEngineExecutor);
    }

    @Test
    public void generateApiUtilsExports() {
        String testApi = "@dirigible-v4/utils";
        String apiVersion = "v4";
        Path path = Paths.get("/utils");

        String expected =
                "export const alphanumeric = dirigibleRequire('utils/v4/alphanumeric');\n" +
                "export const base64 = dirigibleRequire('utils/v4/base64');\n" +
                "export const digest = dirigibleRequire('utils/v4/digest');\n" +
                "export const escape = dirigibleRequire('utils/v4/escape');\n" +
                "export const hex = dirigibleRequire('utils/v4/hex');\n" +
                "export const jsonpath = dirigibleRequire('utils/v4/jsonpath');\n" +
                "export const url = dirigibleRequire('utils/v4/url');\n" +
                "export const uuid = dirigibleRequire('utils/v4/uuid');\n" +
                "export const xml = dirigibleRequire('utils/v4/xml');\n" +
                "export const qrcode = dirigibleRequire('utils/v4/qrcode');\n"+
                "export default { alphanumeric,base64,digest,escape,hex,jsonpath,url,uuid,xml,qrcode }\n";

        logger.info("API export generation test starting... " + testApi);
        String actual = generator.generate(path, apiVersion);
        Assert.assertEquals(expected, actual);
        logger.info("API export generation test passed successfully: " + testApi);
    }
}
