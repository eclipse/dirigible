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
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
import * as scanner from './cssScanner.js';
var _FSL = '/'.charCodeAt(0);
var _NWL = '\n'.charCodeAt(0);
var _CAR = '\r'.charCodeAt(0);
var _LFD = '\f'.charCodeAt(0);
var _TIC = '`'.charCodeAt(0);
var _DOT = '.'.charCodeAt(0);
var customTokenValue = scanner.TokenType.CustomToken;
export var Ellipsis = customTokenValue++;
var LESSScanner = /** @class */ (function (_super) {
    __extends(LESSScanner, _super);
    function LESSScanner() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    LESSScanner.prototype.scanNext = function (offset) {
        // LESS: escaped JavaScript code `const a = "dddd"`
        var tokenType = this.escapedJavaScript();
        if (tokenType !== null) {
            return this.finishToken(offset, tokenType);
        }
        if (this.stream.advanceIfChars([_DOT, _DOT, _DOT])) {
            return this.finishToken(offset, Ellipsis);
        }
        return _super.prototype.scanNext.call(this, offset);
    };
    LESSScanner.prototype.comment = function () {
        if (_super.prototype.comment.call(this)) {
            return true;
        }
        if (!this.inURL && this.stream.advanceIfChars([_FSL, _FSL])) {
            this.stream.advanceWhileChar(function (ch) {
                switch (ch) {
                    case _NWL:
                    case _CAR:
                    case _LFD:
                        return false;
                    default:
                        return true;
                }
            });
            return true;
        }
        else {
            return false;
        }
    };
    LESSScanner.prototype.escapedJavaScript = function () {
        var ch = this.stream.peekChar();
        if (ch === _TIC) {
            this.stream.advance(1);
            this.stream.advanceWhileChar(function (ch) { return ch !== _TIC; });
            return this.stream.advanceIfChar(_TIC) ? scanner.TokenType.EscapedJavaScript : scanner.TokenType.BadEscapedJavaScript;
        }
        return null;
    };
    return LESSScanner;
}(scanner.Scanner));
export { LESSScanner };
