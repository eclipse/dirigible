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
// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * @param {!Node} node
 * @param {string} cssFile
 * @suppressGlobalPropertiesCheck
 */
export function appendStyle(node, cssFile) {
  const content = self.Runtime.cachedResources[cssFile] || '';
  if (!content) {
    console.error(cssFile + ' not preloaded. Check module.json');
  }
  let styleElement = createElement('style');
  styleElement.textContent = content;
  node.appendChild(styleElement);

  const themeStyleSheet = self.UI.themeSupport.themeStyleSheet(cssFile, content);
  if (themeStyleSheet) {
    styleElement = createElement('style');
    styleElement.textContent = themeStyleSheet + '\n' + Root.Runtime.resolveSourceURL(cssFile + '.theme');
    node.appendChild(styleElement);
  }
}
