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
export function findMatchingTagPosition(document, position, htmlDocument) {
    var offset = document.offsetAt(position);
    var node = htmlDocument.findNodeAt(offset);
    if (!node.tag) {
        return null;
    }
    if (!node.endTagStart) {
        return null;
    }
    // Within open tag, compute close tag
    if (node.start + '<'.length <= offset && offset <= node.start + '<'.length + node.tag.length) {
        var mirrorOffset = (offset - '<'.length - node.start) + node.endTagStart + '</'.length;
        return document.positionAt(mirrorOffset);
    }
    // Within closing tag, compute open tag
    if (node.endTagStart + '</'.length <= offset && offset <= node.endTagStart + '</'.length + node.tag.length) {
        var mirrorOffset = (offset - '</'.length - node.endTagStart) + node.start + '<'.length;
        return document.positionAt(mirrorOffset);
    }
    return null;
}
