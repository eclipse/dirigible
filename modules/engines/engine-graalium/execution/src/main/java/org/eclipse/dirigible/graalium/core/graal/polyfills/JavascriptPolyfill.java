package org.eclipse.dirigible.graalium.core.graal.polyfills;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * The Interface JavascriptPolyfill.
 */
public interface JavascriptPolyfill {

    /**
     * Gets the source.
     *
     * @return the source
     */
    String getSource();

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    String getFileName();

    /**
     * Gets the polyfill from resources.
     *
     * @param polyfillPathInResources the polyfill path in resources
     * @return the polyfill from resources
     */
    default String getPolyfillFromResources(String polyfillPathInResources) {
        try {
        	InputStream polyfillCodeStream = JavascriptPolyfill.class.getResourceAsStream(polyfillPathInResources);
            if (polyfillCodeStream == null) {
                throw new RuntimeException("Polyfill '" + polyfillPathInResources + "' not found in resources!");
            }
            return new String(polyfillCodeStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
