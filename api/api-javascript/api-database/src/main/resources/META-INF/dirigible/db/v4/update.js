/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Update
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.execute = function (sql, parameters, databaseType, datasourceName) {
    var result = {};
    if (parameters) {
        var params = JSON.stringify(parameters);
        if (databaseType) {
            if (datasourceName) {
                result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql, params, databaseType, datasourceName);
            } else {
                result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql, params, databaseType);
            }
        } else {
            result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql, params);
        }
    } else {
        result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql);
    }
    return result;
};
