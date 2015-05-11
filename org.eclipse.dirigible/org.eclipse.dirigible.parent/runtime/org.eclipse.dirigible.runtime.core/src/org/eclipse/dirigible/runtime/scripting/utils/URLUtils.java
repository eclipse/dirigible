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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLUtils {

	public String encode(String s, String enc)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(s, enc);
	}

	public String decode(String s, String enc)
			throws UnsupportedEncodingException {
		return URLDecoder.decode(s, enc);
	}

}
