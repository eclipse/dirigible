/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting.utils;

import org.eclipse.dirigible.repository.ext.utils.Xml2JsonUtils;

public class XMLUtils {

	public String fromJson(String json) throws Exception {
		String xml = Xml2JsonUtils.toXml(json);
		return xml;
	}

	public String toJson(String xml) throws Exception {
		String json = Xml2JsonUtils.toJson(xml);
		return json;
	}

}
