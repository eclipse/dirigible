const BpmFacade = Java.type("org.eclipse.dirigible.integration.tests.messaging.MessagesHolder");

exports.onMessage = function (message) {
	BpmFacade.setLatestReceivedMessage(message)
    console.log("--- BACKGROUND LISTENER --- Received a message: " + message);
}

exports.onError = function (error) {
	BpmFacade.setLatestReceivedError(error)
    console.error("--- BACKGROUND LISTENER --- Received an error: " + error);
}