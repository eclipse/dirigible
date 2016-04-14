package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.OnExceptionResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;

import com.google.gson.Gson;

public class OnExceptionHandler implements MessageHandler {

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final OnExceptionResponse response = new OnExceptionResponse(-1, message);
		MessageDispatcher.sendMessage(new Gson().toJson(response), session);
	}
}
