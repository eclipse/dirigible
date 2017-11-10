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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;

import com.google.common.net.UrlEscapers;

public class UrlFacade {

	public static final String encode(String input, String charset) throws UnsupportedEncodingException {
		if (charset == null) {
			charset = StandardCharsets.UTF_8.name();
		}
		return URLEncoder.encode(input, charset);
	}

	public static final String encode(String input) throws UnsupportedEncodingException {
		return encode(input, null);
	}

	public static final String decode(String input, String charset) throws DecoderException, UnsupportedEncodingException {
		if (charset == null) {
			charset = StandardCharsets.UTF_8.name();
		}
		return URLDecoder.decode(input, charset);
	}

	public static final String decode(String input) throws DecoderException, UnsupportedEncodingException {
		return decode(input, null);
	}

	public static final String escape(String input) {
		return UrlEscapers.urlFragmentEscaper().escape(input);

	}

}
