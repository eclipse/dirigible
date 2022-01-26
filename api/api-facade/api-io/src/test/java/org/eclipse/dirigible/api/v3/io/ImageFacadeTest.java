/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ImageFacadeTest {

	@Test
	public void resizeTest() throws IOException {
		InputStream original = ImageFacadeTest.class.getResourceAsStream("/dirigible.png");
		InputStream result = ImageFacade.resize(original, "png", 300, 155);
		FileOutputStream out = new FileOutputStream("./target/dirigible_output.png");
		IOUtils.copy(result, out);
	}

}
