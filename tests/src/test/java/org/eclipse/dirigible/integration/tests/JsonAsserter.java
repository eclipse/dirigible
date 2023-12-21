package org.eclipse.dirigible.integration.tests;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonAsserter {

    public static void assertEquals(String expectedJson, String actualJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            assertThat(mapper.readTree(actualJson)).isEqualTo(mapper.readTree(expectedJson))
                                                   .withFailMessage("Unexpected JSON");
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unexpected JSON", e);
        }
    }
}
