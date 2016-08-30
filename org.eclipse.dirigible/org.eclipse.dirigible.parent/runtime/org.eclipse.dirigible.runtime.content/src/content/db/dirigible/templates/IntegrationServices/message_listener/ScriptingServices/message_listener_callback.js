/* globals $ */
/* eslint-env node, dirigible */

var context = require("core/context");

var recievedMessage = context.get("message");
if(recievedMessage !== null) {
	console.info(JSON.stringify({
		"id": recievedMessage.getId(),
		"topic": recievedMessage.getTopic(),
		"subject": recievedMessage.getSubject(),
		"body": recievedMessage.getBody(),
		"sender": recievedMessage.getSender(),
		"createdBy": recievedMessage.getCreatedBy(),
		"createdAt": recievedMessage.getCreatedAt().getTime()
	}));
} else {
	console.info("Health Check");
}