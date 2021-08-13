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
 * @param {string} string
 * @param {...*} vararg
 * @return {string}
 */
export function UIString(string, ...vararg) {
  return Platform.StringUtilities.vsprintf(localize(string), Array.prototype.slice.call(arguments, 1));
}

/**
 * @param {string} string
 * @param {?ArrayLike<*>} values
 * @return {string}
 */
export function serializeUIString(string, values = []) {
  const messageParts = [string];
  const serializedMessage = {messageParts, values};
  return JSON.stringify(serializedMessage);
}

/**
 * @param {string=} serializedMessage
 * @return {*}
 */
export function deserializeUIString(serializedMessage) {
  if (!serializedMessage) {
    return {};
  }

  return JSON.parse(serializedMessage);
}

/**
 * @param {string} string
 * @return {string}
 */
export function localize(string) {
  return string;
}

/**
 * @unrestricted
 */
export class UIStringFormat {
  /**
   * @param {string} format
   */
  constructor(format) {
    /** @type {string} */
    this._localizedFormat = localize(format);
    /** @type {!Array.<!Object>} */
    this._tokenizedFormat = Platform.StringUtilities.tokenizeFormatString(
        this._localizedFormat, Platform.StringUtilities.standardFormatters);
  }

  /**
   * @param {string} a
   * @param {string} b
   * @return {string}
   */
  static _append(a, b) {
    return a + b;
  }

  /**
   * @param {...*} vararg
   * @return {string}
   */
  format(vararg) {
    return Platform.StringUtilities
        .format(
          // the code here uses odd generics that Closure likes but TS doesn't
          // so rather than fight to typecheck this in a dodgy way we just let TS ignore it
          // @ts-ignore
            this._localizedFormat, arguments, Platform.StringUtilities.standardFormatters, '', UIStringFormat._append,
            this._tokenizedFormat)
        .formattedResult;
  }
}

const _substitutionStrings = new WeakMap();

/**
 * @param {!ITemplateArray|string} strings
 * @param {...*} vararg
 * @return {string}
 */
export function ls(strings, ...vararg) {
  if (typeof strings === 'string') {
    return strings;
  }
  let substitutionString = _substitutionStrings.get(strings);
  if (!substitutionString) {
    substitutionString = strings.join('%s');
    _substitutionStrings.set(strings, substitutionString);
  }
  // @ts-ignore TS gets confused with the arguments slicing
  return UIString(substitutionString, ...vararg);
}
