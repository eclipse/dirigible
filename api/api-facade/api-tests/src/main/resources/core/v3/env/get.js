/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var env = require('core/v3/env');

var obj = JSON.parse(env.list());
var key = Object.keys(obj)[0];

var result = env.get(key);

result !== undefined && result !== null;
