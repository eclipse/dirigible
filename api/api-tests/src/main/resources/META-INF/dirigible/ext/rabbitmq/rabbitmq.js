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
var producer = require("rabbitmq/producer");
var consumer = require("rabbitmq/consumer");
var configurations = require("core/v4/configurations");
var assertTrue = require('utils/assert').assertTrue;

var newMessage = "testMessage";

producer.send("test-queue", newMessage);

consumer.startListening("test-queue", "ext/rabbitmq/handler");

java.lang.Thread.sleep(1000);

consumer.stopListening("test-queue", "ext/rabbitmq/handler");

assertTrue(configurations.get("RABBITMQ_MESSAGE") == newMessage);
