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

/**
 * @interface
 */
export class ContentProvider {
  /**
   * @return {string}
   */
  contentURL() {
    throw new Error('not implemented');
  }

  /**
   * @return {!Common.ResourceType.ResourceType}
   */
  contentType() {
    throw new Error('not implemented');
  }

  /**
   * @return {!Promise<boolean>}
   */
  contentEncoded() {
    throw new Error('not implemented');
  }

  /**
   * @return {!Promise<!DeferredContent>}
   */
  requestContent() {
    throw new Error('not implemented');
  }

  /**
   * @param {string} query
   * @param {boolean} caseSensitive
   * @param {boolean} isRegex
   * @return {!Promise<!Array<!SearchMatch>>}
   */
  searchInContent(query, caseSensitive, isRegex) {
    throw new Error('not implemented');
  }
}

/**
 * @unrestricted
 */
export class SearchMatch {
  /**
   * @param {number} lineNumber
   * @param {string} lineContent
   */
  constructor(lineNumber, lineContent) {
    this.lineNumber = lineNumber;
    this.lineContent = lineContent;
  }
}

/**
 * @param {?string} content
 * @param {string} mimeType
 * @param {boolean} contentEncoded
 * @param {?string=} charset
 * @return {?string}
 */
export const contentAsDataURL = function(content, mimeType, contentEncoded, charset) {
  const maxDataUrlSize = 1024 * 1024;
  if (content === undefined || content === null || content.length > maxDataUrlSize) {
    return null;
  }

  return 'data:' + mimeType + (charset ? ';charset=' + charset : '') + (contentEncoded ? ';base64' : '') + ',' +
      content;
};

/**
 * @typedef {{
 *    content: string,
 *    isEncoded: boolean,
 * }|{
 *    error: string,
 *    isEncoded: boolean,
 * }}
 */
// @ts-ignore typedef
export let DeferredContent;
