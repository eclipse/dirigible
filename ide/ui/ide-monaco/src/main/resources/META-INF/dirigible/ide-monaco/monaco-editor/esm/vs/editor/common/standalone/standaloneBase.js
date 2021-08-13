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
import './promise-polyfill/polyfill.js';
import { CancellationTokenSource } from '../../../base/common/cancellation.js';
import { Emitter } from '../../../base/common/event.js';
import { KeyChord } from '../../../base/common/keyCodes.js';
import { URI } from '../../../base/common/uri.js';
import { Position } from '../core/position.js';
import { Range } from '../core/range.js';
import { Selection } from '../core/selection.js';
import { Token } from '../core/token.js';
import * as standaloneEnums from './standaloneEnums.js';
var KeyMod = /** @class */ (function () {
    function KeyMod() {
    }
    KeyMod.chord = function (firstPart, secondPart) {
        return KeyChord(firstPart, secondPart);
    };
    KeyMod.CtrlCmd = 2048 /* CtrlCmd */;
    KeyMod.Shift = 1024 /* Shift */;
    KeyMod.Alt = 512 /* Alt */;
    KeyMod.WinCtrl = 256 /* WinCtrl */;
    return KeyMod;
}());
export { KeyMod };
export function createMonacoBaseAPI() {
    return {
        editor: undefined,
        languages: undefined,
        CancellationTokenSource: CancellationTokenSource,
        Emitter: Emitter,
        KeyCode: standaloneEnums.KeyCode,
        KeyMod: KeyMod,
        Position: Position,
        Range: Range,
        Selection: Selection,
        SelectionDirection: standaloneEnums.SelectionDirection,
        MarkerSeverity: standaloneEnums.MarkerSeverity,
        MarkerTag: standaloneEnums.MarkerTag,
        Uri: URI,
        Token: Token
    };
}
