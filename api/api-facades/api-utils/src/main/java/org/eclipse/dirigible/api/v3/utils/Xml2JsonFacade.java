/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.utils;

import org.eclipse.dirigible.commons.utils.xml2json.Xml2Json;

public class Xml2JsonFacade {

	public static final String fromJson(String json) throws Exception {
		String xml = Xml2Json.toXml(json);
		return xml;
	}

	public static final String toJson(String xml) throws Exception {
		String json = Xml2Json.toJson(xml);
		return json;
	}
}
