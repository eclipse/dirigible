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

const EtcdFacade = Java.type("org.eclipse.dirigible.components.api.etcd.EtcdFacade");

export function getClient() {
    const native = EtcdFacade.getClient();
    var client = new Client(native);
    return client;
};

class Client {

    constructor(private native) { }

    putStringValue(key, value) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        } else if (typeof (value) !== 'string') {
            return new Error('Value is not a string.');
        } else {
            this.native.put(StringToByteSequence(key), StringToByteSequence(value));
        }
    }

    putByteArrayValue(key, value) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        } else if (!(value instanceof Int8Array)) {
            return new Error('Value is not a Int8Array.');
        } else {
            this.native.put(StringToByteSequence(key), ByteArrayToByteSequence(value));
        }
    }

    getHeader(key) {
        return this.get(key, this.native).getHeader();
    }

    getKvsStringValue(key) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        } else {
            return this.get(key, this.native).getKvsString();
        }
    }

    getKvsByteArrayValue(key) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        } else {
            return this.get(key, this.native).getKvsByteArray();
        }
    }

    getCount(key) {
        return this.get(key, this.native).getCount();
    }

    delete(key) {
        if (typeof (key) !== 'string') {
            return new Error('Key is not a string.');
        } else {
            this.native.delete(StringToByteSequence(key));
        }
    }

    private get(key, native) {
        var etcdCompletableFuture = native.get(StringToByteSequence(key));
        var native = etcdCompletableFuture.get();
        var etcdGetReponse = new GetResponse(native);
        return etcdGetReponse;
    }
}

class GetResponse {

    constructor(private native) { }

    getHeader() {
        var native = this.native.getHeader();
        var header = new Header(native);
        return header;
    }

    getKvsString() {
        return KeyValueObjectString(this.native.getKvs());
    }

    getKvsByteArray() {
        return KeyValueObjectByteArray(this.native.getKvs());
    }
    getCount() {
        return this.native.getCount();
    }
}

class Header {
    constructor(private native) { }

    getRevision() {
        return this.native.getRevision();
    }

    getClusterId() {
        return this.native.getClusterId();
    }

    getMemberId() {
        return this.native.getMemberId();
    }

    getRaftTerm() {
        return this.native.getRaftTerm();
    }
}

function StringToByteSequence(str) {
    return EtcdFacade.stringToByteSequence(str);
}

function ByteArrayToByteSequence(arr) {
    return EtcdFacade.byteArrayToByteSequence(arr);
}

function ByteSequenceToString(value) {
    return EtcdFacade.byteSequenceToString(value);
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