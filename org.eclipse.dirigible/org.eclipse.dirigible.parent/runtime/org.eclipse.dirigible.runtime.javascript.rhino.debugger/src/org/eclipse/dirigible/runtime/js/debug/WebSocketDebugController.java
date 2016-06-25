package org.eclipse.dirigible.runtime.js.debug;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.dirigible.repository.ext.debug.BreakpointMetadata;
import org.eclipse.dirigible.repository.ext.debug.DebugModel;
import org.eclipse.dirigible.repository.ext.debug.DebugModelFacade;
import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.IDebugController;
import org.eclipse.dirigible.repository.ext.debug.LinebreakMetadata;
import org.eclipse.dirigible.repository.logging.Logger;

public class WebSocketDebugController implements IDebugController {

	private String user;
	private static final Logger LOGGER = Logger.getLogger(WebSocketDebugController.class);

	public WebSocketDebugController(String user) {
		this.user = user;
	}

	@Override
	public void register(DebugSessionModel session) {
		
	}

	@Override
	public void finish(DebugSessionModel session) {

	}

	@Override
	public void onLineChange(LinebreakMetadata linebreak, DebugSessionModel session) {

	}

	@Override
	public void refreshVariables() {
	}

	@Override
	public void refreshBreakpoints() {
	}

	@Override
	public void refresh() {
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
	public void clearBreakpoint(String path, int row) {
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
	public void clearAllBreakpoints() {
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
