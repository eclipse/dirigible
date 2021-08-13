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
var IndentRulesSupport = /** @class */ (function () {
    function IndentRulesSupport(indentationRules) {
        this._indentationRules = indentationRules;
    }
    IndentRulesSupport.prototype.shouldIncrease = function (text) {
        if (this._indentationRules) {
            if (this._indentationRules.increaseIndentPattern && this._indentationRules.increaseIndentPattern.test(text)) {
                return true;
            }
            // if (this._indentationRules.indentNextLinePattern && this._indentationRules.indentNextLinePattern.test(text)) {
            // 	return true;
            // }
        }
        return false;
    };
    IndentRulesSupport.prototype.shouldDecrease = function (text) {
        if (this._indentationRules && this._indentationRules.decreaseIndentPattern && this._indentationRules.decreaseIndentPattern.test(text)) {
            return true;
        }
        return false;
    };
    IndentRulesSupport.prototype.shouldIndentNextLine = function (text) {
        if (this._indentationRules && this._indentationRules.indentNextLinePattern && this._indentationRules.indentNextLinePattern.test(text)) {
            return true;
        }
        return false;
    };
    IndentRulesSupport.prototype.shouldIgnore = function (text) {
        // the text matches `unIndentedLinePattern`
        if (this._indentationRules && this._indentationRules.unIndentedLinePattern && this._indentationRules.unIndentedLinePattern.test(text)) {
            return true;
        }
        return false;
    };
    IndentRulesSupport.prototype.getIndentMetadata = function (text) {
        var ret = 0;
        if (this.shouldIncrease(text)) {
            ret += 1 /* INCREASE_MASK */;
        }
        if (this.shouldDecrease(text)) {
            ret += 2 /* DECREASE_MASK */;
        }
        if (this.shouldIndentNextLine(text)) {
            ret += 4 /* INDENT_NEXTLINE_MASK */;
        }
        if (this.shouldIgnore(text)) {
            ret += 8 /* UNINDENT_MASK */;
        }
        return ret;
    };
    return IndentRulesSupport;
}());
export { IndentRulesSupport };
