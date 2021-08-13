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
'use strict';
import { Parser } from './parser/cssParser.js';
import { CSSCompletion } from './services/cssCompletion.js';
import { CSSHover } from './services/cssHover.js';
import { CSSNavigation } from './services/cssNavigation.js';
import { CSSCodeActions } from './services/cssCodeActions.js';
import { CSSValidation } from './services/cssValidation.js';
import { SCSSParser } from './parser/scssParser.js';
import { SCSSCompletion } from './services/scssCompletion.js';
import { LESSParser } from './parser/lessParser.js';
import { LESSCompletion } from './services/lessCompletion.js';
import { getFoldingRanges } from './services/cssFolding.js';
import { cssDataManager } from './languageFacts/facts.js';
import { getSelectionRanges } from './services/cssSelectionRange.js';
import { SCSSNavigation } from './services/scssNavigation.js';
export * from './cssLanguageTypes.js';
function createFacade(parser, completion, hover, navigation, codeActions, validation) {
    return {
        configure: function (settings) {
            validation.configure(settings);
            completion.configure(settings);
        },
        doValidation: validation.doValidation.bind(validation),
        parseStylesheet: parser.parseStylesheet.bind(parser),
        doComplete: completion.doComplete.bind(completion),
        setCompletionParticipants: completion.setCompletionParticipants.bind(completion),
        doHover: hover.doHover.bind(hover),
        findDefinition: navigation.findDefinition.bind(navigation),
        findReferences: navigation.findReferences.bind(navigation),
        findDocumentHighlights: navigation.findDocumentHighlights.bind(navigation),
        findDocumentLinks: navigation.findDocumentLinks.bind(navigation),
        findDocumentLinks2: navigation.findDocumentLinks2.bind(navigation),
        findDocumentSymbols: navigation.findDocumentSymbols.bind(navigation),
        doCodeActions: codeActions.doCodeActions.bind(codeActions),
        doCodeActions2: codeActions.doCodeActions2.bind(codeActions),
        findColorSymbols: function (d, s) { return navigation.findDocumentColors(d, s).map(function (s) { return s.range; }); },
        findDocumentColors: navigation.findDocumentColors.bind(navigation),
        getColorPresentations: navigation.getColorPresentations.bind(navigation),
        doRename: navigation.doRename.bind(navigation),
        getFoldingRanges: getFoldingRanges,
        getSelectionRanges: getSelectionRanges
    };
}
function handleCustomData(options) {
    if (options && options.customDataProviders) {
        cssDataManager.addDataProviders(options.customDataProviders);
    }
}
export function getCSSLanguageService(options) {
    handleCustomData(options);
    return createFacade(new Parser(), new CSSCompletion(null, options && options.clientCapabilities), new CSSHover(options && options.clientCapabilities), new CSSNavigation(), new CSSCodeActions(), new CSSValidation());
}
export function getSCSSLanguageService(options) {
    handleCustomData(options);
    return createFacade(new SCSSParser(), new SCSSCompletion(options && options.clientCapabilities), new CSSHover(options && options.clientCapabilities), new SCSSNavigation(options && options.fileSystemProvider), new CSSCodeActions(), new CSSValidation());
}
export function getLESSLanguageService(options) {
    handleCustomData(options);
    return createFacade(new LESSParser(), new LESSCompletion(options && options.clientCapabilities), new CSSHover(options && options.clientCapabilities), new CSSNavigation(), new CSSCodeActions(), new CSSValidation());
}
