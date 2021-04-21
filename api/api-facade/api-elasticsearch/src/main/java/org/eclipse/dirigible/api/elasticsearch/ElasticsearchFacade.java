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
package org.eclipse.dirigible.api.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ElasticsearchFacade implements IScriptingFacade {

    private static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME = "DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME";

    private static final String CLIENT_HOSTNAME = "localhost";

    private static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT = "DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT";

    private static final String CLIENT_PORT = "9200";

    private static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_SCHEME = "DIRIGIBLE_ELASTICSEARCH_CLIENT_SCHEME";

    private static final String CLIENT_SCHEME = "http";

    public static RestHighLevelClient getClient() {
        String clientHostname = Configuration.get(DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME, CLIENT_HOSTNAME);
        int clientPort = Integer.parseInt(Configuration.get(DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT, CLIENT_PORT));
        String clientScheme = Configuration.get(DIRIGIBLE_ELASTICSEARCH_CLIENT_SCHEME, CLIENT_SCHEME);

        return new RestHighLevelClient(RestClient.builder(new HttpHost(clientHostname, clientPort, clientScheme)));
    }

    public static IndexResponse index(RestHighLevelClient client, String index, String id, String documentSource)
            throws IOException {
        IndexRequest indexRequest = new IndexRequest(index);

        indexRequest.id(id);
        indexRequest.source(documentSource, XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

        return indexResponse;
    }

    public static GetResponse get(RestHighLevelClient client, String index, String id) throws IOException {
        // TODO: add optional arguments
        GetRequest getRequest = new GetRequest(index, id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        return getResponse;
    }

    public static DeleteResponse delete(RestHighLevelClient client, String index, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);

        DeleteResponse deleteResponse = client.delete(
                deleteRequest, RequestOptions.DEFAULT);

        return deleteResponse;
    }

}
