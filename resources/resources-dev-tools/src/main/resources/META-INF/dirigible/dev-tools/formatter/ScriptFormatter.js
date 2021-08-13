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
import * as Platform from '../platform/platform.js';

import {FormatMapping, FormatResult, formatterWorkerPool} from './FormatterWorkerPool.js';  // eslint-disable-line no-unused-vars

/**
 * @interface
 */
export class FormatterInterface {}

/**
 * @param {!Common.ResourceType.ResourceType} contentType
 * @param {string} mimeType
 * @param {string} content
 * @param {function(string, !FormatterSourceMapping)} callback
 */
FormatterInterface.format = function(contentType, mimeType, content, callback) {
  if (contentType.isDocumentOrScriptOrStyleSheet()) {
    new ScriptFormatter(mimeType, content, callback);
  } else {
    new ScriptIdentityFormatter(mimeType, content, callback);
  }
};

/**
 * @param {!Array<number>} lineEndings
 * @param {number} lineNumber
 * @param {number} columnNumber
 * @return {number}
 */
FormatterInterface.locationToPosition = function(lineEndings, lineNumber, columnNumber) {
  const position = lineNumber ? lineEndings[lineNumber - 1] + 1 : 0;
  return position + columnNumber;
};
/**
 * @param {!Array<number>} lineEndings
 * @param {number} position
 * @return {!Array<number>}
 */
FormatterInterface.positionToLocation = function(lineEndings, position) {
  const lineNumber = lineEndings.upperBound(position - 1);
  let columnNumber;
  if (!lineNumber) {
    columnNumber = position;
  } else {
    columnNumber = position - lineEndings[lineNumber - 1] - 1;
  }
  return [lineNumber, columnNumber];
};

/**
 * @implements {FormatterInterface}
 * @unrestricted
 */
export class ScriptFormatter {
  /**
   * @param {string} mimeType
   * @param {string} content
   * @param {function(string, !FormatterSourceMapping)} callback
   */
  constructor(mimeType, content, callback) {
    content = content.replace(/\r\n?|[\n\u2028\u2029]/g, '\n').replace(/^\uFEFF/, '');
    this._callback = callback;
    this._originalContent = content;

    formatterWorkerPool()
        .format(mimeType, content, Common.Settings.Settings.instance().moduleSetting('textEditorIndent').get())
        .then(this._didFormatContent.bind(this));
  }

  /**
   * @param {!FormatResult} formatResult
   */
  _didFormatContent(formatResult) {
    const originalContentLineEndings = Platform.StringUtilities.findLineEndingIndexes(this._originalContent);
    const formattedContentLineEndings = Platform.StringUtilities.findLineEndingIndexes(formatResult.content);

    const sourceMapping =
        new FormatterSourceMappingImpl(originalContentLineEndings, formattedContentLineEndings, formatResult.mapping);
    this._callback(formatResult.content, sourceMapping);
  }
}

/**
 * @implements {FormatterInterface}
 * @unrestricted
 */
class ScriptIdentityFormatter {
  /**
   * @param {string} mimeType
   * @param {string} content
   * @param {function(string, !FormatterSourceMapping)} callback
   */
  constructor(mimeType, content, callback) {
    callback(content, new IdentityFormatterSourceMapping());
  }
}

/**
 * @interface
 */
export class FormatterSourceMapping {
  /**
   * @param {number} lineNumber
   * @param {number=} columnNumber
   * @return {!Array.<number>}
   */
  originalToFormatted(lineNumber, columnNumber) {
  }

  /**
   * @param {number} lineNumber
   * @param {number=} columnNumber
   * @return {!Array.<number>}
   */
  formattedToOriginal(lineNumber, columnNumber) {}
}

/**
 * @implements {FormatterSourceMapping}
 * @unrestricted
 */
class IdentityFormatterSourceMapping {
  /**
   * @override
   * @param {number} lineNumber
   * @param {number=} columnNumber
   * @return {!Array.<number>}
   */
  originalToFormatted(lineNumber, columnNumber) {
    return [lineNumber, columnNumber || 0];
  }

  /**
   * @override
   * @param {number} lineNumber
   * @param {number=} columnNumber
   * @return {!Array.<number>}
   */
  formattedToOriginal(lineNumber, columnNumber) {
    return [lineNumber, columnNumber || 0];
  }
}

/**
 * @implements {FormatterSourceMapping}
 * @unrestricted
 */
class FormatterSourceMappingImpl {
  /**
   * @param {!Array.<number>} originalLineEndings
   * @param {!Array.<number>} formattedLineEndings
   * @param {!FormatMapping} mapping
   */
  constructor(originalLineEndings, formattedLineEndings, mapping) {
    this._originalLineEndings = originalLineEndings;
    this._formattedLineEndings = formattedLineEndings;
    this._mapping = mapping;
  }

  /**
   * @override
   * @param {number} lineNumber
   * @param {number=} columnNumber
   * @return {!Array.<number>}
   */
  originalToFormatted(lineNumber, columnNumber) {
    const originalPosition =
        FormatterInterface.locationToPosition(this._originalLineEndings, lineNumber, columnNumber || 0);
    const formattedPosition =
        this._convertPosition(this._mapping.original, this._mapping.formatted, originalPosition || 0);
    return FormatterInterface.positionToLocation(this._formattedLineEndings, formattedPosition);
  }

  /**
   * @override
   * @param {number} lineNumber
   * @param {number=} columnNumber
   * @return {!Array.<number>}
   */
  formattedToOriginal(lineNumber, columnNumber) {
    const formattedPosition =
        FormatterInterface.locationToPosition(this._formattedLineEndings, lineNumber, columnNumber || 0);
    const originalPosition = this._convertPosition(this._mapping.formatted, this._mapping.original, formattedPosition);
    return FormatterInterface.positionToLocation(this._originalLineEndings, originalPosition || 0);
  }

  /**
   * @param {!Array.<number>} positions1
   * @param {!Array.<number>} positions2
   * @param {number} position
   * @return {number}
   */
  _convertPosition(positions1, positions2, position) {
    const index = positions1.upperBound(position) - 1;
    let convertedPosition = positions2[index] + position - positions1[index];
    if (index < positions2.length - 1 && convertedPosition > positions2[index + 1]) {
      convertedPosition = positions2[index + 1];
    }
    return convertedPosition;
  }
}
