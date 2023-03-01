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
package org.eclipse.dirigible.graalium.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.engine.js.service.JavascriptHandler;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.modules.DirigibleSourceProvider;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GraaliumJavascriptHandler.
 */
public class GraaliumJavascriptHandler implements JavascriptHandler {
	
	/** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(GraaliumJavascriptHandler.class);

    /** The dirigible source provider. */
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    /**
     * Handle request.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param debug the debug
     * @return the object
     */
    @Override
    public Object handleRequest(String projectName, String projectFilePath, String projectFilePathParam, Map<Object, Object> parameters, boolean debug) {
        try {
            if (HttpRequestFacade.isValid()) {
                HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, projectFilePathParam);
            }

            String sourceFilePath = Path.of(projectName, projectFilePath).toString();
			String maybeJSCode = dirigibleSourceProvider.getSource(sourceFilePath);
            if (maybeJSCode == null) {
                throw new IOException("JavaScript source code for project name '" + projectName + "' and file name '" + projectFilePath + " could not be found");
            }

            Path absoluteSourcePath = dirigibleSourceProvider.getAbsoluteSourcePath(projectName, projectFilePath);
            try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner(parameters, debug)) {
            	Source source = runner.prepareSource(absoluteSourcePath);
            	runner.getGraalJSInterceptor().onBeforeRun(sourceFilePath, absoluteSourcePath, source, runner.getCodeRunner().getGraalContext());
            	Value value = runner.run(source);
            	runner.getGraalJSInterceptor().onAfterRun(sourceFilePath, absoluteSourcePath, source, runner.getCodeRunner().getGraalContext(), value);
            	return transformValue(value);
            }
        } catch (Exception e) {
        	if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
            throw new RuntimeException(e);
        }
    }

	private Object transformValue(Value value) {
		if (value.isBoolean()) {
			return value.asBoolean();
		} else if (value.isDate()) {
			return value.asDate();
		} else if (value.isDuration()) {
			return value.asDuration();
		} else if (value.isNull()) {
			return null;
		} else if (value.isNumber()) {
			if (value.fitsInDouble()) {
				return value.asDouble();
			} else if (value.fitsInFloat()) {
				return value.asFloat();
			} else if (value.fitsInLong()) {
				return value.asLong();
			} else if (value.fitsInInt()) {
				return value.asInt();
			} else if (value.fitsInShort()) {
				return value.asShort();
			} else if (value.fitsInByte()) {
				return value.asByte();
			}
		} else if (value.isString()) {
			return value.asString();
		} else if (value.isTime()) {
			return value.asTime();
		} else if (value.isTimeZone()) {
			return value.asTimeZone();
		}
		return null;
	}
	
	public DirigibleSourceProvider getSourceProvider() {
		return dirigibleSourceProvider;
	}
}
