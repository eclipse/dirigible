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
"use strict";

/*The board.js service for annonymous access*/
var visitCfg = require("ide-discussions/lib/boards_service_lib").create().mappings().find("{id}/visit", "put", ['application/json'], undefined).configuration();
var svc = require("ide-discussions/lib/board_stats_service_lib").create();
svc.resource("{id}/visit").put(visitCfg);
svc.execute();
