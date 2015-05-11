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

package org.eclipse.dirigible.cli;

import java.util.Properties;

import org.eclipse.dirigible.cli.commands.ICommand;
import org.eclipse.dirigible.cli.commands.ImportProjectCommand;
import org.eclipse.dirigible.cli.utils.CommonProperties;
import org.eclipse.dirigible.cli.utils.Utils;

public class CLI implements CommonProperties.CLI {

	public static void main(String[] args) throws Exception {
		Properties properties = Utils.loadCLIProperties(args);
		ICommand command = getCommand(properties);
		run(command, properties);
	}

	private static ICommand getCommand(Properties properties) {
		ICommand command = null;
		String commandName = properties.getProperty(COMMAND);
		if (COMMAND_IMPORT.equals(commandName)) {
			command = new ImportProjectCommand();
		}
		return command;
	}

	private static void run(ICommand command, Properties properties) throws Exception {
		if(command != null) {
			command.execute(properties);
		} else {
			String message = String.format("Command \"%s\" does not exsist", properties.getProperty(COMMAND));
			throw new IllegalArgumentException(message);
		}
	}
}
