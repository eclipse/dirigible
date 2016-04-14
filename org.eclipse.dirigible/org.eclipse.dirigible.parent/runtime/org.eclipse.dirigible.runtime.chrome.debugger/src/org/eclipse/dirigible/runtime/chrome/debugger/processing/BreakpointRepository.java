package org.eclipse.dirigible.runtime.chrome.debugger.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.dirigible.runtime.chrome.debugger.models.Breakpoint;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;

public class BreakpointRepository {

	private static BreakpointRepository INSTANCE = new BreakpointRepository();
	private final Map<String, Set<Breakpoint>> breakpoints = new HashMap<String, Set<Breakpoint>>(); // userId : breakpoints

	private BreakpointRepository() {

	}

	public static synchronized BreakpointRepository getInstance() {
		return INSTANCE;
	}

	public void add(String userId, final Breakpoint breakpoint) {
		Set<Breakpoint> userBreakpoints = getUserBreakpoints(userId);
		if (userBreakpoints == null) {
			userBreakpoints = new HashSet<Breakpoint>();
		}
		userBreakpoints.add(breakpoint);
		this.breakpoints.put(userId, userBreakpoints);
		DebuggingService.setBreakpointWithId(userId, breakpoint.getId());
	}

	public Breakpoint getById(String userId, final String breakpointId) {
		Set<Breakpoint> userBreakpoints = getUserBreakpoints(userId);
		if (userBreakpoints == null) {
			return null;
		}
		for (final Breakpoint b : userBreakpoints) {
			if (b.getId().equals(breakpointId)) {
				return b;
			}

		}
		return null;

	}

	public Map<String, Set<Breakpoint>> getBreakpoints() {
		return this.breakpoints;
	}
	
	public Set<Breakpoint> getUserBreakpoints(String userId){
		return this.breakpoints.get(userId);
	}

	public void removeById(String userId, final String breakpointId) {
		Set<Breakpoint> userBreakpoints = getUserBreakpoints(userId);
		if(userBreakpoints == null){
			return;
		}
		final Iterator<Breakpoint> iterator = userBreakpoints.iterator();
		while (iterator.hasNext()) {
			final Breakpoint breakpoint = iterator.next();
			if (breakpoint.getId().equals(breakpointId)) {
				iterator.remove();
			}
		}

		DebuggingService.removeBreakpoint(userId, breakpointId);
	}

	public Set<Breakpoint> getBreakpointsForScript(String userId, final String scriptId) {
		Set<Breakpoint> userBreakpoints = getUserBreakpoints(userId);
		final Set<Breakpoint> result = new HashSet<Breakpoint>();
		if (scriptId == null || userBreakpoints == null) {
			return result;
		}
		for (final Breakpoint b : userBreakpoints) {
			final Location location = b.getLocation();
			if (location != null) {
				final String bScriptId = location.getScriptId();
				if (scriptId.equals(bScriptId)) {
					result.add(b);
				}
			}
		}
		return result;
	}

	public Set<Breakpoint> getSortedBreakpointsForScript(String userId, String scriptId) {
		Set<Breakpoint> breakpointsForScript = getBreakpointsForScript(userId, scriptId);
		return new TreeSet<Breakpoint>(breakpointsForScript);
	}

	public List<Location> getBreakpointLocationsForScript(String userId, String scriptId) {
		Set<Breakpoint> breakpointsForScript = getBreakpointsForScript(userId, scriptId);
		List<Location> locations = new ArrayList<Location>();
		for (Breakpoint b : breakpointsForScript) {
			locations.add(b.getLocation());
		}
		return locations;
	}
}
