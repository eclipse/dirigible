/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var response = require('http/v4/response');
response.setContentType("application/json");
response.println(JSON.stringify({
	"username": require('security/v4/user').getName()
}, null, 2));
response.flush();
response.close();
