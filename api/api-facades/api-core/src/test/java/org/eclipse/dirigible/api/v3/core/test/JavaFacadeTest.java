/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.core.JavaFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.extensions.synchronizer.ExtensionsSynchronizer;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * The Class JavaFacadeTest.
 */
public class JavaFacadeTest extends AbstractGuiceTest {

	/** The extensions publisher. */
	@Inject
	private ExtensionsSynchronizer extensionsPublisher;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.extensionsPublisher = getInjector().getInstance(ExtensionsSynchronizer.class);
	}

	/**
	 * Test call.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws ContextException
	 *             the context exception
	 */
	@Test
	public void testCall() throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ContextException {

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
	}

}
