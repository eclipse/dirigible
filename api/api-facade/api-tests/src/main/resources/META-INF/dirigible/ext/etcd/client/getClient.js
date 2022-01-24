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
// Loading the etcd module.
var etcd = require('etcd/client');
var assertTrue = require('utils/assert').assertTrue;

// Function for comparing typed arrays for equality.
function typedArraysAreEqual(a, b) {
    if (a.byteLength !== b.byteLength) return false;
    return a.every((val, i) => val === b[i]);
}

// Initializing the etcd client.
var etcdClient = etcd.getClient();
var t1 = ((etcdClient !== null) && (etcdClient !== undefined));

// Initializing key and both string and byte array value.
var key = "foo";
var strVal = "bar";
var byteArrayVal = new Int8Array([116, 101, 115, 116]);

console.log(JSON.stringify({[key]: strVal}));
console.log(JSON.stringify({[key]: byteArrayVal}));

// Setting into etcd new kvs pair with string value.
var putStrVal = etcdClient.putStringValue(key, strVal);
java.lang.Thread.sleep(3000);
var t2 = !(putStrVal instanceof Error);

// Getting the kvs pair for the specified key with string value.
var getKvsStrVal = etcdClient.getKvsStringValue(key);
console.log(JSON.stringify(getKvsStrVal));
var t3 = getKvsStrVal[key] === strVal;

// Setting into etcd new kvs pair with byte array value.
var putByteArrayVal = etcdClient.putByteArrayValue(key, byteArrayVal);
java.lang.Thread.sleep(3000);
var t4 = !(putByteArrayVal instanceof Error);

// Getting the kvs pair for the specified key with byte array value.
var getKvsByteArrayVal = etcdClient.getKvsByteArrayValue(key);
console.log(JSON.stringify(getKvsByteArrayVal));
var t5 = typedArraysAreEqual(getKvsByteArrayVal[key], byteArrayVal);

// Deleting the etcd kvs pair with the specified key.
var deleteKvs = etcdClient.delete(key);
java.lang.Thread.sleep(3000);
var t6 = !(deleteKvs instanceof Error);

// Getting deleted kvs pair for both value types after the delete.
var getKvsStrValDel = etcdClient.getKvsStringValue(key);
console.log(JSON.stringify(getKvsStrVal));
var getKvsByteArrayValDel = etcdClient.getKvsByteArrayValue(key);
console.log(JSON.stringify(getKvsByteArrayVal));
var t7 = Object.keys(getKvsStrValDel).length === 0
var t8 = Object.keys(getKvsByteArrayValDel).length === 0

// Test put for error when key is not string.
var putKvs = etcdClient.putStringValue(100, 'bar');
console.log(JSON.stringify(putKvs.message));
var t9 = putKvs instanceof Error;

// Test put for error when value is not string.
var putKvs = etcdClient.putStringValue('foo', 100);
console.log(JSON.stringify(putKvs.message));
var t10 = putKvs instanceof Error;

// Test put for error when key is not string for byte array value.
var putKvs = etcdClient.putByteArrayValue(100, [116, 101, 115, 116]);
console.log(JSON.stringify(putKvs.message));
var t11 = putKvs instanceof Error;

// Test put for error when value is not int8array for byte array value.
var putKvs = etcdClient.putByteArrayValue('foo', [116, 101, 115, 116]);
console.log(JSON.stringify(putKvs.message));
var t12 = putKvs instanceof Error;

// Test get for error when key is not string for string value;
var getKvs = etcdClient.getKvsStringValue(10);
console.log(JSON.stringify(getKvs.message));
var t13 = getKvs instanceof Error;

// Test get for error when key is not string for byte array value;
var getKvs = etcdClient.getKvsByteArrayValue(10);
console.log(JSON.stringify(getKvs.message));
var t14 = getKvs instanceof Error;

// Test delete for error when key is not string.
var deleteKvs = etcdClient.delete(10);
console.log(JSON.stringify(deleteKvs.message));
var t15 = deleteKvs instanceof Error;

assertTrue(t1 && t2 && t3 && t4 && t5 && t6 && t7 && t8 && t9 && t10 && t11 && t12 && t13 && t14 && t15);
