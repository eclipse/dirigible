/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var digest = require('utils/v4/digest');

var input = [41, 42, 43];
var result = digest.md5(input);

console.log(JSON.stringify(result));

result.length === 16 && result[0] === -15;
