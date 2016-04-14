package org.eclipse.dirigible.runtime.chrome.debugger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;

public class DebugConfiguration {

	private static Map<String, Location> currentExecutionLocation = new HashMap<String, Location>(); // UserId : Location
	private static Map<String, List<IResource>> resources; // Project name : List<Scripting Services>
	private static String baseRepositoryUrl;

	private DebugConfiguration() {
	}

	public static Location getCurrentExecutionLocation(String userId) {
		return currentExecutionLocation.get(userId);
	}

	public static void setCurrentExecutionLocation(String userId, final Location currentExecutionLocation) {
		DebugConfiguration.currentExecutionLocation.put(userId, currentExecutionLocation);
	}

	public static void setResources(Map<String, List<IResource>> resources) {
		DebugConfiguration.resources = resources;
	}

	public static Map<String, List<IResource>> getResources() {
		return resources;
	}

	public static String getBaseSourceUrl() {
		return baseRepositoryUrl;
	}

	public static void setBaseSourceUrl(String baseRepositoryURL) {
		DebugConfiguration.baseRepositoryUrl = baseRepositoryURL;
	}
}
