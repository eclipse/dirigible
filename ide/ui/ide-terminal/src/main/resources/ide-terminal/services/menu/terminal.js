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
exports.getMenu = function() {
	var menu = {
			"name":"Terminal",
			"link":"#",
			"order":"100",
			"onClick":"alert('Terminal has been clicked')",
			"items":[
				{
					"name":"Properties",
					"link":"#",
					"order":"110",
					"onClick":"alert('Properties has been clicked')"
				}
			]
		};
	return menu;
}
