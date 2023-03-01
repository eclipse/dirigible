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
/**
 * API Consumer
 */

exports.queue = function(destination) {
	const queue = new Queue();
	queue.destination = destination;
	return queue;
};

exports.topic = function(destination) {
	const topic = new Topic();
	topic.destination = destination;
	return topic;
};

function Queue() {
	this.receive = function(timeout) {
		if (!timeout) {
			timeout = 1000;
		}
		return org.eclipse.dirigible.components.api.messaging.MessagingFacade.receiveFromQueue(this.destination, timeout);
	};
}

function Topic() {
	this.receive = function(timeout) {
		if (!timeout) {
			timeout = 1000;
		}
		return org.eclipse.dirigible.components.api.messaging.MessagingFacade.receiveFromTopic(this.destination, timeout);
	};
}
