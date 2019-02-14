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
var hex = require('utils/v4/hex');

var input = '414243';
var result = hex.decode(input);

console.log('decoded: ' + result);

(result[0] === 65 &&
result[1] === 66 &&
result[2] === 67)
