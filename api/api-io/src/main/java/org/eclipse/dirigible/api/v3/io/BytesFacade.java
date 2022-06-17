/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.io;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BytesFacade {
	
	public static byte[] textToByteArray(String text) {
		byte[] bytes = text.getBytes();
		return bytes;
	}
	
	public static byte[] textToByteArray(String text, String charset) throws UnsupportedEncodingException {
		byte[] bytes = text.getBytes(charset);
		return bytes;
	}

	public static byte[] intToByteArray(int value, String byteOrder) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		ByteOrder order = byteOrder == ByteOrder.BIG_ENDIAN.toString() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		buffer.order(order);
		buffer.putInt(value);
		return buffer.array();
	}

	public static int byteArrayToInt(byte[] data, String byteOrder) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		ByteOrder order = byteOrder == ByteOrder.BIG_ENDIAN.toString() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		buffer.order(order);
		buffer.put(data);
		return buffer.getInt(0);
	}


}
