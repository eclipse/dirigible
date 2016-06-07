/* globals $ */
/* eslint-env node, dirigible */

context = require("core/context");

var recievedMessage = context.get("message");
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
	console.info("Health Check");
}