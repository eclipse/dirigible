/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.command.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandDefinition {
	
	private String description;
	private String contentType;
	private List<CommandLine> commands = new ArrayList<CommandLine>();
	private Map<String, String> set = new HashMap<String, String>();
	private List<String> unset = new ArrayList<String>();
	
	private CommandLine targetCommand;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<CommandLine> getCommands() {
		return commands;
	}

	public Map<String, String> getSet() {
		return set;
	}

	public List<String> getUnset() {
		return unset;
	}

	public CommandLine getTargetCommand() {
		return targetCommand;
	}
	
	public void setTargetCommand(CommandLine targetCommand) {
		this.targetCommand = targetCommand;
	}
	
	public void validate() throws IllegalArgumentException {
		if (this.getCommands().size() == 0) {
			throw new IllegalArgumentException("Commands array is empty. Set appropriate command per target OS");
		}
		
		String os = System.getProperty("os.name").toLowerCase();
		for (Iterator<CommandLine> iterator = this.getCommands().iterator(); iterator.hasNext();) {
			CommandLine commandLine = iterator.next();
			if (os.startsWith(commandLine.getOs().toLowerCase())) {
				this.setTargetCommand(commandLine);
				break;
			}
		}
		
		if (this.getTargetCommand() == null) {
			throw new IllegalArgumentException(String.format("There is no command for your OS: %s", os));
		}
		
	}

}
