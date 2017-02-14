/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2014 Walter Higgins
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * Based on work from:
 * https://github.com/walterhiggins/commonjs-modules-javax-script
 *******************************************************************************/

package org.eclipse.dirigible.runtime.js.v8.functions;

import java.util.List;

import org.eclipse.dirigible.runtime.scripting.ISourceProvider;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.eclipsesource.v8.utils.V8ObjectUtils;

public class Require implements JavaCallback {

	private ISourceProvider sourceProvider;

	public Require(ISourceProvider sourceProvider) {
		this.sourceProvider = sourceProvider;
	}

	public Object invoke(final V8Object receiver, final V8Array parameters) {
		final List<Object> data = V8ObjectUtils.toList(parameters);

		if (data.size() > 0) {
			Object o = data.get(0);
			if (o instanceof String) {
				final String moduleName = (String) o;

				V8 runtime = receiver.getRuntime();
				V8Value exportsValue = new V8Object(runtime);
				runtime.add("exports", exportsValue);
				runtime.registerJavaMethod(new Require(sourceProvider), "require");
				try {
					runtime.executeVoidScript(sourceProvider.loadSource(moduleName));
				} catch (Exception e) {
					e.printStackTrace();
				}

				return exportsValue;
			}
		}
		return null;
	}
}