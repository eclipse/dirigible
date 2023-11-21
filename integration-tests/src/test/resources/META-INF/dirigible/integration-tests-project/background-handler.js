const MessagesHolder = Java.type("org.eclipse.dirigible.integration.tests.api.java.messaging.MessagesHolder");

exports.onMessage = function (message) {
	MessagesHolder.setLatestReceivedMessage(message)
    console.log(new Date() + "### BACKGROUND HANDLER ### - Received a message: [" + message + "]");
}

exports.onError = function (error) {
	MessagesHolder.setLatestReceivedError(error)
    console.error(new Date() + "### BACKGROUND HANDLER ### - Received an error: [" + error + "]");
}