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
var streams = require('io/v3/streams');

var bais = streams.createByteArrayInputStream([61, 62, 63]);
var baos = streams.createByteArrayOutputStream();
streams.copy(bais, baos);
var result = baos.getBytes();

result[1] === 62;
