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
var ResolvedKeybindingItem = /** @class */ (function () {
    function ResolvedKeybindingItem(resolvedKeybinding, command, commandArgs, when, isDefault) {
        this.resolvedKeybinding = resolvedKeybinding;
        this.keypressParts = resolvedKeybinding ? removeElementsAfterNulls(resolvedKeybinding.getDispatchParts()) : [];
        this.bubble = (command ? command.charCodeAt(0) === 94 /* Caret */ : false);
        this.command = this.bubble ? command.substr(1) : command;
        this.commandArgs = commandArgs;
        this.when = when;
        this.isDefault = isDefault;
    }
    return ResolvedKeybindingItem;
}());
export { ResolvedKeybindingItem };
export function removeElementsAfterNulls(arr) {
    var result = [];
    for (var i = 0, len = arr.length; i < len; i++) {
        var element = arr[i];
        if (!element) {
            // stop processing at first encountered null
            return result;
        }
        result.push(element);
    }
    return result;
}
