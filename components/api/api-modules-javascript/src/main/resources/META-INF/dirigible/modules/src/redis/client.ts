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

export function getClient(): Client {
    const native = RedisFacade.getClient();
    return new Client(native);
};

class Client {
    
    constructor(private native) {}

    append(key: string, value: string): number {
        return this.native.append(key, value);
    };

    bitcount(key: string): number {
        return this.native.bitcount(key);
    };

    decr(key: string): number {
        return this.native.decr(key);
    };

    del(key: string): number {
        return this.native.del(key);
    };

    exists(key: string): boolean {
        return this.native.exists(key);
    };

    get(key: string): string {
        return this.native.get(key);
    };

    incr(key: string): number {
        return this.native.incr(key);
    };

    keys(pattern: string): string[] {
        return this.native.keys(pattern);
    };

    set(key: string, value: string): string {
        return this.native.set(key, value);
    };

    // Lists

    lindex(key: string, index: number): string {
        return this.native.lindex(key, index);
    };

    llen(key: string): number {
        return this.native.llen(key);
    };

    lpop(key: string): string {
        return this.native.lpop(key);
    };

    lpush(key: string, value: string): number {
        return this.native.lpush(key, value);
    };

    lrange(key: string, start: number, stop: number): string[] {
        return this.native.lrange(key, start, stop);
    };

    rpop(key: string): string {
        return this.native.rpop(key);
    };

    rpush(key: string, value: string): number {
        return this.native.rpush(key, value);
    };
}
