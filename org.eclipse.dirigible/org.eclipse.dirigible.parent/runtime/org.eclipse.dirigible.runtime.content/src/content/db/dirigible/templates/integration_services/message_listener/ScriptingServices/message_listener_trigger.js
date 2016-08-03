/* globals $ */
/* eslint-env node, dirigible */

var messaging = require('service/messaging');
var response = require('net/http/response');

var clientName = "clinet1";
var topicName = "topic1";

if (!messaging.isClientExists(clientName)) {
	messaging.registerClient(clientName);
}

if (!messaging.isTopicExists(topicName)) {
	messaging.registerTopic(topicName);
}

messaging.send("Message Sender", topicName, "Message Subject", "Message Body");

// print in response
response.setContentType("text/html; charset=UTF-8");
response.setCharacterEncoding("UTF-8");
response.println("Message Trigger Activated");
response.flush();
response.close();
