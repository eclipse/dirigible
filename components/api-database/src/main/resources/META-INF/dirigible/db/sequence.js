/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Sequence
 *
 */

exports.nextval = function (sequence, datasourceName = null, tableName = null) {
	return org.eclipse.dirigible.components.api.db.DatabaseFacade.nextval(sequence, datasourceName, tableName);
};

exports.create = function (sequence, datasourceName = null) {
	org.eclipse.dirigible.components.api.db.DatabaseFacade.createSequence(sequence, datasourceName);
};

exports.drop = function (sequence, datasourceName = null) {
	org.eclipse.dirigible.components.api.db.DatabaseFacade.dropSequence(sequence, datasourceName);
};
