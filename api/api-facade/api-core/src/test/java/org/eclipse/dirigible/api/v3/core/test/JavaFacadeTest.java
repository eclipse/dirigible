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
package org.eclipse.dirigible.api.v3.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.dirigible.api.v3.core.JavaFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.extensions.synchronizer.ExtensionsSynchronizer;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * The Class JavaFacadeTest.
 */
public class JavaFacadeTest extends AbstractDirigibleTest {

	/** The extensions publisher. */
	private ExtensionsSynchronizer extensionsPublisher;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.extensionsPublisher = new ExtensionsSynchronizer();
	}

	/**
	 * Test call.
	 *
	 * @throws Exception
	 *             Signals that an exception has occurred.
	 */
	@Test
	public void testCall() throws Exception {

		ThreadContextFacade.setUp();
		try {
			extensionsPublisher.registerPredeliveredExtensionPoint("/control/control.extensionpoint");
			extensionsPublisher.registerPredeliveredExtension("/control/control.extension");

			extensionsPublisher.synchronize();

			Object result = JavaFacade.call("org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade", "getExtensions",
					new String[] { "/control/control" });
			assertTrue(result instanceof String);
			JsonElement extensions = GsonHelper.PARSER.parse((String) result);
			assertTrue(extensions instanceof JsonArray);
			JsonElement extension = ((JsonArray) extensions).get(0);
			assertTrue(extension instanceof JsonPrimitive);
			assertEquals("/control/control", ((JsonPrimitive) extension).getAsString());
		} finally {
			ThreadContextFacade.tearDown();
		}
	}

	/**
	 * Test deeper inheritance
	 *
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testInheritedMethod() throws NoSuchMethodException {
		Method method = JavaFacade.findMethod("doSomething", ExtendedObject.class,
				new Class[] { HashMap.class, BaseParameter.class, ExactParameter.class },
				Arrays.asList(new Object[] { new HashMap<>(), new BaseParameter(), new ExactParameter() }));
		assertNotNull(method);

		method = JavaFacade.findMethod("doSomethingElse", ExtendedObject.class,
				new Class[] { HashMap.class, BaseParameter.class, ExactParameter.class },
				Arrays.asList(new Object[] { new HashMap<>(), new BaseParameter(), new ExactParameter() }));
		assertNull(method);
	}

}
