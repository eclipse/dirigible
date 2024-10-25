package org.eclipse.dirigible.integration.tests.api.java.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DirigibleJavaScriptRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:callDirigibleScript").to("dirigible-java-script:dirigible-java-script-component/handler.mjs");
    }
}
