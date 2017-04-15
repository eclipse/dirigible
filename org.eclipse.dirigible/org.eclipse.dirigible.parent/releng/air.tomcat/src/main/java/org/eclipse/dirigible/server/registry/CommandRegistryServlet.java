package org.eclipse.dirigible.server.registry;

import javax.servlet.annotation.WebServlet;

/**
 * Wrapper for CommandRegistryServlet
 */
@WebServlet({ "/services/registry-command/*", "/services/scripting/command/*" })
public class CommandRegistryServlet extends org.eclipse.dirigible.runtime.registry.CommandRegistryServlet {
	private static final long serialVersionUID = 1L;
}
