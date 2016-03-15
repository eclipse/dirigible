/* globals $ */
/* eslint-env node, dirigible */

// print in system output
var systemLib = require('system');
var mailObject = $.getExecutionContext().get("message");
if (mailObject !== null) {
	var mailParsed = JSON.parse(mailObject);
	systemLib.println(mailParsed.subject + ": " + mailParsed.content);
} else {
    systemLib.println("sync call");   
}