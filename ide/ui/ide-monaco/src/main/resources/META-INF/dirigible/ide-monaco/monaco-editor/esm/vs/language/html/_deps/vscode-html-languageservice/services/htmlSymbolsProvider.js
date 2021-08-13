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
import { Location, Range, SymbolKind } from './../_deps/vscode-languageserver-types/main.js';
export function findDocumentSymbols(document, htmlDocument) {
    var symbols = [];
    htmlDocument.roots.forEach(function (node) {
        provideFileSymbolsInternal(document, node, '', symbols);
    });
    return symbols;
}
function provideFileSymbolsInternal(document, node, container, symbols) {
    var name = nodeToName(node);
    var location = Location.create(document.uri, Range.create(document.positionAt(node.start), document.positionAt(node.end)));
    var symbol = {
        name: name,
        location: location,
        containerName: container,
        kind: SymbolKind.Field
    };
    symbols.push(symbol);
    node.children.forEach(function (child) {
        provideFileSymbolsInternal(document, child, name, symbols);
    });
}
function nodeToName(node) {
    var name = node.tag;
    if (node.attributes) {
        var id = node.attributes['id'];
        var classes = node.attributes['class'];
        if (id) {
            name += "#" + id.replace(/[\"\']/g, '');
        }
        if (classes) {
            name += classes.replace(/[\"\']/g, '').split(/\s+/).map(function (className) { return "." + className; }).join('');
        }
    }
    return name || '?';
}
