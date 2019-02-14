/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var base64 = require('utils/v4/base64');

var input = 'PT4/';
var result = base64.decode(input);

console.log('decoded: ' + result);

(result[0] === 61 &&
result[1] === 62 &&
result[2] === 63)
