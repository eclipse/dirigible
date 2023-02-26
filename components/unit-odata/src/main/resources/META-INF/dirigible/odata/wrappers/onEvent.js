/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var handler = require(__context.get("handler"));
if (__context.get("method") === 'create') {
	if (__context.get("type") === 'before') {
		handler.onBeforeCreate(__context);
	} else if (__context.get("type") === 'after') {
		handler.onAfterCreate(__context);
	} else if (__context.get("type") === 'on') {
		handler.onCreate(__context);
	}
} else if (__context.get("method") === 'update') {
	if (__context.get("type") === 'before') {
		handler.onBeforeUpdate(__context);
	} else if (__context.get("type") === 'after') {
		handler.onAfterUpdate(__context);
	} else if (__context.get("type") === 'on') {
		handler.onUpdate(__context);
	}
} else if (__context.get("method") === 'delete') {
	if (__context.get("type") === 'before') {
		handler.onBeforeDelete(__context);
	} else if (__context.get("type") === 'after') {
		handler.onAfterDelete(__context);
	} else if (__context.get("type") === 'on') {
		handler.onDelete(__context);
	}
}
