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
package org.eclipse.dirigible.api.cassandra;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CassandraFacade implements IScriptingFacade {
    private static final Logger logger = LoggerFactory.getLogger(CassandraFacade.class);

    private static final String DIRIGIBLE_CASSANDRA_CLIENT_URI = "DIRIGIBLE_CASSANDRA_CLIENT_URI";

    private static final String CLIENT_URI = "127.0.0.1:9042";
    static Session session;

    public static Session connect(String node, Integer port) {
        Cluster.Builder builder = Cluster.builder().addContactPoint(node).withPort(port);
        Cluster cluster = builder.build();
       session =  cluster.connect();
       return session;
    }

    public static ResultSet getResultSet(String keySpaceName, String query) {
        session.execute("use " + keySpaceName + ";");
        return session.execute(query);
    }


}
