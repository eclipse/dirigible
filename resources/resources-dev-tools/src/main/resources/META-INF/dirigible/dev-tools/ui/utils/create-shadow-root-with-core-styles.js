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

import {appendStyle} from './append-style.js';
import {focusChanged} from './focus-changed.js';
import {injectCoreStyles} from './inject-core-styles.js';

/**
 * @param {!Element} element
 * @param {string=} cssFile
 * @param {boolean=} delegatesFocus
 * @return {!DocumentFragment}
 */
export function createShadowRootWithCoreStyles(element, cssFile, delegatesFocus) {
  const shadowRoot = element.attachShadow({mode: 'open', delegatesFocus});
  injectCoreStyles(shadowRoot);
  if (cssFile) {
    appendStyle(shadowRoot, cssFile);
  }
  shadowRoot.addEventListener('focus', focusChanged.bind(UI), true);
  return shadowRoot;
}
