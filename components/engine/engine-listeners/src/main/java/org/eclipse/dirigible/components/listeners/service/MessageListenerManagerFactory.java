package org.eclipse.dirigible.components.listeners.service;

import javax.jms.Session;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerManagerFactory {

    private final Session session;

    @Autowired
    MessageListenerManagerFactory(@Qualifier("ActiveMQSession") Session session) {
        this.session = session;
    }

    MessageListenerManager create(Listener listener) {
        return new MessageListenerManager(listener, session);
    }
}
