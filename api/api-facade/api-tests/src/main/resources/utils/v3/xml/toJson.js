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
var xml2json = require('utils/v3/xml');

var input = '<a><b>text_b</b><c>text_c</c><d><e>text_e</e></d></a>';
var result = xml2json.toJson(input);

JSON.parse(result).a.d.e === 'text_e';

