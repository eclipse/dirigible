package org.eclipse.dirigible.runtime.chrome.debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Location;
import org.eclipse.dirigible.runtime.chrome.debugger.models.Variable;

public class DebugConfiguration {

	// UserId : Location
	private static Map<String, Location> currentExecutionLocation = new HashMap<String, Location>(); 
	// Project name : List<Scripting Services>
	private static Map<String, List<IResource>> resources = new HashMap<String, List<IResource>>(); 
	// < userId : < objectId : List<Variables> > >
	private static Map<String, Map<String, List<Variable>>> userVariablesForObjectId = new HashMap<String, Map<String, List<Variable>>>();
	private static String baseRepositoryUrl;
	
	private DebugConfiguration() {
	}
	
	public static Map<String, List<Variable>> getUserVariablesForObjectId(String userId) {
		return userVariablesForObjectId.get(userId);
	}

	public static void setUserVablesForObjectId(String userId, String objectId, List<Variable> variablesForObjectId){
		Map<String, List<Variable>> userObjectIdVars = userVariablesForObjectId.get(userId);
		if(userObjectIdVars == null){
			userObjectIdVars = new HashMap<String, List<Variable>>();
		}
		List<Variable> objectIdVars = userObjectIdVars.get(objectId);
		if(objectIdVars == null){
			objectIdVars = new ArrayList<Variable>();
		}
		objectIdVars.addAll(variablesForObjectId);
		userObjectIdVars.put(objectId, objectIdVars);
		userVariablesForObjectId.put(userId, userObjectIdVars);
	}
	
	public static void setUserVariablesForObjectId(Map<String, Map<String, List<Variable>>> userVariablesForObjectId) {
		DebugConfiguration.userVariablesForObjectId = userVariablesForObjectId;
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
