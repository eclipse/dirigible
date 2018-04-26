/*
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.engine.js.rhino.debugger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.engine.js.debug.model.BreakpointMetadata;
import org.eclipse.dirigible.engine.js.debug.model.DebugModel;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionMetadata;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionModel;
import org.eclipse.dirigible.engine.js.debug.model.IDebugExecutor.DebugCommand;
import org.eclipse.dirigible.engine.js.debug.model.LinebreakMetadata;
import org.eclipse.dirigible.engine.js.debug.model.VariableValue;
import org.eclipse.dirigible.engine.js.debug.model.VariableValuesMetadata;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class RhinoJavascriptDebugFrame implements DebugFrame {

	private static final String ILLEGAL = "illegal";

	private static final String NULL = "null";

	private static final String NATIVE = "native";

	private static final String FUNCTION = "function";

	private static final String UNDEFINED = "undefined";

	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptDebugFrame.class);

	private static final int SLEEP_TIME = 50;
	// private DebuggerActionManager debuggerActionManager;
	private RhinoJavascriptDebugActionCommander debuggerActionCommander;
	private Stack<DebuggableScript> scriptStack;
	private Stack<Scriptable> activationStack;
	private int stepOverLineNumber = 0;
	private int previousLineNumber = 0;
	private boolean stepOverFinished = true;
	private DebugModel debugModel;
	// private VariableValuesMetadata variableValuesMetadata;

	private DebugSessionModel session;

	public RhinoJavascriptDebugFrame(DebugModel debugModel, HttpServletRequest request, RhinoJavascriptDebugger javaScriptDebugger) {
		// get the instance of debugger action manager from the session

		logDebug("entering constructor");

		// this.debuggerActionManager = DebuggerActionManager.getInstance(request.getSession());

		this.debugModel = debugModel;

		// create a new instance of commander per frame
		String sessionId = request.getSession().getId();
		String executionId = UUID.randomUUID().toString();
		String userId = UserFacade.getName(request);

		this.session = this.debugModel.createSession();

		this.debuggerActionCommander = new RhinoJavascriptDebugActionCommander(this.session, sessionId, executionId, userId);

		this.session.setDebugExecutor(getDebuggerActionCommander());

		this.debuggerActionCommander.init();
		this.debuggerActionCommander.setDebugFrame(this);
		this.debuggerActionCommander.setDebugger(javaScriptDebugger);

		this.scriptStack = new Stack<DebuggableScript>();
		this.activationStack = new Stack<Scriptable>();

		this.debugModel.getDebugController().register(session);

		// registerDebugFrame();

		logDebug("exiting constructor");
	}

	public DebugModel getDebugModel() {
		return debugModel;
	}

	// private void registerDebugFrame() {
	//// logDebug("entering registerDebugFrame");
	//// String commandBody = new Gson().toJson(new DebugSessionMetadata(
	//// getDebuggerActionCommander().getSessionId(), getDebuggerActionCommander()
	//// .getExecutionId(), getDebuggerActionCommander().getUserId()));
	//// send(DebugConstants.VIEW_REGISTER, commandBody);
	//
	// this.session = this.debugModel.createSession();
	// session.setDebugExecutor(getDebuggerActionCommander());
	//
	// logDebug("exiting registerDebugFrame");
	// }

	@Override
	public void onEnter(Context context, Scriptable activation, Scriptable thisObj, Object[] args) {
		logDebug("entering onEnter()");
		DebuggableScript script = (DebuggableScript) context.getDebuggerContextData();
		scriptStack.push(script);
		activationStack.push(activation);
		logDebug("exiting onEnter()");
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
		logDebug("entering onExit()");
		scriptStack.pop();
		activationStack.pop();
		if (scriptStack.isEmpty()) {
			this.debuggerActionCommander.clean();
			RhinoJavascriptDebugActionCommander commander = getDebuggerActionCommander();
			DebugSessionMetadata metadata = new DebugSessionMetadata(commander.getSessionId(), commander.getExecutionId(), commander.getUserId());

			// clear variables for the UI
			clearVariables();
			// // clear breakpoints for the UI
			// clearBreakpoints();
			// remove the session
			finishDebugSession(metadata);
		}
		logDebug("exiting onExit()");
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
		logDebug("entering hitBreakpoint(): " + lineNumber);
		print(lineNumber);
		debuggerActionCommander.stepOver();
		debuggerActionCommander.pauseExecution();
		logDebug("exiting hitBreakpoint()");
	}

	private void stepInto(int lineNumber) {
		logDebug("entering stepInto(): " + lineNumber);
		print(lineNumber);
		debuggerActionCommander.pauseExecution();
		logDebug("exiting stepInto()");
	}

	private void stepOver(int lineNumber) {
		logDebug("entering stepOver(): " + lineNumber);
		if (stepOverFinished) {
			stepOverFinished = false;
			stepOverLineNumber = previousLineNumber;
		}
		if ((stepOverLineNumber + 1) == lineNumber) {
			stepOverFinished = true;
			stepOverLineNumber = -1;
			stepInto(lineNumber);
		}
		logDebug("entering stepOver()");
	}

	private void blockExecution() {
		logDebug("entering blockExecution()");
		while (!debuggerActionCommander.isExecuting() && (getNextCommand() != DebugCommand.CONTINUE)
				&& (getNextCommand() != DebugCommand.SKIP_ALL_BREAKPOINTS)) {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		logDebug("exiting blockExecution()");
	}

	private DebugCommand getNextCommand() {
		return debuggerActionCommander.getCommand();
	}

	private boolean isBreakpoint(int row) {
		String path = scriptStack.peek().getSourceName();
		if (!path.startsWith(IRepositoryStructure.SEPARATOR)) {
			path = IRepositoryStructure.SEPARATOR + path;
		}
		RhinoJavascriptDebugActionCommander commander = getDebuggerActionCommander();
		LinebreakMetadata breakpoint = new LinebreakMetadata(commander.getSessionId(), commander.getExecutionId(), commander.getUserId(), path, row);
		Set<BreakpointMetadata> breakpoints = debuggerActionCommander.getBreakpoints();
		return breakpoints.contains(breakpoint.getBreakpoint());
	}

	private void print(int row) {
		RhinoJavascriptDebugActionCommander commander = getDebuggerActionCommander();
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
			} catch (Throwable error) {
				result = ILLEGAL;
			}
		} else {
			result = NULL;
		}
		return result;
	}

	private void notifyVariableValuesMetadata() {
		if (this.session.getVariableValuesMetadata() != null) {
			this.session.getDebugController().refreshVariables();
		}
	}

	private void sendOnBreakLineChange(String path, Integer row) {
		RhinoJavascriptDebugActionCommander commander = getDebuggerActionCommander();
		LinebreakMetadata currentLineBreak = new LinebreakMetadata(commander.getSessionId(), commander.getExecutionId(), commander.getUserId(), path,
				row);
		this.session.setCurrentLineBreak(currentLineBreak);
		this.session.getDebugController().onLineChange(currentLineBreak, this.session);
	}

	public RhinoJavascriptDebugActionCommander getDebuggerActionCommander() {
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
