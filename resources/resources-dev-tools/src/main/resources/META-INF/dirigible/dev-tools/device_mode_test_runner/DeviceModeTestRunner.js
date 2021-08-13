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

DeviceModeTestRunner.buildFakePhone = function(overrides) {
  const StandardPhoneJSON = {
    'show-by-default': false,
    'title': 'Fake Phone 1',

    'screen': {
      'horizontal': {'width': 480, 'height': 320},

      'device-pixel-ratio': 2,

      'vertical': {'width': 320, 'height': 480}
    },

    'capabilities': ['touch', 'mobile'],
    'user-agent': 'fakeUserAgent',
    'type': 'phone',

    'modes': [
      {
        'title': 'default',
        'orientation': 'vertical',

        'insets': {'left': 0, 'top': 0, 'right': 0, 'bottom': 0}
      },
      {
        'title': 'default',
        'orientation': 'horizontal',

        'insets': {'left': 0, 'top': 0, 'right': 0, 'bottom': 0}
      }
    ]
  };

  const json = Object.assign(StandardPhoneJSON, overrides || {});
  return Emulation.EmulatedDevice.fromJSONV1(json);
};
