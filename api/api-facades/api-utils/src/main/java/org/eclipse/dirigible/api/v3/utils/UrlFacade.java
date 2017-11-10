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

/**
 * The Class UrlFacade.
 */
public class UrlFacade {

	/**
	 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. This method
	 * uses the supplied encoding scheme to obtain the bytes for unsafe characters.
	 *
	 * @param input
	 * @param charset
	 * @return the translated input
	 * @throws UnsupportedEncodingException
	 */
	public static final String encode(String input, String charset) throws UnsupportedEncodingException {
		if (charset == null) {
			charset = StandardCharsets.UTF_8.name();
		}
		return URLEncoder.encode(input, charset);
	}

	/**
	 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. This method
	 * uses the supplied encoding scheme to obtain the bytes for unsafe characters.
	 *
	 * @param input
	 * @return the translated input
	 * @throws UnsupportedEncodingException
	 */
	public static final String encode(String input) throws UnsupportedEncodingException {
		return encode(input, null);
	}

	/**
	 * Decodes a application/x-www-form-urlencoded string using a specific encoding scheme. The supplied encoding is
	 * used to determine what characters are represented by any consecutive sequences of the form "%xy".
	 *
	 * @param input
	 * @param charset
	 * @return the decoded input
	 * @throws DecoderException
	 * @throws UnsupportedEncodingException
	 */
	public static final String decode(String input, String charset) throws DecoderException, UnsupportedEncodingException {
		if (charset == null) {
			charset = StandardCharsets.UTF_8.name();
		}
		return URLDecoder.decode(input, charset);
	}

	/**
	 * Decodes a application/x-www-form-urlencoded string using a specific encoding scheme. The supplied encoding is
	 * used to determine what characters are represented by any consecutive sequences of the form "%xy".
	 *
	 * @param input
	 * @return the decoded input
	 * @throws DecoderException
	 * @throws UnsupportedEncodingException
	 */
	public static final String decode(String input) throws DecoderException, UnsupportedEncodingException {
		return decode(input, null);
	}

	/**
	 * Escape URL fragments
	 * 
	 * @param input
	 * @return escaped input
	 */
	public static final String escape(String input) {
		return UrlEscapers.urlFragmentEscaper().escape(input);

	}

}
