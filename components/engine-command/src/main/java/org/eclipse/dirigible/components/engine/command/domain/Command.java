/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.command.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Class CommandDefinition.
 */
public class Command {
	
	/** The description. */
	private String description;
	
	/** The content type. */
	private String contentType;
	
	/** The commands. */
	private List<CommandLine> commands = new ArrayList<CommandLine>();
	
	/** The set. */
	private Map<String, String> set = new HashMap<String, String>();
	
	/** The unset. */
	private List<String> unset = new ArrayList<String>();
	
	/** The target command. */
	private CommandLine targetCommand;

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the new content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the commands.
	 *
	 * @return the commands
	 */
	public List<CommandLine> getCommands() {
		return commands;
	}

	/**
	 * Gets the sets the.
	 *
	 * @return the sets the
	 */
	public Map<String, String> getSet() {
		return set;
	}

	/**
	 * Gets the unset.
	 *
	 * @return the unset
	 */
	public List<String> getUnset() {
		return unset;
	}

	/**
	 * Gets the target command.
	 *
	 * @return the target command
	 */
	public CommandLine getTargetCommand() {
		return targetCommand;
	}
	
	/**
	 * Sets the target command.
	 *
	 * @param targetCommand the new target command
	 */
	public void setTargetCommand(CommandLine targetCommand) {
		this.targetCommand = targetCommand;
	}
	
	/**
	 * Validate.
	 *
	 * @throws IllegalArgumentException the illegal argument exception
	 */
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
