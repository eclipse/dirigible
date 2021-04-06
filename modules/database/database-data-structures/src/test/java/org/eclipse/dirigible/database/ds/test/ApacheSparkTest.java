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
package org.eclipse.dirigible.database.ds.test;

import org.eclipse.dirigible.api.spark.SparkFacade;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ApacheSparkTest {

    @Test
    public void sparkFileRead() {
        try {
            System.out.println(SparkFacade.getHead(SparkFacade.readFile("E:/apache-tomcat-8.5.63/bin/data.txt")));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
