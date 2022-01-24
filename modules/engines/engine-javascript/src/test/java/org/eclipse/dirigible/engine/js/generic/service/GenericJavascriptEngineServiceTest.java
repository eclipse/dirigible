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
package org.eclipse.dirigible.engine.js.generic.service;

import static org.junit.Assert.assertEquals;

import org.eclipse.dirigible.engine.api.resource.ResourcePath;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericJavascriptEngineServiceTest.
 */
public class GenericJavascriptEngineServiceTest {

	/**
	 * Generate resource path full.
	 */
	@Test
	public void generateResourcePathFull() {
		String module = "myproject/module1.js/1/2?n=v";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.js", resourcePath.getModule());
		assertEquals("1/2?n=v", resourcePath.getPath());
	}

	/**
	 * Generate resource path rhino full.
	 */
	@Test
	public void generateResourcePathRhinoFull() {
		String module = "myproject/module1.rhino/1/2?n=v";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.rhino", resourcePath.getModule());
		assertEquals("1/2?n=v", resourcePath.getPath());
	}

	/**
	 * Generate resource path no path.
	 */
	@Test
	public void generateResourcePathNoPath() {
		String module = "myproject/module1.js";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.js", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

	/**
	 * Generate resource path no path slash.
	 */
	@Test
	public void generateResourcePathNoPathSlash() {
		String module = "myproject/module1.js/";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.js", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

	/**
	 * Generate resource path rhino no path.
	 */
	@Test
	public void generateResourcePathRhinoNoPath() {
		String module = "myproject/module1.rhino";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.rhino", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

	/**
	 * Generate resource path rhino no path slash.
	 */
	@Test
	public void generateResourcePathRhinoNoPathSlash() {
		String module = "myproject/module1.rhino";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.rhino", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

}
