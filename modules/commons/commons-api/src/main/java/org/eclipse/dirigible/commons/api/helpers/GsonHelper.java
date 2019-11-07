/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.commons.api.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * The GsonHelper utility class.
 */
public class GsonHelper {

	/** The GSON instance. */
	public static final transient Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();;
	

	/** The GSON Parser instance. */
	public static final transient JsonParser PARSER = new JsonParser();
	
}
