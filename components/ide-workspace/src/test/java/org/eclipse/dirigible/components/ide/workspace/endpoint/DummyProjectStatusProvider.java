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
package org.eclipse.dirigible.components.ide.workspace.endpoint;

import java.io.IOException;

import org.eclipse.dirigible.components.ide.workspace.domain.ProjectStatus;
import org.eclipse.dirigible.components.ide.workspace.domain.ProjectStatusProvider;

public class DummyProjectStatusProvider implements ProjectStatusProvider {

	@Override
	public ProjectStatus getProjectStatus(String workspace, String project) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProjectGitFolder(String workspace, String project) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
