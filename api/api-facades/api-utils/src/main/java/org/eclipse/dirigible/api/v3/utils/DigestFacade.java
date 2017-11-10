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

// TODO: Auto-generated Javadoc
/**
 * The Class DigestFacade.
 */
public class DigestFacade {

	/**
	 * Md 5.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] md5(byte[] input) {
		return DigestUtils.md5(input);
	}

	/**
	 * Md 5.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] md5(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return md5(bytes);
	}

	/**
	 * Md 5 hex.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String md5Hex(byte[] input) {
		return DigestUtils.md5Hex(input);
	}

	/**
	 * Md 5 hex.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String md5Hex(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return md5Hex(bytes);
	}

	/**
	 * Sha 1.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha1(byte[] input) {
		return DigestUtils.sha1(input);
	}

	/**
	 * Sha 1.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha1(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha1(bytes);
	}

	/**
	 * Sha 256.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha256(byte[] input) {
		return DigestUtils.sha256(input);
	}

	/**
	 * Sha 256.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha256(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha256(bytes);
	}

	/**
	 * Sha 384.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha384(byte[] input) {
		return DigestUtils.sha384(input);
	}

	/**
	 * Sha 384.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha384(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha384(bytes);
	}

	/**
	 * Sha 512.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha512(byte[] input) {
		return DigestUtils.sha512(input);
	}

	/**
	 * Sha 512.
	 *
	 * @param input the input
	 * @return the byte[]
	 */
	public static final byte[] sha512(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha512(bytes);
	}

	/**
	 * Sha 1 hex.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String sha1Hex(byte[] input) {
		return DigestUtils.sha1Hex(input);
	}

	/**
	 * Sha 1 hex.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static final String sha1Hex(String input) {
		byte[] bytes = BytesHelper.jsonToBytes(input);
		return sha1Hex(bytes);
	}

}
