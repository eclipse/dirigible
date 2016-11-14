/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ */
/* eslint-env node, dirigible */

/**
 * Register a client by name or does nothing, if such a client exists
 */
exports.registerClient = function(client) {
	$.getMessagingService().registerClient(client);
};

/**
 * Unregister a client by name
 */
exports.unregisterClient = function(client) {
	$.getMessagingService().unregisterClient(client);
};

/**
 * Check the existence of a client by name
 */
exports.isClientExists = function(client) {
	return $.getMessagingService().isClientExists(client);
};

/**
 * Register a topic by name or does nothing, if such a topic exists
 */
exports.registerTopic = function(topic) {
	$.getMessagingService().registerTopic(topic);
};

/**
 * Unregister a topic by name
 */
exports.unregisterTopic = function(topic) {
	$.getMessagingService().unregisterTopic(topic);
};

/**
 * Checks the existence of a topic by name
 */
exports.isTopicExists = function(topic) {
	return $.getMessagingService().isTopicExists(topic);
};


/**
 * Subscribe a given client for a given topic,
 * so that this client will get the new messages 
 * from this topic after the routing process 
 */
exports.subscribe = function(client, topic) {
	$.getMessagingService().subscribe(client, topic);
};

/**
 * Un-subscribe a given client from a given topic 
 */
exports.unsubscribe = function(client, topic) {
	$.getMessagingService().unsubscribe(client, topic);
};
	
/**
 * Check whether subscription of a given client to a given topic exists
 */
exports.isSubscriptionExists = function(subscriber, topic) {
	return $.getMessagingService().isTopicExists(subscriber, topic);
};
	
/**
 * Send a message to the service
 */
exports.send = function(sender, topic, subject, body) {
	$.getMessagingService().send(sender, topic, subject, body);
};
	
/**
 * Get all the new messages for this client for all the topics
 */
exports.receive = function(receiver) {
	var internalMessages = $.getMessagingService().receive(receiver);
	var messages = [];
	for(var i = 0; i < internalMessages.size(); i ++) {
    	var internalMessage = internalMessages.get(i);
		messages.push(new MessageDefiniton(internalMessage));  	
	}
	return messages;
};

/**
 * MessageDefinition object
 */
function MessageDefiniton(internalMessage) {
	this.internalMessage = internalMessage;

	this.getInternalObject = function() {
		return this.internalDatasource;
	};

	this.id = internalMessage.getId();

	this.topic = internalMessage.getTopic();

	this.subject = internalMessage.getSubject();

	this.body = internalMessage.getBody();

	this.sender = internalMessage.getSender();

	this.createdBy = internalMessage.getCreatedBy();

	this.createdAt = new Date(internalMessage.getCreatedAt().getTime());
}
	
/**
 * Get all the new messages for this Client for a given Topic
 */
exports.receiveByTopic = function(receiver, topic) {
	var internalMessages = $.getMessagingService().receiveByTopic(receiver, topic);
	var messages = [];
	for(var i = 0; i < internalMessages.size(); i ++) {
    	var internalMessage = internalMessages.get(i);
		messages.push(new MessageDefiniton(internalMessage));  	
	}
	return messages;
};

/**
 * Triggers the routing process.
 * Takes new incoming messages and creates
 * outgoing links for all subscribed clients,
 * so that they can retrieve them by calling receive() method
 */
exports.route = function() {
	$.getMessagingService().route();
};
	
/**
 * Removes older messages
 */
exports.cleanup = function() {
	$.getMessagingService().cleanup();
};