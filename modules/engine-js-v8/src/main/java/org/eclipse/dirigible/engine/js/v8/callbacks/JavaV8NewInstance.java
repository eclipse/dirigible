package org.eclipse.dirigible.engine.js.v8.callbacks;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.dirigible.api.v3.core.JavaFacade;
import org.eclipse.dirigible.commons.api.context.ContextException;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

public class JavaV8NewInstance extends JavaV8Callback implements JavaCallback {

	@Override
	public Object invoke(V8Object receiver, V8Array parameters) {
		int i = 0;
		String className = (String) parameters.get(i++);
		List<Object> params = normalizeParameters(parameters, i);
		try {
			Object result = JavaFacade.instantiate(className, params.toArray(new Object[] {}));
			return result;
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException
				| SecurityException | ContextException e) {
			throw new RuntimeException(e);
		} finally {
			parameters.release();
		}
	}

}
