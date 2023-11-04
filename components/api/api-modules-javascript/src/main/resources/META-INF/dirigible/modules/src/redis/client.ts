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

const RedisFacade = Java.type("org.eclipse.dirigible.components.api.redis.RedisFacade");

export function getClient() {
    const native = RedisFacade.getClient();
    return new Client(native);
};

class Client {
    
    constructor(private native) {}

    append(key, value) {
        return this.native.append(key, value);
    };

    bitcount(key) {
        return this.native.bitcount(key);
    };

    decr(key) {
        return this.native.decr(key);
    };

    del(key) {
        return this.native.del(key);
    };

    exists(key) {
        return this.native.exists(key);
    };

    get(key) {
        return this.native.get(key);
    };

    incr(key) {
        return this.native.incr(key);
    };

    keys(pattern) {
        return this.native.keys(pattern);
    };

    set(key, value) {
        return this.native.set(key, value);
    };

    // Lists

    lindex(key, index) {
        return this.native.lindex(key, index);
    };

    llen(key) {
        return this.native.llen(key);
    };

    lpop(key) {
        return this.native.lpop(key);
    };

    lpush(key, value) {
        return this.native.lpush(key, value);
    };

    lrange(key, start, stop) {
        return this.native.lrange(key, start, stop);
    };

    rpop(key) {
        return this.native.rpop(key);
    };

    rpush(key, value) {
        return this.native.rpush(key, value);
    };
}
