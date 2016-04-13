package org.eclipse.dirigible.runtime.js.debug;

import org.eclipse.dirigible.repository.ext.debug.DebugSessionModel;
import org.eclipse.dirigible.repository.ext.debug.IDebugController;
import org.eclipse.dirigible.repository.ext.debug.LinebreakMetadata;

public class WebSocketDebugController implements IDebugController {

	private String user;

	public WebSocketDebugController(String user) {
		this.user = user;
	}

	@Override
	public void register(DebugSessionModel session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finish(DebugSessionModel session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLineChange(LinebreakMetadata linebreak, DebugSessionModel session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshBreakpoints() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepInto() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepOver() {
		// TODO Auto-generated method stub

	}

	@Override
	public void continueExecution() {
		// TODO Auto-generated method stub

	}

	@Override
	public void skipAllBreakpoints() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBreakpoint(String path, int row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearBreakpoint(String path, int row) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllBreakpoints() {
		// TODO Auto-generated method stub

	}

}
