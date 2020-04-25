/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.rhino.debugger;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RhinoJavascriptDebugInvocationErrorReporter implements ErrorReporter {

	private static final String MESSAGE_SOURCE_AT_LINE_LINESOURCE_LINEOFFSET = "%s, source: %s, at line: %s, line source: %s, lineOffset: %s";

	private static final Logger logger = LoggerFactory.getLogger(RhinoJavascriptDebugInvocationErrorReporter.class);

	@Override
	public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
		logger.error(String.format(MESSAGE_SOURCE_AT_LINE_LINESOURCE_LINEOFFSET, message,
				sourceName, line, lineSource, lineOffset));
	}

	@Override
	public EvaluatorException runtimeError(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		logger.error(String.format(MESSAGE_SOURCE_AT_LINE_LINESOURCE_LINEOFFSET, message,
				sourceName, line, lineSource, lineOffset));
		return new EvaluatorException(message, sourceName, line);
	}

	@Override
	public void warning(String message, String sourceName, int line, String lineSource,
			int lineOffset) {
		logger.warn(String.format(MESSAGE_SOURCE_AT_LINE_LINESOURCE_LINEOFFSET, message,
				sourceName, line, lineSource, lineOffset));
	}
}
