/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.runtime.java.executors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.runtime.java.JavaExecutor;

public class JavaExecutorStub extends JavaExecutor {

	public JavaExecutorStub(IRepository repository,
			String classpath, String... rootPaths) {
		super(repository, classpath, rootPaths);
	}

	@Override
	protected void registerDefaultVariables(HttpServletRequest request,
			HttpServletResponse response, Object input,
			Map<Object, Object> executionContext, IRepository repository,
			Object scope) {
		// Do nothing in addition
	}

}
