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
package org.eclipse.dirigible.services.spring.boot.picocli;

import org.eclipse.dirigible.runtime.core.embed.EmbeddedDirigible;
import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class DirigibleCLIApplication implements CommandLineRunner {
	
	static EmbeddedDirigible DIRIGIBLE = new EmbeddedDirigible();
	static {
		DirigibleInitializer initializer = DIRIGIBLE.initialize();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(DirigibleCLIApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DirigibleCLIApplication.class, args);
    }
    
    private ExecuteCommand javaScriptCommand;

    @Autowired
    public DirigibleCLIApplication(ExecuteCommand javaScriptCommand) {
        this.javaScriptCommand = javaScriptCommand;
    }

    @Override
    public void run(String... args) {
        CommandLine commandLine = new CommandLine(javaScriptCommand);

        commandLine.parseWithHandler(new CommandLine.RunLast(), args);
    }

}