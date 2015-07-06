package org.eclipse.dirigible.runtime.content;

import org.eclipse.dirigible.repository.api.IRepositoryPaths;

public class CloneImporterServlet extends ContentImporterServlet {

	private static final long serialVersionUID = -7275411031828315889L;
	
	static final String CLONE_PATH_FOR_IMPORT = IRepositoryPaths.DB_DIRIGIBLE_BASE;

	protected String getDefaultPathForImport() {
		return CLONE_PATH_FOR_IMPORT;
	}

}
