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

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;

public class DigestFacade {

	public static final byte[] md5(byte[] input) {
		return DigestUtils.md5(input);
	}

	public static final byte[] md5(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return md5(bytes);
	}

	public static final String md5Hex(byte[] input) {
		return DigestUtils.md5Hex(input);
	}

	public static final String md5Hex(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return md5Hex(bytes);
	}

	public static final byte[] sha1(byte[] input) {
		return DigestUtils.sha1(input);
	}

	public static final byte[] sha1(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha1(bytes);
	}

	public static final byte[] sha256(byte[] input) {
		return DigestUtils.sha256(input);
	}

	public static final byte[] sha256(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha256(bytes);
	}

	public static final byte[] sha384(byte[] input) {
		return DigestUtils.sha384(input);
	}

	public static final byte[] sha384(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha384(bytes);
	}

	public static final byte[] sha512(byte[] input) {
		return DigestUtils.sha512(input);
	}

	public static final byte[] sha512(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha512(bytes);
	}

	public static final String sha1Hex(byte[] input) {
		return DigestUtils.sha1Hex(input);
	}

	public static final String sha1Hex(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha1Hex(bytes);
	}

}
