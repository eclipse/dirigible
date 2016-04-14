package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;

import javax.websocket.Session;

import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.EmptyResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils;

import com.google.gson.Gson;

/**
 * Common handler for all messages which do not have a specific handler.
 */
public class EmptyResultHandler implements MessageHandler {

	private static final Logger logger = Logger.getLogger(MessageHandlerFactory.class.getCanonicalName());

	@Override
	public void handle(final String message, final Session session) throws IOException {
		logger.debug(String.format("No handler found to handle message with method [ %s ].",
				RequestUtils.getMessageMethod(message)));
		final Integer messageId = RequestUtils.getMessageId(message);
		final EmptyResponse result = new EmptyResponse(messageId);
		MessageDispatcher.sendMessage(new Gson().toJson(result), session);
	}
}
