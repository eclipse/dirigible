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
var sequence = require('db/v4/sequence');

sequence.create('mysequence');
var zero = sequence.nextval('mysequence');
var one = sequence.nextval('mysequence');
sequence.drop('mysequence');

zero === 0 && one === 1