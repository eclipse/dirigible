exports.onMessage = (message) => {
    let messageBody = message.getBodyAsString();
    let modifiedMessageBody = messageBody + " -> calledFromCamel.js handled this message";
    console.log('[CamelTest] CalledFromCamel.js called with message: ' + messageBody);
    message.setBody(modifiedMessageBody);
    return message;
}