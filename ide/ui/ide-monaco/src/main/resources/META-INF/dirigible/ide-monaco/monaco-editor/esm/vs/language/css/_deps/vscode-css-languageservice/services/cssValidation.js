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
import * as nodes from '../parser/cssNodes.js';
import { LintConfigurationSettings, Rules } from './lintRules.js';
import { LintVisitor } from './lint.js';
import { Range, DiagnosticSeverity } from '../cssLanguageTypes.js';
var CSSValidation = /** @class */ (function () {
    function CSSValidation() {
    }
    CSSValidation.prototype.configure = function (settings) {
        this.settings = settings;
    };
    CSSValidation.prototype.doValidation = function (document, stylesheet, settings) {
        if (settings === void 0) { settings = this.settings; }
        if (settings && settings.validate === false) {
            return [];
        }
        var entries = [];
        entries.push.apply(entries, nodes.ParseErrorCollector.entries(stylesheet));
        entries.push.apply(entries, LintVisitor.entries(stylesheet, document, new LintConfigurationSettings(settings && settings.lint)));
        var ruleIds = [];
        for (var r in Rules) {
            ruleIds.push(Rules[r].id);
        }
        function toDiagnostic(marker) {
            var range = Range.create(document.positionAt(marker.getOffset()), document.positionAt(marker.getOffset() + marker.getLength()));
            var source = document.languageId;
            return {
                code: marker.getRule().id,
                source: source,
                message: marker.getMessage(),
                severity: marker.getLevel() === nodes.Level.Warning ? DiagnosticSeverity.Warning : DiagnosticSeverity.Error,
                range: range
            };
        }
        return entries.filter(function (entry) { return entry.getLevel() !== nodes.Level.Ignore; }).map(toDiagnostic);
    };
    return CSSValidation;
}());
export { CSSValidation };
