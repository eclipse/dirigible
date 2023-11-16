package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.Consumer;
import org.apache.camel.component.platform.http.HttpEndpointModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DirigibleHttpEndpointModelTest {

    @Test
    public void testCreateFromInputModel() {
        String uri = "test-uri";
        String verbs = "GET";
        Consumer consumer = mock(Consumer.class);
        HttpEndpointModel inputModel = new HttpEndpointModel(uri, verbs, consumer);

        var outputModel = DirigibleHttpEndpointModel.from(inputModel);
        assertEquals("/services/integrations/test-uri", outputModel.getUri(), "Unexpected URI");
        assertEquals(verbs, outputModel.getVerbs(), "Unexpected Verbs");
        assertEquals(consumer, outputModel.getConsumer(), "Unexpected Consumer");
    }
}
