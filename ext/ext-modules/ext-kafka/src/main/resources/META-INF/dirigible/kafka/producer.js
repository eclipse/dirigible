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
exports.topic = function(destination, configuration) {
	var topic = new Topic();
	topic.destination = destination;
    topic.configuration = configuration;
	return topic;
};

exports.close = function(configuration) {
	org.eclipse.dirigible.api.kafka.KafkaFacade.closeProducer(configuration);
};

function Topic() {
	this.send = function(key, value) {
        org.eclipse.dirigible.api.kafka.KafkaFacade.send(this.destination, key, value, this.configuration);
	};
};