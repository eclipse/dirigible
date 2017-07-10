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

public class RhinoJavascriptEngineServiceTest extends AbstractGuiceTest {
	
	@Inject
	private IRepository repository;
	
	private RhinoJavascriptEngineExecutor rhinoJavascriptEngineExecutor;
	
	@Before
	public void setUp() throws Exception {
		this.repository = getInjector().getInstance(IRepository.class);
		this.rhinoJavascriptEngineExecutor = getInjector().getInstance(RhinoJavascriptEngineExecutor.class);
	}
	
	@Test
	public void basicScript() throws IOException, ScriptingException {
		repository.createResource(IRepositoryStructure.REGISTRY_PUBLIC + "/tests/rhino/testSum.js", 
				("var testSum = function(){ var a=2; var b=2; " + "var c=a+b; return c;}; testSum(); ").getBytes());
		
		Object result = rhinoJavascriptEngineExecutor.executeServiceModule("tests/rhino/testSum", null);
		
		assertNotNull(result);
		assertEquals(4.0, result);
		
	}
	
}
