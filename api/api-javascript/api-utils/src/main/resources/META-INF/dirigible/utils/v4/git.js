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

exports.initRepository = function (user, email, workspaceName, projectName, repositoryName, commitMessage) {
    return org.eclipse.dirigible.api.v4.git.GitFacade.initRepository(user, email, workspaceName, projectName, repositoryName, commitMessage);
}

exports.commit = function (user, userEmail, workspaceName, repositoryName, commitMessage, add)  {
    return org.eclipse.dirigible.api.v4.git.GitFacade.commit(user, userEmail, workspaceName, repositoryName, commitMessage, add);
}
