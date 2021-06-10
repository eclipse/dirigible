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
package utils;

public class TestContainerConstants {

    private TestContainerConstants() {
    }

    // Docker hub images
    public static final String RABBITMQ_DOCKER_IMAGE = "rabbitmq:alpine";
    public static final String CASSANDRA_DOCKER_IMEGE = "cassandra";

    public static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
    public static final String ELASTICSEARCH_DOCKER_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.7.1";
    public static final String ETCD_CLUSTER_IMAGE = "test-etcd";

    // Configuration constants
    public static final String DIRIGIBLE_RABBITMQ_CLIENT_URI_CONST = "DIRIGIBLE_RABBITMQ_CLIENT_URI";
    public static final String DIRIGIBLE_CASSANDRA_CLIENT_URI_CONST = "DIRIGIBLE_CASSANDRA_CLIENT_URI";
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME_CONST = "DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME";
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT_CONST = "DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT";
    public static final String DIRIGIBLE_REDIS_CLIENT_URI_CONST = "DIRIGIBLE_REDIS_CLIENT_URI";
    public static final String DIRIGIBLE_ETCD_CLIENT_ENDPOINT_CONST = "DIRIGIBLE_ETCD_CLIENT_ENDPOINT";

    // Env properties after profile activation
    public static final String RABBITMQ_POM_CONST = "rabbitmq.value";
    public static final String REDIS_POM_CONST = "redis.value";
    public static final String ELASTICSEARCH_POM_CONST = "elastic.value";
    public static final String ETCD_POM_CONST = "etcd.value";
    public static final String SPARK_POM_CONST = "spark.value";
    public static final String CASSANDRA_POM_CONST = "cassandra.value";
}
