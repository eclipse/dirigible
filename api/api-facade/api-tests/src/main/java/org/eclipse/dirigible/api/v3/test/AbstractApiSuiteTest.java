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
package org.eclipse.dirigible.api.v3.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.cxf.helpers.IOUtils;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.extensions.api.ExtensionsException;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.eclipse.dirigible.core.extensions.service.ExtensionsCoreService;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryWriteException;
import org.junit.Before;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractApiSuiteTest.
 */
public abstract class AbstractApiSuiteTest extends AbstractDirigibleTest {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiSuiteTest.class);

    /** The test modules. */
    private static List<String> TEST_MODULES = new ArrayList<String>();

    /** The extensions core service. */
    private IExtensionsCoreService extensionsCoreService;

    /** The repository. */
    private IRepository repository;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        this.extensionsCoreService = new ExtensionsCoreService();
        this.repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
    }

    /**
     * Register modules.
     */
    @Before
    public void registerModules() {
        // v3
        registerModulesV3();

        // v4
        registerModulesV4();

        // Apache Spark
        sparkRegisterModule();

        // Apache Cassandra
        cassandraRegisterModule();

        // RabbitMQ
        registerModulesRabbitMQ();

        // ElasticSearch
        registerModulesElastic();

        // Redis
        registerModulesRedisExt();

        // etcd
        registerModulesEtcd();
    }

    /**
     * Register modules V 4.
     */
    protected void registerModulesV4() {
        registerModulesUtilsV4();
        registerModulesDbV4();
        registerModulesCoreV4();
        registerModulesIndexingV4();
        registerModulesHttpV4();
        registerModulesCmsV4();
        registerModulesIoV4();
        registerModulesSecurityV4();
        registerModulesPlatformV4();
    }

    /**
     * Register modules platform V 4.
     */
    private void registerModulesPlatformV4() {
        TEST_MODULES.add("platform/v4/lifecycle/publishProject.js");
        TEST_MODULES.add("platform/v4/repository/createFile.js");
        TEST_MODULES.add("platform/v4/workspace/createWorkspace.js");
        TEST_MODULES.add("platform/v4/engines/getTypes.js");
    }

    /**
     * Register modules security V 4.
     */
    private void registerModulesSecurityV4() {
        TEST_MODULES.add("security/v4/user/getName.js");
    }

    /**
     * Register modules io V 4.
     */
    private void registerModulesIoV4() {
        TEST_MODULES.add("io/v4/streams/copy.js");
        TEST_MODULES.add("io/v4/streams/text.js");
        TEST_MODULES.add("io/v4/files/createTempFile.js");
        TEST_MODULES.add("io/v4/files/fileStreams.js");
    }

    /**
     * Register modules cms V 4.
     */
    private void registerModulesCmsV4() {
        TEST_MODULES.add("cms/v4/cmis/getSession.js");
        TEST_MODULES.add("cms/v4/cmis/getRootFolder.js");
        TEST_MODULES.add("cms/v4/cmis/getChildren.js");
        TEST_MODULES.add("cms/v4/cmis/createFolder.js");
        TEST_MODULES.add("cms/v4/cmis/createDocument.js");
    }

    /**
     * Register modules http V 4.
     */
    private void registerModulesHttpV4() {
        TEST_MODULES.add("http/v4/request/isValid.js");
        TEST_MODULES.add("http/v4/request/getMethod.js");
        TEST_MODULES.add("http/v4/request/getRemoteUser.js");
        TEST_MODULES.add("http/v4/request/getPathInfo.js");
        TEST_MODULES.add("http/v4/request/getPathTranslated.js");
        TEST_MODULES.add("http/v4/request/getHeader.js");
        TEST_MODULES.add("http/v4/request/isUserInRole.js");
        TEST_MODULES.add("http/v4/request/getAttribute.js");
        TEST_MODULES.add("http/v4/request/getAuthType.js");
        TEST_MODULES.add("http/v4/request/getHeaderNames.js");
        TEST_MODULES.add("http/v4/request/getServerName.js");
        TEST_MODULES.add("http/v4/response/getHeaderNames.js");
        TEST_MODULES.add("http/v4/client/get.js");
        TEST_MODULES.add("http/v4/client/get-binary.js");
        TEST_MODULES.add("http/v4/session/getAttributeNames.js");
    }

    /**
     * Register modules indexing V 4.
     */
    private void registerModulesIndexingV4() {
        TEST_MODULES.add("indexing/v4/writer/add.js");
        TEST_MODULES.add("indexing/v4/searcher/search.js");
        TEST_MODULES.add("indexing/v4/searcher/between.js");
    }

    /**
     * Register modules core V 4.
     */
    private void registerModulesCoreV4() {
        TEST_MODULES.add("core/v4/configurations/get.js");
        TEST_MODULES.add("core/v4/console/log.js");
        TEST_MODULES.add("core/v4/context/get.js");
        TEST_MODULES.add("core/v4/env/get.js");
        TEST_MODULES.add("core/v4/env/list.js");
        TEST_MODULES.add("core/v4/extensions/getExtensions.js");
        TEST_MODULES.add("core/v4/extensions/getExtensionPoints.js");
        TEST_MODULES.add("core/v4/globals/get.js");
        TEST_MODULES.add("core/v4/globals/list.js");
        TEST_MODULES.add("core/v4/destinations/get.js");
    }

    /**
     * Register modules db V 4.
     */
    private void registerModulesDbV4() {
        TEST_MODULES.add("db/v4/database/getDatabaseTypes.js");
        TEST_MODULES.add("db/v4/database/getDataSources.js");
        TEST_MODULES.add("db/v4/database/getMetadata.js");
        TEST_MODULES.add("db/v4/database/getConnection.js");
        TEST_MODULES.add("db/v4/query/query.js");
        TEST_MODULES.add("db/v4/update/update.js");
        TEST_MODULES.add("db/v4/sequence/nextval.js");
    }

    /**
     * Register modules utils V 4.
     */
    private void registerModulesUtilsV4() {
        TEST_MODULES.add("utils/v4/base64/encode.js");
        TEST_MODULES.add("utils/v4/base64/decode.js");
        TEST_MODULES.add("utils/v4/digest/md5.js");
        TEST_MODULES.add("utils/v4/digest/md5Hex.js");
        TEST_MODULES.add("utils/v4/digest/sha1.js");
        TEST_MODULES.add("utils/v4/digest/sha1Hex.js");
        TEST_MODULES.add("utils/v4/digest/sha256.js");
        TEST_MODULES.add("utils/v4/digest/sha384.js");
        TEST_MODULES.add("utils/v4/digest/sha512.js");
        TEST_MODULES.add("utils/v4/hex/encode.js");
        TEST_MODULES.add("utils/v4/hex/decode.js");
        TEST_MODULES.add("utils/v4/escape/escapeCsv.js");
        TEST_MODULES.add("utils/v4/escape/unescapeCsv.js");
        TEST_MODULES.add("utils/v4/escape/escapeJavascript.js");
        TEST_MODULES.add("utils/v4/escape/unescapeJavascript.js");
        TEST_MODULES.add("utils/v4/escape/escapeHtml3.js");
        TEST_MODULES.add("utils/v4/escape/unescapeHtml3.js");
        TEST_MODULES.add("utils/v4/escape/escapeHtml4.js");
        TEST_MODULES.add("utils/v4/escape/unescapeHtml4.js");
        TEST_MODULES.add("utils/v4/escape/escapeJava.js");
        TEST_MODULES.add("utils/v4/escape/unescapeJava.js");
        TEST_MODULES.add("utils/v4/escape/escapeJson.js");
        TEST_MODULES.add("utils/v4/escape/unescapeJson.js");
        TEST_MODULES.add("utils/v4/escape/escapeXml.js");
        TEST_MODULES.add("utils/v4/escape/unescapeXml.js");
        TEST_MODULES.add("utils/v4/url/encode.js");
        TEST_MODULES.add("utils/v4/url/decode.js");
        TEST_MODULES.add("utils/v4/url/escape.js");
        TEST_MODULES.add("utils/v4/url/escapePath.js");
        TEST_MODULES.add("utils/v4/url/escapeForm.js");
        TEST_MODULES.add("utils/v4/qrcode/qrCode.js");
        TEST_MODULES.add("git/v4/client/local.js");
    }

    /**
     * Register modules V 3.
     */
    protected void registerModulesV3() {
        registerModulesCoreV3();
        registerModulesSecurityV3();
        registerModulesDbV3();
        registerModulesHttpV3();
        registerModulesIoV3();
        registerModulesUtilsV3();
        registerModulesIndexingV3();
        registerModulesCmsV3();
    }


    /**
     * Register modules cms V 3.
     */
    private void registerModulesCmsV3() {
        TEST_MODULES.add("cms/v3/cmis/getSession.js");
        TEST_MODULES.add("cms/v3/cmis/getRootFolder.js");
        TEST_MODULES.add("cms/v3/cmis/getChildren.js");
    }

    /**
     * Register modules indexing V 3.
     */
    private void registerModulesIndexingV3() {
        TEST_MODULES.add("indexing/v3/writer/add.js");
        TEST_MODULES.add("indexing/v3/searcher/search.js");
        TEST_MODULES.add("indexing/v3/searcher/between.js");
    }

    /**
     * Register modules utils V 3.
     */
    private void registerModulesUtilsV3() {
        TEST_MODULES.add("utils/v3/base64/encode.js");
        TEST_MODULES.add("utils/v3/base64/decode.js");
        TEST_MODULES.add("utils/v3/hex/encode.js");
        TEST_MODULES.add("utils/v3/hex/decode.js");
        TEST_MODULES.add("utils/v3/digest/sha1.js");
        TEST_MODULES.add("utils/v3/xml/fromJson.js");
        TEST_MODULES.add("utils/v3/xml/toJson.js");
        TEST_MODULES.add("utils/v3/uuid/validate.js");
        TEST_MODULES.add("utils/v3/uuid/alias.js");
        TEST_MODULES.add("utils/v3/uuid/alias-modules.js");
        TEST_MODULES.add("utils/v3/url/encode.js");
        TEST_MODULES.add("utils/v3/url/decode.js");
    }

    /**
     * Register modules io V 3.
     */
    private void registerModulesIoV3() {
        TEST_MODULES.add("io/v3/streams/copy.js");
        TEST_MODULES.add("io/v3/streams/text.js");
        TEST_MODULES.add("io/v3/files/createTempFile.js");
        TEST_MODULES.add("io/v3/files/fileStreams.js");
    }

    /**
     * Register modules http V 3.
     */
    private void registerModulesHttpV3() {
        TEST_MODULES.add("http/v3/request/isValid.js");
        TEST_MODULES.add("http/v3/request/getMethod.js");
        TEST_MODULES.add("http/v3/request/getRemoteUser.js");
        TEST_MODULES.add("http/v3/request/getPathInfo.js");
        TEST_MODULES.add("http/v3/request/getPathTranslated.js");
        TEST_MODULES.add("http/v3/request/getHeader.js");
        TEST_MODULES.add("http/v3/request/isUserInRole.js");
        TEST_MODULES.add("http/v3/request/getAttribute.js");
        TEST_MODULES.add("http/v3/request/getAuthType.js");
        TEST_MODULES.add("http/v3/request/getHeaderNames.js");
        TEST_MODULES.add("http/v3/request/getServerName.js");
        TEST_MODULES.add("http/v3/response/getHeaderNames.js");
        TEST_MODULES.add("http/v3/client/get.js");
        TEST_MODULES.add("http/v3/client/get-binary.js");
        TEST_MODULES.add("http/v3/session/getAttributeNames.js");
    }

    /**
     * Register modules db V 3.
     */
    private void registerModulesDbV3() {
        TEST_MODULES.add("db/v3/database/getDatabaseTypes.js");
        TEST_MODULES.add("db/v3/database/getDataSources.js");
        TEST_MODULES.add("db/v3/database/getMetadata.js");
        TEST_MODULES.add("db/v3/database/getConnection.js");
        TEST_MODULES.add("db/v3/query/query.js");
        TEST_MODULES.add("db/v3/update/update.js");
    }

    /**
     * Register modules security V 3.
     */
    private void registerModulesSecurityV3() {
        TEST_MODULES.add("security/v3/user/getName.js");
    }

    /**
     * Register modules core V 3.
     */
    private void registerModulesCoreV3() {
        TEST_MODULES.add("core/v3/java/call.js");
        TEST_MODULES.add("core/v3/java/invoke.js");
        TEST_MODULES.add("core/v3/java/deep.js");
        TEST_MODULES.add("core/v3/java/null.js");
        TEST_MODULES.add("core/v3/console/log.js");
        TEST_MODULES.add("core/v3/env/get.js");
        TEST_MODULES.add("core/v3/env/list.js");
        TEST_MODULES.add("core/v3/globals/get.js");
        TEST_MODULES.add("core/v3/globals/list.js");
        TEST_MODULES.add("core/v3/context/get.js");
        TEST_MODULES.add("core/v3/extensions/getExtensions.js");
        TEST_MODULES.add("core/v3/extensions/getExtensionPoints.js");
    }

    /**
     * Spark register module.
     */
    protected void sparkRegisterModule() {
        sparkRegisterModulesUtils();
    }

    /**
     * Spark register modules utils.
     */
    private void sparkRegisterModulesUtils() {
        TEST_MODULES.add("ext/spark/client.js");
    }

    /**
     * Cassandra register module.
     */
    protected void cassandraRegisterModule() {
        cassandraRegisterUtils();
    }

    /**
     * Cassandra register utils.
     */
    private void cassandraRegisterUtils() {
        TEST_MODULES.add("ext/cassandra/client.js");
    }

    /**
     * Register modules rabbit MQ.
     */
    protected void registerModulesRabbitMQ() {
        registerModulesRabbitMQExt();
    }

    /**
     * Register modules rabbit MQ ext.
     */
    private void registerModulesRabbitMQExt() {
        TEST_MODULES.add("ext/rabbitmq/rabbitmq.js");
    }

    /**
     * Register modules elastic.
     */
    protected void registerModulesElastic() {
        registerModulesElasticsearch();
    }

    /**
     * Register modules elasticsearch.
     */
    protected void registerModulesElasticsearch() {
        TEST_MODULES.add("ext/elasticsearch/client/client.js");
    }

    /**
     * Register modules redis ext.
     */
    protected void registerModulesRedisExt() {
        registerModulesRedis();
    }

    /**
     * Register modules redis.
     */
    private void registerModulesRedis() {
        TEST_MODULES.add("ext/redis/client/client.js");
    }

    /**
     * Register modules etcd.
     */
    protected void registerModulesEtcd() {
        registerModulesEtcdExt();
    }

    /**
     * Register modules etcd ext.
     */
    private void registerModulesEtcdExt() {
        TEST_MODULES.add("ext/etcd/client/getClient.js");
    }

    /**
     * Run suite.
     *
     * @param executor the executor
     * @param repository the repository
     * @throws RepositoryWriteException the repository write exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ScriptingException the scripting exception
     * @throws ContextException the context exception
     * @throws ExtensionsException the extensions exception
     */
    public void runSuite(IJavascriptEngineExecutor executor, IRepository repository)
            throws RepositoryWriteException, IOException, ScriptingException, ContextException, ExtensionsException {
        for (String testModule : TEST_MODULES) {
            try {
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

                    logger.info("API test starting... " + testModule);

                    runTest(executor, repository, testModule);
                    logger.info("API test passed successfully: " + testModule);

                } finally {
                    extensionsCoreService.removeExtension("/test_ext1");
                    extensionsCoreService.removeExtensionPoint("/test_extpoint1");
                }
            } finally {
                ThreadContextFacade.tearDown();
            }
        }
    }

    /**
     * Mock request.
     *
     * @param mockedRequest the mocked request
     */
    protected void mockRequest(HttpServletRequest mockedRequest) {
        Mockito.lenient().when(mockedRequest.getMethod()).thenReturn("GET");
        Mockito.lenient().when(mockedRequest.getRemoteUser()).thenReturn("tester");
        Mockito.lenient().when(mockedRequest.getPathInfo()).thenReturn("/path");
        Mockito.lenient().when(mockedRequest.getPathTranslated()).thenReturn("/translated");
        Mockito.lenient().when(mockedRequest.getHeader("header1")).thenReturn("header1");
        Mockito.lenient().when(mockedRequest.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("header1", "header2")));
        Mockito.lenient().when(mockedRequest.getServerName()).thenReturn("server1");
        Mockito.lenient().when(mockedRequest.getHeader("header1")).thenReturn("header1");
        Mockito.lenient().when(mockedRequest.isUserInRole("role1")).thenReturn(true);
        Mockito.lenient().when(mockedRequest.getAttribute("attr1")).thenReturn("val1");
        Mockito.lenient().when(mockedRequest.getAuthType()).thenReturn("Basic");

        HttpSession mockedSession = Mockito.mock(HttpSession.class);
        //when(mockedRequest.getSession()).thenReturn(mockedSession);
        Mockito.lenient().when(mockedRequest.getSession(true)).thenReturn(mockedSession);
        mockSession(mockedSession);
    }

    /**
     * Mock session.
     *
     * @param mockedSession the mocked session
     */
    protected void mockSession(HttpSession mockedSession) {
        Mockito.lenient().when(mockedSession.getAttributeNames()).thenReturn(Collections.enumeration(Arrays.asList("attr1")));
    }

    /**
     * Mock response.
     *
     * @param mockedResponse the mocked response
     */
    protected void mockResponse(HttpServletResponse mockedResponse) {
        Mockito.lenient().when(mockedResponse.getHeaderNames()).thenReturn(Arrays.asList("header1", "header2"));
    }

    /**
     * Run test.
     *
     * @param executor the executor
     * @param repository the repository
     * @param testModule the test module
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ScriptingException the scripting exception
     */
    protected void runTest(IJavascriptEngineExecutor executor, IRepository repository, String testModule) throws IOException, ScriptingException {

        try {
            InputStream in = AbstractApiSuiteTest.class.getResourceAsStream("/META-INF/dirigible/" + testModule);
            try {
                if (in == null) {
                    throw new IOException(IRepositoryStructure.SEPARATOR + testModule + " does not exist");
                }
                repository.createResource(
                        IRepositoryStructure.PATH_REGISTRY_PUBLIC + IRepositoryStructure.SEPARATOR + testModule,
                        IOUtils.readBytesFromStream(in));
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (RepositoryWriteException e) {
            throw new IOException(IRepositoryStructure.SEPARATOR + testModule, e);
        }
        Object result = null;
        long start = System.currentTimeMillis();
        Object error = executor.executeServiceModule(testModule, null);
        if (error != null && error.toString() != null) {
            throw new ScriptingException(error.toString());
        }
        long time = System.currentTimeMillis() - start;
        System.out.println(String.format("API test [%s] on engine [%s] passed for: %d ms", testModule,
                executor.getType(), time));
    }
}
