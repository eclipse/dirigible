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
package org.eclipse.dirigible.engine.odata2.sql;

import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.GET;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.MERGE;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.POST;
import static org.apache.olingo.odata2.api.commons.ODataHttpMethod.PUT;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.helpers.IOUtils;
import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;
import org.apache.olingo.odata2.core.ep.feed.ODataDeltaFeedImpl;
import org.easymock.EasyMockSupport;
import org.eclipse.dirigible.engine.odata2.sql.entities.Car;
import org.eclipse.dirigible.engine.odata2.sql.entities.Driver;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

import liquibase.exception.LiquibaseException;

public class ODataSQLProcessorTest {

    DataSource ds;

    AnnotationEdmProvider edm;

    ODataServiceFactory sf;

    @Before
    public void setup() throws ODataException, SQLException {
        ds = createDataSource();
        edm = new AnnotationEdmProvider(Arrays.asList(Car.class, Driver.class));
        edm.getSchemas();
        sf = new OData2TestServiceFactory(ds, Car.class, Driver.class);
        OData2TestUtils.initLiquibase(ds);
    }

    @After
    public void clearDb() {
        try (Connection c = ds.getConnection()) {
            try (PreparedStatement s = c.prepareStatement("DROP ALL OBJECTS")) {
                s.execute();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear the H2 database, the tests are not isolated!");
        }
    }

    public DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:odata2;JMX=TRUE;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        return ds;
    }

    @Test
    public void testSQLProcessor() throws Exception {

        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$top", "10") //
                .executeRequest(GET);
        String content = IOUtils.toString((InputStream) response.getEntity());
        assertEquals(200, response.getStatus());
        assertTrue(content.contains("BMW"));
        assertTrue(content.contains("Moskvitch"));
        assertTrue(content.contains("Lada"));
    }

    @Test
    public void testNotFoundXmlResponse() throws Exception {
        String nonExistingUUID = "77777777-7777-7777-7777-777777777777";
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('" + nonExistingUUID + "')") //
                .accept("application/atom+xml").executeRequest(GET);
        assertNotNull(response);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testNotFoundJsonResponse() throws Exception {
        String nonExistingUUID = "77777777-7777-7777-7777-777777777777";
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('" + nonExistingUUID + "')") //
                .accept("application/atom+xml").executeRequest(GET);
        assertNotNull(response);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testRequestEntity() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());
        ODataEntry resultEntry = retrieveODataEntry(response, "Cars");
        Map<String, Object> properties = resultEntry.getProperties();
        assertEquals("BMW", properties.get("Make"));
        assertEquals("530e", properties.get("Model"));
        assertEquals(2021, properties.get("Year"));
        assertEquals(67000.0d, properties.get("Price"));

        Calendar timestamp = (Calendar) properties.get("Updated");
        assertEquals(2021, timestamp.get(Calendar.YEAR));
        assertEquals(5, timestamp.get(Calendar.MONTH));
        assertEquals(7, timestamp.get(Calendar.DAY_OF_MONTH));

    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testTop() throws Exception {

        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Drivers")//
                .param("$top", "2") //
                .accept("application/json")//
                .executeRequest(GET);

        assertEquals(200, response.getStatus());

        String content = IOUtils.toString((InputStream) response.getEntity());
        Map jobj = new Gson().fromJson(content, Map.class);
        List elements = (List) ((Map) jobj.get("d")).get("results");
        assertEquals(2, elements.size());
    }

    @Test
    public void testCount() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars", "$count") //
                .accept("application/json")//
                .executeRequest(GET);

        assertEquals(200, response.getStatus());

        ByteArrayInputStream bais = (ByteArrayInputStream) response.getEntity();
        assertEquals("7", IOUtils.toString(bais));
    }

    @Test
    public void testCreateEntity() throws Exception {

        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "    }," //
                + "    \"Id\": \"3ab18d92-a574-45bb-a5e5-bcce38b7afb8\"," //
                + "    \"Make\": \"BMW\"," //
                + "    \"Model\": \"320i\"," //
                + "    \"Year\": 2021," //
                + "    \"Price\": 30000.0" //
                + " }" + "}";

        Response response = modifyingRequestBuilder(sf, content)//
                .segments("Cars") //
                .accept("application/json")//
                .content(content).param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(POST);
        assertEquals(201, response.getStatus());

        String res = IOUtils.toString((InputStream) response.getEntity());
        assertEquals("{" // 
                + "\"d\":{" //
                + "\"__metadata\":{" //
                + "\"id\":\"http://localhost:8080/api/v1/Cars('3ab18d92-a574-45bb-a5e5-bcce38b7afb8')\"," //
                + "\"uri\":\"http://localhost:8080/api/v1/Cars('3ab18d92-a574-45bb-a5e5-bcce38b7afb8')\"," //
                + "\"type\":\"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "}," //
                + "\"Id\":\"3ab18d92-a574-45bb-a5e5-bcce38b7afb8\"," //
                + "\"Make\":\"BMW\"," //
                + "\"Model\":\"320i\"," //
                + "\"Year\":2021," //
                + "\"Price\":\"30000.0\"," //
                + "\"Updated\":null," //
                + "\"Drivers\":{" //
                + "\"__deferred\":{" //
                + "\"uri\":\"http://localhost:8080/api/v1/Cars('3ab18d92-a574-45bb-a5e5-bcce38b7afb8')/Drivers\"" //
                + "}" //
                + "}" //
                + "}" //
                + "}", //
                res);

    }
    
    @Test
    public void testReadEntity() throws Exception {
    	
    	String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "    }," //
                + "    \"Id\": \"3ab18d92-a574-45bb-a5e5-bcce38b7af11\"," //
                + "    \"Make\": \"BMW\"," //
                + "    \"Model\": \"320i\"," //
                + "    \"Year\": 2021," //
                + "    \"Price\": 30000.0" //
                + " }" + "}";

        modifyingRequestBuilder(sf, content)//
                .segments("Cars") //
//                .accept("application/json")//
                .content(content).param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(POST);
        
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('3ab18d92-a574-45bb-a5e5-bcce38b7af11')") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        String res = IOUtils.toString((InputStream) response.getEntity());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(res.getBytes()));
        String idValue = null;
		NodeList nList = document.getElementsByTagName("d:Id");
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				idValue = eElement.getTextContent();
			}
		}
		assertEquals("3ab18d92-a574-45bb-a5e5-bcce38b7af11", idValue);
    }

    @Test
    public void testCreateEntityWithoutId() throws Exception {

        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "    }," //
                + "    \"Make\": \"BMW\"," //
                + "    \"Model\": \"320i\"," //
                + "    \"Price\": 30000.0" //
                + " }" + "}";

        Response response = modifyingRequestBuilder(sf, content)//
                .segments("Cars") //
                .accept("application/json")//
                .content(content).param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(POST);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateEntityWithReferenceID() throws IOException, ODataException {
        String existingCarId = "639cac17-4cfd-4d94-b5d0-111fd5488423";
        String newDriverId = "11111111-1111-1111-1111-111111111111";
        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Driver\"" //
                + "    }," //
                + "    \"Id\": \"" + newDriverId + "\"," //
                + "    \"FirstName\": \"John\"," //
                + "    \"LastName\": \"Smith\"," //
                + "    \"Car\": " + "         { \"Id\": \"" + existingCarId + "\"}" // 
                + " }" //
                + "}";

        System.out.println(content);
        Response createResponse = modifyingRequestBuilder(sf, content)//
                .segments("Drivers") //
                .accept("application/json")//
                .param("content-type", "application/json") //
                .content(content)//
                .contentSize(content.length()).executeRequest(POST);

        assertEquals(201, createResponse.getStatus());
        String createResponseStr = IOUtils.toString((InputStream) createResponse.getEntity());
        System.out.println(createResponseStr);

        Response getResponseCreatedEntity = OData2RequestBuilder.createRequest(sf) //
                .segments("Drivers('" + newDriverId + "')") //
                .segments("Car").accept("application/json").executeRequest(GET);
        assertEquals(200, getResponseCreatedEntity.getStatus());
        String referencedCarResponse = IOUtils.toString((InputStream) getResponseCreatedEntity.getEntity());
        assertTrue(referencedCarResponse.contains(existingCarId));

    }

    @Test
    public void testMergeEntity() throws Exception {
        Response existingCar = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/json").executeRequest(GET);
        assertEquals(200, existingCar.getStatus());

        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "    }," //
                + "    \"Id\": \"639cac17-4cfd-4d94-b5d0-111fd5488423\"," //
                + "    \"Price\": 50000.0" //
                + " }" + "}";

        Response response = modifyingRequestBuilder(sf, content)//
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/json")//
                .content(content)//
                .param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(MERGE);
        assertEquals(204, response.getStatus());

        existingCar = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/json").executeRequest(GET);
        assertEquals(200, existingCar.getStatus());
        ODataEntry resultEntry = retrieveODataEntry(existingCar, "Cars");
        Map<String, Object> properties = resultEntry.getProperties();
        assertEquals("BMW", properties.get("Make"));
        assertEquals("530e", properties.get("Model"));
        assertEquals(2021, properties.get("Year"));
        assertEquals(50000.0d, properties.get("Price"));
    }

    @Test
    public void testChangeReferenceId() throws Exception {
        String existingCarId = "639cac17-4cfd-4d94-b5d0-111fd5488423";

        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Driver\"" //
                + "    }," //
                + "    \"Id\": \"695796c4-09a1-11ec-9a03-0242ac130005\"," //
                + "    \"Car\": " + "         { \"Id\": \"" + existingCarId + "\"}" // 
                + " }" + "}";

        Response response = modifyingRequestBuilder(sf, content)//
                .segments("Drivers('695796c4-09a1-11ec-9a03-0242ac130005')") //
                .accept("application/json")//
                .content(content)//
                .param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(MERGE);
        assertEquals(204, response.getStatus());
    }

    @Test
    public void testPutEntity() throws Exception {
        Response existingCar = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/json").executeRequest(GET);
        assertEquals(200, existingCar.getStatus());

        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "    }," //
                + "    \"Id\": \"639cac17-4cfd-4d94-b5d0-111fd5488423\"," //
                + "    \"Price\": 50000.0" //
                + " }" + "}";

        Response response = modifyingRequestBuilder(sf, content)//
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/json")//
                .content(content)//
                .param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(PUT);
        assertEquals(204, response.getStatus());

        existingCar = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //
                .accept("application/json").executeRequest(GET);
        assertEquals(200, existingCar.getStatus());
        ODataEntry resultEntry = retrieveODataEntry(existingCar, "Cars");
        Map<String, Object> properties = resultEntry.getProperties();
        assertEquals("BMW", properties.get("Make"));
        assertEquals("530e", properties.get("Model"));
        assertEquals(2021, properties.get("Year"));
        assertEquals(50000.0d, properties.get("Price"));
    }


    @Test
    public void testPutEntityWithFilterNotAllowed() throws Exception {
        String content = "{" //
                + "  \"d\": {" //
                + "    \"__metadata\": {" //
                + "      \"type\": \"org.eclipse.dirigible.engine.odata2.sql.entities.Car\"" //
                + "    }," //
                + "    \"Id\": \"639cac17-4cfd-4d94-b5d0-111fd5488423\"," //
                + "    \"Price\": 50000.0" //
                + " }" + "}";

        Response response = modifyingRequestBuilder(sf, content)//
                .segments("Cars('639cac17-4cfd-4d94-b5d0-111fd5488423')") //

                .accept("application/json")//
                .content(content)//
                .param("$filter", "Year eq 1982 or endswith(Make,'W')") //
                .param("content-type", "application/json")//
                .contentSize(content.length()).executeRequest(PUT);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testNavigation() throws IOException, ODataException {

        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars('7990d49f-cfaf-48ab-8c6f-adbe7aaa069e')", "Drivers") //
                .accept("application/json").executeRequest(GET);

        assertEquals(200, response.getStatus());
        InputStream res = (InputStream) response.getEntity();
        String responseStr = IOUtils.toString(res);
        assertTrue(responseStr.contains("\"uri\":\"http://localhost:8080/api/v1/Drivers('695796c4-09a1-11ec-9a03-0242ac130003')/Car"));
        assertTrue(responseStr.contains("\"uri\":\"http://localhost:8080/api/v1/Drivers('695796c4-09a1-11ec-9a03-0242ac130006')/Car"));
    }

    @Test
    public void testTop2() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Drivers") //
                .param("$top", "3") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());
        ODataFeed resultFeed = retrieveODataFeed(response, "Drivers");
        assertEquals(3, resultFeed.getEntries().size());
    }

    @Test
    public void testSkip1() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$skip", "3") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());
        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        assertEquals(4, resultFeed.getEntries().size());

    }

    @Test
    public void testSkip2() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Drivers") //
                .param("$skip", "3") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());
        ODataFeed resultFeed = retrieveODataFeed(response, "Drivers");
        assertEquals(3, resultFeed.getEntries().size());

    }

    @Test
    public void testSelect() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$select", "Make,Model") //
                .accept("application/atom+xml").executeRequest(GET);
        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        Map<String, Object> properties = resultFeed.getEntries().get(0).getProperties();
        assertEquals(2, properties.size());
        assertNotNull(properties.get("Make"));
        assertNotNull(properties.get("Model"));
    }

    @Test
    public void testOrderByAsc() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$orderby", "Price asc") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(7, entries.size());
        Map<String, Object> firstCarProperties = entries.get(0).getProperties();
        assertEquals(3000.0, firstCarProperties.get("Price"));
    }

    @Test
    public void testOrderByDesc() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$orderby", "Price desc") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(7, entries.size());
        Map<String, Object> firstCarProperties = entries.get(0).getProperties();
        assertEquals(67000.0, firstCarProperties.get("Price"));
    }

    @Test
    public void tesFilter() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$orderby", "Price desc") //
                .param("$filter", "Year eq 1982 or endswith(Make,'W')") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(2, entries.size());
        assertEquals(67000.0, entries.get(0).getProperties().get("Price"));
        assertEquals(4000.0, entries.get(1).getProperties().get("Price"));

    }

    @Test
    public void tesFilterTwoConditionsAnd() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$orderby", "Price desc") //
                .param("$filter", "Year eq 1982 and startswith(Make,'Mos')") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(1, entries.size());
        assertEquals(4000.0, entries.get(0).getProperties().get("Price"));
        assertEquals("Moskvitch", entries.get(0).getProperties().get("Make"));
    }

    @Test
    public void testFilter2() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$orderby", "Price desc") //
                .param("$filter", "Make eq 'Moskvitch' ") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(1, entries.size());
        Map<String, Object> properties = entries.get(0).getProperties();
        assertEquals("Moskvitch", properties.get("Make"));
    }

    @Test
    public void testIncompatibleValue() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$filter", "Price eq 'hugo' ") //<--has to be convertible in a Double
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testSQLProcessorFilterLeTime() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$filter", "Updated le datetime'2016-11-07T10:12:39'") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(1, entries.size());
    }

    @Test
    public void testFilterGeTime() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Drivers") //
                .param("$filter", "Updated ge datetime'2011-11-07T10:12:39'") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Drivers");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals(6, entries.size());
    }
    
    @Test
    public void testExpand() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$top", "3") //
                .param("$expand", "Drivers") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());

        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        assertEquals(3, resultFeed.getEntries().size());
        assertEquals(7, resultFeed.getEntries().get(0).getProperties().size());

        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("There shall be exactly 3 entries", 3, entries.size());
        Map<String, Object> firstEntryProperties = entries.get(0).getProperties();
        assertEquals(1982, firstEntryProperties.get("Year"));
        assertEquals("Moskvitch", firstEntryProperties.get("Make"));
        assertEquals("412", firstEntryProperties.get("Model"));

        List<ODataEntry> drivers = ((ODataDeltaFeedImpl) (firstEntryProperties.get("Drivers"))).getEntries();
        assertEquals(2, drivers.size());
        Map<String, Object> firstDriverProperties = drivers.get(0).getProperties();
        assertEquals("Johnny", firstDriverProperties.get("FirstName"));
        Map<String, Object> secondDriverProperties = drivers.get(1).getProperties();
        assertEquals("Natalie", secondDriverProperties.get("FirstName"));
    }
    
    
    @Test
    public void testOrderByExpandedEntityWithoutExpand() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$top", "3") //
                .param("$orderby", "Drivers/FirstName desc") //
                //expand missing
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(500, response.getStatus()); //TODO refine the error codes, here it should be a bad request
        
    }

    @Test
    public void testOrderByExpandedEntityProperty() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$top", "3") //
                .param("$orderby", "Drivers/FirstName desc") //
                .param("$expand", "Drivers") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());
        
        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("The limit must work with expand", 3, entries.size());
        Map<String, Object> firstEntryProperties = entries.get(0).getProperties();
        assertEquals(1982, firstEntryProperties.get("Year"));
        assertEquals("Moskvitch", firstEntryProperties.get("Make"));
        assertEquals("412", firstEntryProperties.get("Model"));

        List<ODataEntry> drivers = ((ODataDeltaFeedImpl) (firstEntryProperties.get("Drivers"))).getEntries();
        assertEquals(2, drivers.size());
        Map<String, Object> firstDriverProperties = drivers.get(0).getProperties();
        assertEquals("Natalie", firstDriverProperties.get("FirstName"));
        Map<String, Object> secondDriverProperties = drivers.get(1).getProperties();
        assertEquals("Johnny", secondDriverProperties.get("FirstName"));
        
        
        
        Response responseAsc = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$top", "3") //
                .param("$orderby", "Drivers/FirstName asc") //
                .param("$expand", "Drivers") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, responseAsc.getStatus());
        
        ODataFeed resultFeedAsc = retrieveODataFeed(responseAsc, "Cars");
        List<ODataEntry> entriesAsc = resultFeedAsc.getEntries();
        assertEquals("The limit must work with expand", 3, entriesAsc.size());
        Map<String, Object> firstEntryPropertiesAsc = entriesAsc.get(0).getProperties();
        assertEquals(2015, firstEntryPropertiesAsc.get("Year"));
        assertEquals("Ford", firstEntryPropertiesAsc.get("Make"));
        assertEquals("S-Max", firstEntryPropertiesAsc.get("Model"));

        List<ODataEntry> driversAsc = ((ODataDeltaFeedImpl) (firstEntryPropertiesAsc.get("Drivers"))).getEntries();
        assertEquals(0, driversAsc.size());
    }
    
    @Test
    public void testOrderByExpandedEntityProperties() throws Exception {
        Response response = OData2RequestBuilder.createRequest(sf) //
                .segments("Cars") //
                .param("$top", "3") //
                .param("$orderby", "Drivers/FirstName desc,Drivers/LastName asc") //
                .param("$expand", "Drivers") //
                .accept("application/atom+xml").executeRequest(GET);
        assertEquals(200, response.getStatus());
        ODataFeed resultFeed = retrieveODataFeed(response, "Cars");
        List<ODataEntry> entries = resultFeed.getEntries();
        assertEquals("The limit must work with expand", 3, entries.size());
        Map<String, Object> firstEntryProperties = entries.get(0).getProperties();
        assertEquals(1982, firstEntryProperties.get("Year"));
        assertEquals("Moskvitch", firstEntryProperties.get("Make"));
        assertEquals("412", firstEntryProperties.get("Model"));

        List<ODataEntry> drivers = ((ODataDeltaFeedImpl) (firstEntryProperties.get("Drivers"))).getEntries();
        assertEquals(2, drivers.size());
        Map<String, Object> firstDriverProperties = drivers.get(0).getProperties();
        assertEquals("Natalie", firstDriverProperties.get("FirstName"));
        Map<String, Object> secondDriverProperties = drivers.get(1).getProperties();
        assertEquals("Johnny", secondDriverProperties.get("FirstName"));

    }
    
    
    OData2RequestBuilder modifyingRequestBuilder(ODataServiceFactory sf, String content) {
        OData2RequestBuilder builder = new OData2RequestBuilder() {
            @Override
            protected void getServletInputStream(final ODataHttpMethod method, final EasyMockSupport easyMockSupport,
                    final HttpServletRequest servletRequest) throws IOException {

                final ServletInputStream s = new DelegateServletInputStream(new ByteArrayInputStream(content.getBytes()));
                expect(servletRequest.getInputStream()).andReturn(s).atLeastOnce();
            }

        };
        return builder.serviceFactory(sf);
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

    private ODataFeed retrieveODataFeed(final Response response, final String entitySetName) throws IOException, ODataException {
        EdmEntitySet entitySet = new EdmImplProv(edm).getDefaultEntityContainer().getEntitySet(entitySetName);
        return OData2TestUtils.retrieveODataFeedFromResponse(response, entitySet);
    }

    private ODataEntry retrieveODataEntry(final Response response, final String entitySetName) throws IOException, ODataException {
        EdmEntitySet entitySet = new EdmImplProv(edm).getDefaultEntityContainer().getEntitySet(entitySetName);
        return OData2TestUtils.retrieveODataEntryFromResponse(response, entitySet);
    }

}
