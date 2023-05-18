package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.RoutesLoader;

import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.apache.camel.support.ResourceHelper;
import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CamelProcessor {

    private final SpringBootCamelContext context;

    private final RoutesLoader loader;

    private final Map<Long, Resource> camels = new HashMap<>();

    @Autowired
    public CamelProcessor(CamelContext context) {
        this.context = context.adapt(SpringBootCamelContext.class);
        loader = this.context.getRoutesLoader();
    }

    public void process(Camel camel) {
        Resource resource = ResourceHelper.fromBytes("any.yaml", camel.getContent());
        camels.put(camel.getId(), resource);
        camels.values().forEach(routesResource -> {
            try {
                loader.loadRoutes(routesResource);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void clear() {
        try {
            context.getRouteController().stopAllRoutes();
            context.getRouteController().removeAllRoutes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
