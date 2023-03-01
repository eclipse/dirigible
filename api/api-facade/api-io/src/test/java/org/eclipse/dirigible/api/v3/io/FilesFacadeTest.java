/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.io;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class FilesFacadeTest {

	@Test
	public void traverseTest() throws IOException {
		String json = FilesFacade.traverse(".");
		System.out.println(json);
		assertTrue(json.contains("FilesFacadeTest.class"));
	}
	
	@Test
	public void listTest() throws IOException {
		String json = FilesFacade.list(".");
		System.out.println(json);
		assertTrue(json.contains("about.html"));
	}

}
