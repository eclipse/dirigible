/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');

exports.queue = function(destination) {
	var queue = new Queue();
	queue.destination = destination;
	return queue;
};

exports.topic = function(destination) {
	var topic = new Topic();
	topic.destination = destination;
	return topic;
};

function Queue() {
	this.send = function(message) {
		java.call('org.eclipse.dirigible.api.v3.messaging.MessagingFacade', 'sendToQueue', [this.destination, message]);
	}
};

function Topic() {
	this.send = function(message) {
		java.call('org.eclipse.dirigible.api.v3.messaging.MessagingFacade', 'sendToTopic', [this.destination, message]);
	}
};



