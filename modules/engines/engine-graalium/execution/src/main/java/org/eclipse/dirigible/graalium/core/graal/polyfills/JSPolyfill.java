package org.eclipse.dirigible.graalium.core.graal.polyfills;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface JSPolyfill {

    String getSource();

    String getFileName();

    default String getPolyfillFromResources(String polyfillPathInResources) {
        try {
            var polyfillCodeStream = JSPolyfill.class.getResourceAsStream(polyfillPathInResources);
            if (polyfillCodeStream == null) {
                throw new RuntimeException("Polyfill '" + polyfillPathInResources + "' not found in resources!");
            }
            return new String(polyfillCodeStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
