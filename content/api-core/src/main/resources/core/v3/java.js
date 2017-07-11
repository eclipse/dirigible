/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

exports.call = function(className, methodName, params) {
	var result = null;
	if (engine === "rhino") {
		result = org.eclipse.dirigible.api.v3.core.JavaFacade.call(className, methodName, params);
	} else if (engine === "nashorn") {
		result = Packages.org.eclipse.dirigible.api.v3.core.JavaFacade.call(className, methodName, params);
	}
	return result;
};
