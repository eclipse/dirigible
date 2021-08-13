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
// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * @param {string} folderPath
 * @return {!{isolatedFileSystem: !Persistence.IsolatedFileSystem, project: !Workspace.Project, testFileSystem: !BindingsTestRunner.TestFileSystem}}
 */
BindingsTestRunner.createOverrideProject = async function(folderPath) {
  const testFileSystem = new BindingsTestRunner.TestFileSystem(folderPath);
  const isolatedFileSystem = await testFileSystem.reportCreatedPromise('overrides');
  isolatedFileSystem._type = 'overrides';
  const project =
      self.Workspace.workspace.project(Persistence.FileSystemWorkspaceBinding.projectId(isolatedFileSystem.path()));
  console.assert(project);
  return {isolatedFileSystem, project, testFileSystem};
};

/**
 * @param {boolean} enabled
 */
BindingsTestRunner.setOverridesEnabled = function(enabled) {
  self.Common.settings.moduleSetting('persistenceNetworkOverridesEnabled').set(enabled);
};
