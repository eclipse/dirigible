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
import { StandardAutoClosingPairConditional } from '../languageConfiguration.js';
var CharacterPairSupport = /** @class */ (function () {
    function CharacterPairSupport(config) {
        if (config.autoClosingPairs) {
            this._autoClosingPairs = config.autoClosingPairs.map(function (el) { return new StandardAutoClosingPairConditional(el); });
        }
        else if (config.brackets) {
            this._autoClosingPairs = config.brackets.map(function (b) { return new StandardAutoClosingPairConditional({ open: b[0], close: b[1] }); });
        }
        else {
            this._autoClosingPairs = [];
        }
        if (config.__electricCharacterSupport && config.__electricCharacterSupport.docComment) {
            var docComment = config.__electricCharacterSupport.docComment;
            // IDocComment is legacy, only partially supported
            this._autoClosingPairs.push(new StandardAutoClosingPairConditional({ open: docComment.open, close: docComment.close || '' }));
        }
        this._autoCloseBefore = typeof config.autoCloseBefore === 'string' ? config.autoCloseBefore : CharacterPairSupport.DEFAULT_AUTOCLOSE_BEFORE_LANGUAGE_DEFINED;
        this._surroundingPairs = config.surroundingPairs || this._autoClosingPairs;
    }
    CharacterPairSupport.prototype.getAutoClosingPairs = function () {
        return this._autoClosingPairs;
    };
    CharacterPairSupport.prototype.getAutoCloseBeforeSet = function () {
        return this._autoCloseBefore;
    };
    CharacterPairSupport.shouldAutoClosePair = function (autoClosingPair, context, column) {
        // Always complete on empty line
        if (context.getTokenCount() === 0) {
            return true;
        }
        var tokenIndex = context.findTokenIndexAtOffset(column - 2);
        var standardTokenType = context.getStandardTokenType(tokenIndex);
        return autoClosingPair.isOK(standardTokenType);
    };
    CharacterPairSupport.prototype.getSurroundingPairs = function () {
        return this._surroundingPairs;
    };
    CharacterPairSupport.DEFAULT_AUTOCLOSE_BEFORE_LANGUAGE_DEFINED = ';:.,=}])> \n\t';
    return CharacterPairSupport;
}());
export { CharacterPairSupport };
