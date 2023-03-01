/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.elasticsearch;

import org.eclipse.dirigible.commons.config.Configuration;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * The Class ElasticsearchFacadeTest.
 */
public class ElasticsearchFacadeTest {

    /** The Docker Elasticsearch image. */
    private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.7.1";
    /** The Elasticsearch container client. */
    private static ElasticsearchContainer container;

    /** The Elasticsearch client. */
    private static RestHighLevelClient client;

    /** The test index's name. */
    private static final String INDEX_NAME = "posts-test-index";
    /** The test document's id. */
    private static final String DOCUMENT_ID = "1";
    /** The test document's source. */
    private static final String DOCUMENT_SOURCE =
            "{\"user\":\"test\",\"postDate\":\"2021-04-20\",\"message\":\"test index document\"}";

    /**
     * Setup.
     *
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {
        container = new ElasticsearchContainer(ELASTICSEARCH_IMAGE);
        container.start();

        Configuration.set(ElasticsearchFacade.DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME, container.getHost());
        Configuration.set(ElasticsearchFacade.DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT, container.getFirstMappedPort().toString());

        client = ElasticsearchFacade.getClient();

        if(ElasticsearchFacade.indexExists(client, INDEX_NAME)) {
            ElasticsearchFacade.deleteIndex(client, INDEX_NAME);
        }

        ElasticsearchFacade.createIndex(client, INDEX_NAME);
    }

    /**
     * Close opened resources.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @After
    public void after() throws IOException {
        client.close();

        container.stop();
    }

    /**
     * Documents test.
     *
     * @throws IOException
     *             the indexing exception
     */
    @Test
    public void documentsTest() throws IOException {
        assertTrue(ElasticsearchFacade.indexExists(client, INDEX_NAME));

        ElasticsearchFacade.indexDocument(client, INDEX_NAME, DOCUMENT_ID, DOCUMENT_SOURCE, "JSON");
        assertTrue(ElasticsearchFacade.documentExists(client, INDEX_NAME, DOCUMENT_ID));

        String indexedDocument = ElasticsearchFacade.getDocument(client, INDEX_NAME, DOCUMENT_ID).getSourceAsString();
        assertEquals(indexedDocument, DOCUMENT_SOURCE);

        String nonIndexedDocument = ElasticsearchFacade.getDocument(client, INDEX_NAME, "nonExistentId").getSourceAsString();
        assertNull(nonIndexedDocument);

        ElasticsearchFacade.deleteDocument(client, INDEX_NAME, DOCUMENT_ID);
        assertFalse(ElasticsearchFacade.documentExists(client, INDEX_NAME, DOCUMENT_ID));
    }
}
