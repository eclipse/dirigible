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
/**
 * @internal
 */
export function isThemeColor(o) {
    return o && typeof o.id === 'string';
}
/**
 * The type of the `IEditor`.
 */
export var EditorType = {
    ICodeEditor: 'vs.editor.ICodeEditor',
    IDiffEditor: 'vs.editor.IDiffEditor'
};
/**
 * Built-in commands.
 * @internal
 */
export var Handler = {
    ExecuteCommand: 'executeCommand',
    ExecuteCommands: 'executeCommands',
    Type: 'type',
    ReplacePreviousChar: 'replacePreviousChar',
    CompositionStart: 'compositionStart',
    CompositionEnd: 'compositionEnd',
    Paste: 'paste',
    Cut: 'cut',
    Undo: 'undo',
    Redo: 'redo',
};
