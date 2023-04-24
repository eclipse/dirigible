package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.RoutesLoader;
import org.apache.camel.support.ResourceHelper;
import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class CamelProcessor extends RouteBuilder {

    public void process(Camel camel) {
        CamelContext context = getContext();
        String fileContent = new String(camel.getContent(), StandardCharsets.UTF_8);

        ExtendedCamelContext extendedCamelContext = context.adapt(ExtendedCamelContext.class);
        RoutesLoader loader = extendedCamelContext.getRoutesLoader();
        Resource resource = ResourceHelper.fromString("any.yaml", fileContent);
        try {
            loader.loadRoutes(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        CamelContext context = getContext();
        try {
            context.getRouteController().removeAllRoutes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void configure() throws Exception {
        // No-op
    }
}
