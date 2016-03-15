/* globals $ */
/* eslint-env node, dirigible */

var clientName = "clinet1";
var topicName = "topic1";

if (!$.getMessagingService().isClientExists(clientName)) {
	$.getMessagingService().registerClient(clientName);
}

if (!$.getMessagingService().isTopicExists(topicName)) {
	$.getMessagingService().registerTopic(topicName);
}

$.getMessagingService().send("Message Sender", topicName, "Message Subject", "Message Body");

// print in response
$.getResponse().setContentType("text/html; charset=UTF-8");
$.getResponse().setCharacterEncoding("UTF-8");
$.getResponse().getWriter().println("Message Trigger Activated");
$.getResponse().getWriter().flush();
$.getResponse().getWriter().close();
