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
"use strict";
var rsdata = require('http/v3/rs-data'); 
var tagsORMDef = require("ide-discussions/lib/tags_dao").tagsORMDef;
var svc = rsdata.service().dao(tagsORMDef);
svc.execute();
