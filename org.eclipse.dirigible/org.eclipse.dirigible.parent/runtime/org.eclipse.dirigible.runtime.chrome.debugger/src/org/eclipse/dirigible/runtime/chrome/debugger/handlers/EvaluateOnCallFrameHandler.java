package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Variable.Value;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * Handler for evaluating an expression a call frame.
 */
public class EvaluateOnCallFrameHandler implements MessageHandler {

	private static final Gson GSON = new Gson();

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final MessageRequest request = GSON.fromJson(message, MessageRequest.class);
		final ScriptRepository repository = ScriptRepository.getInstance();
		final Map<String, Object> params = request.getParams();
		final String variableName = (String) params.get("expression");
		final Value variableValue = repository.getVariableValueByName(session.getUserPrincipal().getName(), variableName);
		final JSONObject response = new JSONObject();
		try {
			response.put("id", RequestUtils.getMessageId(message));
			final Map<String, Object> result = new HashMap<String, Object>();
			result.put("result", variableValue);
			result.put("wasThrown", false);
			response.put("result", result);
		} catch (JSONException e) {
			new OnExceptionHandler().handle(e.getMessage(), session);
		}

		MessageDispatcher.sendMessage(response.toString(), session);
	}
}
