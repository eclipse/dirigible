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

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.runtime.core.embed.EmbeddedDirigible;
import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.RunLast;


@Command(
  name = "execute",
  description = "Executes a Script Module"
)
@Component
public class ExecuteCommand implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(ExecuteCommand.class);
	
	/** The Constant DIRIGIBLE_REGISTRY_EXTERNAL_FOLDER. */
	public static final String DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE = "DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE"; //$NON-NLS-1$
	
	@Option(names = {"-w", "--workspace"})
	private String workspace;
	
	@Option(names = {"-f", "--file"})
	private String file;

	@Option(names = {"-c", "--context"})
	private String context;
	
	@Option(names = {"-t", "--type"})
	private String type;
	
	@Option(names = {"-e", "--exit"})
	private Boolean exit;
	
	public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new ExecuteCommand());
        
        commandLine.parseWithHandler(new RunLast(), args);
    }

    @Override
    public void run() {
    	if (workspace == null) {
    		workspace = Configuration.get(DIRIGIBLE_REGISTRY_IMPORT_WORKSPACE);
			if (workspace == null) {
				if (logger.isErrorEnabled()) {logger.error("Workspace parameter is missing\nUsage: -w or --workspace");}
	    		return;
			}
    	}
    	if (file == null) {
    		if (logger.isErrorEnabled()) {logger.error("File parameter is missing\nUsage: -f or --file");}
    		return;
    	}
    	Properties properties = new Properties();
    	if (context != null) {
    		StringTokenizer tokenizer = new StringTokenizer(context, "+");
    		while (tokenizer.hasMoreTokens()) {
    			String pair = tokenizer.nextToken();
    			int index = pair.indexOf("=");
				if (index > 0) {
    				properties.put(pair.substring(0, index), pair.substring(index + 1));
    			} else {
    				if (logger.isErrorEnabled()) {logger.error("Invalid context parameter\nUsage: name=value");}
    			}
    	     }
    	}
    	if (type == null) {
			type = "javascript";
		}
    	try {
    		DirigibleCLIApplication.DIRIGIBLE.load(workspace);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
    	try {
    		DirigibleCLIApplication.DIRIGIBLE.execute(type, file, properties);
		} catch (ScriptingException | ContextException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
		}
    	if (exit != null && exit) {
    		DirigibleCLIApplication.DIRIGIBLE.destroy();
	    	System.exit(0);
    	}
    }

}
