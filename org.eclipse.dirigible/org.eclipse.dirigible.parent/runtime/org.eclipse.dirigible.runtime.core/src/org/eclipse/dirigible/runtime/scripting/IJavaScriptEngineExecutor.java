package org.eclipse.dirigible.runtime.scripting;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IJavaScriptEngineExecutor {

	public static final String JS_ENGINE_TYPE = "engine";

	public static final String JS_TYPE_RHINO = "rhino";

	public static final String JS_TYPE_NASHORN = "nashorn";

	public static final String JS_TYPE_V8 = "v8";

	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException;

}
