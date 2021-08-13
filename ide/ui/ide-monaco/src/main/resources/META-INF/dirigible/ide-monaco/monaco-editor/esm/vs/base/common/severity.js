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
import * as nls from '../../nls.js';
import * as strings from './strings.js';
var Severity;
(function (Severity) {
    Severity[Severity["Ignore"] = 0] = "Ignore";
    Severity[Severity["Info"] = 1] = "Info";
    Severity[Severity["Warning"] = 2] = "Warning";
    Severity[Severity["Error"] = 3] = "Error";
})(Severity || (Severity = {}));
(function (Severity) {
    var _error = 'error';
    var _warning = 'warning';
    var _warn = 'warn';
    var _info = 'info';
    var _displayStrings = Object.create(null);
    _displayStrings[Severity.Error] = nls.localize('sev.error', "Error");
    _displayStrings[Severity.Warning] = nls.localize('sev.warning', "Warning");
    _displayStrings[Severity.Info] = nls.localize('sev.info', "Info");
    /**
     * Parses 'error', 'warning', 'warn', 'info' in call casings
     * and falls back to ignore.
     */
    function fromValue(value) {
        if (!value) {
            return Severity.Ignore;
        }
        if (strings.equalsIgnoreCase(_error, value)) {
            return Severity.Error;
        }
        if (strings.equalsIgnoreCase(_warning, value) || strings.equalsIgnoreCase(_warn, value)) {
            return Severity.Warning;
        }
        if (strings.equalsIgnoreCase(_info, value)) {
            return Severity.Info;
        }
        return Severity.Ignore;
    }
    Severity.fromValue = fromValue;
})(Severity || (Severity = {}));
export default Severity;
