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
import * as Common from '../common/common.js';  // eslint-disable-line no-unused-vars

import * as SDK from '../sdk/sdk.js';  // eslint-disable-line no-unused-vars

import {ChunkedFileReader, ChunkedReader} from './FileUtils.js';  // eslint-disable-line no-unused-vars

export class TempFile {
  constructor() {
    /** @type {?Blob} */
    this._lastBlob = null;
  }

  /**
   * @param {!Array<string|!Blob>} pieces
   */
  write(pieces) {
    if (this._lastBlob) {
      pieces.unshift(this._lastBlob);
    }
    this._lastBlob = new Blob(pieces, {type: 'text/plain'});
  }

  /**
   * @return {!Promise<?string>}
   */
  read() {
    return this.readRange();
  }

  /**
   * @return {number}
   */
  size() {
    return this._lastBlob ? this._lastBlob.size : 0;
  }

  /**
   * @param {number=} startOffset
   * @param {number=} endOffset
   * @return {!Promise<?string>}
   */
  async readRange(startOffset, endOffset) {
    if (!this._lastBlob) {
      Common.Console.Console.instance().error('Attempt to read a temp file that was never written');
      return Promise.resolve('');
    }
    const blob = typeof startOffset === 'number' || typeof endOffset === 'number' ?
        this._lastBlob.slice(/** @type {number} */ (startOffset), /** @type {number} */ (endOffset)) :
        this._lastBlob;

    const reader = new FileReader();
    try {
      await new Promise((resolve, reject) => {
        reader.onloadend = resolve;
        reader.onerror = reject;
        reader.readAsText(blob);
      });
    } catch (error) {
      Common.Console.Console.instance().error('Failed to read from temp file: ' + error.message);
    }

    return /** @type {?string} */ (reader.result);
  }

  /**
   * @param {!Common.StringOutputStream.OutputStream} outputStream
   * @param {function(!ChunkedReader)=} progress
   * @return {!Promise<?FileError>}
   */
  copyToOutputStream(outputStream, progress) {
    if (!this._lastBlob) {
      outputStream.close();
      return Promise.resolve(/** @type {?FileError} */ (null));
    }
    const reader = new ChunkedFileReader(/** @type {!Blob} */ (this._lastBlob), 10 * 1000 * 1000, progress);
    return reader.read(outputStream).then(success => success ? null : reader.error());
  }

  remove() {
    this._lastBlob = null;
  }
}

/**
 * @implements {SDK.TracingModel.BackingStorage}
 */
export class TempFileBackingStorage {
  constructor() {
    /** @type {?TempFile} */
    this._file = null;
    /** @type {!Array<string>} */
    this._strings;
    /** @type {number} */
    this._stringsLength;
    this.reset();
  }

  /**
   * @override
   * @param {string} string
   */
  appendString(string) {
    this._strings.push(string);
    this._stringsLength += string.length;
    const flushStringLength = 10 * 1024 * 1024;
    if (this._stringsLength > flushStringLength) {
      this._flush();
    }
  }

  /**
   * @override
   * @param {string} string
   * @return {function():!Promise<?string>}
   */
  appendAccessibleString(string) {
    this._flush();
    const startOffset = this._file.size();
    this._strings.push(string);
    this._flush();
    return this._file.readRange.bind(this._file, startOffset, this._file.size());
  }

  _flush() {
    if (!this._strings.length) {
      return;
    }
    if (!this._file) {
      this._file = new TempFile();
    }
    this._stringsLength = 0;
    this._file.write(this._strings.splice(0));
  }

  /**
   * @override
   */
  finishWriting() {
    this._flush();
  }

  /**
   * @override
   */
  reset() {
    if (this._file) {
      this._file.remove();
    }
    this._file = null;
    /** @type {!Array<string>} */
    this._strings = [];
    this._stringsLength = 0;
  }

  /**
   * @param {!Common.StringOutputStream.OutputStream} outputStream
   * @return {!Promise<?FileError>}
   */
  writeToStream(outputStream) {
    return this._file ? this._file.copyToOutputStream(outputStream) : Promise.resolve(null);
  }
}
