package org.eclipse.dirigible.engine.js.v8.callbacks;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dirigible.api.v3.core.JavaFacade;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;

public class JavaV8Call implements JavaCallback {

	@Override
	public Object invoke(V8Object receiver, V8Array parameters) {
		int i = 0;
		String className = (String) parameters.get(i++);
		String methodName = (String) parameters.get(i++);
		List<Object> params = new ArrayList<Object>();
		for (int j = i; j < parameters.length(); j++) {
			V8Array paramArray = (V8Array) parameters.get(j);
			List<Object> paramList = V8ObjectUtils.toList(paramArray);
			for (Object param : paramList) {
				params.add(param);
			}
			paramArray.release();
		}
		Iterator<Object> iterator = params.iterator();
		while (iterator.hasNext()) {
			Object param = iterator.next();
			if (param.equals(V8.getUndefined())) {
				iterator.remove();
			}
		}
		try {
			Object result = JavaFacade.call(className, methodName, params.toArray(new Object[] {}));
			if ((result != null) && result.getClass().isArray()) {
				List<Object> list = new ArrayList<>();
				for (int j = 0; j < Array.getLength(result); j++) {
					Object next = Array.get(result, j);
					if (next instanceof Byte) {
						list.add(Integer.valueOf((byte) next));
					} else {
						list.add(next);
					}
				}
				return V8ObjectUtils.toV8Array(receiver.getRuntime(), list);
			}
			return result;
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		} finally {
			parameters.release();
		}
	}
}
