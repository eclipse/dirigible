package org.eclipse.dirigible.components.listeners.service;

import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageListener implements javax.jms.MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_MESSAGE = "messaging/wrappers/onMessage.js";

    private final Listener listener;

    MessageListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onMessage(Message message) {
        LOGGER.trace("Start processing a received message in [{}] by [{}] ...", listener.getName(), listener.getHandler());
        if (!(message instanceof TextMessage textMsg)) {
            String msg = String.format("Invalid message [%s] has been received in destination [%s]", message, listener.getName());
            throw new IllegalStateException(msg);
        }
        Map<Object, Object> context = createMessagingContext();
        context.put("message", extractMessage(textMsg));
        RepositoryPath path = new RepositoryPath(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_MESSAGE);
        JavascriptService.get()
                         .handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
        LOGGER.trace("Done processing the received message in [{}] by [{}]", listener.getName(), listener.getHandler());
    }

    private Map<Object, Object> createMessagingContext() {
        Map<Object, Object> context = new HashMap<>();
        context.put("handler", listener.getHandler());
        return context;
    }

    private String extractMessage(TextMessage textMsg) {
        try {
            return escapeCodeString(textMsg.getText());
        } catch (JMSException ex) {
            throw new IllegalStateException("Failed to extract test message from " + textMsg, ex);
        }
    }

    private String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }

}
