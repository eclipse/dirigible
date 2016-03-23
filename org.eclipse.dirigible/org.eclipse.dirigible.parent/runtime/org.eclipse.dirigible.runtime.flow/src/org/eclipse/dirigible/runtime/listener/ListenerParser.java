/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.listener;

import java.io.IOException;

import com.google.gson.Gson;

public class ListenerParser {

	static final String NODE_NAME = "name";
	static final String NODE_DESCRIPTION = "description";
	static final String NODE_EXPRESSION = "expression";
	static final String NODE_TYPE = "type";
	static final String NODE_MODULE = "module";

	private static Gson gson = new Gson();

	public static Listener parseListener(String listenerDefinition) throws IOException {
		// {
		// "name": "MyListener",
		// "description": "MyListener Description",
		// "trigger": "message",
		// "type": "javascript",
		// "module": "/${packageName}/service1.js",
		// "params": {
		// "client": "client1",
		// "topic": "/topic1"
		// }
		// }

		return gson.fromJson(listenerDefinition, Listener.class);
	}
}
