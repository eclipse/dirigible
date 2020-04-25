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
var files = require('io/v4/files');
var streams = require('io/v4/streams');

var tempFile1 = files.createTempFile("dirigible", ".txt");
console.log('Temp file 1: ' + tempFile1);
files.writeText(tempFile1, "Eclipse Dirigible");

var tempFile2 = files.createTempFile("dirigible", ".txt");
console.log('Temp file 2: ' + tempFile2);

var input = files.createInputStream(tempFile1);
var output = files.createOutputStream(tempFile2);

streams.copy(input, output);

var result = files.readText(tempFile2);

files.deleteFile(tempFile1);
files.deleteFile(tempFile2);

result == "Eclipse Dirigible";
