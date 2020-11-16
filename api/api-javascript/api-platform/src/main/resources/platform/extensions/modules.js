/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getContent = function() {
	return [{
		name: "platform/v4/lifecycle",
		description: "Lifecycle API"
	}, {
		name: "platform/v4/registry",
		description: "Registry API"
	}, {
		name: "platform/v4/repository",
		description: "Repository API"
	}, {
		name: "platform/v4/workspace",
		description: "Workspace API"
	}, {
		name: "platform/v4/engines",
		description: "Engines API"
	}, {
		name: "platform/v4/template-engines",
		description: "Template Engines API"
	}];
};
