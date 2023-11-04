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
var QUnit = require("qunit/qunit");

QUnit.module('core:');
QUnit.test("configurations", function (assert) {
    assert.ok(require('core-tests/configurations-get-test'), "get - ok");
});
QUnit.test("context", function (assert) {
    assert.ok(require('core-tests/context-get-test'), "get - ok");
});
QUnit.test("env", function (assert) {
    assert.ok(require('core-tests/env-get-test'), "get - ok");
    assert.ok(require('core-tests/env-list-test'), "list - ok");
});
QUnit.test("globals", function (assert) {
    assert.ok(require('core-tests/globals-get-test'), "get - ok");
    assert.ok(require('core-tests/globals-list-test'), "list - ok");
});
QUnit.test("destinations", function (assert) {
    assert.ok(require('core-tests/destinations-get-test'), "get - ok");
});

require("qunit/runner").run();