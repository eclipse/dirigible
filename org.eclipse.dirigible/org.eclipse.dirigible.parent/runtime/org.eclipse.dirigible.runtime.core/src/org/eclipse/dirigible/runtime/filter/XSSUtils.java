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

package org.eclipse.dirigible.runtime.filter;

import org.apache.commons.lang.StringEscapeUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.safety.Whitelist;

public class XSSUtils {

	public static String stripXSS(String value) {
		if (value != null) {
			value = StringEscapeUtils.escapeHtml(value);
			// value = StringEscapeUtils.escapeJavaScript(value);
			
			// TODO use something else
//			value = Jsoup.clean(value, Whitelist.none());
		}
		return value;
	}

}