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
import { ignoreBracketsInToken } from '../supports.js';
import { BracketsUtils } from './richEditBrackets.js';
var BracketElectricCharacterSupport = /** @class */ (function () {
    function BracketElectricCharacterSupport(richEditBrackets) {
        this._richEditBrackets = richEditBrackets;
    }
    BracketElectricCharacterSupport.prototype.getElectricCharacters = function () {
        var result = [];
        if (this._richEditBrackets) {
            for (var _i = 0, _a = this._richEditBrackets.brackets; _i < _a.length; _i++) {
                var bracket = _a[_i];
                for (var _b = 0, _c = bracket.close; _b < _c.length; _b++) {
                    var close_1 = _c[_b];
                    var lastChar = close_1.charAt(close_1.length - 1);
                    result.push(lastChar);
                }
            }
        }
        // Filter duplicate entries
        result = result.filter(function (item, pos, array) {
            return array.indexOf(item) === pos;
        });
        return result;
    };
    BracketElectricCharacterSupport.prototype.onElectricCharacter = function (character, context, column) {
        if (!this._richEditBrackets || this._richEditBrackets.brackets.length === 0) {
            return null;
        }
        var tokenIndex = context.findTokenIndexAtOffset(column - 1);
        if (ignoreBracketsInToken(context.getStandardTokenType(tokenIndex))) {
            return null;
        }
        var reversedBracketRegex = this._richEditBrackets.reversedRegex;
        var text = context.getLineContent().substring(0, column - 1) + character;
        var r = BracketsUtils.findPrevBracketInRange(reversedBracketRegex, 1, text, 0, text.length);
        if (!r) {
            return null;
        }
        var bracketText = text.substring(r.startColumn - 1, r.endColumn - 1).toLowerCase();
        var isOpen = this._richEditBrackets.textIsOpenBracket[bracketText];
        if (isOpen) {
            return null;
        }
        var textBeforeBracket = context.getActualLineContentBefore(r.startColumn - 1);
        if (!/^\s*$/.test(textBeforeBracket)) {
            // There is other text on the line before the bracket
            return null;
        }
        return {
            matchOpenBracket: bracketText
        };
    };
    return BracketElectricCharacterSupport;
}());
export { BracketElectricCharacterSupport };
