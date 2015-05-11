/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.command;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommandDataParser {
	
	/**

	{
	    "description":"test command printing the os name and version",
	    "contentType":"plain/text",
	    "useContent":"false",
	    "workDir":".",
	    "commands": 
	        [
	            {
	                "osName": "Linux",
	                "command":"bash -c \"cat /proc/version\""
	            }
	            ,
	            {
	                "osName": "Windows",
	                "command":"cmd /c \"systeminfo\""
	            }
	        ],
	    "envAdd":
	        [
	            {
	            	"name":"java.env1",
	                "value":"toBeAdded"
	            }
	        ],
	    "envRemove":
	        [
	            {
	                "name":"java.env2"
	            }
	        ]
	}

	 */
	
	public static CommandData parseCommandData(String commandSource) throws IllegalArgumentException {
		JsonParser parser = new JsonParser();
		JsonObject commandObject = (JsonObject) parser.parse(commandSource);
		CommandData commandData = new CommandData();
		commandData.setDescription(commandObject.get("description").getAsString());
		commandData.setContentType(commandObject.get("contentType").getAsString());
		commandData.setUseContent(commandObject.get("useContent").getAsBoolean());
		commandData.setWorkDir(commandObject.get("workDir").getAsString());
		JsonArray commandsData = commandObject.get("commands").getAsJsonArray();
		Iterator<JsonElement> iter = commandsData.iterator();
		while (iter.hasNext()) {
			CommandLineData commandLineData = new CommandLineData();
			JsonObject object = (JsonObject) iter.next();
			commandLineData.setOsName(object.get("osName").getAsString());
			commandLineData.setCommand(object.get("command").getAsString());
			commandData.getCommands().add(commandLineData);
		}
		
		JsonArray envAddData = commandObject.get("envAdd").getAsJsonArray();
		iter = envAddData.iterator();
		while (iter.hasNext()) {
			JsonObject object = (JsonObject) iter.next();
			commandData.getEnvAdd().put(object.get("name").getAsString(), object.get("value").getAsString());
		}
		
		JsonArray envRemoveData = commandObject.get("envRemove").getAsJsonArray();
		iter = envRemoveData.iterator();
		while (iter.hasNext()) {
			JsonObject object = (JsonObject) iter.next();
			commandData.getEnvRemove().add(object.get("name").getAsString());
		}
		
		validateCommandData(commandData);
		
		return commandData;
	}

	private static void validateCommandData(CommandData commandData) throws IllegalArgumentException {
		if (commandData.getCommands().size() == 0) {
			throw new IllegalArgumentException("Commands array is empty. Set appropriate command per target OS");
		}
		
		String os = System.getProperty("os.name").toLowerCase();
		for (Iterator<CommandLineData> iterator = commandData.getCommands().iterator(); iterator.hasNext();) {
			CommandLineData commandLineData = iterator.next();
			if (os.startsWith(commandLineData.getOsName().toLowerCase())) {
				commandData.setTargetCommand(commandLineData);
				break;
			}
		}
		
		if (commandData.getTargetCommand() == null) {
			throw new IllegalArgumentException(String.format("There is no command for your OS: %s", os));
		}
		
	}

}
