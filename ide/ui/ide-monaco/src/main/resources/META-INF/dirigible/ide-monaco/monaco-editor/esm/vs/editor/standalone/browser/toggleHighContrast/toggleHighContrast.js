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
import { EditorAction, registerEditorAction } from '../../../browser/editorExtensions.js';
import { IStandaloneThemeService } from '../../common/standaloneThemeService.js';
import { ToggleHighContrastNLS } from '../../../common/standaloneStrings.js';
var ToggleHighContrast = /** @class */ (function (_super) {
    __extends(ToggleHighContrast, _super);
    function ToggleHighContrast() {
        var _this = _super.call(this, {
            id: 'editor.action.toggleHighContrast',
            label: ToggleHighContrastNLS.toggleHighContrast,
            alias: 'Toggle High Contrast Theme',
            precondition: undefined
        }) || this;
        _this._originalThemeName = null;
        return _this;
    }
    ToggleHighContrast.prototype.run = function (accessor, editor) {
        var standaloneThemeService = accessor.get(IStandaloneThemeService);
        if (this._originalThemeName) {
            // We must toggle back to the integrator's theme
            standaloneThemeService.setTheme(this._originalThemeName);
            this._originalThemeName = null;
        }
        else {
            this._originalThemeName = standaloneThemeService.getTheme().themeName;
            standaloneThemeService.setTheme('hc-black');
        }
    };
    return ToggleHighContrast;
}(EditorAction));
registerEditorAction(ToggleHighContrast);
