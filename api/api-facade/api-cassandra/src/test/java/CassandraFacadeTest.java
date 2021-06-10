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
import com.datastax.driver.core.*;
import org.eclipse.dirigible.api.cassandra.CassandraFacade;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.CassandraContainer;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CassandraFacadeTest {

    @Rule
    public CassandraContainer cassandra = new CassandraContainer();

    // get Cassandra Test Container
    @Test
    public void testA() {
        Cluster cluster = cassandra.getCluster();
        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS test WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");

            List<KeyspaceMetadata> keyspaces = session.getCluster().getMetadata().getKeyspaces();
            List<KeyspaceMetadata> filteredKeyspaces = keyspaces
                    .stream()
                    .filter(km -> km.getName().equals("test"))
                    .collect(Collectors.toList());

            assertEquals(1, filteredKeyspaces.size());
        }

    }

    //Create Cassandra keyspace and table
    @Test
    public void testB() {

        try (Session session = CassandraFacade.connect(cassandra.getHost(), (Integer) cassandra.getExposedPorts().get(0))) {

            session.execute("CREATE KEYSPACE IF NOT EXISTS test WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
            Thread.sleep(1000);
            session.execute("use test");
            assertNotNull(session);
            assertEquals("test", session.getLoggedKeyspace());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testC() {
        Cluster cluster = cassandra.getCluster();
        try (Session session = CassandraFacade.connect(cassandra.getHost(), (Integer) cassandra.getExposedPorts().get(0))) {

            session.execute("CREATE KEYSPACE IF NOT EXISTS test WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
            Thread.sleep(1000);
            session.execute("use test");
            Thread.sleep(1000);
            session.execute("CREATE table IF NOT EXISTS  test_table(id int primary key,name varchar,age int)");
            Thread.sleep(1000);
            session.execute("insert into test_table(id,name,age) values (1,'test_user',18)");
            Thread.sleep(1000);
            ResultSet resultSet = CassandraFacade.getResultSet("test", "select*from test_table");
            assertNotNull(resultSet);
            int id=0;
            String name="";
            int age=0;

            for(Row row:resultSet){
                id = row.getInt("id");
                name = row.getString("name");
                age = row.getInt("age");
            }
            assertEquals(1,id);
            assertEquals("test_user",name);
            assertEquals(18,age);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
