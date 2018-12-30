/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
