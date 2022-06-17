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
/** Client API for Etcd */

exports.getClient = function () {
    var client = new Client();
    var native = org.eclipse.dirigible.api.etcd.EtcdFacade.getClient();
    client.native = native;
    return client;
};

function Client() {
    this.putStringValue = function (key, value) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        }
        else if (typeof (value) !== 'string') {
            return new Error('Value is not a string.');
        }
        else {
            this.native.put(StringToByteSequence(key), StringToByteSequence(value));
        }
    }

    this.putByteArrayValue = function (key, value) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        }
        else if (!(value instanceof Int8Array)) {
            return new Error('Value is not a Int8Array.');
        }
        else {
            this.native.put(StringToByteSequence(key), ByteArrayToByteSequence(value));
        }
    }

    this.getHeader = function (key) {
        return get(key, this.native).getHeader();
    }

    this.getKvsStringValue = function (key) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        }
        else {
            return get(key, this.native).getKvsString();
        }
    }

    this.getKvsByteArrayValue = function (key) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        }
        else {
            return get(key, this.native).getKvsByteArray();
        }
    }

    this.getCount = function (key) {
        return get(key, this.native).getCount();
    }

    this.delete = function (key) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        }
        else {
            this.native.delete(StringToByteSequence(key));
        }
    }

    function get(key, native) {
        var etcdCompletableFuture = native.get(StringToByteSequence(key));
        var etcdGetReponse = new GetResponse();
        var native = etcdCompletableFuture.get();
        etcdGetReponse.native = native;
        return etcdGetReponse;
    }
}

function GetResponse() {
    this.getHeader = function () {
        var header = new Header();
        var native = this.native.getHeader();
        header.native = native;
        return header;
    }

    this.getKvsString = function () {
        return KeyValueObjectString(this.native.getKvs());
    }

    this.getKvsByteArray = function () {
        return KeyValueObjectByteArray(this.native.getKvs());
    }
    this.getCount = function () {
        return this.native.getCount();
    }
}

function Header() {
    this.getRevision = function () {
        return this.native.getRevision();
    }

    this.getClusterId = function () {
        return this.native.getClusterId();
    }

    this.getMemberId = function () {
        return this.native.getMemberId();
    }

    this.getRaftTerm = function () {
        return this.native.getRaftTerm();
    }
}

function StringToByteSequence(str) {
    return org.eclipse.dirigible.api.etcd.EtcdFacade.stringToByteSequence(str);
}

function ByteArrayToByteSequence(arr) {
    return org.eclipse.dirigible.api.etcd.EtcdFacade.byteArrayToByteSequence(arr);
}

function ByteSequenceToString(value) {
    return org.eclipse.dirigible.api.etcd.EtcdFacade.byteSequenceToString(value);
}

function KeyValueObjectString(kvsList) {
    return KeyValueObject(kvsList, ByteSequenceToString);
}

function KeyValueObjectByteArray(kvsList) {
    return KeyValueObject(kvsList, BytesToArray);
}

function KeyValueObject(kvsList, func) {
    var kvObject = {};
    kvsList.forEach(kvs => {
        var key = ByteSequenceToString(kvs.getKey());
        var value = func(kvs.getValue());
        Object.assign(kvObject, { [key]: value });
    })

    return kvObject;
}

function BytesToArray(value) {
    var array = value.getBytes();
    var result = new Int8Array(array.length);
    for (var i = 0; i < array.length; i++) {
        result[i] = parseInt(array[i]);
    }
    return result;
}