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
var Token = /** @class */ (function () {
    function Token(offset, type, language) {
        this.offset = offset | 0; // @perf
        this.type = type;
        this.language = language;
    }
    Token.prototype.toString = function () {
        return '(' + this.offset + ', ' + this.type + ')';
    };
    return Token;
}());
export { Token };
var TokenizationResult = /** @class */ (function () {
    function TokenizationResult(tokens, endState) {
        this.tokens = tokens;
        this.endState = endState;
    }
    return TokenizationResult;
}());
export { TokenizationResult };
var TokenizationResult2 = /** @class */ (function () {
    function TokenizationResult2(tokens, endState) {
        this.tokens = tokens;
        this.endState = endState;
    }
    return TokenizationResult2;
}());
export { TokenizationResult2 };
