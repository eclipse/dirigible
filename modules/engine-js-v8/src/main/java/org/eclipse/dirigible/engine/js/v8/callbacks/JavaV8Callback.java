package org.eclipse.dirigible.engine.js.v8.callbacks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.utils.V8ObjectUtils;

public class JavaV8Callback {

	protected List<Object> normalizeParameters(V8Array parameters, int i) {
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
		return params;
	}

}
