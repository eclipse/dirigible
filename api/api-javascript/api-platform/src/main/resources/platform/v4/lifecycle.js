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