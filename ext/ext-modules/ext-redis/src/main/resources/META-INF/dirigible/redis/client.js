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
exports.getClient = function () {
  var client = new Client();
  var native = org.eclipse.dirigible.api.redis.RedisFacade.getClient();
  client.native = native;
  return client;
};

function Client() {
  // Strings

  this.append = function (key, value) {
    return this.native.append(key, value);
  };

  this.bitcount = function (key) {
    return this.native.bitcount(key);
  };

  this.decr = function (key) {
    return this.native.decr(key);
  };

  this.del = function (key) {
    return this.native.del(key);
  };

  this.exists = function (key) {
    return this.native.exists(key);
  };

  this.get = function (key) {
    return this.native.get(key);
  };

  this.incr = function (key) {
    return this.native.incr(key);
  };

  this.keys = function (pattern) {
    return this.native.keys(pattern);
  };

  this.set = function (key, value) {
    return this.native.set(key, value);
  };

  // Lists

  this.lindex = function (key, index) {
    return this.native.lindex(key, index);
  };

  this.llen = function (key) {
    return this.native.llen(key);
  };

  this.lpop = function (key) {
    return this.native.lpop(key);
  };

  this.lpush = function (key, value) {
    return this.native.lpush(key, value);
  };

  this.lrange = function (key, start, stop) {
    return this.native.lrange(key, start, stop);
  };

  this.rpop = function (key) {
    return this.native.rpop(key);
  };

  this.rpush = function (key, value) {
    return this.native.rpush(key, value);
  };
}
