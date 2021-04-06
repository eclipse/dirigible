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

	public static SparkSession getSession() {

		String clientUri = Configuration.get(DIRIGIBLE_SPARK_CLIENT_URI, CLIENT_URI);

		SparkSession sparkSession = SparkSession
				.builder()
				.appName("Dirigible-Spark")
				.master(clientUri)
				.config("spark.driver.memory", "5g")
				.getOrCreate();

		return sparkSession;
	}
	//TODO getSessionByParameters pass mongo clientUri and default DB
	/*
	в джаваскрипта с празни параметри
	а тук подавам параметри в метода
	 */
	public static Dataset<String> readFile(String path) {
		return getSession().read().textFile(path);
	}

	public static String getHead(Dataset<String> data) {
		return data.head();
	}

	public static Dataset<Row> getDBTableDataset(String dbName,
												 String user,
												 String pass,
												 String table) {

		String url = String.format(POSTGRESQL_URI, dbName, user, pass);

		Dataset<Row> df = getSession().sqlContext()
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
