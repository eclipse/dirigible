package org.eclipse.dirigible.components.listeners.util;

import java.util.List;
import java.util.stream.Collectors;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class InMemoryAppender extends ListAppender<ILoggingEvent> {

    boolean contains(String string, Level level) {
        return this.list.stream()
                        .anyMatch(event -> event.toString()
                                                .contains(string)
                                && event.getLevel()
                                        .equals(level));
    }

    List<String> getAllLoggedMessages() {
        return list.stream()
                   .map(e -> e.getMessage())
                   .collect(Collectors.toList());
    }
}
