package org.eclipse.dirigible.components.engine.camel.invoke;

import org.apache.camel.Message;

import java.util.Map;

public class IntegrationMessage {
    private final Message message;

    public IntegrationMessage(Message message) {
        this.message = message;
    }

    public String getBodyAsString() {
        return message.getBody(String.class);
    }

    public Object getBody() {
        return message.getBody();
    }

    public void setBody(Object body) {
        message.setBody(body);
    }

    public Map<String, Object> getHeaders() {
        return message.getHeaders();
    }

    public void setHeaders(Map<String, Object> headers) {
        message.setHeaders(headers);
    }

    public void setHeader(String key, Object value) {
        message.setHeader(key, value);
    }

    public Message getCamelMessage() {
        return message;
    }
}
