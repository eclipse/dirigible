/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.imports;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Turns the bytes of an imported file into its text, in the encoding the import declares.
 *
 * <p>
 * Decoding is strict: a byte sequence the declared encoding cannot read rejects the file instead of
 * being replaced with {@code U+FFFD}. A windows-1251 statement uploaded to an import that declares
 * UTF-8 would otherwise import with every Cyrillic letter turned into a replacement character,
 * which no later step can repair - the wrong encoding has to fail where it happens.
 */
public final class ImportText {

    private static final char BYTE_ORDER_MARK = 0xFEFF;

    private ImportText() {}

    /**
     * Decode a file's bytes, dropping a leading byte-order mark.
     *
     * @param content the file's bytes
     * @param encoding the declared encoding (a Java charset name such as {@code UTF-8} or
     *        {@code windows-1251})
     * @return the file's text
     * @throws IllegalArgumentException when the encoding is unknown or the bytes are not valid in it
     */
    public static String decode(byte[] content, String encoding) {
        Charset charset = charset(encoding);
        try {
            String text = charset.newDecoder()
                                 .onMalformedInput(CodingErrorAction.REPORT)
                                 .onUnmappableCharacter(CodingErrorAction.REPORT)
                                 .decode(ByteBuffer.wrap(content))
                                 .toString();
            return !text.isEmpty() && text.charAt(0) == BYTE_ORDER_MARK ? text.substring(1) : text;
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("the file is not valid " + charset.name() + " text - check the encoding it was exported in",
                    e);
        }
    }

    /**
     * The SHA-256 checksum of a file, in lower-case hex - what the import ledger records, so two
     * uploads of byte-identical files can be recognised as such.
     *
     * @param content the file's bytes
     * @return the checksum
     */
    public static String checksum(byte[] content) {
        try {
            return HexFormat.of()
                            .formatHex(MessageDigest.getInstance("SHA-256")
                                                    .digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available in this runtime", e);
        }
    }

    private static Charset charset(String encoding) {
        try {
            return Charset.forName(encoding);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            throw new IllegalArgumentException("unknown encoding [" + encoding + "]", e);
        }
    }
}
