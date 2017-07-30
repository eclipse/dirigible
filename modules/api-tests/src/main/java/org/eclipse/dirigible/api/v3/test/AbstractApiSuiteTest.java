package org.eclipse.dirigible.api.v3.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;

public abstract class AbstractApiSuiteTest extends AbstractGuiceTest {

	private static List<String> TEST_MODULES = new ArrayList<String>();

	@Before
	public void registerModules() {
		TEST_MODULES.add("utils/v3/base64_encode.js");
		TEST_MODULES.add("utils/v3/base64_decode.js");
	}

	public void runSuite(IJavascriptEngineExecutor executor, IRepository repository)
			throws RepositoryWriteException, IOException, ScriptingException {
		for (String testModule : TEST_MODULES) {
			Object result = runTest(executor, repository, testModule);
			assertNotNull(result);
			assertTrue("Javascript API test failed: " + testModule, Boolean.parseBoolean(result.toString()));
			System.out.println(String.format("API test [%s] on engine [%s] passed successfully.", testModule, executor.getType()));
		}
	}

	private Object runTest(IJavascriptEngineExecutor executor, IRepository repository, String testModule) throws IOException, ScriptingException {
		InputStream in = AbstractApiSuiteTest.class.getResourceAsStream(IRepositoryStructure.SEPARATOR + testModule);
		repository.createResource(IRepositoryStructure.REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + testModule,
				IOUtils.readBytesFromStream(in));
		Object result = executor.executeServiceModule(testModule, null);
		return result;
	}
}
