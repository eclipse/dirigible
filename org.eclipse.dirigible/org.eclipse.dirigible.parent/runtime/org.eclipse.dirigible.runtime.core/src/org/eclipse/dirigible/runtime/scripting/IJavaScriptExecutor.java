package org.eclipse.dirigible.runtime.scripting;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;

public interface IJavaScriptExecutor extends IBaseScriptExecutor {

	public static final String REQUESTED_ENDPOINT_IS_NOT_A_SERVICE_BUT_RATHER_A_LIBRARY = "Requested endpoint is not a service, but rather a library.";

	public static final String EXPORTS_ERR = "\"exports\" is not defined";

	public static final String JAVA_SCRIPT_MODULE_NAME_CANNOT_BE_NULL = "JavaScript module name cannot be null"; //$NON-NLS-1$

	public void beforeExecution(HttpServletRequest request, HttpServletResponse response, String module, Object context);

	public IRepository getRepository();

	public String[] getRootPaths();
}
