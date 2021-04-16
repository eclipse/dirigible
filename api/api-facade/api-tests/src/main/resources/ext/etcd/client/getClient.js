/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var etcd = require('etcd/client');

var etcdClient = etcd.getClient();
((etcdClient !== null) && (etcdClient !== undefined));

var key = "foo";
var strVal = "test";
var byteArrayVal = [116, 101, 115, 116];
console.log(JSON.stringify({[key]: strVal}));
console.log(JSON.stringify({[key]: byteArrayVal}));

var putStrVal = etcdClient.putStringValue(key, strVal);
var getKvsStrVal = etcdClient.getKvsStringValue(key);
console.log(JSON.stringify(getKvsStrVal));
// (getKvsStrVal === {[key]: strVal});

// var putByteArrayVal = etcdClient.putByteArrayValue(key, byteArrayVal);
// var getKvsByteArrayVal = etcdClient.getKvsByteArrayValue(key);
// (getKvsByteArrayVal === {[key]: byteArrayVal});

// var deleteKvs = etcdClient.delete(key);
// var getKvsStrVal = etcdClient.getKvsStringValue(key);
// var getKvsByteArrayVal = etcdClient.getKvsByteArrayValue(key);
// (getKvsStrVal === { });
// (getKvsByteArrayVal === { });
