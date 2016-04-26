package org.eclipse.dirigible.runtime.chrome.debugger.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageRequest;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.MessageResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.ResultResponse;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.DebuggingService;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.MessageDispatcher;
import org.eclipse.dirigible.runtime.chrome.debugger.processing.ScriptRepository;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.ScriptUtils;

import com.google.gson.Gson;

public class SetScriptSourceHandler implements MessageHandler{

	private static final Gson GSON = new Gson();
	
	@Override
	public void handle(String message, Session session) throws IOException {
		MessageRequest request = GSON.fromJson(message, MessageRequest.class);
		Integer messageId = request.getId();
		Map<String, Object> params = request.getParams();
		String scriptId = (String) params.get("scriptId");
		String source = (String) params.get("scriptSource");
		
		ScriptRepository repo = ScriptRepository.getInstance();
		String oldSource = repo.getSourceFor(scriptId);
		repo.update(scriptId, source);
		String url = repo.getUrl(scriptId);
		DebuggingService.updateSource(url, source);
		MessageResponse response = getResponse(messageId, scriptId, source, oldSource);
		MessageDispatcher.sendMessage(GSON.toJson(response), session);
	}

	private MessageResponse getResponse(Integer messageId, String scriptId, String newSource, String oldSource) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> res = new HashMap<String, Object>();
		result.put("change_tree", getChangeTree(scriptId, true, oldSource.length(), newSource.length()));
		result.put("stack_modified", false);
		result.put("stack_update_needs_step_in", false);
		result.put("textual_diff", getTextualDiff(oldSource, newSource));
		result.put("updated", true);
		res.put("result", result);
		res.put("callFrames", new ArrayList<Object>());
		ResultResponse response = new ResultResponse(messageId, res);
		return response;
	}

	private Map<String, Object> getChangeTree(String scriptId, boolean isTopTree, int oldLen, int newLen) {
		Map<String, Object> changeTree = new HashMap<String, Object>();
		List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
		Map<String, Integer> positions = new HashMap<String, Integer>();
		Map<String, Object> child;
		Double firstLine = ScriptUtils.getFirstLine(scriptId);
		Double startColumn = ScriptUtils.getStartColumnForLine(scriptId, firstLine);
		if(isTopTree){
			 child = getChangeTree(scriptId, false, oldLen, newLen);
			 child.put("status", "changed");
			 positions.put("end_position", newLen);
			 positions.put("start_position", startColumn.intValue());
			 child.put("positions", positions);
			 child.put("new_positions", positions);
			 children.add(child);
		}
		changeTree.put("children", children);
		changeTree.put("name", "");
		changeTree.put("new_children", new ArrayList<Object>());
		positions.put("end_position", oldLen);
		positions.put("start_position", startColumn.intValue());
		changeTree.put("new_positions", positions);
		changeTree.put("positions", positions);
		changeTree.put("status", "source changed");
		return changeTree;
	}

	private Map<String, Object> getTextualDiff(String oldSource, String newSource) {
		Map<String, Object> textualDiff = new HashMap<String, Object>();
		Map<String, Integer> chunks = new HashMap<String, Integer>();
		chunks.put("0", getFirstDifferentIndex(oldSource, newSource));
		chunks.put("1", getEndIndexOfDifference(oldSource, newSource));
		textualDiff.put("chunks", chunks);
		textualDiff.put("new_len", newSource.length());
		textualDiff.put("old_len", oldSource.length());
		return textualDiff;
	}
	
	private int getEndIndexOfDifference(String oldSource, String newSource) {
		int oldLen = oldSource.length();
		int newLen = newSource.length();
		while(oldLen-- >= 0 && newLen-- >= 0){
			char oldChar = oldSource.charAt(oldLen);
			char newChar = newSource.charAt(newLen);
			if(newChar != oldChar){
				return newLen+1;
			}
		}
		return newSource.length();
	}

	private int getFirstDifferentIndex(String source1, String source2){
		int i = 0;
		while(source1.charAt(i) == source2.charAt(i)){
			i++;
		}
		return i;
	}
}
