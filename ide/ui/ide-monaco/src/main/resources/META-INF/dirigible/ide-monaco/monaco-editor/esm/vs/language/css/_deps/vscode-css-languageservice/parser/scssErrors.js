/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
'use strict';
import * as nls from './../../../fillers/vscode-nls.js';
var localize = nls.loadMessageBundle();
var SCSSIssueType = /** @class */ (function () {
    function SCSSIssueType(id, message) {
        this.id = id;
        this.message = message;
    }
    return SCSSIssueType;
}());
export { SCSSIssueType };
export var SCSSParseError = {
    FromExpected: new SCSSIssueType('scss-fromexpected', localize('expected.from', "'from' expected")),
    ThroughOrToExpected: new SCSSIssueType('scss-throughexpected', localize('expected.through', "'through' or 'to' expected")),
    InExpected: new SCSSIssueType('scss-fromexpected', localize('expected.in', "'in' expected")),
};
