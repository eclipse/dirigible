/* globals $ */
/* eslint-env node, dirigible */

context = require("core/context");

var mailObject = context.get("message");
if (mailObject !== null) {
	var mailParsed = JSON.parse(mailObject);
	console.info(mailParsed.subject + ": " + mailParsed.content);
} else {
    console.warn("sync call");   
}