/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.debugger;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.engine.js.debug.model.BreakpointMetadata;
import org.eclipse.dirigible.engine.js.debug.model.BreakpointsMetadata;
import org.eclipse.dirigible.engine.js.debug.model.DebugModel;
import org.eclipse.dirigible.engine.js.debug.model.DebugModelFacade;
import org.eclipse.dirigible.engine.js.debug.model.DebugSessionModel;
import org.eclipse.dirigible.engine.js.debug.model.IDebugController;
import org.eclipse.dirigible.engine.js.debug.model.LinebreakMetadata;
import org.eclipse.dirigible.engine.js.debug.model.VariableValuesMetadata;
import org.slf4j.LoggerFactory;

public class RhinoJavascriptDebugController implements IDebugController {

	private String user;
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RhinoJavascriptDebugController.class);

	public RhinoJavascriptDebugController(String user) {
		this.user = user;
	}

	@Override
	public void register(DebugSessionModel session) {
		String userId = UserFacade.getName();
		RhinoJavascriptDebugSender.sendRegisterSession(userId, session);
	}

	@Override
	public void finish(DebugSessionModel session) {
		String userId = UserFacade.getName();
		RhinoJavascriptDebugSender.sendFinishSession(userId, session);
	}

	@Override
	public void onLineChange(LinebreakMetadata linebreak, DebugSessionModel session) {
		String userId = UserFacade.getName();
		RhinoJavascriptDebugSender.sendLineBreak(userId, session, linebreak);
	}

	@Override
	public void refreshVariables() {
		String userId = UserFacade.getName();
		VariableValuesMetadata variables = getDebugModel().getActiveSession().getVariableValuesMetadata();
		RhinoJavascriptDebugSender.sendVariables(userId, getDebugModel().getActiveSession(), variables);
	}

	@Override
	public void refreshBreakpoints() {
		String userId = UserFacade.getName();
		BreakpointsMetadata breakpoints = getDebugModel().getBreakpointsMetadata();
		RhinoJavascriptDebugSender.sendBreakpoints(userId, breakpoints);
	}

	@Override
	public void stepInto() {
		if (checkDebugExecutor()) {
			getDebugModel().getActiveSession().getDebugExecutor().stepInto();
		}
	}

	@Override
	public void stepOver() {
		if (checkDebugExecutor()) {
			getDebugModel().getActiveSession().getDebugExecutor().stepOver();
		}
	}

	@Override
	public void continueExecution() {
		if (checkDebugExecutor()) {
			getDebugModel().getActiveSession().getDebugExecutor().continueExecution();
		}
	}

	@Override
	public void skipAllBreakpoints() {
		if (checkDebugExecutor()) {
			getDebugModel().getActiveSession().getDebugExecutor().skipAllBreakpoints();
		}
	}

	@Override
	public void setBreakpoint(String path, int row) {
		BreakpointMetadata breakpoint = new BreakpointMetadata(path, row);
		getDebugModel().getBreakpointsMetadata().getBreakpoints().add(breakpoint);
	}

	@Override
	public void removeBreakpoint(String path, int row) {
		BreakpointMetadata breakpoint = new BreakpointMetadata(path, row);
		Set<BreakpointMetadata> breakpoints = getDebugModel().getBreakpointsMetadata().getBreakpoints();
		for (Iterator<BreakpointMetadata> iterator = breakpoints.iterator(); iterator.hasNext();) {
			BreakpointMetadata breakpointMetadata = (BreakpointMetadata) iterator.next();
			if (breakpointMetadata.equals(breakpoint)) {
				iterator.remove();
				break;
			}
		}
	}

	@Override
	public void removeAllBreakpoints() {
		getDebugModel().getBreakpointsMetadata().getBreakpoints().clear();
	}

	private DebugModel getDebugModel() {
		return DebugModelFacade.getDebugModel(this.user);
	}

	private boolean checkDebugExecutor() {
		if (getDebugModel().getActiveSession() == null) {
			LOGGER.error("No active debug session");
			return false;
		}
		if (getDebugModel().getActiveSession().getDebugExecutor() == null) {
			LOGGER.error("Active debug session exists, but there is no executor assigned");
			return false;
		}
		return true;
	}
}
