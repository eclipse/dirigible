package org.eclipse.dirigible.api.v3.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.DecoderException;

public class UrlFacade {

	public static final String encode(String input, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(input, charset);
	}

	public static final String decode(String input, String charset) throws DecoderException, UnsupportedEncodingException {
		return URLDecoder.decode(input, charset);
	}

}
