package org.eclipse.dirigible.components.listeners.config;

import java.util.HashMap;
import java.util.Map;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageConsumerExceptionListener implements ExceptionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerExceptionListener.class);

    private static final String DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR = "messaging/wrappers/onError.js";

    private final String handler;
    private final JavascriptService javascriptService;

    MessageConsumerExceptionListener(JavascriptService javascriptService) {
        this(null, javascriptService);
    }

    MessageConsumerExceptionListener(String handler, JavascriptService javascriptService) {
        this.handler = handler;
        this.javascriptService = javascriptService;
    }

    @Override
    public synchronized void onException(JMSException jmsException) {
        LOGGER.error("JMS exception occured", jmsException);
        try {
            Map<Object, Object> context = createMessagingContext();
            context.put("error", escapeCodeString(jmsException.getMessage()));
            RepositoryPath path = new RepositoryPath(DIRIGIBLE_MESSAGING_WRAPPER_MODULE_ON_ERROR);
            javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
        } catch (RuntimeException ex) {
            ex.addSuppressed(jmsException);
            LOGGER.error("Failed to handle exception properly", ex);
        }
    }

    private Map<Object, Object> createMessagingContext() {
        Map<Object, Object> context = new HashMap<>();
        context.put("handler", handler);
        return context;
    }

    private String escapeCodeString(String raw) {
        return raw.replace("'", "&amp;");
    }

}
