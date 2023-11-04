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

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.junit.jupiter.api.Test;

/**
 * The Class CommandTest.
 */
public class CommandTest {

	/**
	 * Command test.
	 */
	@Test
	public void commandTest() {
		Command commandDefinition = new Command();
		commandDefinition.setDescription("command description");
		commandDefinition.setContentType("text/plain");
		commandDefinition.getCommands().add(new CommandLine("Linux", "uname"));
		commandDefinition.getSet().put("var1", "val1");
		commandDefinition.getUnset().add("var2");
		
		String json = GsonHelper.toJson(commandDefinition);
		System.out.println(json);
	}

}
