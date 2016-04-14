package org.eclipse.dirigible.runtime.chrome.debugger.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestUtils {

	public static boolean isBreakpointMessage(final String message) {
		return message != null && message.contains("Debugger") && message.toLowerCase().contains("breakpoint");
	}

	public static boolean isSetBreakpointMessage(final String message) {
		return message != null && message.contains("Debugger.setBreakpoint");
	}

	public static boolean isGetResourceContent(final String message) {
		return message != null && message.contains("Page.getResourceContent");
	}

	public static Integer getMessageId(final String message) {
		try {
			return new JSONObject(message).getInt("id");
		} catch (JSONException e) {
			return null;
		}
	}

	public static String getMessageMethod(final String message) {
		try {
			return new JSONObject(message).getString("method");
		} catch (JSONException e) {
			return null;
		}
	}

	public static boolean isGetResourceTree(final String message) {
		return message != null && message.contains("Page.getResourceTree");
	}

	public static boolean isRemoveBreakpointMessage(final String message) {
		return message != null && message.contains("Debugger.removeBreakpoint");
	}

	public static boolean isGetScritpSource(final String message) {
		return message != null && message.contains("Debugger.getScriptSource");
	}

	public static boolean isStepIntoMessage(final String message) {
		return message != null && message.contains("Debugger.stepInto");
	}

	public static boolean isStepOutMessage(final String message) {
		return message != null && message.contains("Debugger.stepOut");
	}

	public static boolean isStepOverMessage(final String message) {
		return message != null && message.contains("debugger.stepOver");
	}

	public static boolean isDebuggerStepMessage(final String message) {
		return message != null && message.contains("Debugger.step");
	}

	public static boolean isGetProperties(final String message) {
		return message != null && message.contains("Runtime.getProperties");
	}

	public static boolean isEvaluateOnCallFrame(final String message) {
		return message != null && message.contains("Debugger.evaluateOnCallFrame");
	}

	public static boolean isSetScriptSource(String message) {
		return message != null && message.contains("Debugger.setScriptSource");
	}

	public static boolean isInspectorEnable(String message) {
		return message != null && message.contains("Inspector.enable");
	}
}
