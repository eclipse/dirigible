package org.eclipse.dirigible.runtime.chrome.debugger.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest.CallFrame;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest.Scope;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Variable;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Variable.Value;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.URIUtils;

public class ScriptRepository {
	
	private static final Logger LOGGER = Logger.getLogger(DebuggingService.class.getCanonicalName());
	private static ScriptRepository INSTACE = new ScriptRepository();
	private static Map<String, String> mappedBps = new HashMap<String, String>(); // URL : ScriptId
	private static Integer scriptId = 1024;
	private static Map<String, String> scriptSources = new HashMap<String, String>(); // ScriptID : Source
	private static List<CallFrame> frames = new ArrayList<CallFrame>();

	private ScriptRepository() {
	}

	public static synchronized ScriptRepository getInstance() {
		return INSTACE;
	}

	public String addScript(final String url) {
		try {
			final String nextId = String.valueOf(++scriptId);
			this.addScript(url, nextId);
			return nextId;
		} catch (final IOException e) {
			LOGGER.error(String.format("Could not add script with url %s to repository!", url), e);
			return String.valueOf(-1);
		}
	}

	public void update(final String scriptId, final String source){
		scriptSources.put(scriptId, source);
	}

	private void addScript(final String url, final String scriptId) throws IOException {
		mappedBps.put(url, String.valueOf(scriptId));
		String scriptSource = "";
		Map<String, List<IResource>> resources = DebugConfiguration.getResources();
		for(Map.Entry<String, List<IResource>> e : resources.entrySet()){
			for(IResource res : e.getValue()){
				if(url.equals(URIUtils.getUrlForResource(res))){
					scriptSource = new String(res.getContent());
				}
			}
		}
		scriptSources.put(String.valueOf(scriptId), scriptSource);
	}

	public String getScriptIdByURL(final String url) {
		return mappedBps.get(url);
	}

	public String getUrl(final String scriptId) {
		for (final Map.Entry<String, String> e : mappedBps.entrySet()) {
			final String eScriptId = e.getValue();
			if (eScriptId.equals(scriptId)) {
				return e.getKey();
			}
		}
		return null;
	}

	public String getSourceFor(final String scriptId) {
		return scriptSources.get(scriptId);
	}

	public void addFrame(final CallFrame frame) {
		frames.add(frame);
	}

	public CallFrame getFrameById(final String callFrameId) {
		for (final CallFrame frame : frames) {
			if (frame.getCallFrameId().equalsIgnoreCase(callFrameId)) {
				return frame;
			}
		}
		return null;
	}

	// TODO:
//	public List<Variable> getVariablesForObject(final String objectId) {
//		final Location location = DebugConfiguration.getCurrentExecutionLocation();
//		final List<Variable> variables = new ArrayList<Variable>();
//		final DebuggableScript script = Mockito.mock(DebuggableScript.class);
//		final Scriptable activation = Mockito.mock(Scriptable.class);
//		final ScriptableObject obj = Mockito.mock(ScriptableObject.class);
//
//		for(int i = 0; i<script.getParamAndVarCount(); i++){
//			final String name = script.getParamOrVarName(i);
//			final Object varValue = activation.get(name, activation);
//			final String valueContent = this.parseValueToString(varValue);
//			final Variable var = new Variable();
//			var.setName(name);
//			final String className = activation.getClassName();
//			final Value value = new Value();
//			value.setClassName(className);
//			Preview preview;
//			if(className.equalsIgnoreCase("Array")){
//				final NativeArray arr = (NativeArray) varValue;
//				value.setDescription(String.format("Array[%d]",	arr.size()));
//				value.setObjectId(String.format("{\"injectedScriptId\":%d,\"id\":%d", 0, 0)); //TODO
//				value.setSubtype("array");
//				value.setType("object");
//
//				preview = new Preview();
//				preview.setDescription(value.getDescription());
//				preview.setLossless(true);
//				preview.setOverflow(false);
//				preview.setSubtype(value.getSubtype());
//				preview.setType(value.getType());
//				final List<Property> properties = new ArrayList<Property>();
//				final Property prop = new Property();
//				prop.setName(className);
//
//				preview.setProperties(properties );
//
//				value.setPreview(preview);
//				var.setWritable(true);
//				var.setIsOwn(true);
//				var.setEnumerable(true);
//				var.setConfigurable(true);
//			}
//
//			var.setValue(value);
//		}
//
//		return variables;
//	}

	public Value getVariableValueByName(final String variableName) {
		for (final CallFrame frame : frames) {
			final List<Scope> scopeChain = frame.getScopeChain();
			for (final Scope scope : scopeChain) {
				final String objectId = scope.getObject().get("objectId");
				final List<Variable> variables = new ArrayList<Variable>();//this.getVariablesForObject(objectId);
				for (final Variable var : variables) {
					if (var.getName().equalsIgnoreCase(variableName)) {
						return var.getValue();
					}
				}
			}
		}

		return null;
	}
}
