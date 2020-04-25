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
var _java = require('core/v3/java');

var process = _java.instantiate('org.eclipse.dirigible.api.v3.test.Process', []);

console.log('Process: ' + process.uuid);

_java.invoke(process.uuid, 'setName', ['process1']);

var task = _java.invoke(process.uuid, 'createTask', ['task1'], true);

console.log('Task: ' + task.uuid);

var result = _java.invoke(task.uuid, 'getName', []);

console.log('Task.name: ' + result);

var exists = _java.invoke(process.uuid, 'existsTask', [task.uuid]);

console.log('Task exists?: ' + exists);

result == 'task1';
