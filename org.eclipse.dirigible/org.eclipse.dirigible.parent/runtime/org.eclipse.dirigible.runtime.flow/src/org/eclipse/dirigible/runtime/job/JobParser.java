/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.job;

import java.io.IOException;

import com.google.gson.Gson;

public class JobParser {

	static final String NODE_NAME = "name";
	static final String NODE_DESCRIPTION = "description";
	static final String NODE_EXPRESSION = "expression";
	static final String NODE_TYPE = "type";
	static final String NODE_MODULE = "module";

	private static Gson gson = new Gson();

	public static Job parseJob(String jobDefinition) throws IOException {
		// {
		// "name":"MyJob",
		// "description":"MyJob Description",
		// "expression":"0/20 * * * * ?",
		// "type":"javascript",
		// "module":"/${projectName}/service1.js"
		// }

		// JsonParser parser = new JsonParser();
		// JsonObject jobDefinitionObject = (JsonObject) parser.parse(jobDefinition);

		Job job = gson.fromJson(jobDefinition, Job.class);

		// TODO validate the parsed content has the right structure

		// return jobDefinitionObject;
		return job;
	}
}
