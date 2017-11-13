/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.runtime.ide.console.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * The Console Logging Appender.
 */
public class ConsoleLoggingAppender extends AppenderBase<ILoggingEvent> {

	/*
	 * (non-Javadoc)
	 * @see ch.qos.logback.core.AppenderBase#append(java.lang.Object)
	 */
	@Override
	protected void append(ILoggingEvent event) {
		ConsoleLogRecord record = new ConsoleLogRecord(event.getLevel().toString(), event.getFormattedMessage(), event.getTimeStamp());
		ConsoleWebsocketService.distribute(record);
	}

}
