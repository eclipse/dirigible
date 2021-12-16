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
let platform = require("platform/v4/registry")
exports.getContent = function () {
	let file = platform.getText("platform/extensions/modules.json");
	return JSON.parse(file);
};
