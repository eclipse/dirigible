package org.eclipse.dirigible.runtime.chrome.debugger.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ScriptProcessor {

	public static ScriptResponse getScriptResponseForURL(final String url, final Integer contextId){
		final ScriptRepository repo = ScriptRepository.getInstance();
		final String scriptId = repo.getScriptIdByURL(url);
		final String source = repo.getSourceFor(scriptId);
		final ScriptResponse response = new ScriptResponse(url, scriptId, contextId);
		try {
			response.setStartLine(getStartLine(source));
			response.setEndLine(getEndLine(source));
			response.setStartColumn(getStartColumn(source));
			response.setEndColumn(getEndColumn(source));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private static Integer getStartColumn(String source) throws IOException {
		Integer startLine = getStartLine(source);
		String[] lines = source.split("\n");
		String firstLine;
		if(lines.length == 0){
			firstLine = source;
		}else{
			firstLine = lines[startLine];
		}
		int column = 0;
		while(!Character.isLetter(firstLine.charAt(column))){
			column++;
		}
		return column;
	}

	private static Integer getStartLine(String source) {
		String[] lines = source.split("\n");
		for(int i = 0; i<lines.length; i++){
			String line = lines[i].trim();
			if(!line.isEmpty()){
				return i;
			}
		}
		return 0;
	}

	private static Integer getEndColumn(final String source) throws IOException {
		int currentMaxColumns = 0;
		final BufferedReader reader = new BufferedReader(new StringReader(source));
		String line;
		while((line = reader.readLine()) != null){
			if(line.length() > currentMaxColumns){
				currentMaxColumns = line.length();
			}
		}
		reader.close();
		return currentMaxColumns;
	}

	private static Integer getEndLine(final String source) throws IOException {
		return source.split("\n").length;
	}

	public static class ScriptResponse{
		@SuppressWarnings("unused")
		private final String method = "Debugger.scriptParsed";
		private final Map<String, Object> params = new HashMap<String, Object>();

		public ScriptResponse(final String url, final String scriptId, final Integer contextId){
			this.params.put("deprecatedCommentWasUsed", false);
			this.params.put("hasSourceURL", true);
			this.params.put("url", url);
			this.params.put("hash", String.valueOf(url.hashCode()));
			this.params.put("isContentScript", false);
			this.params.put("isInternalScript", true);
			this.params.put("isLiveEdit", false);
			this.params.put("sourceMapURL", "");
			this.params.put("scriptId", scriptId);
			this.params.put("executionContextId", contextId);
		}

		public void setStartLine(final Integer startLine){
			this.params.put("startLine", startLine);
		}

		public void setEndLine(final Integer endLine){
			this.params.put("endLine", endLine);
		}

		public void setStartColumn(final Integer startColumn){
			this.params.put("startColumn", startColumn);
		}

		public void setEndColumn(final Integer endColumn){
			this.params.put("endColumn", endColumn);
		}
	}
}
