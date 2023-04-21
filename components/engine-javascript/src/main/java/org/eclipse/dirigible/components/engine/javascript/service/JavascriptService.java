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
package org.eclipse.dirigible.components.engine.javascript.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.dirigible.components.base.http.access.UserRequestVerifier;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.modules.DirigibleSourceProvider;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * The Class JavascriptHandler.
 */
@Service
public class JavascriptService implements InitializingBean {
	
	/** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(JavascriptService.class);

    /** The dirigible source provider. */
    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();
    
    /** The instance. */
    private static JavascriptService INSTANCE;
    
    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    /**
     * Gets the.
     *
     * @return the javascript service
     */
    public static JavascriptService get() {
        return INSTANCE;
    }

    /**
     * Handle request.
     *
     * @param projectName the project name
     * @param projectFilePath the project file path
     * @param projectFilePathParam the project file path param
     * @param parameters the parameters
     * @param debug the debug
     * @return the object
     */
    public Object handleRequest(String projectName, String projectFilePath, String projectFilePathParam, Map<Object, Object> parameters, boolean debug) {
        try {
            if (UserRequestVerifier.isValid()) {
            	UserRequestVerifier.getRequest().setAttribute("dirigible-rest-resource-path", projectFilePathParam);
            }

            String sourceFilePath = Path.of(projectName, projectFilePath).toString();
			String maybeJSCode = dirigibleSourceProvider.getSource(sourceFilePath);
            if (maybeJSCode == null) {
                throw new IOException("JavaScript source code for project name '" + projectName + "' and file name '" + projectFilePath + "' could not be found, consider publishing it.");
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
        	if (logger.isErrorEnabled()) {
        		if (e.getMessage() == null) {
        			logger.error("Null object has been found");
        			return e.getMessage();
        		} else if (e.getMessage().contains("consider publish")) {
        			logger.error(e.getMessage());
        			return e.getMessage();
        		} else {
        			logger.error(e.getMessage(), e);
        			throw new RuntimeException(e);
        		}
        	}
        }
        return "";
    }
    
    /**
     * Handle callback.
     *
     * @param filePath the file path
     * @param parameters the parameters
     * @return the object
     */
    public Object handleCallback(String filePath, Map<Object, Object> parameters) {
    	if (filePath == null) {
    		throw new RuntimeException("Path to the file to be executed cannot be null");
    	}
    	Path path = Path.of(filePath);
    	if (path.getNameCount() > 1) {
    		return handleRequest(path.getRoot().toString(), path.subpath(1, path.getNameCount() - 1).toString(), null, parameters, false);
    	}
    	throw new RuntimeException("Path to the file to be executed must contain a parent folder");
    }

	/**
	 * Transform value.
	 *
	 * @param value the value
	 * @return the object
	 */
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
	
	/**
	 * Gets the source provider.
	 *
	 * @return the source provider
	 */
	public DirigibleSourceProvider getSourceProvider() {
		return dirigibleSourceProvider;
	}

}
