package org.eclipse.dirigible.runtime.js.rhino;

import java.io.IOException;

import org.eclipse.dirigible.ide.bridge.DirigibleBridge;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.js.debug.WebSocketDebugBridgeServletInternal;
import org.eclipse.dirigible.runtime.js.debug.WebSocketDebugSessionServletInternal;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class RhinoJavaScriptDebuggerActivator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(RhinoJavaScriptDebuggerActivator.class);

	private static final String DEBUG_ENDPOINT = "debug";

	private static WebSocketDebugBridgeServletInternal webSocketDebugBridgeServletInternal;
	private static WebSocketDebugSessionServletInternal webScoketDebugSessionsServletInternal;

	@Override
	public void start(BundleContext context) throws Exception {
		try {
			setupDebugChannel();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	protected void setupDebugChannel() throws IOException {

		logger.debug("Setting debug channel internal ...");

		webSocketDebugBridgeServletInternal = new WebSocketDebugBridgeServletInternal();
		webScoketDebugSessionsServletInternal = new WebSocketDebugSessionServletInternal();
		DirigibleBridge.BRIDGES.put("websocket_debug_channel_internal", webSocketDebugBridgeServletInternal);
		DirigibleBridge.BRIDGES.put("websocket_debug_sessions_internal", webScoketDebugSessionsServletInternal);

		logger.debug("Debug channel internal has been set.");

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		webSocketDebugBridgeServletInternal.closeAll();
		webScoketDebugSessionsServletInternal.closeAll();
	}

}
