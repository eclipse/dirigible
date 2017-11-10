/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.generic.service;

import static org.junit.Assert.assertEquals;

import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.ResourcePath;
import org.junit.Test;

public class GenericJavascriptEngineServiceTest {

	@Test
	public void generateResourcePathFull() {
		String module = "myproject/module1.js/1/2?n=v";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.js", resourcePath.getModule());
		assertEquals("1/2?n=v", resourcePath.getPath());
	}

	@Test
	public void generateResourcePathRhinoFull() {
		String module = "myproject/module1.rhino/1/2?n=v";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.rhino", resourcePath.getModule());
		assertEquals("1/2?n=v", resourcePath.getPath());
	}

	@Test
	public void generateResourcePathNoPath() {
		String module = "myproject/module1.js";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.js", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

	@Test
	public void generateResourcePathNoPathSlash() {
		String module = "myproject/module1.js/";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.js", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

	@Test
	public void generateResourcePathRhinoNoPath() {
		String module = "myproject/module1.rhino";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.rhino", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

	@Test
	public void generateResourcePathRhinoNoPathSlash() {
		String module = "myproject/module1.rhino";
		String[] extensions = new String[] { ".js/", ".rhino/" };
		ResourcePath resourcePath = AbstractJavascriptExecutor.generateResourcePath(module, extensions);
		assertEquals("myproject/module1.rhino", resourcePath.getModule());
		assertEquals("", resourcePath.getPath());
	}

}
