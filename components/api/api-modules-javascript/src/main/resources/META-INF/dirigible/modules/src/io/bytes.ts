/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Bytes
 */

import { bytes } from ".";

/**
 * Convert the native JavaScript byte array to Java one. To be used internally by the API layer
 */

const JString = Java.type("java.lang.String");
const JByte = Java.type("java.lang.Byte");
const JArray = Java.type("java.lang.reflect.Array");
const BytesFacade = Java.type("org.eclipse.dirigible.components.api.io.BytesFacade");

export class Bytes{

   static toJavaScriptBytes(internalBytes): Array<bytes> {
       const bytes = [];
       for (let i=0; i<internalBytes.length; i++) {
           bytes.push(internalBytes[i]);
       }
       return bytes;
   };

    static toJavaBytes(bytes): typeof JArray {
       const internalBytes = JArray.newInstance(JByte.TYPE, bytes.length);
       for (let i=0; i<bytes.length; i++) {
           internalBytes[i] = bytes[i];
       }
       return internalBytes;
   };

   /**
    * Convert the Java byte array to a native JavaScript one. To be used internally by the API layer
    */
   static textToByteArray(text: string) { //Kakvo trqbva da vrushta
       const javaString = new JString(text);
       const native = BytesFacade.textToByteArray(text);
       return this.toJavaScriptBytes(native);
   };

   /**
    * Converts a text to a byte array
    */
   static byteArrayToText(data): string {
       const native = this.toJavaBytes(data);
       return String.fromCharCode.apply(String, this.toJavaScriptBytes(native));
   };

   /**
    * Converts an integer to a byte array
    */
   static intToByteArray(value: number, byteOrder: string) {
       return BytesFacade.intToByteArray(value, byteOrder)
   }

   /**
    * Converts a byte array to integer
    */
   static byteArrayToInt(data, byteOrder: string): number {
       return BytesFacade.byteArrayToInt(data, byteOrder);
   }

}
