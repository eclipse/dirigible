package org.eclipse.dirigible.components.engine.camel.invoke;

import org.apache.camel.Message;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class Invoker {
    private final JavascriptService javascriptService;

    @Autowired
    public Invoker(JavascriptService javascriptService) {
        this.javascriptService = javascriptService;
    }

    public void invoke(Message camelMessage) {
        String resourcePath = (String)camelMessage.getExchange().getProperty("resource");
        RepositoryPath path = new RepositoryPath(resourcePath);

        String messageBody = camelMessage.getBody(String.class);

        Map<Object, Object> context = new HashMap<>();
        context.put("camelMessage", messageBody);

        javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }
}
