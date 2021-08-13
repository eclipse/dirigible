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
 * @unrestricted
 */
export class FilePathScoreFunction {
  /**
   * @param {string} query
   */
  constructor(query) {
    this._query = query;
    this._queryUpperCase = query.toUpperCase();
    this._score = new Int32Array(20 * 100);
    this._sequence = new Int32Array(20 * 100);
    this._dataUpperCase = '';
    this._fileNameIndex = 0;
  }

  /**
   * @param {string} data
   * @param {?Array<number>} matchIndexes
   * @return {number}
   */
  score(data, matchIndexes) {
    if (!data || !this._query) {
      return 0;
    }
    const n = this._query.length;
    const m = data.length;
    if (!this._score || this._score.length < n * m) {
      this._score = new Int32Array(n * m * 2);
      this._sequence = new Int32Array(n * m * 2);
    }
    const score = this._score;
    const sequence = /** @type {!Int32Array} */ (this._sequence);
    this._dataUpperCase = data.toUpperCase();
    this._fileNameIndex = data.lastIndexOf('/');
    for (let i = 0; i < n; ++i) {
      for (let j = 0; j < m; ++j) {
        const skipCharScore = j === 0 ? 0 : score[i * m + j - 1];
        const prevCharScore = i === 0 || j === 0 ? 0 : score[(i - 1) * m + j - 1];
        const consecutiveMatch = i === 0 || j === 0 ? 0 : sequence[(i - 1) * m + j - 1];
        const pickCharScore = this._match(this._query, data, i, j, consecutiveMatch);
        if (pickCharScore && prevCharScore + pickCharScore >= skipCharScore) {
          sequence[i * m + j] = consecutiveMatch + 1;
          score[i * m + j] = (prevCharScore + pickCharScore);
        } else {
          sequence[i * m + j] = 0;
          score[i * m + j] = skipCharScore;
        }
      }
    }
    if (matchIndexes) {
      this._restoreMatchIndexes(sequence, n, m, matchIndexes);
    }
    const maxDataLength = 256;
    return score[n * m - 1] * maxDataLength + (maxDataLength - data.length);
  }

  /**
   * @param {string} data
   * @param {number} j
   * @return {boolean}
   */
  _testWordStart(data, j) {
    if (j === 0) {
      return true;
    }

    const prevChar = data.charAt(j - 1);
    return prevChar === '_' || prevChar === '-' || prevChar === '/' ||
        (data[j - 1] !== this._dataUpperCase[j - 1] && data[j] === this._dataUpperCase[j]);
  }

  /**
   * @param {!Int32Array} sequence
   * @param {number} n
   * @param {number} m
   * @param {!Array<number>} out
   */
  _restoreMatchIndexes(sequence, n, m, out) {
    let i = n - 1, j = m - 1;
    while (i >= 0 && j >= 0) {
      switch (sequence[i * m + j]) {
        case 0:
          --j;
          break;
        default:
          out.push(j);
          --i;
          --j;
          break;
      }
    }
    out.reverse();
  }

  /**
   * @param {string} query
   * @param {string} data
   * @param {number} i
   * @param {number} j
   * @return {number}
   */
  _singleCharScore(query, data, i, j) {
    const isWordStart = this._testWordStart(data, j);
    const isFileName = j > this._fileNameIndex;
    const isPathTokenStart = j === 0 || data[j - 1] === '/';
    const isCapsMatch = query[i] === data[j] && query[i] === this._queryUpperCase[i];
    let score = 10;
    if (isPathTokenStart) {
      score += 4;
    }
    if (isWordStart) {
      score += 2;
    }
    if (isCapsMatch) {
      score += 6;
    }
    if (isFileName) {
      score += 4;
    }
    // promote the case of making the whole match in the filename
    if (j === this._fileNameIndex + 1 && i === 0) {
      score += 5;
    }
    if (isFileName && isWordStart) {
      score += 3;
    }
    return score;
  }

  /**
   * @param {string} query
   * @param {string} data
   * @param {number} i
   * @param {number} j
   * @param {number} sequenceLength
   * @return {number}
   */
  _sequenceCharScore(query, data, i, j, sequenceLength) {
    const isFileName = j > this._fileNameIndex;
    const isPathTokenStart = j === 0 || data[j - 1] === '/';
    let score = 10;
    if (isFileName) {
      score += 4;
    }
    if (isPathTokenStart) {
      score += 5;
    }
    score += sequenceLength * 4;
    return score;
  }

  /**
   * @param {string} query
   * @param {string} data
   * @param {number} i
   * @param {number} j
   * @param {number} consecutiveMatch
   * @return {number}
   */
  _match(query, data, i, j, consecutiveMatch) {
    if (this._queryUpperCase[i] !== this._dataUpperCase[j]) {
      return 0;
    }

    if (!consecutiveMatch) {
      return this._singleCharScore(query, data, i, j);
    }
    return this._sequenceCharScore(query, data, i, j - consecutiveMatch, consecutiveMatch);
  }
}
