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

// TODO(crbug.com/1029037): lazily load these files again after the
// race-condition with CodeMirror is fixed
import './clike.js';
import './coffeescript.js';
import './php.js';
import './python.js';
import './shell.js';
import './livescript.js';
import './markdown.js';
import './clojure.js';
import './jsx.js';

import * as DefaultCodeMirrorMimeMode from './DefaultCodeMirrorMimeMode.js';

export {
  DefaultCodeMirrorMimeMode,
};
