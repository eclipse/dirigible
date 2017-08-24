package org.eclipse.dirigible.api.v3.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.mockito.Mockito;

public abstract class AbstractApiSuiteTest extends AbstractGuiceTest {

	private static List<String> TEST_MODULES = new ArrayList<String>();

	private IExtensionsCoreService extensionsCoreService;

	private IRepository repository;

	@Before
	public void setUp() throws Exception {
		this.extensionsCoreService = getInjector().getInstance(ExtensionsCoreService.class);
		this.repository = getInjector().getInstance(IRepository.class);
	}

	@Before
	public void registerModules() {

		TEST_MODULES.add("core/v3/java/call.js");
		TEST_MODULES.add("core/v3/java/invoke.js");
		TEST_MODULES.add("core/v3/java/deep.js");
		TEST_MODULES.add("core/v3/console/log.js");

		TEST_MODULES.add("auth/v3/user/getName.js");

		TEST_MODULES.add("core/v3/env/get.js");
		TEST_MODULES.add("core/v3/env/list.js");
		TEST_MODULES.add("core/v3/globals/get.js");
		TEST_MODULES.add("core/v3/globals/list.js");
		TEST_MODULES.add("core/v3/context/get.js");
		TEST_MODULES.add("core/v3/extensions/getExtensions.js");
		TEST_MODULES.add("core/v3/extensions/getExtensionPoints.js");

		TEST_MODULES.add("db/v3/database/getDatabaseTypes.js");
		TEST_MODULES.add("db/v3/database/getDataSources.js");
		TEST_MODULES.add("db/v3/database/getMetadata.js");
		TEST_MODULES.add("db/v3/database/getConnection.js");
		TEST_MODULES.add("db/v3/query/query.js");
		TEST_MODULES.add("db/v3/update/update.js");

		TEST_MODULES.add("http/v3/request/getMethod.js");
		TEST_MODULES.add("http/v3/request/getRemoteUser.js");
		TEST_MODULES.add("http/v3/request/getHeaderNames.js");
		TEST_MODULES.add("http/v3/request/getServerName.js");
		TEST_MODULES.add("http/v3/response/getHeaderNames.js");
		TEST_MODULES.add("http/v3/client/get.js");
		TEST_MODULES.add("http/v3/client/get-binary.js");
		
		TEST_MODULES.add("io/v3/streams/copy.js");
		TEST_MODULES.add("io/v3/streams/text.js");
		TEST_MODULES.add("io/v3/files/createTempFile.js");
		TEST_MODULES.add("io/v3/files/fileStreams.js");

		TEST_MODULES.add("utils/v3/base64/encode.js");
		TEST_MODULES.add("utils/v3/base64/decode.js");
		TEST_MODULES.add("utils/v3/xml2json/fromJson.js");
		TEST_MODULES.add("utils/v3/xml2json/toJson.js");
		TEST_MODULES.add("utils/v3/uuid/validate.js");
		TEST_MODULES.add("utils/v3/uuid/alias.js");
		TEST_MODULES.add("utils/v3/uuid/alias-modules.js");

	}

	public void runSuite(IJavascriptEngineExecutor executor, IRepository repository)
			throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse mockedResponse = Mockito.mock(HttpServletResponse.class);
		mockRequest(mockedRequest);
		mockResponse(mockedResponse);

		ThreadContextFacade.setUp();
		try {
			ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), mockedRequest);
			ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), mockedResponse);
			extensionsCoreService.createExtensionPoint("/test_extpoint1", "test_extpoint1", "Test");
			extensionsCoreService.createExtension("/test_ext1", "/test_ext_module1", "test_extpoint1", "Test");
			try {
				for (String testModule : TEST_MODULES) {
					Object result = runTest(executor, repository, testModule);
					assertNotNull(result);
					assertTrue("API test failed: " + testModule, Boolean.parseBoolean(result.toString()));
					System.out.println(String.format("API test [%s] on engine [%s] passed successfully.", testModule,
							executor.getType()));
				} 
			} finally {
				extensionsCoreService.removeExtension("/test_ext1");
				extensionsCoreService.removeExtensionPoint("/test_extpoint1");
			}
		} finally {
			ThreadContextFacade.tearDown();
		}
	}

	private void mockRequest(HttpServletRequest mockedRequest) {
		when(mockedRequest.getMethod()).thenReturn("GET");
		when(mockedRequest.getRemoteUser()).thenReturn("tester");
		when(mockedRequest.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("header1", "header2")));
		when(mockedRequest.getServerName()).thenReturn("server1");
		when(mockedRequest.getHeader("header1")).thenReturn("header1");
		when(mockedRequest.isUserInRole("role1")).thenReturn(true);
	}

	private void mockResponse(HttpServletResponse mockedResponse) {
		when(mockedResponse.getHeaderNames()).thenReturn(Arrays.asList("header1", "header2"));
	}

	private Object runTest(IJavascriptEngineExecutor executor, IRepository repository, String testModule) throws IOException, ScriptingException {

		try {
			InputStream in = AbstractApiSuiteTest.class.getResourceAsStream(IRepositoryStructure.SEPARATOR + testModule);
			if (in == null) {
				throw new IOException(IRepositoryStructure.SEPARATOR + testModule + " does not exist");
			}
			repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + testModule,
					IOUtils.readBytesFromStream(in));
		} catch (RepositoryWriteException e) {
			throw new IOException(IRepositoryStructure.SEPARATOR + testModule, e);
		}
		Object result = executor.executeServiceModule(testModule, null);
		return result;
	}
}
