exports.onMessage = (message) => {
    console.log('[CamelTest] CalledFromCamel.js called with message: ' + message);
    return message + " -> calledFromCamel.js handled this message";
}