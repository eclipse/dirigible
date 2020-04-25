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
var escape = require('utils/v4/escape');

var input = '"1,2,3,4,5,6"';
var result = escape.unescapeCsv(input);

result === '1,2,3,4,5,6';
