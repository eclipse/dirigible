package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.component.platform.http.HttpEndpointModel;
import org.apache.camel.component.platform.http.PlatformHttpComponent;
import org.apache.camel.component.platform.http.spi.PlatformHttpEngine;
import org.apache.camel.component.platform.http.springboot.CamelRequestHandlerMapping;

public class CamelDirigibleRequestHandlerMapping extends CamelRequestHandlerMapping {

    public CamelDirigibleRequestHandlerMapping(PlatformHttpComponent component, PlatformHttpEngine engine) {
        super(component, engine);
    }

    @Override
    public void registerHttpEndpoint(HttpEndpointModel model) {
        var patchedModel = DirigibleHttpEndpointModel.from(model);
        super.registerHttpEndpoint(patchedModel);
    }
}
