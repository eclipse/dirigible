package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.component.platform.http.HttpEndpointModel;
import org.apache.camel.component.platform.http.PlatformHttpComponent;
import org.apache.camel.component.platform.http.spi.PlatformHttpEngine;
import org.apache.camel.component.platform.http.springboot.CamelRequestHandlerMapping;

import java.nio.file.Path;

public class CamelDirigibleRequestHandlerMapping extends CamelRequestHandlerMapping {

    public CamelDirigibleRequestHandlerMapping(PlatformHttpComponent component, PlatformHttpEngine engine) {
        super(component, engine);
    }

    @Override
    public void registerHttpEndpoint(HttpEndpointModel model) {
        var patchedUri = Path.of("/services/integrations", model.getUri())
                             .toString();
        var patchedModel = new HttpEndpointModel(patchedUri, model.getVerbs(), model.getConsumer());
        super.registerHttpEndpoint(patchedModel);
    }
}
