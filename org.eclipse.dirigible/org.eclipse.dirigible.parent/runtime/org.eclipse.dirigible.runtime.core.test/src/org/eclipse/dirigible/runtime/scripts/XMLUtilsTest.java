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

package org.eclipse.dirigible.runtime.scripts;

import static org.junit.Assert.assertTrue;

import org.eclipse.dirigible.runtime.scripting.utils.XMLUtils;
import org.json.JSONException;
import org.junit.Test;

public class XMLUtilsTest {

	@Test
	public void testFromJson() throws JSONException {
		XMLUtils xmlUtils = new XMLUtils();

		String xml = xmlUtils.fromJson(
				"{"
				+ "  'name':'JSON',"
				+ "  'integer':1,"
				+ "  'double':2.0,"
				+ "  'boolean':true,"
				+ "  'nested':"
				+ "  {"
				+ "    'id':42"
				+ "  },"
				+ "  'array':[1,2,3]"
				+ "}");

		assertTrue(xml.contains("<nested><id>42</id></nested>"));
		assertTrue(xml.contains("<integer>1</integer>"));
		assertTrue(xml.contains("<name>JSON</name>"));
		assertTrue(xml.contains("<boolean>true</boolean>"));
		assertTrue(xml.contains("<double>2.0</double>"));
		assertTrue(xml.contains("<array>1</array><array>2</array><array>3</array>"));
	}

	@Test
	public void testToJson() throws JSONException {
		XMLUtils xmlUtils = new XMLUtils();

		String json = xmlUtils.toJson(
				"  <nested>"
				+ "  <id>42</id>"
				+ "</nested>"
				+ "<integer>1</integer>"
				+ "<name>JSON</name>"
				+ "<boolean>true</boolean>"
				+ "<double>2.0</double>"
				+ "<array>1</array>"
				+ "<array>2</array>"
				+ "<array>3</array>");

		assertTrue(json.contains("{\"nested\":{\"id\":42}"));
		assertTrue(json.contains("\"integer\":1"));
		assertTrue(json.contains("\"name\":\"JSON\""));
		assertTrue(json.contains("\"boolean\":true"));
		assertTrue(json.contains("\"double\":2"));
		assertTrue(json.contains("\"array\":[1,2,3]}"));
	}
}
