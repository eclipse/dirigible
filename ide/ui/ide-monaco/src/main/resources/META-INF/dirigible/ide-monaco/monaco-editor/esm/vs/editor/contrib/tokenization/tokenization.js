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
import * as nls from '../../../nls.js';
import { EditorAction, registerEditorAction } from '../../browser/editorExtensions.js';
import { StopWatch } from '../../../base/common/stopwatch.js';
var ForceRetokenizeAction = /** @class */ (function (_super) {
    __extends(ForceRetokenizeAction, _super);
    function ForceRetokenizeAction() {
        return _super.call(this, {
            id: 'editor.action.forceRetokenize',
            label: nls.localize('forceRetokenize', "Developer: Force Retokenize"),
            alias: 'Developer: Force Retokenize',
            precondition: undefined
        }) || this;
    }
    ForceRetokenizeAction.prototype.run = function (accessor, editor) {
        if (!editor.hasModel()) {
            return;
        }
        var model = editor.getModel();
        model.resetTokenization();
        var sw = new StopWatch(true);
        model.forceTokenization(model.getLineCount());
        sw.stop();
        console.log("tokenization took " + sw.elapsed());
    };
    return ForceRetokenizeAction;
}(EditorAction));
registerEditorAction(ForceRetokenizeAction);
