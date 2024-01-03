export function onMessage(message) {
    const messageBody = message.getBodyAsString();
    const modifiedMessageBody = `${messageBody} -> calledFromCamel.mjs handled this message`;
    message.setBody(modifiedMessageBody);
    return message;
}