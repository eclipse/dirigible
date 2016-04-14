package org.eclipse.dirigible.runtime.chrome.debugger.handlers;
import java.io.IOException;

import javax.websocket.Session;

public interface MessageHandler {

	void handle(String message, Session session) throws IOException;
}
