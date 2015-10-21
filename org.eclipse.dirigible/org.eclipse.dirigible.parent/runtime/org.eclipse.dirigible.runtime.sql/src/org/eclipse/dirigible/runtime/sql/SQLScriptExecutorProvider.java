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

package org.eclipse.dirigible.runtime.sql;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutor;
import org.eclipse.dirigible.runtime.scripting.IScriptExecutorProvider;

public class SQLScriptExecutorProvider implements
		IScriptExecutorProvider {

	@Override
	public String getType() {
		return ICommonConstants.ENGINE_TYPE.SQL;
	}
	
	@Override
	public String getAlias() {
		return ICommonConstants.ENGINE_ALIAS.SQL;
	}

	@Override
	public IScriptExecutor createExecutor(HttpServletRequest request) throws IOException {
		SQLServlet sqlServlet = new SQLServlet();
		SQLExecutor sqlExecutor = sqlServlet.createExecutor(request);
		return sqlExecutor;
	}

}
