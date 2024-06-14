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

export function getClient(): Client {
    const native = EtcdFacade.getClient();
    var client = new Client(native);
    return client;
};

class Client {

    constructor(private native: any) {
        this.native = native;
    }

    putStringValue(key: string, value: string): void {
        this.native.put(StringToByteSequence(key), StringToByteSequence(value));
    }

    putByteArrayValue(key: string, value: Int8Array): void {
        this.native.put(StringToByteSequence(key), ByteArrayToByteSequence(value));
    }

    getHeader(key: string): Header {
        return this.get(key, this.native).getHeader();
    }

    getKvsStringValue(key: string): {[key: string]: any} {
        return this.get(key, this.native).getKvsString();
    }

    getKvsByteArrayValue(key: string): {[key: string]: Int8Array}{
            return this.get(key, this.native).getKvsByteArray();
    }

    getCount(key: string): number {
        return this.get(key, this.native).getCount();
    }

    delete(key: string): void {
        this.native.delete(StringToByteSequence(key));
    }

    //? is native needed as a parameter?
    private get(key: string, native?: any): GetResponse {
        var etcdCompletableFuture = native.get(StringToByteSequence(key));
        var native = etcdCompletableFuture.get();
        var etcdGetReponse = new GetResponse(native);
        return etcdGetReponse;
    }
}

class GetResponse {

    constructor(private native) { }

    getHeader(): Header {
        var native = this.native.getHeader();
        var header = new Header(native);
        return header;
    }

    getKvsString(): {[key: string]: string} {
        return KeyValueObjectString(this.native.getKvs());
    }

    getKvsByteArray(): {[key: string]: Int8Array}{
        return KeyValueObjectByteArray(this.native.getKvs());
    }
    getCount(): number {
        return this.native.getCount();
    }
}

class Header {
    constructor(private native) { 
        this.native = native;
    }

    getRevision(): number {
        return this.native.getRevision();
    }

    getClusterId(): number {
        return this.native.getClusterId();
    }

    getMemberId(): number {
        return this.native.getMemberId();
    }

    getRaftTerm(): number {
        return this.native.getRaftTerm();
    }
}

function StringToByteSequence(str: string): any /* ByteSequence */ {
    return EtcdFacade.stringToByteSequence(str);
}

function ByteArrayToByteSequence(arr: Int8Array): any /* ByteSequence */{
    return EtcdFacade.byteArrayToByteSequence(arr);
}

function ByteSequenceToString(value): string {
    return EtcdFacade.byteSequenceToString(value);
}

function KeyValueObjectString(kvsList): {[key: string]: string} {
    return KeyValueObject(kvsList, ByteSequenceToString);
}

function KeyValueObjectByteArray(kvsList): {[key: string]: Int8Array} {
    return KeyValueObject(kvsList, BytesToArray);
}

function KeyValueObject(kvsList, func: Function): {[key: string]: any} {
    var kvObject: {[key: string]: any} = {};
    kvsList.forEach(kvs => {
        var key = ByteSequenceToString(kvs.getKey());
        var value = func(kvs.getValue());
        Object.assign(kvObject, { [key]: value });
    })

    return kvObject;
}

function BytesToArray(value): Int8Array {
    var array = value.getBytes();
    var result = new Int8Array(array.length);
    for (var i = 0; i < array.length; i++) {
        result[i] = parseInt(array[i]);
    }
    return result;
}