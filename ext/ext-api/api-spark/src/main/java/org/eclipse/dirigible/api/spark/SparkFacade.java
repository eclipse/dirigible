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
package org.eclipse.dirigible.api.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.config.Configuration;

public class SparkFacade implements IScriptingFacade {

	private static final String DIRIGIBLE_SPARK_CLIENT_URI = "DIRIGIBLE_SPARK_CLIENT_URI";

	private static final String CLIENT_URI = "spark://192.168.0.143:7077";

	private static final String POSTGRESQL_URI = "jdbc:postgresql://localhost:5432/%s?user=%s&password=%s";

	public static SparkSession getSession(String sparkUri) {

		String clientUri = Configuration.get(DIRIGIBLE_SPARK_CLIENT_URI, CLIENT_URI);

		SparkSession sparkSession = SparkSession
				.builder()
				.appName("Dirigible-Spark")
				.master(sparkUri != null && !sparkUri.isBlank()? sparkUri : clientUri)
				.config("spark.driver.memory", "5g")
				.getOrCreate();

		return sparkSession;
	}

	public static Dataset<Row> getDBTableDataset(String sparkUri,
												 String dbName,
												 String user,
												 String pass,
												 String table) {

		String url = String.format(POSTGRESQL_URI, dbName, user, pass);

		Dataset<Row> df = getSession(sparkUri).sqlContext()
				.read()
				.format("jdbc")
				.option("url", url)
				.option("user", user)
				.option("password", pass)
				.option("dbtable", table)
				.load();

		return df;
	}
}
