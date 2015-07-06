package org.eclipse.dirigible.runtime.content;

import java.net.UnknownHostException;

import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.logging.Logger;

public class CloneExporterServlet extends ContentExporterServlet {

	private static final long serialVersionUID = -2906745438795974322L;
	
	private static final Logger logger = Logger.getLogger(CloneExporterServlet.class);
	
	static final String CLONE_PATH_FOR_EXPORT = 
			IRepositoryPaths.DB_DIRIGIBLE_ROOT.substring(0, IRepositoryPaths.DB_DIRIGIBLE_ROOT.length()-1);
	
	public static final String CLONE = "clone";

	protected String getDefaultPathForExport() {
		return CLONE_PATH_FOR_EXPORT;
	}

	protected String getExportFilePrefix() {
		StringBuilder buff = new StringBuilder();
		buff.append(CLONE)
			.append(UNDERSCORE);
		try {
			buff.append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			buff.append(UNKNOWN_HOST);
		}
		return buff.toString();
	}
}
