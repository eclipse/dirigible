/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.sql.processor;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.POST;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.MERGE;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.PUT;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.apache.cxf.helpers.IOUtils;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.ep.feed.ODataDeltaFeedImpl;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.eclipse.dirigible.engine.odata2.sql.test.util.MockRequestBuilder;
import org.eclipse.dirigible.engine.odata2.sql.test.util.OData2TestUtils;
import org.eclipse.dirigible.engine.odata2.sql.test.util.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultSQLProcessorTest {
    /**
     * 
     */
    private static EntityManagerFactory emf;
    private static DataSource ds;

    protected static final long TIMESTAMP = 1510049539887l; // 2017-11-07T09:12:19 GMT

    private static EdmImplProv EDM;

    @BeforeClass
    public static void grantEMF() throws ODataException {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("test");
            initializeDB(emf);
        }
        EDM = new EdmImplProv(DefaultSQLTestServiceFactory.createAnnotationEdmProvider());
    }

    @AfterClass
    public static void grantEMFClosed() {
        if (emf != null) {
            emf.close();
            emf = null;
        }
    }

    @Test
    public void testNotFoundResponseAtom() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots('NotExistentId1')") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 404);
        validateODataErrorResponse(response, HttpStatusCodes.NOT_FOUND.toString(), "No content");
    }

    @Test
    public void testNotFoundResponseJson() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots('NotExistentId1')") //
                .accept("application/json").executeRequest(GET);
        validateHttpResponse(response, 404);
        validateODataErrorResponse(response, HttpStatusCodes.NOT_FOUND.toString(), "No content");
    }

    @Test
    public void testSQLProcessorElement() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots('id_7')") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);
        ODataEntry resultEntry = retrieveODataEntry(response, "TestRoots");
        Map<String, Object> properties = resultEntry.getProperties();
        assertEquals("There shall be 5 properties found", 5, properties.size());
        assertEquals("StringValue is not correct", "stringValue_7", properties.get("StringValue"));
        Calendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp = (Calendar) properties.get("TimeValue");
        assertEquals("TimeValue is not correct", new Date(TIMESTAMP + 5000 * 7), timestamp.getTime());
        assertEquals("Media contentType is not correct", "application/octet-stream", resultEntry.getMediaMetadata().getContentType());
        assertEquals("Media sourceLink is not correct", "TestRoots('id_7')/$value", resultEntry.getMediaMetadata().getSourceLink());
    }

    @Test
    public void testSQLProcessorElementJson() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots('id_7')") //
                .accept("application/json").executeRequest(GET);
        validateHttpResponse(response, 200);
        ODataEntry resultEntry = retrieveODataEntry(response, "TestRoots");
        Map<String, Object> properties = resultEntry.getProperties();
        assertEquals("There shall be 5 properties found", 5, properties.size());
        assertEquals("StringValue is not correct", "stringValue_7", properties.get("StringValue"));
        Calendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp = (Calendar) properties.get("TimeValue");
        assertEquals("TimeValue is not correct", new Date(TIMESTAMP + 5000 * 7), timestamp.getTime());
    }

    @Test
    public void testSQLProcessorElementMediaProperty() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots('id_7')", "$value") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 501);
    }

    @Test
    public void testSQLProcessor()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, ODataException, IOException {

        List<Map<String, Object>> expectedEntities = createExpectedRootData(false);
        String entitySetName = "TestRoots";

        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$top", "10") //
                .executeRequest(GET);
        validateHttpResponse(response, 200);

        validateODataFeed(response, expectedEntities, entitySetName);
    }

    @Test
    public void testSQLProcessorCount() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots", "$count") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ByteArrayInputStream bais = (ByteArrayInputStream) response.getEntity();
        assertEquals("Invalid $count value", "10", IOUtils.toString(bais));
    }
    
	@Test
	public void testSQLProcessorCreateEntity()
			throws InstantiationException, IllegalAccessException, IOException, ODataException {
		
		String content = "{\n"
				+ "  \"d\": {\n"
				+ "    \"__metadata\": {\n"
				+ "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.edm.TestChild\"\n"
				+ "    },\n"
				+ "    \"Id\": \"123456\",\n"
				+ "    \"ChildName\": \"Name\",\n"
				+ "    \"ChildValue\": \"Value\"\n"
				+ "	}\n"
				+ "}";
		
	
		Response response = modifyingRequestBuilder(content)//
				.segments("TestChilds") //
				.accept("application/json")
				.content(content)
				.param("content-type", "application/json")
				.contentSize(content.length())
				.executeRequest(POST);
		validateHttpResponse(response, 201);

		InputStream res = (InputStream)response.getEntity();
		assertEquals("{\"d\":" //
				+ "{\"__metadata\":{" //
				+ "\"id\":\"http://localhost:8080/api/v1/TestChilds('123456')\"," //
				+ "\"uri\":\"http://localhost:8080/api/v1/TestChilds('123456')\"," //
				+ "\"type\":\"org.eclipse.dirigible.engine.odata2.sql.edm.TestChild\"}," //
				+ "\"Id\":\"123456\"," //
				+ "\"ChildName\":\"Name\"," //
				+ "\"ChildValue\":\"Value\"," //
				+ "\"Root\":{\"__deferred\":{\"uri\":\"http://localhost:8080/api/v1/TestChilds('123456')/Root\"}}}}",  //
				IOUtils.toString(res));
	}

	   @Test
	    public void testSQLProcessorCannotCreateEntityWithoutId()
	            throws InstantiationException, IllegalAccessException, IOException, ODataException {
	        
	        String content = "{\n"
	                + "  \"d\": {\n"
	                + "    \"__metadata\": {\n"
	                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.edm.TestChild\"\n"
	                + "    },\n"
	                + "    \"ChildName\": \"Name\",\n"
	                + "    \"ChildValue\": \"Value\"\n"
	                + " }\n"
	                + "}";
	        
	    
	        Response response = modifyingRequestBuilder(content)//
	                .segments("TestChilds") //
	                .accept("application/json")
	                .content(content)
	                .param("content-type", "application/json")
	                .contentSize(content.length())
	                .executeRequest(POST);
	        validateHttpResponse(response, 500);
	    }
	   
	@Test
	public void testSQLProcessorMergeEntity()
			throws InstantiationException, IllegalAccessException, IOException, ODataException {
		
		Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestChilds('1_1')") //
                .accept("application/json").executeRequest(GET);
        validateHttpResponse(response, 200);
		
		String content = "{\n"
				+ "  \"d\": {\n"
				+ "    \"__metadata\": {\n"
				+ "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.edm.TestChild\"\n"
				+ "    },\n"
				+ "    \"Id\": \"1_1\",\n"
				+ "    \"ChildName\": \"XXXX\",\n"
				+ "    \"ChildValue\":\"YYYYY\"" //
				+ "	}\n"
				+ "}";
		
	
	   response = modifyingRequestBuilder(content)//
			    .segments("TestChilds('1_1')") //
				.accept("application/json")
				.content(content)
				.param("content-type", "application/json")
				.contentSize(content.length())
				.executeRequest(MERGE);
		validateHttpResponse(response, 204);
	}
	
	@Test
	public void testSQLProcessorUpdateEntity()
			throws InstantiationException, IllegalAccessException, IOException, ODataException {
		
		Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestChilds('1_1')") //
                .accept("application/json").executeRequest(GET);
        validateHttpResponse(response, 200);
		
		String content = "{\n"
				+ "  \"d\": {\n"
				+ "    \"__metadata\": {\n"
				+ "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.edm.TestChild\"\n"
				+ "    },\n"
				+ "    \"Id\": \"1_1\",\n"
				+ "    \"ChildValue\":\"YYYYY\"" //
				+ "	}\n"
				+ "}";
		
	
	   response = modifyingRequestBuilder(content).segments("TestChilds('1_1')") //
				.accept("application/json")
				.content(content)
				.param("content-type", "application/json")
				.contentSize(content.length())
				.executeRequest(PUT);
		validateHttpResponse(response, 204);
	}
	@Test
    public void testSQLProcessorNavigation()
            throws InstantiationException, IllegalAccessException, IOException, ODataException {
        
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots('id_0')", "Child") //
                .accept("application/json").executeRequest(GET);
        
        validateHttpResponse(response, 200);
        InputStream res = (InputStream)response.getEntity();
        String responseStr = IOUtils.toString(res);
        assertTrue(responseStr.contains("\"uri\":\"http://localhost:8080/api/v1/TestChilds('0_1')"));
        assertTrue(responseStr.contains("\"uri\":\"http://localhost:8080/api/v1/TestChilds('0_2')"));
        assertTrue(responseStr.contains("\"uri\":\"http://localhost:8080/api/v1/TestChilds('0_3')"));
    }
	
	MockRequestBuilder modifyingRequestBuilder(String content) {
		MockRequestBuilder builder = new MockRequestBuilder() {

			@Override
			protected ODataServiceFactory getServiceFactoryClass() {
				DefaultSQLTestServiceFactory sf = new DefaultSQLTestServiceFactory();
				
				return sf;
			}

			@Override
			protected void enrichServletRequestMock(final ServletRequest servletRequest) {
				EasyMock.expect(servletRequest.getAttribute(DefaultSQLProcessor.DEFAULT_DATA_SOURCE_CONTEXT_KEY))
						.andReturn(grantDatasource());
				
				EasyMock.expectLastCall();
			}
			@Override
			protected void getServletInputStream(final ODataHttpMethod method, final EasyMockSupport easyMockSupport,
		            final HttpServletRequest servletRequest) throws IOException {
				
				final ServletInputStream s = new DelegateServletInputStream(new ByteArrayInputStream(content.getBytes()));
		        expect(servletRequest.getInputStream()).andReturn(s).atLeastOnce();
		    }

		};
		return builder;
	}
	
    @Test
    public void testSQLProcessorTop() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$top", "3") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);
        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        assertEquals("There shall be 3 entries found", 3, resultFeed.getEntries().size());
    }

    @Test
    public void testSQLProcessorSkip() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$skip", "3") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);
        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        assertEquals("There shall be 7 entries found", 7, resultFeed.getEntries().size());

    }

    @Test
    public void testSQLProcessorSelect() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$select", "StringValue,IntValue") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);
        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        Map<String, Object> properties = resultFeed.getEntries().get(0).getProperties();
        assertEquals("There shall be 2 properties found", 2, properties.size());
        assertNotNull("There shall be property IntValue", properties.get("IntValue"));
        assertNotNull("There shall be property StringValue", properties.get("StringValue"));
    }

    @Test
    public void testSQLProcessorOrderByAsc() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$orderby", "IntValue asc") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 10 entries", 10, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        assertEquals("IntValue is not correct", 100, properties.get("IntValue"));
    }

    @Test
    public void testSQLProcessorOrderByDesc() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$orderby", "IntValue desc") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 10 entries", 10, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        assertEquals("IntValue is not correct", 109, properties.get("IntValue"));
    }

    @Test
    public void testSQLProcessorTimeOrderByDesc() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$orderby", "TimeValue desc") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 10 entries", 10, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        Calendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        timestamp = (Calendar) properties.get("TimeValue");
        assertEquals("TimeValue is not correct", new Date(TIMESTAMP + 5000 * 9), timestamp.getTime());
    }

    @Test
    public void testSQLProcessorFilter() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$orderby", "IntValue desc") //
                .param("$filter", "IntValue eq 103 or endswith(StringValue,'7')") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 2 entries", 2, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        assertEquals("IntValue is not correct", 107, properties.get("IntValue"));
    }

    @Test
    public void testSQLProcessorFilterOnConvertValue() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$orderby", "IntValue desc") //
                .param("$filter", "ConvertValue eq '103' ") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 1 entries", 1, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        assertEquals("ConvertValue is not correct", "103", properties.get("ConvertValue"));
    }

    @Test
    public void testSQLProcessorFilterOnConvertValueWithIncompatibleValue()
            throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$orderby", "IntValue desc") //
                .param("$filter", "ConvertValue eq 'hugo' ") //<--has to be convertible in a Long
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 500);
        //TODO: HTTP response shall be 400 but for that we have to enhance the exception handling in the generic
        //DelegateProcessor in it-op such that olingo honors the passed HTTP Status code 
    }

    @Test
    public void testSQLProcessorFilterLeTime() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$filter", "TimeValue le datetime'2017-11-07T10:12:39'") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 4 entries", 4, entries.size());
    }

    @Test
    public void testSQLProcessorFilterGeTime() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$filter", "TimeValue ge datetime'2017-11-07T10:12:39'") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 6 entries", 6, entries.size());
    }

    @Test
    public void testSQLProcessorExpand() throws InstantiationException, IllegalAccessException, IOException, ODataException {
        Response response = DefaultMockRequestBuilder.createRequest(grantDatasource()) //
                .segments("TestRoots") //
                .param("$top", "3") //
                .param("$expand", "Child") //
                .accept("application/atom+xml").executeRequest(GET);
        validateHttpResponse(response, 200);

        ODataFeed resultFeed = retrieveODataFeed(response, "TestRoots");
        assertEquals("There shall be 3 entries found", 3, resultFeed.getEntries().size());
        assertEquals("There shall be 5 properties found", 6, resultFeed.getEntries().get(0).getProperties().size());

        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 3 entries", 3, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        assertEquals("IntValue is not correct", 100, properties.get("IntValue"));

        List<ODataEntry> childEntries = ((ODataDeltaFeedImpl) (properties.get("Child"))).getEntries();
        Map<String, Object> childProperties = childEntries.get(0).getProperties();
        assertEquals("There shall be 3 properties found", 3, childProperties.size());
        assertEquals("ChildName is not correct", "childName", childProperties.get("ChildName"));
    }

    private List<Map<String, Object>> createExpectedRootData(final boolean expand) {
        List<Map<String, Object>> expectedEntities = new ArrayList<>(1);
        for (int i = 0; i < 10; i++) {
            Map<String, Object> expectedEntity = new HashMap<>(2);
            expectedEntity.put("ChangeId", "id_" + i);
            expectedEntity.put("StringValue", "stringValue_" + i);
            Calendar timestamp = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            timestamp.setTime(new Date(TIMESTAMP + i * 5000));
            expectedEntity.put("TimeValue", timestamp);
            expectedEntity.put("IntValue", 100 + i);
            expectedEntity.put("ConvertValue", String.valueOf(100 + i));
            if (expand && i < 3) {
                for (int j = 0; j < 10; j++) {
                    Map<String, Object> expectedChildEntity = new HashMap<>(2);
                    expectedChildEntity.put("ChildName", "childName");
                    expectedChildEntity.put("ChildValue", "childValue_" + i);
                    List<Map<String, Object>> expectedChildEntities = new ArrayList<>();
                    expectedChildEntities.add(expectedChildEntity);
                    expectedEntity.put("Child", expectedChildEntities);
                }
            }
            expectedEntities.add(expectedEntity);
        }
        return expectedEntities;
    }

    private static DataSource grantDatasource() {
        if (ds == null) {
            ds = new JpaWrappingDataSource(emf);
        }
        return ds;
    }

    private static void validateHttpResponse(final Response response, final int expStatus) {
        assertNotNull(response);
        assertEquals(expStatus, response.getStatus());
    }

    private static ODataFeed retrieveODataFeed(final Response response, final String entitySetName) throws IOException, ODataException {
        EdmEntitySet entitySet = EDM.getDefaultEntityContainer().getEntitySet(entitySetName);

        return OData2TestUtils.retrieveODataFeedFromResponse(response, entitySet);
    }

    private static ODataEntry retrieveODataEntry(final Response response, final String entitySetName) throws IOException, ODataException {
        EdmEntitySet entitySet = EDM.getDefaultEntityContainer().getEntitySet(entitySetName);

        return OData2TestUtils.retrieveODataEntryFromResponse(response, entitySet);
    }

    private ODataFeed validateODataFeed(final Response response, final List<Map<String, Object>> expectedEntries,
            final String entitySetName) throws IOException, ODataException {
        ODataFeed oDataFeed = retrieveODataFeed(response, entitySetName);
        Pair<Boolean, String> result = OData2TestUtils.validateODataFeed(expectedEntries, oDataFeed);
        if (!result.getFirst()) {
            fail(result.getSecond());
        }
        return oDataFeed;
    }

    private ODataEntry validateODataEntry(final Response response, final Map<String, Object> expected, final String entitySetName)
            throws IOException, ODataException {
        ODataEntry oDataEntry = retrieveODataEntry(response, entitySetName);
        Pair<Boolean, String> result = OData2TestUtils.validateODataEntry(expected, oDataEntry);
        if (!result.getFirst()) {
            fail(result.getSecond());
        }
        return oDataEntry;
    }

    private void validateODataErrorResponse(final Response response, final String expectedErrorCode, final String expectedErrorMessage) {
        ODataErrorContext errorDocument = null;
        try {
            errorDocument = OData2TestUtils.retrieveODataErrorDocumentFromResponse(response);
        } catch (Exception e) {
            fail("The response body could not be parsed as a valid error document. Error: " + e.getMessage());
        }
        assertEquals(expectedErrorCode, errorDocument.getErrorCode());
        assertEquals(expectedErrorMessage, errorDocument.getMessage());
    }

    private void validateNotImplementedErrorResponse(final Response response) {
        validateODataErrorResponse(response, HttpStatusCodes.NOT_IMPLEMENTED.toString(), "Not implemented");
    }

    private static void initializeDB(final EntityManagerFactory emf) {
        fillDBWithData();
    }

    /**
     *
     */
    private static void fillDBWithData() {

        DataSource ds = grantDatasource();

        executeUpdate(ds, "CREATE SCHEMA TEST");
        executeUpdate(ds, "CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) PRIMARY KEY, SEQ_COUNT DECIMAL(15))");
        executeUpdate(ds,
                "CREATE TABLE TESTROOTTABLE (ID BIGINT PRIMARY KEY, CHANGEID VARCHAR(32) NOT NULL UNIQUE, STRINGVALUE VARCHAR(20), TIMEVALUE TIMESTAMP NOT NULL, INTVALUE INT, CONVERTVALUE BIGINT, MEDIAVALUE BLOB)");
        executeUpdate(ds, "INSERT INTO SEQUENCE VALUES ('TestRootTable', 0)");
        executeUpdate(ds,
                "CREATE TABLE TESTCHILDTABLE (ID VARCHAR(32) PRIMARY KEY, CHILDNAME VARCHAR(10), CHILDVALUE VARCHAR(20), ROOT_ID BIGINT)");
        executeUpdate(ds, "INSERT INTO SEQUENCE VALUES ('TestChildTable', 0)");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        for (int i = 0; i < 10; i++) {
            executeUpdate(ds, "INSERT INTO TESTROOTTABLE VALUES (" + i + ",'" + "id_" + i + "', '" + "stringValue_" + i + "', '"
                    + sdf.format(new Date(TIMESTAMP + i * 5000)) + "', " + (100 + i) + ", " + (100 + i) + ", null)");
            for (int j = 0; j < 10; j++) {
                executeUpdate(ds,
                        "INSERT INTO TESTCHILDTABLE VALUES ('" + i + "_" + j + "', 'childName', '" + "childValue_" + i + "', " + i + ")");
            }
        }

    }

    private static void executeUpdate(DataSource ds, String sql) {
        try {
            Connection con = ds.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    class DelegateServletInputStream extends ServletInputStream {

    	private final InputStream delegate;

    	private boolean finished = false;

    	public DelegateServletInputStream(InputStream sourceStream) {
    		this.delegate = sourceStream;
    	}

    	public final InputStream getSourceStream() {
    		return this.delegate;
    	}

    	@Override
    	public int read() throws IOException {
    		int data = this.delegate.read();
    		if (data == -1) {
    			this.finished = true;
    		}
    		return data;
    	}

    	@Override
    	public int available() throws IOException {
    		return this.delegate.available();
    	}

    	@Override
    	public void close() throws IOException {
    		super.close();
    		this.delegate.close();
    	}

    	@Override
    	public boolean isFinished() {
    		return this.finished;
    	}

    	@Override
    	public boolean isReady() {
    		return true;
    	}

    	@Override
    	public void setReadListener(ReadListener readListener) {
    	}

	}

}
