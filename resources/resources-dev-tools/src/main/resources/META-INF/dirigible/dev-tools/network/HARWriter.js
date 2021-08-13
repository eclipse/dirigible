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
import * as Common from '../common/common.js';
import * as Platform from '../platform/platform.js';
import * as SDK from '../sdk/sdk.js';

export class HARWriter {
  /**
   * @param {!Common.StringOutputStream.OutputStream} stream
   * @param {!Array.<!SDK.NetworkRequest.NetworkRequest>} requests
   * @param {!Common.Progress.Progress} progress
   * @return {!Promise}
   */
  static async write(stream, requests, progress) {
    const compositeProgress = new Common.Progress.CompositeProgress(progress);

    const content = await HARWriter._harStringForRequests(requests, compositeProgress);
    if (progress.isCanceled()) {
      return Promise.resolve();
    }
    return HARWriter._writeToStream(stream, compositeProgress, content);
  }

  /**
   * @param {!Array<!SDK.NetworkRequest.NetworkRequest>} requests
   * @param {!Common.Progress.CompositeProgress} compositeProgress
   * @return {!Promise<string>}
   */
  static async _harStringForRequests(requests, compositeProgress) {
    const progress = compositeProgress.createSubProgress();
    progress.setTitle(Common.UIString.UIString('Collecting content…'));
    progress.setTotalWork(requests.length);

    const harLog = await SDK.HARLog.HARLog.build(requests);
    const promises = [];
    for (let i = 0; i < requests.length; i++) {
      const promise = requests[i].contentData();
      promises.push(promise.then(contentLoaded.bind(null, harLog.entries[i])));
    }

    await Promise.all(promises);
    progress.done();

    if (progress.isCanceled()) {
      return '';
    }
    return JSON.stringify({log: harLog}, null, _jsonIndent);

    function isValidCharacter(code_point) {
      // Excludes non-characters (U+FDD0..U+FDEF, and all codepoints ending in
      // 0xFFFE or 0xFFFF) from the set of valid code points.
      return code_point < 0xD800 || (code_point >= 0xE000 && code_point < 0xFDD0) ||
          (code_point > 0xFDEF && code_point <= 0x10FFFF && (code_point & 0xFFFE) !== 0xFFFE);
    }

    function needsEncoding(content) {
      for (let i = 0; i < content.length; i++) {
        if (!isValidCharacter(content.charCodeAt(i))) {
          return true;
        }
      }
      return false;
    }

    /**
     * @param {!Object} entry
     * @param {!SDK.NetworkRequest.ContentData} contentData
     */
    function contentLoaded(entry, contentData) {
      progress.worked();
      let encoded = contentData.encoded;
      if (contentData.content !== null) {
        let content = contentData.content;
        if (content && !encoded && needsEncoding(content)) {
          content = Platform.StringUtilities.toBase64(content);
          encoded = true;
        }
        entry.response.content.text = content;
      }
      if (encoded) {
        entry.response.content.encoding = 'base64';
      }
    }
  }

  /**
   * @param {!Common.StringOutputStream.OutputStream} stream
   * @param {!Common.Progress.CompositeProgress} compositeProgress
   * @param {string} fileContent
   * @return {!Promise}
   */
  static async _writeToStream(stream, compositeProgress, fileContent) {
    const progress = compositeProgress.createSubProgress();
    progress.setTitle(Common.UIString.UIString('Writing file…'));
    progress.setTotalWork(fileContent.length);
    for (let i = 0; i < fileContent.length && !progress.isCanceled(); i += _chunkSize) {
      const chunk = fileContent.substr(i, _chunkSize);
      await stream.write(chunk);
      progress.worked(chunk.length);
    }
    progress.done();
  }
}

/** @const */
export const _jsonIndent = 2;

/** @const */
export const _chunkSize = 100000;
