package org.eclipse.dirigible.repository.ext.debug;

public class LinebreakMetadata extends DebugSessionMetadata implements Comparable<BreakpointMetadata> {
	
	private BreakpointMetadata breakpoint;

	public LinebreakMetadata(String sessionId, String executionId, String userId, BreakpointMetadata breakpoint) {
		super(sessionId, executionId, userId);
		this.breakpoint = breakpoint;
	}

	public LinebreakMetadata(String sessionId, String executionId, String userId, String path, Integer row) {
		super(sessionId, executionId, userId);
		this.breakpoint = new BreakpointMetadata(path, row);
	}

	public BreakpointMetadata getBreakpoint() {
		return breakpoint;
	}
	
	public void setBreakpoint(BreakpointMetadata breakpoint) {
		this.breakpoint = breakpoint;
	}

	@Override
	public int compareTo(BreakpointMetadata o) {
		return this.getBreakpoint().compareTo(o);
	}
}
