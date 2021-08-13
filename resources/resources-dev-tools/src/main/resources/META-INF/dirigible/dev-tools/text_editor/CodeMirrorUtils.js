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
import * as TextUtils from '../text_utils/text_utils.js';

/**
 * @param {!TextUtils.TextRange.TextRange} range
 * @return {!{start: !CodeMirror.Pos, end: !CodeMirror.Pos}}
 */
export function toPos(range) {
  return {
    start: new CodeMirror.Pos(range.startLine, range.startColumn),
    end: new CodeMirror.Pos(range.endLine, range.endColumn)
  };
}

/**
 * @param {!CodeMirror.Pos} start
 * @param {!CodeMirror.Pos} end
 * @return {!TextUtils.TextRange.TextRange}
 */
export function toRange(start, end) {
  return new TextUtils.TextRange.TextRange(start.line, start.ch, end.line, end.ch);
}

/**
 * @param {!CodeMirror.ChangeObject} changeObject
 * @return {{oldRange: !TextUtils.TextRange.TextRange, newRange: !TextUtils.TextRange.TextRange}}
 */
export function changeObjectToEditOperation(changeObject) {
  const oldRange = toRange(changeObject.from, changeObject.to);
  const newRange = oldRange.clone();
  const linesAdded = changeObject.text.length;
  if (linesAdded === 0) {
    newRange.endLine = newRange.startLine;
    newRange.endColumn = newRange.startColumn;
  } else if (linesAdded === 1) {
    newRange.endLine = newRange.startLine;
    newRange.endColumn = newRange.startColumn + changeObject.text[0].length;
  } else {
    newRange.endLine = newRange.startLine + linesAdded - 1;
    newRange.endColumn = changeObject.text[linesAdded - 1].length;
  }
  return {oldRange: oldRange, newRange: newRange};
}

/**
 * @param {!CodeMirror} codeMirror
 * @param {number} linesCount
 * @return {!Array.<string>}
 */
export function pullLines(codeMirror, linesCount) {
  const lines = [];
  codeMirror.eachLine(0, linesCount, onLineHandle);
  return lines;

  /**
   * @param {!{text: string}} lineHandle
   */
  function onLineHandle(lineHandle) {
    lines.push(lineHandle.text);
  }
}

/**
 * @implements {TextUtils.TextUtils.TokenizerFactory}
 * @unrestricted
 */
export class TokenizerFactory {
  /**
   * @override
   * @param {string} mimeType
   * @return {function(string, function(string, ?string, number, number))}
   */
  createTokenizer(mimeType) {
    const mode = CodeMirror.getMode({indentUnit: 2}, mimeType);
    const state = CodeMirror.startState(mode);
    function tokenize(line, callback) {
      const stream = new CodeMirror.StringStream(line);
      while (!stream.eol()) {
        const style = mode.token(stream, state);
        const value = stream.current();
        callback(value, style, stream.start, stream.start + value.length);
        stream.start = stream.pos;
      }
    }
    return tokenize;
  }
}
