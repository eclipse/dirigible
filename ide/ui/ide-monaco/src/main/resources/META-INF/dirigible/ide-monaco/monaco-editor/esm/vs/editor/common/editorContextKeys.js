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
import { RawContextKey } from '../../platform/contextkey/common/contextkey.js';
export var EditorContextKeys;
(function (EditorContextKeys) {
    EditorContextKeys.editorSimpleInput = new RawContextKey('editorSimpleInput', false);
    /**
     * A context key that is set when the editor's text has focus (cursor is blinking).
     */
    EditorContextKeys.editorTextFocus = new RawContextKey('editorTextFocus', false);
    /**
     * A context key that is set when the editor's text or an editor's widget has focus.
     */
    EditorContextKeys.focus = new RawContextKey('editorFocus', false);
    /**
     * A context key that is set when any editor input has focus (regular editor, repl input...).
     */
    EditorContextKeys.textInputFocus = new RawContextKey('textInputFocus', false);
    EditorContextKeys.readOnly = new RawContextKey('editorReadonly', false);
    EditorContextKeys.writable = EditorContextKeys.readOnly.toNegated();
    EditorContextKeys.hasNonEmptySelection = new RawContextKey('editorHasSelection', false);
    EditorContextKeys.hasOnlyEmptySelection = EditorContextKeys.hasNonEmptySelection.toNegated();
    EditorContextKeys.hasMultipleSelections = new RawContextKey('editorHasMultipleSelections', false);
    EditorContextKeys.hasSingleSelection = EditorContextKeys.hasMultipleSelections.toNegated();
    EditorContextKeys.tabMovesFocus = new RawContextKey('editorTabMovesFocus', false);
    EditorContextKeys.tabDoesNotMoveFocus = EditorContextKeys.tabMovesFocus.toNegated();
    EditorContextKeys.isInEmbeddedEditor = new RawContextKey('isInEmbeddedEditor', false);
    EditorContextKeys.canUndo = new RawContextKey('canUndo', false);
    EditorContextKeys.canRedo = new RawContextKey('canRedo', false);
    // -- mode context keys
    EditorContextKeys.languageId = new RawContextKey('editorLangId', '');
    EditorContextKeys.hasCompletionItemProvider = new RawContextKey('editorHasCompletionItemProvider', false);
    EditorContextKeys.hasCodeActionsProvider = new RawContextKey('editorHasCodeActionsProvider', false);
    EditorContextKeys.hasCodeLensProvider = new RawContextKey('editorHasCodeLensProvider', false);
    EditorContextKeys.hasDefinitionProvider = new RawContextKey('editorHasDefinitionProvider', false);
    EditorContextKeys.hasDeclarationProvider = new RawContextKey('editorHasDeclarationProvider', false);
    EditorContextKeys.hasImplementationProvider = new RawContextKey('editorHasImplementationProvider', false);
    EditorContextKeys.hasTypeDefinitionProvider = new RawContextKey('editorHasTypeDefinitionProvider', false);
    EditorContextKeys.hasHoverProvider = new RawContextKey('editorHasHoverProvider', false);
    EditorContextKeys.hasDocumentHighlightProvider = new RawContextKey('editorHasDocumentHighlightProvider', false);
    EditorContextKeys.hasDocumentSymbolProvider = new RawContextKey('editorHasDocumentSymbolProvider', false);
    EditorContextKeys.hasReferenceProvider = new RawContextKey('editorHasReferenceProvider', false);
    EditorContextKeys.hasRenameProvider = new RawContextKey('editorHasRenameProvider', false);
    EditorContextKeys.hasSignatureHelpProvider = new RawContextKey('editorHasSignatureHelpProvider', false);
    // -- mode context keys: formatting
    EditorContextKeys.hasDocumentFormattingProvider = new RawContextKey('editorHasDocumentFormattingProvider', false);
    EditorContextKeys.hasDocumentSelectionFormattingProvider = new RawContextKey('editorHasDocumentSelectionFormattingProvider', false);
    EditorContextKeys.hasMultipleDocumentFormattingProvider = new RawContextKey('editorHasMultipleDocumentFormattingProvider', false);
    EditorContextKeys.hasMultipleDocumentSelectionFormattingProvider = new RawContextKey('editorHasMultipleDocumentSelectionFormattingProvider', false);
})(EditorContextKeys || (EditorContextKeys = {}));
