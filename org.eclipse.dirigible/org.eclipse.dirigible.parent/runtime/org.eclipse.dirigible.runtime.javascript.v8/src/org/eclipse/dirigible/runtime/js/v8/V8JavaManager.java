package org.eclipse.dirigible.runtime.js.v8;

import java.lang.reflect.Method;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class V8JavaManager {

	private V8 v8;

	public V8JavaManager(V8 v8) {
		this.v8 = v8;
	}

//	public <T extends JsHandler> void registerFunctionsForJsObject(T handler) {
//		Class<?> clazz = handler.getClass();
//		boolean isJsObject = clazz.isAnnotationPresent(JSObject.class);
//		if (!isJsObject) {
//			throw new UnsupportedOperationException();
//		}
//
//		JSObject annotation = clazz.getAnnotation(JSObject.class);
//		String jsObjName = annotation.name();
//
//		registerFunctionsForJsObject(jsObjName, handler);
//	}

	public void registerFunctionsForJsObject(String name, Object handler) {
		if (handler != null) {
			V8Object v8Val = new V8Object(v8);
			v8.add(name, v8Val);

			Class<?> clazz = handler.getClass();
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				v8Val.registerJavaMethod(handler, methodName, methodName, parameterTypes);
			}
			v8Val.release();
		}
	}
}
