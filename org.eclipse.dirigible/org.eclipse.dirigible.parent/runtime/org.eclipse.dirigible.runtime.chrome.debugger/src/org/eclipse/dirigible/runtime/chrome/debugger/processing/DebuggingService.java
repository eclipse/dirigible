package org.eclipse.dirigible.runtime.chrome.debugger.processing;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.ext.debug.DebugManager;
import org.eclipse.dirigible.repository.ext.debug.IDebugController;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.chrome.debugger.DebugConfiguration;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Breakpoint;
import org.eclipse.dirigible.runtime.chrome.debugger.utils.URIUtils;

public class DebuggingService {

	private static final Logger LOGGER = Logger.getLogger(DebuggingService.class.getCanonicalName());

	private DebuggingService() {
	}

	public static void setBreakpointWithId(String userId, final String breakpointId) {
		getDebugController(userId).setBreakpoint(getBreakpointUrl(breakpointId), getBreakpointRow(breakpointId));
		LOGGER.debug(String.format("Setting breakpoint with id %s", breakpointId));
	}

	public static void removeBreakpoint(String userId, final String breakpointId) {
		getDebugController(userId).clearBreakpoint(getBreakpointUrl(breakpointId), getBreakpointRow(breakpointId));
		LOGGER.debug(String.format("Removing breakpoint with id %s", breakpointId));
	}

	private static void activate(final String breakpointId) {
		LOGGER.debug(String.format("Activating breakpoint with id %s", breakpointId));
	}

	private static void deactivate(final String breakpointId) {
		LOGGER.debug(String.format("Deactivating breakpoint with id %s", breakpointId));
	}

	public static void stepOver(String userId, int breakpointLine) {
		getDebugController(userId).stepOver();
		LOGGER.debug(String.format("Stepping into breakpoint with id "));
	}

	public static void stepOut(String userId, int breakpointLine) {
		//TODO: no stepOut in Rhino
	}

	public static void stepInto(String userId, int breakpointLine) {
		getDebugController(userId).stepInto();
	}

	public static void activateAllBreakpoints(String userId) {
		final BreakpointRepository repo = BreakpointRepository.getInstance();
		Set<Breakpoint> breakpoints = repo.getUserBreakpoints(userId);
		activateAllBreakpoints(breakpoints);
	}

	private static void activateAllBreakpoints(Set<Breakpoint> userBreakpoints) {
		for (final Breakpoint b : userBreakpoints) {
			DebuggingService.activate(b.getId());
		}
	}

	public static void deactivateAllBreakpoints(String userId) {
		final BreakpointRepository repo = BreakpointRepository.getInstance();
		Set<Breakpoint> breakpoints = repo.getUserBreakpoints(userId);
		deactivateAll(breakpoints);
	}

	private static void deactivateAll(Set<Breakpoint> userBreakpoints) {
		for (final Breakpoint b : userBreakpoints) {
			DebuggingService.deactivate(b.getId());
		}
	}
	
	private static IDebugController getDebugController(String userId) {
		return DebugManager.getDebugModel(userId).getDebugController();
	}
	
	private static String getBreakpointUrl(String breakpointId){
		String urlPattern = ".+.js";
		Pattern compile = Pattern.compile(urlPattern);
		Matcher matcher = compile.matcher(breakpointId);

		if(matcher.find()){
			return matcher.group(0);
		}
		return null;
	}
	
	private static Integer getBreakpointRow(String breakpointId){
		String breakpointUrl = getBreakpointUrl(breakpointId);
		String rowAndCol = breakpointId.substring(breakpointUrl.length(), breakpointId.length());
		String rowPattern = ":\\d+:";
		Matcher matcher = Pattern.compile(rowPattern).matcher(rowAndCol);
		String rowWithCollons;
		if(matcher.find()){
			rowWithCollons = matcher.group(0);
			
			String row = rowWithCollons.replace(":", "");
			return Integer.valueOf(row);
		}
		return null;
	}

	public static void updateSource(String url, String source) {
		Map<String, List<IResource>> resources = DebugConfiguration.getResources();
		for(Map.Entry<String, List<IResource>> e : resources.entrySet()){
			for(IResource res : e.getValue()){
				if(url.equals(URIUtils.getUrlForResource(res))){
					try {
						IRepository repository = res.getRepository();
						res.setContent(source.getBytes());
						repository.createResource(res.getPath(), res.getContent());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		DebugConfiguration.setResources(resources);
	}
}
