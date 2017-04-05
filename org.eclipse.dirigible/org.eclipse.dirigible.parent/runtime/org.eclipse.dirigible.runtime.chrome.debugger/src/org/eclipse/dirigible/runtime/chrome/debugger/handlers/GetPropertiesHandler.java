package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Variable;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GetPropertiesHandler implements MessageHandler {

	private static final Gson GSON = new Gson();

	@Override
	public void handle(final String message, final Session session) throws IOException {
		final MessageRequest request = GSON.fromJson(message, MessageRequest.class);
		final Map<String, Object> params = request.getParams();
		final String objectId = (String) params.get("objectId");
		this.sendVariableInfo(message, objectId, session);
	}

	private void sendVariableInfo(final String message, final String objectId, final Session session)
			throws IOException {
//		final ScriptRepository repository = ScriptRepository.getInstance();
		final List<Variable> variables = new ArrayList<Variable>();// repo.getVariablesForObject(objectId);
		final Integer id = RequestUtils.getMessageId(message);
		final JsonObject result = new JsonObject();
		try {
			// TODO to be migrated/checked to Gson properly
			JsonArray array = new JsonArray();
			for (Iterator iterator = variables.iterator(); iterator.hasNext();) {
				Variable variable = (Variable) iterator.next();
				array.add(variable.toJson());
			}
			result.add("result", array);
			final JsonObject response = new JsonObject();
			response.addProperty("id", id);
			response.add("result", result);
		} catch (Exception e) {
			new OnExceptionHandler().handle(e.getMessage(), session);
		}

		MessageDispatcher.sendMessage(result.toString(), session);
	}
}
