/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
exports.getPerspective = function() {
	var perspective = {
			"name":"Debugger",
			"link":"../ide-debugger/index.html",
			"order":"106",
			"image":"bug"
	};
	return perspective;
}
