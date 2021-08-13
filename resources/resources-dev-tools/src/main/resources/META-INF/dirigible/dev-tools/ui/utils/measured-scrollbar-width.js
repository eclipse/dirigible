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

/** @type {number} */
let _measuredScrollbarWidth;

/**
 * @param {?Document=} document
 * @return {number}
 */
export function measuredScrollbarWidth(document) {
  if (typeof _measuredScrollbarWidth === 'number') {
    return _measuredScrollbarWidth;
  }
  if (!document) {
    return 16;
  }

  const scrollDiv = document.createElement('div');
  const innerDiv = document.createElement('div');
  scrollDiv.setAttribute('style', 'display: block; width: 100px; height: 100px; overflow: scroll;');
  innerDiv.setAttribute('style', 'height: 200px');
  scrollDiv.appendChild(innerDiv);
  document.body.appendChild(scrollDiv);
  _measuredScrollbarWidth = scrollDiv.offsetWidth - scrollDiv.clientWidth;
  document.body.removeChild(scrollDiv);
  return _measuredScrollbarWidth;
}
