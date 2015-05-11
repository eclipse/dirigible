/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.scripting.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class XMLUtils {
	
	public String fromJson(String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		String xml = XML.toString(json);
		return xml;
	}
	
	public String toJson(String xmlString) throws JSONException {
		JSONObject json  = XML.toJSONObject(xmlString);
		String result = json.toString();
		return result;
	}

}
