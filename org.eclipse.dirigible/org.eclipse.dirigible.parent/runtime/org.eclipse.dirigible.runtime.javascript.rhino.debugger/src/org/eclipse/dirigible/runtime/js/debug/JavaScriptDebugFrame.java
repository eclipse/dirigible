/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.IDebugExecutor.DebugCommand;
import org.eclipse.dirigible.repository.ext.debug.LinebreakMetadata;
import org.eclipse.dirigible.repository.ext.debug.VariableValue;
import org.eclipse.dirigible.repository.ext.debug.VariableValuesMetadata;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;

import com.google.gson.Gson;

public class JavaScriptDebugFrame implements DebugFrame {
	private static final String NULL = "null";

	private static final String NATIVE = "native";

	private static final String FUNCTION = "function";

	private static final String UNDEFINED = "undefined";

	private static final Logger logger = Logger.getLogger(JavaScriptDebugFrame.class);

	private static final int SLEEP_TIME = 50;
	// private DebuggerActionManager debuggerActionManager;
	private DebuggerActionCommander debuggerActionCommander;
	private Stack<DebuggableScript> scriptStack;
	private Stack<Scriptable> activationStack;
	private int stepOverLineNumber = 0;
	private int previousLineNumber = 0;
	private boolean stepOverFinished = true;
	private DebugModel debugModel;
	// private VariableValuesMetadata variableValuesMetadata;

	private DebugSessionModel session;

	public JavaScriptDebugFrame(DebugModel debugModel, HttpServletRequest request, JavaScriptDebugger javaScriptDebugger) {
		// get the instance of debugger action manager from the session

		logDebug("entering JavaScriptDebugFrame.constructor");

		// this.debuggerActionManager = DebuggerActionManager.getInstance(request.getSession());

		this.debugModel = debugModel;

		// create a new instance of commander per frame
		String sessionId = request.getSession().getId();
		String executionId = UUID.randomUUID().toString();
		String userId = RequestUtils.getUser(request);

		this.session = this.debugModel.createSession();

		this.debuggerActionCommander = new DebuggerActionCommander(this.session, sessionId, executionId, userId);

		this.session.setDebugExecutor(getDebuggerActionCommander());

		this.debuggerActionCommander.init();
		this.debuggerActionCommander.setDebugFrame(this);
		this.debuggerActionCommander.setDebugger(javaScriptDebugger);

		this.scriptStack = new Stack<DebuggableScript>();
		this.activationStack = new Stack<Scriptable>();

		this.debugModel.getDebugController().register(session);

		// registerDebugFrame();

		logDebug("exiting JavaScriptDebugFrame.constructor");
	}

	public DebugModel getDebugModel() {
		return debugModel;
	}

	// private void registerDebugFrame() {
	//// logDebug("entering JavaScriptDebugFrame.registerDebugFrame");
	//// String commandBody = new Gson().toJson(new DebugSessionMetadata(
	//// getDebuggerActionCommander().getSessionId(), getDebuggerActionCommander()
	//// .getExecutionId(), getDebuggerActionCommander().getUserId()));
	//// send(DebugConstants.VIEW_REGISTER, commandBody);
	//
	// this.session = this.debugModel.createSession();
	// session.setDebugExecutor(getDebuggerActionCommander());
	//
	// logDebug("exiting JavaScriptDebugFrame.registerDebugFrame");
	// }

	@Override
	public void onEnter(Context context, Scriptable activation, Scriptable thisObj, Object[] args) {
		logDebug("entering JavaScriptDebugFrame.onEnter()");
		DebuggableScript script = (DebuggableScript) context.getDebuggerContextData();
		scriptStack.push(script);
		activationStack.push(activation);
		logDebug("exiting JavaScriptDebugFrame.onEnter()");
	}

	@Override
	public void onLineChange(Context context, int lineNumber) {
		blockExecution();
		processAction(lineNumber, getNextCommand());
	}

	@Override
	public void onExceptionThrown(Context context, Throwable ex) {
		logError("[debugger] onExceptionThrown()");
	}

	@Override
	public void onExit(Context context, boolean byThrow, Object resultOrException) {
		logDebug("entering JavaScriptDebugFrame.onExit()");
		scriptStack.pop();
		activationStack.pop();
		if (scriptStack.isEmpty()) {
			this.debuggerActionCommander.clean();
			DebuggerActionCommander commander = getDebuggerActionCommander();
			DebugSessionMetadata metadata = new DebugSessionMetadata(commander.getSessionId(), commander.getExecutionId(), commander.getUserId());

			// clear variables for the UI
			clearVariables();
			// // clear breakpoints for the UI
			// clearBreakpoints();
			// remove the session
			finishDebugSession(metadata);
		}
		logDebug("exiting JavaScriptDebugFrame.onExit()");
	}

	private void finishDebugSession(DebugSessionMetadata metadata) {
		// String json = new Gson().toJson(metadata);
		// send(DebugConstants.VIEW_FINISH, json);
		this.session.getDebugController().finish(this.session);
	}

	private void clearVariables() {
		if (this.session.getVariableValuesMetadata() != null) {
			this.session.getVariableValuesMetadata().getVariableValueList().clear();
			notifyVariableValuesMetadata();
		}
	}

	// private void clearBreakpoints() {
	// if (debuggerActionManager.getBreakpoints() != null) {
	// debuggerActionManager.getBreakpoints().clear();
	//// sendBreakpointsMetadata();
	// }
	// }

	@Override
	public void onDebuggerStatement(Context context) {
		print(-1);
		if (debuggerActionCommander.isExecuting()) {
			debuggerActionCommander.pauseExecution();
		}
	}

	private void processAction(int lineNumber, DebugCommand nextCommand) {
		if (nextCommand != DebugCommand.SKIP_ALL_BREAKPOINTS) {
			if (isBreakpoint(lineNumber)) {
				hitBreakpoint(lineNumber);
			} else {
				switch (nextCommand) {
					case CONTINUE:
						break;
					case STEPINTO:
						stepInto(lineNumber);
						break;
					case STEPOVER:
						stepOver(lineNumber);
						break;
					case SKIP_ALL_BREAKPOINTS:
						break;
					default:
						break;
				}
			}
			previousLineNumber = lineNumber;
		}
	}

	private void hitBreakpoint(int lineNumber) {
		logDebug("entering JavaScriptDebugFrame.hitBreakpoint(): " + lineNumber);
		print(lineNumber);
		debuggerActionCommander.stepOver();
		debuggerActionCommander.pauseExecution();
		logDebug("exiting JavaScriptDebugFrame.hitBreakpoint()");
	}

	private void stepInto(int lineNumber) {
		logDebug("entering JavaScriptDebugFrame.stepInto(): " + lineNumber);
		print(lineNumber);
		debuggerActionCommander.pauseExecution();
		logDebug("exiting JavaScriptDebugFrame.stepInto()");
	}

	private void stepOver(int lineNumber) {
		logDebug("entering JavaScriptDebugFrame.stepOver(): " + lineNumber);
		if (stepOverFinished) {
			stepOverFinished = false;
			stepOverLineNumber = previousLineNumber;
		}
		if ((stepOverLineNumber + 1) == lineNumber) {
			stepOverFinished = true;
			stepOverLineNumber = -1;
			stepInto(lineNumber);
		}
		logDebug("entering JavaScriptDebugFrame.stepOver()");
	}

	private void blockExecution() {
		logDebug("entering JavaScriptDebugFrame.blockExecution()");
		while (!debuggerActionCommander.isExecuting() && (getNextCommand() != DebugCommand.CONTINUE)
				&& (getNextCommand() != DebugCommand.SKIP_ALL_BREAKPOINTS)) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		logDebug("exiting JavaScriptDebugFrame.blockExecution()");
	}

	private DebugCommand getNextCommand() {
		return debuggerActionCommander.getCommand();
	}

	private boolean isBreakpoint(int row) {
		String path = scriptStack.peek().getSourceName();
		if (!path.startsWith(ICommonConstants.SEPARATOR)) {
			path = ICommonConstants.SEPARATOR + path;
		}
		DebuggerActionCommander commander = getDebuggerActionCommander();
		LinebreakMetadata breakpoint = new LinebreakMetadata(commander.getSessionId(), commander.getExecutionId(), commander.getUserId(), path, row);
		Set<BreakpointMetadata> breakpoints = debuggerActionCommander.getBreakpoints();
		return breakpoints.contains(breakpoint.getBreakpoint());
	}

	private void print(int row) {
		DebuggerActionCommander commander = getDebuggerActionCommander();
		DebuggableScript script = scriptStack.peek();
		Scriptable activation = activationStack.peek();
		List<VariableValue> variableValuesList = new ArrayList<VariableValue>();
		for (int i = 0; i < script.getParamAndVarCount(); i++) {
			String variable = script.getParamOrVarName(i);
			Object value = activation.get(variable, activation);

			if ((variable != null) && (value != null)) {
				String valueContent = parseValueToString(value);
				variableValuesList.add(new VariableValue(variable, valueContent));
			}
		}
		// if (variableValuesMetadata == null) {
		VariableValuesMetadata variableValuesMetadata = new VariableValuesMetadata(commander.getSessionId(), commander.getExecutionId(),
				commander.getUserId(), variableValuesList);
		// }
		this.session.setVariableValuesMetadata(variableValuesMetadata);
		notifyVariableValuesMetadata();
		String sourceName = script.getSourceName();
		sendOnBreakLineChange(sourceName, row);
	}

	private String parseValueToString(Object value) {
		String result = null;
		if (value instanceof Undefined) {
			result = UNDEFINED;
		} else if (value instanceof Boolean) {
			result = value.toString();
		} else if (value instanceof Number) {
			result = value.toString();
		} else if (value instanceof CharSequence) {
			result = value.toString();
		} else if (value instanceof BaseFunction) {
			result = FUNCTION;
		} else if (value instanceof NativeJavaObject) {
			result = NATIVE;
		} else if (value instanceof ScriptableObject) {
			try {
				result = new Gson().toJson(value);
			} catch (StackOverflowError error) {
				result = NATIVE;
			}
		} else {
			result = NULL;
		}
		return result;
	}

	// @Override
	// public void propertyChange(PropertyChangeEvent event) {
	// String commandId = event.getPropertyName();
	// String clientId = (String) event.getOldValue();
	// String commandBody = (String) event.getNewValue();
	// logDebug("JavaScriptDebugFrame propertyChange() command: " + commandId + ", clientId: "
	// + clientId + ", body: " + commandBody);
	//
	// if (clientId == null || !clientId.equals(getDebuggerActionCommander().getExecutionId())) {
	// // skip as the command is not for the current frame
	// return;
	// }
	//
	// Gson gson = new Gson();
	// if (commandId.startsWith(DebugConstants.DEBUG)) {
	// if (commandId.equals(DebugConstants.DEBUG_REFRESH)) {
	// sendBreakpointsMetadata();
	// notifyVariableValuesMetadata();
	// } else if (commandId.equals(DebugConstants.DEBUG_STEP_INTO)) {
	// debuggerActionCommander.stepInto();
	// debuggerActionCommander.resumeExecution();
	// } else if (commandId.equals(DebugConstants.DEBUG_STEP_OVER)) {
	// debuggerActionCommander.stepOver();
	// debuggerActionCommander.resumeExecution();
	// } else if (commandId.equals(DebugConstants.DEBUG_CONTINUE)) {
	// debuggerActionCommander.continueExecution();
	// debuggerActionCommander.resumeExecution();
	// } else if (commandId.equals(DebugConstants.DEBUG_SKIP_ALL_BREAKPOINTS)) {
	// debuggerActionCommander.skipAllBreakpoints();
	// debuggerActionCommander.resumeExecution();
	// } else if (commandId.equals(DebugConstants.DEBUG_SET_BREAKPOINT)) {
	// BreakpointMetadata breakpoint = gson
	// .fromJson(commandBody, BreakpointMetadata.class);
	// debuggerActionCommander.addBreakpoint(breakpoint);
	// sendBreakpointsMetadata();
	// } else if (commandId.equals(DebugConstants.DEBUG_CLEAR_BREAKPOINT)) {
	// BreakpointMetadata breakpoint = gson
	// .fromJson(commandBody, BreakpointMetadata.class);
	// debuggerActionCommander.clearBreakpoint(breakpoint);
	// sendBreakpointsMetadata();
	// } else if (commandId.equals(DebugConstants.DEBUG_CLEAR_ALL_BREAKPOINTS)) {
	// debuggerActionCommander.clearAllBreakpoints();
	//// sendBreakpointsMetadata();
	// } else if (commandId.equals(DebugConstants.DEBUG_CLEAR_ALL_BREAKPOINTS_FOR_FILE)) {
	// String path = commandBody;
	// debuggerActionCommander.clearAllBreakpoints(path);
	//// sendBreakpointsMetadata();
	// }
	// }
	// }

	private void notifyVariableValuesMetadata() {
		if (this.session.getVariableValuesMetadata() != null) {
			// Gson gson = new Gson();
			// String variableValuesJson = gson.toJson(variableValuesMetadata);
			// send(DebugConstants.VIEW_VARIABLE_VALUES, variableValuesJson);
			// this.session.setUpdated(true);
			this.session.getDebugController().refreshVariables();
		}
	}

	private void sendOnBreakLineChange(String path, Integer row) {
		DebuggerActionCommander commander = getDebuggerActionCommander();
		LinebreakMetadata currentLineBreak = new LinebreakMetadata(commander.getSessionId(), commander.getExecutionId(), commander.getUserId(), path,
				row);
				// Gson gson = new Gson();
				// String variableValuesJson = gson.toJson(breakLine);
				// send(DebugConstants.VIEW_ON_LINE_CHANGE, variableValuesJson);

		// this.session.setUpdated(true);
		this.session.setCurrentLineBreak(currentLineBreak);
		this.session.getDebugController().onLineChange(currentLineBreak, this.session);
	}

	// private void sendBreakpointsMetadata() {
	// Set<BreakpointMetadata> breakpoints = debuggerActionCommander.getBreakpoints();
	// DebuggerActionCommander commander = getDebuggerActionCommander();
	// BreakpointsMetadata metadata = new BreakpointsMetadata(commander.getSessionId(),
	// commander.getExecutionId(), commander.getUserId(), breakpoints);
	//// String json = new Gson().toJson(metadata);
	//// send(DebugConstants.VIEW_BREAKPOINT_METADATA, json);
	//
	//
	// }

	public DebuggerActionCommander getDebuggerActionCommander() {
		return debuggerActionCommander;
	}

	private void logError(String message) {
		logger.error(message);
	}

	private void logDebug(String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}
}
