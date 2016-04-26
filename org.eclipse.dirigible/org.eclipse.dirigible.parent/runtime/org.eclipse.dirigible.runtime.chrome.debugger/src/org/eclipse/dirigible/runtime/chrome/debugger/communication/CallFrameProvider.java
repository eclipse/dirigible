package org.eclipse.dirigible.runtime.chrome.debugger.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest.CallFrame;
import org.eclipse.dirigible.runtime.chrome.debugger.communication.DebuggerPausedRequest.Scope;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.ScriptUtils;

class CallFrameProvider {
	
	/**
	 * <pre>
	 * Scope chain is created as follows:
	 * 	Common scope object structure:
	 * 		className:"Object"
	 *		description:"Object"
	 *		objectId = scope:%callFrameId%:%currentObjectIncrement%
	 *		type: "object"
	 *
	 * 	scope 0: This scope's id is associated with a list of local variables (inner most scope) 
	 * 			 object: as stated above
	 * 			 type: "local" 
	 *  scope 1: This scope's id is associated with a list of closure variables. 
	 *  			<i>Closures are functions that refer to independent (free) variables. 
	 *  			In other words, the function defined in the closure 'remembers' the environment in which it was created.</i>
	 *  		 object: as stated above 
	 *  		 type: "closure"
	 *  scope 2: This scope's id is associated with a list of unknown variables (usually empty)
	 *  		 object: as stated above
	 *  		 type: "unknown"
	 *  scope 3: This scope's id is associated with a list of global variables - can be found from Rhino's ScriptableObject.get()
	 *  		 object: as stated above		 
	 *  		 type: "global"
	 *  this: same as scope object but with a different id
	 * 	
	 * </pre>
	 */
	static List<CallFrame> get(String userId, String scriptId, Integer breakpointLine){
		final List<CallFrame> callFrames = new ArrayList<CallFrame>();
		// TODO: traverse script and get call frames
		// currently it gets only a call frame for the current execution location
		int callFrameId = 0; // increment with each callFrame
		final CallFrame callFrame = getCallFrame(userId, callFrameId, scriptId, breakpointLine);
		callFrames.add(callFrame);
		return callFrames;
	}

	private static CallFrame getCallFrame(String userId, int callFrameId, String scriptId, Integer breakpointLine) {
		final CallFrame callFrame = new CallFrame();
		
		callFrame.setCallFrameId(String.valueOf(callFrameId)); 
		
		final List<Scope> scopeChain = getScopeChain(callFrameId);
	
		Location startLocation;
		Location endLocation;
		if(ScriptUtils.hasFunctions(scriptId)){
			startLocation = ScriptUtils.getStartLocation(scriptId, breakpointLine);
			endLocation = ScriptUtils.getEndLocation(scriptId, breakpointLine);
			
			callFrame.setFunctionName(ScriptUtils.getEnclosingFunctionName(scriptId, breakpointLine));
			callFrame.setFunctionLocation(startLocation);
		}else{
			startLocation = new Location();
			startLocation.setLineNumber(ScriptUtils.getFirstLine(scriptId));
			startLocation.setColumnNumber(0.0);
			startLocation.setScriptId(scriptId);
			
			endLocation = new Location();
			endLocation.setLineNumber(ScriptUtils.getLastLine(scriptId));
			endLocation.setColumnNumber(ScriptUtils.getLastColumn(scriptId));
			endLocation.setScriptId(scriptId);
			
			callFrame.setFunctionName("");
		}
		
//		scope.setStartLocation(startLocation);
//		scope.setEndLocation(endLocation);
		
		callFrame.setLocation(DebugConfiguration.getCurrentExecutionLocation(userId));
				
		callFrame.setScopeChain(scopeChain);
		return callFrame;
	}

	private static List<Scope> getScopeChain(int callFrameId) {
		List<Scope> scopeChain = new ArrayList<Scope>();
		int currentObjectId = 0;
		
		Scope localScope = getScopeWithType("local", callFrameId, currentObjectId++);
		scopeChain.add(localScope);
		
		Scope closureScope = getScopeWithType("closure", callFrameId, currentObjectId++);
		scopeChain.add(closureScope);
		
		Scope unknownScope = getScopeWithType("unknown", callFrameId, currentObjectId++);
		scopeChain.add(unknownScope);
		
		Scope globalScope = getScopeWithType("global", callFrameId, currentObjectId++);
		scopeChain.add(globalScope);
		
		return scopeChain;
	}

	private static Scope getScopeWithType(String type, int callFrameId, int currentObjectId) {
 		final Scope scope = new Scope();
		final Map<String, String> object = getObject(callFrameId, currentObjectId);
		scope.setObject(object);
		scope.setType(type);
		return scope;
	}

	private static Map<String, String> getObject(int callFrameId, int currentObjectId) {
		final Map<String, String> object = new HashMap<String, String>();
		object.put("className", "Object");
		object.put("description", "Object");
		object.put("objectId",
				String.format("scope:%d:%d", callFrameId, currentObjectId));
		object.put("type", "object");
		return object;
	}
}
