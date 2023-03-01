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
package utils;

/**
 * The Class TestContainerConstants.
 */
public class TestContainerConstants {

    /**
     * Instantiates a new test container constants.
     */
    private TestContainerConstants() {
    }

    /** The Constant RABBITMQ_DOCKER_IMAGE. */
    // Docker hub images
    public static final String RABBITMQ_DOCKER_IMAGE = "rabbitmq:alpine";
    
    /** The Constant CASSANDRA_DOCKER_IMEGE. */
    public static final String CASSANDRA_DOCKER_IMEGE = "cassandra";

    /** The Constant REDIS_DOCKER_IMAGE. */
    public static final String REDIS_DOCKER_IMAGE = "redis:5.0.3-alpine";
    
    /** The Constant ELASTICSEARCH_DOCKER_IMAGE. */
    public static final String ELASTICSEARCH_DOCKER_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.7.1";
    
    /** The Constant ETCD_CLUSTER_IMAGE. */
    public static final String ETCD_CLUSTER_IMAGE = "test-etcd";

    /** The Constant DIRIGIBLE_RABBITMQ_CLIENT_URI_CONST. */
    // Configuration constants
    public static final String DIRIGIBLE_RABBITMQ_CLIENT_URI_CONST = "DIRIGIBLE_RABBITMQ_CLIENT_URI";
    
    /** The Constant DIRIGIBLE_CASSANDRA_CLIENT_URI_CONST. */
    public static final String DIRIGIBLE_CASSANDRA_CLIENT_URI_CONST = "DIRIGIBLE_CASSANDRA_CLIENT_URI";
    
    /** The Constant DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME_CONST. */
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME_CONST = "DIRIGIBLE_ELASTICSEARCH_CLIENT_HOSTNAME";
    
    /** The Constant DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT_CONST. */
    public static final String DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT_CONST = "DIRIGIBLE_ELASTICSEARCH_CLIENT_PORT";
    
    /** The Constant DIRIGIBLE_REDIS_CLIENT_URI_CONST. */
    public static final String DIRIGIBLE_REDIS_CLIENT_URI_CONST = "DIRIGIBLE_REDIS_CLIENT_URI";
    
    /** The Constant DIRIGIBLE_ETCD_CLIENT_ENDPOINT_CONST. */
    public static final String DIRIGIBLE_ETCD_CLIENT_ENDPOINT_CONST = "DIRIGIBLE_ETCD_CLIENT_ENDPOINT";

    /** The Constant RABBITMQ_POM_CONST. */
    // Env properties after profile activation
    public static final String RABBITMQ_POM_CONST = "rabbitmq.value";
    
    /** The Constant REDIS_POM_CONST. */
    public static final String REDIS_POM_CONST = "redis.value";
    
    /** The Constant ELASTICSEARCH_POM_CONST. */
    public static final String ELASTICSEARCH_POM_CONST = "elastic.value";
    
    /** The Constant ETCD_POM_CONST. */
    public static final String ETCD_POM_CONST = "etcd.value";
    
    /** The Constant SPARK_POM_CONST. */
    public static final String SPARK_POM_CONST = "spark.value";
    
    /** The Constant CASSANDRA_POM_CONST. */
    public static final String CASSANDRA_POM_CONST = "cassandra.value";
}
