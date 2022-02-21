package org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.engine.js.graalvm.callbacks.Require;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public interface JSPolyfill {

    String getSource();

    String getFileName();

    default String getPolyfillFromResources(String polyfillPathInResources) {
        try {
            InputStream xhrPolyfill = Require.class.getResourceAsStream(polyfillPathInResources);
            if (xhrPolyfill == null) {
                throw new IOException("XHR polyfill not found in resources!");
            }
            return IOUtils.toString(xhrPolyfill, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
