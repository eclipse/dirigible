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
import * as Platform from '../platform/platform.js';

/**
 * @unrestricted
 */
export class TextRange {
  /**
   * @param {number} startLine
   * @param {number} startColumn
   * @param {number} endLine
   * @param {number} endColumn
   */
  constructor(startLine, startColumn, endLine, endColumn) {
    this.startLine = startLine;
    this.startColumn = startColumn;
    this.endLine = endLine;
    this.endColumn = endColumn;
  }

  /**
   * @param {number} line
   * @param {number} column
   * @return {!TextRange}
   */
  static createFromLocation(line, column) {
    return new TextRange(line, column, line, column);
  }

  /**
   * @param {{startLine: number, startColumn: number, endLine: number, endColumn: number}} serializedTextRange
   * @return {!TextRange}
   */
  static fromObject(serializedTextRange) {
    return new TextRange(
        serializedTextRange.startLine, serializedTextRange.startColumn, serializedTextRange.endLine,
        serializedTextRange.endColumn);
  }

  /**
   * @param {!TextRange} range1
   * @param {!TextRange} range2
   * @return {number}
   */
  static comparator(range1, range2) {
    return range1.compareTo(range2);
  }

  /**
   * @param {!TextRange} oldRange
   * @param {string} newText
   * @return {!TextRange}
   */
  static fromEdit(oldRange, newText) {
    let endLine = oldRange.startLine;
    let endColumn = oldRange.startColumn + newText.length;

    const lineEndings = Platform.StringUtilities.findLineEndingIndexes(newText);
    if (lineEndings.length > 1) {
      endLine = oldRange.startLine + lineEndings.length - 1;
      const len = lineEndings.length;
      endColumn = lineEndings[len - 1] - lineEndings[len - 2] - 1;
    }
    return new TextRange(oldRange.startLine, oldRange.startColumn, endLine, endColumn);
  }

  /**
   * @return {boolean}
   */
  isEmpty() {
    return this.startLine === this.endLine && this.startColumn === this.endColumn;
  }

  /**
   * @param {!TextRange=} range
   * @return {boolean}
   */
  immediatelyPrecedes(range) {
    if (!range) {
      return false;
    }
    return this.endLine === range.startLine && this.endColumn === range.startColumn;
  }

  /**
   * @param {!TextRange=} range
   * @return {boolean}
   */
  immediatelyFollows(range) {
    if (!range) {
      return false;
    }
    return range.immediatelyPrecedes(this);
  }

  /**
   * @param {!TextRange} range
   * @return {boolean}
   */
  follows(range) {
    return (range.endLine === this.startLine && range.endColumn <= this.startColumn) || range.endLine < this.startLine;
  }

  /**
   * @return {number}
   */
  get linesCount() {
    return this.endLine - this.startLine;
  }

  /**
   * @return {!TextRange}
   */
  collapseToEnd() {
    return new TextRange(this.endLine, this.endColumn, this.endLine, this.endColumn);
  }

  /**
   * @return {!TextRange}
   */
  collapseToStart() {
    return new TextRange(this.startLine, this.startColumn, this.startLine, this.startColumn);
  }

  /**
   * @return {!TextRange}
   */
  normalize() {
    if (this.startLine > this.endLine || (this.startLine === this.endLine && this.startColumn > this.endColumn)) {
      return new TextRange(this.endLine, this.endColumn, this.startLine, this.startColumn);
    }
    return this.clone();
  }

  /**
   * @return {!TextRange}
   */
  clone() {
    return new TextRange(this.startLine, this.startColumn, this.endLine, this.endColumn);
  }

  /**
   * @return {!{startLine: number, startColumn: number, endLine: number, endColumn: number}}
   */
  serializeToObject() {
    const serializedTextRange = {};
    serializedTextRange.startLine = this.startLine;
    serializedTextRange.startColumn = this.startColumn;
    serializedTextRange.endLine = this.endLine;
    serializedTextRange.endColumn = this.endColumn;
    return serializedTextRange;
  }

  /**
   * @param {!TextRange} other
   * @return {number}
   */
  compareTo(other) {
    if (this.startLine > other.startLine) {
      return 1;
    }
    if (this.startLine < other.startLine) {
      return -1;
    }
    if (this.startColumn > other.startColumn) {
      return 1;
    }
    if (this.startColumn < other.startColumn) {
      return -1;
    }
    return 0;
  }

  /**
   * @param {number} lineNumber
   * @param {number} columnNumber
   * @return {number}
   */
  compareToPosition(lineNumber, columnNumber) {
    if (lineNumber < this.startLine || (lineNumber === this.startLine && columnNumber < this.startColumn)) {
      return -1;
    }
    if (lineNumber > this.endLine || (lineNumber === this.endLine && columnNumber > this.endColumn)) {
      return 1;
    }
    return 0;
  }

  /**
   * @param {!TextRange} other
   * @return {boolean}
   */
  equal(other) {
    return this.startLine === other.startLine && this.endLine === other.endLine &&
        this.startColumn === other.startColumn && this.endColumn === other.endColumn;
  }

  /**
   * @param {number} line
   * @param {number} column
   * @return {!TextRange}
   */
  relativeTo(line, column) {
    const relative = this.clone();

    if (this.startLine === line) {
      relative.startColumn -= column;
    }
    if (this.endLine === line) {
      relative.endColumn -= column;
    }

    relative.startLine -= line;
    relative.endLine -= line;
    return relative;
  }

  /**
   * @param {number} line
   * @param {number} column
   * @return {!TextRange}
   */
  relativeFrom(line, column) {
    const relative = this.clone();

    if (this.startLine === 0) {
      relative.startColumn += column;
    }
    if (this.endLine === 0) {
      relative.endColumn += column;
    }

    relative.startLine += line;
    relative.endLine += line;
    return relative;
  }

  /**
   * @param {!TextRange} originalRange
   * @param {!TextRange} editedRange
   * @return {!TextRange}
   */
  rebaseAfterTextEdit(originalRange, editedRange) {
    console.assert(originalRange.startLine === editedRange.startLine);
    console.assert(originalRange.startColumn === editedRange.startColumn);
    const rebase = this.clone();
    if (!this.follows(originalRange)) {
      return rebase;
    }
    const lineDelta = editedRange.endLine - originalRange.endLine;
    const columnDelta = editedRange.endColumn - originalRange.endColumn;
    rebase.startLine += lineDelta;
    rebase.endLine += lineDelta;
    if (rebase.startLine === editedRange.endLine) {
      rebase.startColumn += columnDelta;
    }
    if (rebase.endLine === editedRange.endLine) {
      rebase.endColumn += columnDelta;
    }
    return rebase;
  }

  /**
   * @override
   * @return {string}
   */
  toString() {
    return JSON.stringify(this);
  }

  /**
   * @param {number} lineNumber
   * @param {number} columnNumber
   * @return {boolean}
   */
  containsLocation(lineNumber, columnNumber) {
    if (this.startLine === this.endLine) {
      return this.startLine === lineNumber && this.startColumn <= columnNumber && columnNumber <= this.endColumn;
    }
    if (this.startLine === lineNumber) {
      return this.startColumn <= columnNumber;
    }
    if (this.endLine === lineNumber) {
      return columnNumber <= this.endColumn;
    }
    return this.startLine < lineNumber && lineNumber < this.endLine;
  }
}

/**
 * @unrestricted
 */
export class SourceRange {
  /**
   * @param {number} offset
   * @param {number} length
   */
  constructor(offset, length) {
    this.offset = offset;
    this.length = length;
  }
}

/**
 * @unrestricted
 */
export class SourceEdit {
  /**
   * @param {string} sourceURL
   * @param {!TextRange} oldRange
   * @param {string} newText
   */
  constructor(sourceURL, oldRange, newText) {
    this.sourceURL = sourceURL;
    this.oldRange = oldRange;
    this.newText = newText;
  }

  /**
   * @param {!SourceEdit} edit1
   * @param {!SourceEdit} edit2
   * @return {number}
   */
  static comparator(edit1, edit2) {
    return TextRange.comparator(edit1.oldRange, edit2.oldRange);
  }

  /**
   * @return {!TextRange}
   */
  newRange() {
    return TextRange.fromEdit(this.oldRange, this.newText);
  }
}
