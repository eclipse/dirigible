package org.eclipse.dirigible.runtime.ws;

import java.io.IOException;

import com.google.gson.Gson;

public class WebSocketRequestParser {

	static Gson gson = new Gson();

	public static WebSocketRequest parseRequest(String webSocketRequestDefinition) throws IOException {
		// {
		// "module": "/${packageName}/service1.js",
		// "params": {
		// "param1": "value1",
		// "param2": "value2"
		// }
		// }

		return gson.fromJson(webSocketRequestDefinition, WebSocketRequest.class);
	}

}
