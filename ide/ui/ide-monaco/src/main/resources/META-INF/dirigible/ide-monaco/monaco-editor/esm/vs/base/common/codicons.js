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
var escapeCodiconsRegex = /(\\)?\$\([a-z0-9\-]+?(?:~[a-z0-9\-]*?)?\)/gi;
export function escapeCodicons(text) {
    return text.replace(escapeCodiconsRegex, function (match, escaped) { return escaped ? match : "\\" + match; });
}
var markdownEscapedCodiconsRegex = /\\\$\([a-z0-9\-]+?(?:~[a-z0-9\-]*?)?\)/gi;
export function markdownEscapeEscapedCodicons(text) {
    // Need to add an extra \ for escaping in markdown
    return text.replace(markdownEscapedCodiconsRegex, function (match) { return "\\" + match; });
}
var renderCodiconsRegex = /(\\)?\$\((([a-z0-9\-]+?)(?:~([a-z0-9\-]*?))?)\)/gi;
export function renderCodicons(text) {
    return text.replace(renderCodiconsRegex, function (_, escaped, codicon, name, animation) {
        return escaped
            ? "$(" + codicon + ")"
            : "<span class=\"codicon codicon-" + name + (animation ? " codicon-animation-" + animation : '') + "\"></span>";
    });
}
