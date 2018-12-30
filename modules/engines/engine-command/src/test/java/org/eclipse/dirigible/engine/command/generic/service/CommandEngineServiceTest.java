/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.command.generic.service;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.engine.command.definition.CommandDefinition;
import org.eclipse.dirigible.engine.command.definition.CommandLine;
import org.junit.Test;

/**
 * The Class CommandEngineServiceTest.
 */
public class CommandEngineServiceTest {

	
	@Test
	public void commandTest() {
		CommandDefinition commandDefinition = new CommandDefinition();
		commandDefinition.setDescription("command description");
		commandDefinition.setContentType("text/plain");
		commandDefinition.getCommands().add(new CommandLine("Linux", "uname"));
		commandDefinition.getSet().put("var1", "val1");
		commandDefinition.getUnset().add("var2");
		
		String json = GsonHelper.GSON.toJson(commandDefinition);
		System.out.println(json);
		
	}


}
