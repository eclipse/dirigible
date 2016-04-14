package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.NoIdResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.PageGetResourceTreeResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptProcessor;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptProcessor.ScriptResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.RequestUtils;

import com.google.gson.Gson;

public class GetResourceTreeHandler implements MessageHandler {

	private static final Gson GSON = new Gson();
	
	@Override
	public void handle(final String message, final Session session) throws IOException {
		final Integer messageId = RequestUtils.getMessageId(message);
		final PageGetResourceTreeResponse resourceTree = PageGetResourceTreeResponse.buildForProjectsWithResources(DebugConfiguration.getResources());
		resourceTree.setId(messageId);
		String treeAsJson = GSON.toJson(resourceTree);
		MessageDispatcher.sendSyncMessage(treeAsJson, session);
		this.registerScripts(resourceTree);
		final Integer contextId = this.initializeExecutionContext(session, resourceTree);
		this.parseScripts(session, resourceTree, contextId);
	}

	private void registerScripts(final PageGetResourceTreeResponse resourceTree) {
		final List<Map<String, String>> resources = resourceTree.getResult().getFrameTree().getResources();
		final ScriptRepository repo = ScriptRepository.getInstance();
		for (final Map<String, String> resource : resources) {
			repo.addScript(resource.get("url"));
		}
	}

	private Integer initializeExecutionContext(final Session session, final PageGetResourceTreeResponse resourceTree) {
		final Context context = new Context(resourceTree.getResult().getFrameTree().getFrame().get("id"));
		final NoIdResponse noIdResponse = new NoIdResponse();
		noIdResponse.setMethod("Runtime.executionContextCreated");
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("context", context);
		noIdResponse.setParams(params);
		MessageDispatcher.sendMessage(GSON.toJson(noIdResponse), session);
		return context.id;
	}

	private void parseScripts(final Session session, final PageGetResourceTreeResponse resourceTree,
			final Integer contextId) {
		final List<Map<String, String>> resources = resourceTree.getResult().getFrameTree().getResources();
		for (final Map<String, String> resource : resources) {
			final String type = resource.get("type");
			if (type.equalsIgnoreCase("script")) {
				final String url = resource.get("url");
				final ScriptResponse parsedScript = ScriptProcessor.getScriptResponseForURL(url, contextId);
				MessageDispatcher.sendMessage(GSON.toJson(parsedScript), session);
			}
		}
	}

	@SuppressWarnings("unused")
	private class Context { 
		private final Integer id = new Random().nextInt(10000);
		private final String name = "";
		private final String origin = "";
		private String frameId;
		private Boolean isPageContext = true;

		private Context(String frameId) {
			this.frameId = frameId;
		}
	}
}
