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
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.eclipse.dirigible.api.spark.SparkFacade;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SparkFacadeTest {

    @Test
    public void getSession() {
        SparkSession sparkSession = SparkFacade.getSession("spark://192.168.0.108:7077");
        assertNotNull(sparkSession);
        assertEquals("Dirigible-Spark", sparkSession.initialSessionOptions().get("spark.app.name").get());
    }

    @Test
    public void getDBTableDataset() {

        Dataset<Row> row = SparkFacade.getDBTableDataset("spark://192.168.0.108:7077",
                                                         "test",
                                                            "postgres",
                                                            "admin",
                                                            "users");
        assertNotNull(row);
        assertEquals("[1,Riley,Fletcher,4342536246,34]", row.collectAsList().get(0).toString());
    }
}
