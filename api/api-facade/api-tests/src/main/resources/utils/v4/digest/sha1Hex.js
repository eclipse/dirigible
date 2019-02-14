/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var digest = require('utils/v4/digest');

var input = [61, 62, 63];
var result = digest.sha1Hex(input);

console.log(result);

result === '3b543c8b5ddc61fe39de1e5a3aece34082b12777';
