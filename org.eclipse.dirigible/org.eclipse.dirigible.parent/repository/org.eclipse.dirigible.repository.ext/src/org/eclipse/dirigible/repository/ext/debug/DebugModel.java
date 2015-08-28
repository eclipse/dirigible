package org.eclipse.dirigible.repository.ext.debug;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DebugModel {
	
	public static final String DEBUG_MODEL = "debug.model";
	
	private IDebugController debugController;
	
	private BreakpointsMetadata breakpointsMetadata;
	
	private List<DebugSessionModel> sessions;
	
	private DebugSessionModel activeSession;
	
	public DebugModel(IDebugController debugController) {
		this.debugController = debugController;
		this.breakpointsMetadata = new BreakpointsMetadata(this);
		this.sessions = new ArrayList<DebugSessionModel>();
	}
	
	public IDebugController getDebugController() {
		return debugController;
	}
	
	public void setDebugController(IDebugController debugController) {
		this.debugController = debugController;
	}
	
	public DebugSessionModel createSession() {
		DebugSessionModel session = new DebugSessionModel(this);
		this.sessions.add(session);
		return session;
	}
	
	public void removeSession(DebugSessionModel session) {
		session.release();
		this.sessions.remove(session);
	}
	
	public List<DebugSessionModel> getSessions() {
		return sessions;
	}
	
	public DebugSessionModel getActiveSession() {
		return activeSession;
	}
	
	public void setActiveSession(DebugSessionModel activeSession) {
		this.activeSession = activeSession;
	}
	
	public DebugSessionModel getSessionByExecutionId(String executionId) {
		for (Iterator<DebugSessionModel> iterator = sessions.iterator(); iterator.hasNext();) {
			DebugSessionModel debugSessionModel = iterator.next();
			if (debugSessionModel.getExecutionId() != null
					&& debugSessionModel.getExecutionId().equals(executionId)) {
				return debugSessionModel;
			}
		}
		return null;
	}

	public BreakpointsMetadata getBreakpointsMetadata() {
		return breakpointsMetadata;
	}

	public void setBreakpointsMetadata(BreakpointsMetadata breakpointMetadata) {
		this.breakpointsMetadata = breakpointMetadata;
	}
}
