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
package org.eclipse.dirigible.api.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

/**
 * The Class ElasticsearchFacade.
 */
public class ElasticsearchFacade implements IScriptingFacade {

    /** The Elasticsearch client's host env var. */
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME = "DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME";
    /** The default value for Elasticsearch client's host env var. */
    private static final String CLIENT_HOSTNAME = "localhost";

    /** The Elasticsearch client's port env var . */
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT = "DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT";
    /** The default value for Elasticsearch client's port env var. */
    private static final String CLIENT_PORT = "9200";

    /** The Elasticsearch client's scheme env var. */
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_SCHEME = "DIRIGIBLE_ELASTICSEARCH_CLIENT_SCHEME";
    /** The default value for Elasticsearch client's scheme env var. */
    private static final String CLIENT_SCHEME = "http";

    /**
     * Creates a client.
     *
     * @return the client
     */
    public static RestHighLevelClient getClient() {
        String clientHostname = Configuration.get(DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME, CLIENT_HOSTNAME);
        int clientPort = Integer.parseInt(Configuration.get(DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT, CLIENT_PORT));
        String clientScheme = Configuration.get(DIRIGIBLE_ELASTICSEARCH_CLIENT_SCHEME, CLIENT_SCHEME);

        return new RestHighLevelClient(RestClient.builder(new HttpHost(clientHostname, clientPort, clientScheme)));
    }

    // DOCUMENT API

    /**
     * Indexes a document.
     *
     * @param client the client
     * @param index the index's name
     * @param id the document's id
     * @param documentSource the document's source
     * @param xContentType the document source's x-content-type
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean indexDocument(RestHighLevelClient client, String index, String id, String documentSource, String xContentType)
            throws IOException {
        IndexRequest indexRequest = new IndexRequest(index);

        indexRequest.id(id);
        indexRequest.source(documentSource, XContentType.valueOf(xContentType));

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();

        return shardInfo.getTotal() == shardInfo.getSuccessful();
    }

    /**
     * Retrieves a document.
     *
     * @param client the client
     * @param index the index's name
     * @param id the document's id
     * @return the document
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static GetResponse getDocument(RestHighLevelClient client, String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        return getResponse;
    }

    /**
     * Checks if a document exists.
     *
     * @param client the client
     * @param index the index's name
     * @param id the document's id
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean documentExists(RestHighLevelClient client, String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);

        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        return client.exists(getRequest, RequestOptions.DEFAULT);
    }

    /**
     * Deletes a document.
     *
     * @param client the client
     * @param index the index's name
     * @param id the document's id
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean deleteDocument(RestHighLevelClient client, String index, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);

        DeleteResponse deleteResponse = client.delete(
                deleteRequest, RequestOptions.DEFAULT);
        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();

        return shardInfo.getTotal() != shardInfo.getSuccessful();
    }

    // INDEX API

    /**
     * Creates an index.
     *
     * @param client the client
     * @param name the name of the index
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean createIndex(RestHighLevelClient client, String name) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(name);

        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        return createIndexResponse.isAcknowledged() && createIndexResponse.isShardsAcknowledged();
    }

    /**
     * Deletes an index.
     *
     * @param client the client
     * @param name the name of the index
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean deleteIndex(RestHighLevelClient client, String name) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(name);

        AcknowledgedResponse deleteIndexResponse = client.indices().delete(request, RequestOptions.DEFAULT);

        return deleteIndexResponse.isAcknowledged();
    }

    /**
     * Checks if an index exists.
     *
     * @param client the client
     * @param name the name of the index
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static boolean indexExists(RestHighLevelClient client, String name) throws IOException {
        GetIndexRequest request = new GetIndexRequest(name);

        return client.indices().exists(request, RequestOptions.DEFAULT);
    }
}
