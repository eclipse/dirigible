/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.rhino.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.engine.js.rhino.processor.RhinoJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class RhinoJavascriptEngineServiceTest.
 */
public class RhinoJavascriptEngineServiceTest extends AbstractGuiceTest {
	
	/** The repository. */
	@Inject
	private IRepository repository;
	
	/** The rhino javascript engine executor. */
	private RhinoJavascriptEngineExecutor rhinoJavascriptEngineExecutor;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.repository = getInjector().getInstance(IRepository.class);
		this.rhinoJavascriptEngineExecutor = getInjector().getInstance(RhinoJavascriptEngineExecutor.class);
	}
	
	/**
	 * Basic script.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ScriptingException the scripting exception
	 */
	@Test
	public void basicScript() throws IOException, ScriptingException {
		repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/tests/rhino/testSum.js", 
				("var testSum = function(){ var a=2; var b=2; " + "var c=a+b; return c;}; testSum(); ").getBytes());
		
		Object result = rhinoJavascriptEngineExecutor.executeServiceModule("tests/rhino/testSum", null);
		
		assertNotNull(result);
		assertEquals(4.0, result);
		
	}
	
}
