package org.eclipse.dirigible.runtime.scripting;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IJavaScriptEngineExecutor {

	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException;

}
