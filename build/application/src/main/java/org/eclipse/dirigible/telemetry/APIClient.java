package org.eclipse.dirigible.telemetry;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class APIClient {

    private final RestTemplate restTemplate;

    APIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @WithSpan("my_custom_span_2")
    public String makeCall(@SpanAttribute("param1_value") String param1) {

        Span currentSpan = Span.current();
        currentSpan.addEvent("Custom event #1");
        currentSpan.addEvent("Custom event #2");
        currentSpan.addEvent("Custom event #3");
        currentSpan.setAttribute("a_custom_span_attribute", "my-custom-attribute-value");

        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.ipify.org")
                                      .queryParam("format", "json")
                                      .build()
                                      .toUri();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        String responseBody = responseEntity.getBody();
        int statusCode = responseEntity.getStatusCode()
                                       .value();
        currentSpan.addEvent("Received response status " + statusCode + " and body: " + responseBody);

        return "Some response for param " + param1;
    }
}
