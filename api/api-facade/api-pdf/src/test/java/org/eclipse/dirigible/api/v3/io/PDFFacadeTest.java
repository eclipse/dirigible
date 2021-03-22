/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.pdf.PDFFacade;
import org.junit.Test;

public class PDFFacadeTest {

	@Test
	public void convertToPdfTest() throws IOException {
		int expectedPdfSize = 5339;

		String template = IOUtils.toString(PDFFacadeTest.class.getResourceAsStream("template.xsl"), Charset.defaultCharset());
		String data = IOUtils.toString(PDFFacadeTest.class.getResourceAsStream("data.xml"), Charset.defaultCharset());

		byte[] pdf = PDFFacade.convertToPdf(template, data);
		
		assertNotNull(pdf);
		assertEquals(expectedPdfSize, pdf.length);
	}

	@Test
	public void convertToPdfLargerTestDataTest() throws IOException {
		int expectedPdfSize = 5421;

		String template = IOUtils.toString(PDFFacadeTest.class.getResourceAsStream("template.xsl"), Charset.defaultCharset());
		String data = IOUtils.toString(PDFFacadeTest.class.getResourceAsStream("data2.xml"), Charset.defaultCharset());

		byte[] pdf = PDFFacade.convertToPdf(template, data);
		
		assertNotNull(pdf);
		assertEquals(expectedPdfSize, pdf.length);
	}
}
