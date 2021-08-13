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
export function doRename(document, position, newName, htmlDocument) {
    var _a;
    var offset = document.offsetAt(position);
    var node = htmlDocument.findNodeAt(offset);
    if (!node.tag) {
        return null;
    }
    if (!isWithinTagRange(node, offset, node.tag)) {
        return null;
    }
    var edits = [];
    var startTagRange = {
        start: document.positionAt(node.start + '<'.length),
        end: document.positionAt(node.start + '<'.length + node.tag.length)
    };
    edits.push({
        range: startTagRange,
        newText: newName
    });
    if (node.endTagStart) {
        var endTagRange = {
            start: document.positionAt(node.endTagStart + '</'.length),
            end: document.positionAt(node.endTagStart + '</'.length + node.tag.length)
        };
        edits.push({
            range: endTagRange,
            newText: newName
        });
    }
    var changes = (_a = {},
        _a[document.uri.toString()] = edits,
        _a);
    return {
        changes: changes
    };
}
function toLocString(p) {
    return "(" + p.line + ", " + p.character + ")";
}
function isWithinTagRange(node, offset, nodeTag) {
    // Self-closing tag
    if (node.endTagStart) {
        if (node.endTagStart + '</'.length <= offset && offset <= node.endTagStart + '</'.length + nodeTag.length) {
            return true;
        }
    }
    return node.start + '<'.length <= offset && offset <= node.start + '<'.length + nodeTag.length;
}
