package org.eclipse.dirigible.engine.js.v8.callbacks;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

public class Console implements JavaCallback {

	@Override
	public Object invoke(V8Object receiver, V8Array parameters) {
		return null;
	}

}
