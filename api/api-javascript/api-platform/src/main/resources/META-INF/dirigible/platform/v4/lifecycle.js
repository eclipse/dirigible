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
exports.publish = function(user, workspace, project) {
    if (!project) {
        project = "*";
    }
    return org.eclipse.dirigible.api.v3.platform.LifecycleFacade.publish(user, workspace, project);
};

exports.unpublish = function(user, workspace, project) {
    if (!project) {
        project = "*";
    }
    return org.eclipse.dirigible.api.v3.platform.LifecycleFacade.unpublish(user, workspace, project);
};