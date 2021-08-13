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
export class SyntaxHighlighter {
  /**
   * @param {string} mimeType
   * @param {boolean} stripExtraWhitespace
   */
  constructor(mimeType, stripExtraWhitespace) {
    this._mimeType = mimeType;
    this._stripExtraWhitespace = stripExtraWhitespace;
  }

  /**
   * @param {string} content
   * @param {string} className
   * @return {!Element}
   */
  createSpan(content, className) {
    const span = createElement('span');
    span.className = className.replace(/\S+/g, 'cm-$&');
    if (this._stripExtraWhitespace && className !== 'whitespace') {
      content = content.replace(/^[\n\r]*/, '').replace(/\s*$/, '');
    }
    span.createTextChild(content);
    return span;
  }

  /**
   * @param {!Element} node
   * @return {!Promise.<undefined>}
   */
  syntaxHighlightNode(node) {
    const lines = node.textContent.split('\n');
    let plainTextStart;
    let line;

    return self.runtime.extension(TextUtils.TokenizerFactory).instance().then(processTokens.bind(this));

    /**
     * @param {!TextUtils.TokenizerFactory} tokenizerFactory
     * @this {SyntaxHighlighter}
     */
    function processTokens(tokenizerFactory) {
      node.removeChildren();
      const tokenize = tokenizerFactory.createTokenizer(this._mimeType);
      for (let i = 0; i < lines.length; ++i) {
        line = lines[i];
        plainTextStart = 0;
        tokenize(line, processToken.bind(this));
        if (plainTextStart < line.length) {
          const plainText = line.substring(plainTextStart, line.length);
          node.createTextChild(plainText);
        }
        if (i < lines.length - 1) {
          node.createTextChild('\n');
        }
      }
    }

    /**
     * @param {string} token
     * @param {?string} tokenType
     * @param {number} column
     * @param {number} newColumn
     * @this {SyntaxHighlighter}
     */
    function processToken(token, tokenType, column, newColumn) {
      if (!tokenType) {
        return;
      }

      if (column > plainTextStart) {
        const plainText = line.substring(plainTextStart, column);
        node.createTextChild(plainText);
      }
      node.appendChild(this.createSpan(token, tokenType));
      plainTextStart = newColumn;
    }
  }
}
