package org.eclipse.dirigible.runtime.ide.console.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class ConsoleLoggingAppender extends AppenderBase<ILoggingEvent> {

	@Override
	protected void append(ILoggingEvent event) {
		ConsoleLogRecord record = new ConsoleLogRecord(
				event.getLevel().toString(),
				event.getFormattedMessage(),
				event.getTimeStamp());
		ConsoleWebsocketService.distribute(record);
	}

}
