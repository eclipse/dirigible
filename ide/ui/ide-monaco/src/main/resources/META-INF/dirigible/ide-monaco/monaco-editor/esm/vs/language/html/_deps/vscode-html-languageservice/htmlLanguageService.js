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
import { createScanner } from './parser/htmlScanner.js';
import { parse } from './parser/htmlParser.js';
import { HTMLCompletion } from './services/htmlCompletion.js';
import { HTMLHover } from './services/htmlHover.js';
import { format } from './services/htmlFormatter.js';
import { findDocumentLinks } from './services/htmlLinks.js';
import { findDocumentHighlights } from './services/htmlHighlighting.js';
import { findDocumentSymbols } from './services/htmlSymbolsProvider.js';
import { doRename } from './services/htmlRename.js';
import { findMatchingTagPosition } from './services/htmlMatchingTagPosition.js';
import { getFoldingRanges } from './services/htmlFolding.js';
import { getSelectionRanges } from './services/htmlSelectionRange.js';
import { handleCustomDataProviders } from './languageFacts/builtinDataProviders.js';
import { HTMLDataProvider } from './languageFacts/dataProvider.js';
export * from './htmlLanguageTypes.js';
export { TextDocument } from './../vscode-languageserver-textdocument/lib/esm/main.js';
export * from './_deps/vscode-languageserver-types/main.js';
export function getLanguageService(options) {
    var htmlHover = new HTMLHover(options && options.clientCapabilities);
    var htmlCompletion = new HTMLCompletion(options && options.clientCapabilities);
    if (options && options.customDataProviders) {
        handleCustomDataProviders(options.customDataProviders);
    }
    return {
        createScanner: createScanner,
        parseHTMLDocument: function (document) { return parse(document.getText()); },
        doComplete: htmlCompletion.doComplete.bind(htmlCompletion),
        setCompletionParticipants: htmlCompletion.setCompletionParticipants.bind(htmlCompletion),
        doHover: htmlHover.doHover.bind(htmlHover),
        format: format,
        findDocumentHighlights: findDocumentHighlights,
        findDocumentLinks: findDocumentLinks,
        findDocumentSymbols: findDocumentSymbols,
        getFoldingRanges: getFoldingRanges,
        getSelectionRanges: getSelectionRanges,
        doTagComplete: htmlCompletion.doTagComplete.bind(htmlCompletion),
        doRename: doRename,
        findMatchingTagPosition: findMatchingTagPosition
    };
}
export function newHTMLDataProvider(id, customData) {
    return new HTMLDataProvider(id, customData);
}
