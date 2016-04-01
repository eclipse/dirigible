package org.eclipse.dirigible.runtime.command;

import org.eclipse.dirigible.repository.logging.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CommandActivator implements BundleActivator {

	private static final Logger logger = Logger.getLogger(CommandActivator.class);

	WebSocketTerminalBridgeServletInternal webSocketTerminalBridgeServletInternal;

	@Override
	public void start(BundleContext context) throws Exception {
		setupTerminalChannel();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		webSocketTerminalBridgeServletInternal.closeAll();
	}

	protected void setupTerminalChannel() {

		logger.debug("Setting terminal channel internal ...");

		webSocketTerminalBridgeServletInternal = new WebSocketTerminalBridgeServletInternal();
		System.getProperties().put("websocket_terminal_channel_internal", webSocketTerminalBridgeServletInternal);

		logger.debug("Terminal channel internal has been set.");

	}

}
