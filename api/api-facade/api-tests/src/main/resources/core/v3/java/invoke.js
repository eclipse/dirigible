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
var _java = require('core/v3/java');

var arrayList = _java.instantiate('java.util.ArrayList', []);

_java.invoke(arrayList.uuid, 'add', ['some text']);

var result = _java.invoke(arrayList.uuid, 'get', [0]);

console.log(result);

result == 'some text';
