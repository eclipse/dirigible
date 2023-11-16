package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.Consumer;
import org.apache.camel.component.platform.http.HttpEndpointModel;

import java.nio.file.Path;

public class DirigibleHttpEndpointModel extends HttpEndpointModel {

    public DirigibleHttpEndpointModel(String uri, String verbs, Consumer consumer) {
        super(patchUri(uri), verbs, consumer);
    }

    public static DirigibleHttpEndpointModel from(HttpEndpointModel model) {
        return new DirigibleHttpEndpointModel(model.getUri(), model.getVerbs(), model.getConsumer());
    }

    private static String patchUri(String uri) {
        return Path.of("/services/integrations", uri)
                   .toString();
    }
}
