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
import { createDecorator } from '../../instantiation/common/instantiation.js';
import { toDisposable } from '../../../base/common/lifecycle.js';
import * as platform from '../../registry/common/platform.js';
import { Emitter } from '../../../base/common/event.js';
export var IThemeService = createDecorator('themeService');
export function themeColorFromId(id) {
    return { id: id };
}
// base themes
export var DARK = 'dark';
export var HIGH_CONTRAST = 'hc';
export function getThemeTypeSelector(type) {
    switch (type) {
        case DARK: return 'vs-dark';
        case HIGH_CONTRAST: return 'hc-black';
        default: return 'vs';
    }
}
// static theming participant
export var Extensions = {
    ThemingContribution: 'base.contributions.theming'
};
var ThemingRegistry = /** @class */ (function () {
    function ThemingRegistry() {
        this.themingParticipants = [];
        this.themingParticipants = [];
        this.onThemingParticipantAddedEmitter = new Emitter();
    }
    ThemingRegistry.prototype.onThemeChange = function (participant) {
        var _this = this;
        this.themingParticipants.push(participant);
        this.onThemingParticipantAddedEmitter.fire(participant);
        return toDisposable(function () {
            var idx = _this.themingParticipants.indexOf(participant);
            _this.themingParticipants.splice(idx, 1);
        });
    };
    ThemingRegistry.prototype.getThemingParticipants = function () {
        return this.themingParticipants;
    };
    return ThemingRegistry;
}());
var themingRegistry = new ThemingRegistry();
platform.Registry.add(Extensions.ThemingContribution, themingRegistry);
export function registerThemingParticipant(participant) {
    return themingRegistry.onThemeChange(participant);
}
