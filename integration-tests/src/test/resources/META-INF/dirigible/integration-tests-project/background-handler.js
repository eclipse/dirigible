const MessagesHolder = Java.type("org.eclipse.dirigible.integration.tests.messaging.MessagesHolder");

exports.onMessage = function (message) {
	MessagesHolder.setLatestReceivedMessage(message)
    console.log("--- BACKGROUND LISTENER --- Received a message: " + message);
}

exports.onError = function (error) {
	MessagesHolder.setLatestReceivedError(error)
    console.error("--- BACKGROUND LISTENER --- Received an error: " + error);
}