package org.eclipse.dirigible.engine.js.v8.service;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.dirigible.api.v3.test.AbstractApiSuiteTest;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.v8.processor.V8JavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.junit.Test;

public class V8ApiSuiteTest extends AbstractApiSuiteTest {

	@Inject
	private IRepository repository;

	private V8JavascriptEngineExecutor v8JavascriptEngineExecutor;

	@Before
	public void setUp() throws Exception {
		this.repository = getInjector().getInstance(IRepository.class);
		this.v8JavascriptEngineExecutor = getInjector().getInstance(V8JavascriptEngineExecutor.class);
	}

	@Test
	public void runSuite() throws RepositoryWriteException, IOException, ScriptingException, ContextException {
		super.runSuite(this.v8JavascriptEngineExecutor, repository);
	}

}
