package org.eclipse.dirigible.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

@Component
public class APIClient {

    private final RestTemplate restTemplate;
    private final Tracer tracer;

    APIClient(RestTemplate restTemplate, OpenTelemetry openTelemetry) {
        this.restTemplate = restTemplate;
        this.tracer = openTelemetry.getTracer("dirigible");
    }

    @WithSpan("my_api_client_span")
    public String makeCall(@SpanAttribute("param1_value") String param1) throws Exception {

        Span currentSpan = Span.current();

        currentSpan.addEvent("Custom event #1");
        currentSpan.addEvent("Custom event #2");
        currentSpan.addEvent("Custom event #3");

        currentSpan.setAttribute("a_custom_span_attribute", "my-custom-attribute-value");

        requestWithRestTemplate();
        requestWithHttpClient();

        return "Some response for param " + param1;
    }

    private void requestWithRestTemplate() throws InterruptedException {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://api.ipify.org")
                                      .queryParam("format", "json")
                                      .build()
                                      .toUri();

        Span customSpan = tracer.spanBuilder("my-custom-get-ip-span-rest-template")
                                .setParent(Context.current()
                                                  .with(Span.current()))
                                .startSpan();
        customSpan.addEvent("Making a request with rest template");

        Thread.sleep(new Random().nextInt(3) * 1000);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        customSpan.addEvent("Received response from request with rest template");

        String responseBody = responseEntity.getBody();
        int statusCode = responseEntity.getStatusCode()
                                       .value();
        customSpan.addEvent("Received response status " + statusCode + " and body: " + responseBody);
        customSpan.end();
    }

    private void requestWithHttpClient() throws IOException, InterruptedException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                                                               .useSystemProperties()
                                                               .build()) {
            Span customSpan = tracer.spanBuilder("my-custom-get-ip-span-http-client")
                                    .setParent(Context.current()
                                                      .with(Span.current()))
                                    .startSpan();
            customSpan.addEvent("Making a request with http client");

            Thread.sleep(new Random().nextInt(3) * 1000);

            HttpGet get = new HttpGet("https://api.ipify.org?format=json");
            String responseBody = httpClient.execute(get, new BasicResponseHandler());

            customSpan.addEvent("Received response from request with http client");
            customSpan.addEvent("Received response body: " + responseBody);

            customSpan.end();
        }
    }

}
