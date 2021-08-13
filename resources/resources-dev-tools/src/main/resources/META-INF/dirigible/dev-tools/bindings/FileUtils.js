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
import * as Workspace from '../workspace/workspace.js';

/**
 * @interface
 */
export class ChunkedReader {
  /**
   * @return {number}
   */
  fileSize() {
  }

  /**
   * @return {number}
   */
  loadedSize() {
  }

  /**
   * @return {string}
   */
  fileName() {
  }

  cancel() {
  }

  /**
   * @return {?FileError}
   */
  error() {}
}

/**
 * @implements {ChunkedReader}
 * @unrestricted
 */
export class ChunkedFileReader {
  /**
   * @param {!Blob} blob
   * @param {number} chunkSize
   * @param {function(!ChunkedReader)=} chunkTransferredCallback
   */
  constructor(blob, chunkSize, chunkTransferredCallback) {
    this._file = blob;
    this._fileSize = blob.size;
    this._loadedSize = 0;
    this._chunkSize = chunkSize;
    this._chunkTransferredCallback = chunkTransferredCallback;
    this._decoder = new TextDecoder();
    this._isCanceled = false;
    /** @type {?FileError} */
    this._error = null;
  }

  /**
   * @param {!Common.StringOutputStream.OutputStream} output
   * @return {!Promise<boolean>}
   */
  read(output) {
    if (this._chunkTransferredCallback) {
      this._chunkTransferredCallback(this);
    }
    this._output = output;
    this._reader = new FileReader();
    this._reader.onload = this._onChunkLoaded.bind(this);
    this._reader.onerror = this._onError.bind(this);
    this._loadChunk();
    return new Promise(resolve => this._transferFinished = resolve);
  }

  /**
   * @override
   */
  cancel() {
    this._isCanceled = true;
  }

  /**
   * @override
   * @return {number}
   */
  loadedSize() {
    return this._loadedSize;
  }

  /**
   * @override
   * @return {number}
   */
  fileSize() {
    return this._fileSize;
  }

  /**
   * @override
   * @return {string}
   */
  fileName() {
    return this._file.name;
  }

  /**
   * @override
   * @return {?FileError}
   */
  error() {
    return this._error;
  }

  /**
   * @param {!Event} event
   */
  _onChunkLoaded(event) {
    if (this._isCanceled) {
      return;
    }

    if (event.target.readyState !== FileReader.DONE) {
      return;
    }

    const buffer = this._reader.result;
    this._loadedSize += buffer.byteLength;
    const endOfFile = this._loadedSize === this._fileSize;
    const decodedString = this._decoder.decode(buffer, {stream: !endOfFile});
    this._output.write(decodedString);
    if (this._isCanceled) {
      return;
    }
    if (this._chunkTransferredCallback) {
      this._chunkTransferredCallback(this);
    }

    if (endOfFile) {
      this._file = null;
      this._reader = null;
      this._output.close();
      this._transferFinished(!this._error);
      return;
    }

    this._loadChunk();
  }

  _loadChunk() {
    const chunkStart = this._loadedSize;
    const chunkEnd = Math.min(this._fileSize, chunkStart + this._chunkSize);
    const nextPart = this._file.slice(chunkStart, chunkEnd);
    this._reader.readAsArrayBuffer(nextPart);
  }

  /**
   * @param {!Event} event
   */
  _onError(event) {
    this._error = event.target.error;
    this._transferFinished(false);
  }
}

/**
 * @implements {Common.StringOutputStream.OutputStream}
 * @unrestricted
 */
export class FileOutputStream {
  /**
   * @param {string} fileName
   * @return {!Promise<boolean>}
   */
  async open(fileName) {
    this._closed = false;
    /** @type {!Array<function()>} */
    this._writeCallbacks = [];
    this._fileName = fileName;
    const saveResponse = await self.Workspace.fileManager.save(this._fileName, '', true);
    if (saveResponse) {
      self.Workspace.fileManager.addEventListener(Workspace.FileManager.Events.AppendedToURL, this._onAppendDone, this);
    }
    return !!saveResponse;
  }

  /**
   * @override
   * @param {string} data
   * @return {!Promise}
   */
  write(data) {
    return new Promise(resolve => {
      this._writeCallbacks.push(resolve);
      self.Workspace.fileManager.append(this._fileName, data);
    });
  }

  /**
   * @override
   */
  async close() {
    this._closed = true;
    if (this._writeCallbacks.length) {
      return;
    }
    self.Workspace.fileManager.removeEventListener(
        Workspace.FileManager.Events.AppendedToURL, this._onAppendDone, this);
    self.Workspace.fileManager.close(this._fileName);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onAppendDone(event) {
    if (event.data !== this._fileName) {
      return;
    }
    this._writeCallbacks.shift()();
    if (this._writeCallbacks.length) {
      return;
    }
    if (!this._closed) {
      return;
    }
    self.Workspace.fileManager.removeEventListener(
        Workspace.FileManager.Events.AppendedToURL, this._onAppendDone, this);
    self.Workspace.fileManager.close(this._fileName);
  }
}
