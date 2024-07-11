package org.eclipse.dirigible.tests.util;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtil {

    public static int getFreeRandomPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to get a free random port", ex);
        }
    }
}
