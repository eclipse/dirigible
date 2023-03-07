/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let constraintsProcessor = require("ide-documents/api/processors/constraintsProcessor");

let accessDefinitions = constraintsProcessor.getAccessDefinitions();
constraintsProcessor.updateAccessDefinitions(accessDefinitions);

//console.debug("Access Definitions successfully synchronized by [CMIS Security] Job");