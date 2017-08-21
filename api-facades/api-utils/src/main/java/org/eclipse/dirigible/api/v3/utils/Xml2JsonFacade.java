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
