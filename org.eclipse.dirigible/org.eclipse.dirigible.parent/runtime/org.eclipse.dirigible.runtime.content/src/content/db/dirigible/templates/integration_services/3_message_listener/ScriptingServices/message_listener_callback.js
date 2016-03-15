/* globals $ */
/* eslint-env node, dirigible */

// print in system output
var systemLib = require('system');

var recievedMessage = $.getExecutionContext().get("message");
if(recievedMessage !== null) {
	systemLib.println(JSON.stringify({
		"id": recievedMessage.getId(),
		"topic": recievedMessage.getTopic(),
		"subject": recievedMessage.getSubject(),
		"body": recievedMessage.getBody(),
		"sender": recievedMessage.getSender(),
		"createdBy": recievedMessage.getCreatedBy(),
		"createdAt": recievedMessage.getCreatedAt().getTime()
	}));
} else {
	systemLib.println("Health Check");
}