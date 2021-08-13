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
export function createScopedLineTokens(context, offset) {
    var tokenCount = context.getCount();
    var tokenIndex = context.findTokenIndexAtOffset(offset);
    var desiredLanguageId = context.getLanguageId(tokenIndex);
    var lastTokenIndex = tokenIndex;
    while (lastTokenIndex + 1 < tokenCount && context.getLanguageId(lastTokenIndex + 1) === desiredLanguageId) {
        lastTokenIndex++;
    }
    var firstTokenIndex = tokenIndex;
    while (firstTokenIndex > 0 && context.getLanguageId(firstTokenIndex - 1) === desiredLanguageId) {
        firstTokenIndex--;
    }
    return new ScopedLineTokens(context, desiredLanguageId, firstTokenIndex, lastTokenIndex + 1, context.getStartOffset(firstTokenIndex), context.getEndOffset(lastTokenIndex));
}
var ScopedLineTokens = /** @class */ (function () {
    function ScopedLineTokens(actual, languageId, firstTokenIndex, lastTokenIndex, firstCharOffset, lastCharOffset) {
        this._actual = actual;
        this.languageId = languageId;
        this._firstTokenIndex = firstTokenIndex;
        this._lastTokenIndex = lastTokenIndex;
        this.firstCharOffset = firstCharOffset;
        this._lastCharOffset = lastCharOffset;
    }
    ScopedLineTokens.prototype.getLineContent = function () {
        var actualLineContent = this._actual.getLineContent();
        return actualLineContent.substring(this.firstCharOffset, this._lastCharOffset);
    };
    ScopedLineTokens.prototype.getActualLineContentBefore = function (offset) {
        var actualLineContent = this._actual.getLineContent();
        return actualLineContent.substring(0, this.firstCharOffset + offset);
    };
    ScopedLineTokens.prototype.getTokenCount = function () {
        return this._lastTokenIndex - this._firstTokenIndex;
    };
    ScopedLineTokens.prototype.findTokenIndexAtOffset = function (offset) {
        return this._actual.findTokenIndexAtOffset(offset + this.firstCharOffset) - this._firstTokenIndex;
    };
    ScopedLineTokens.prototype.getStandardTokenType = function (tokenIndex) {
        return this._actual.getStandardTokenType(tokenIndex + this._firstTokenIndex);
    };
    return ScopedLineTokens;
}());
export { ScopedLineTokens };
export function ignoreBracketsInToken(standardTokenType) {
    return (standardTokenType & 7 /* value */) !== 0;
}
