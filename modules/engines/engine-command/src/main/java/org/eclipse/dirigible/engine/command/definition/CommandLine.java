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

/**
 * The Class CommandLine.
 */
public class CommandLine {
	
	/** The os. */
	private String os;
	
	/** The command. */
	private String command;
	
	/**
	 * Instantiates a new command line.
	 */
	public CommandLine() {
		super();
	}
	
	/**
	 * Instantiates a new command line.
	 *
	 * @param os the os
	 * @param command the command
	 */
	public CommandLine(String os, String command) {
		super();
		this.os = os;
		this.command = command;
	}
	
	/**
	 * Gets the os.
	 *
	 * @return the os
	 */
	public String getOs() {
		return os;
	}
	
	/**
	 * Sets the os.
	 *
	 * @param os the new os
	 */
	public void setOs(String os) {
		this.os = os;
	}
	
	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Sets the command.
	 *
	 * @param command the new command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

}
