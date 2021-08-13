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
 * @fileoverview using private properties isn't a Closure violation in tests.
 * @suppress {accessControls}
 */

self.extension_getRequestByUrl = function(urls, callback) {
  function onHAR(response) {
    const entries = response.entries;

    for (let i = 0; i < entries.length; ++i) {
      for (let url = 0; url < urls.length; ++url) {
        if (urls[url].test(entries[i].request.url)) {
          callback(entries[i]);
          return;
        }
      }
    }

    output('no item found');
    callback(null);
  }

  webInspector.network.getHAR(onHAR);
};
