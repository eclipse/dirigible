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
import {VBox} from './Widget.js';
import {XLink} from './XLink.js';

/**
 * @unrestricted
 */
export class EmptyWidget extends VBox {
  /**
   * @param {string} text
   */
  constructor(text) {
    super();
    this.registerRequiredCSS('ui/emptyWidget.css');
    this.element.classList.add('empty-view-scroller');
    this._contentElement = this.element.createChild('div', 'empty-view');
    this._textElement = this._contentElement.createChild('div', 'empty-bold-text');
    this._textElement.textContent = text;
  }

  /**
   * @return {!Element}
   */
  appendParagraph() {
    return this._contentElement.createChild('p');
  }

  /**
   * @param {string} link
   * @return {!Node}
   */
  appendLink(link) {
    return this._contentElement.appendChild(XLink.create(link, 'Learn more'));
  }

  /**
   * @param {string} text
   */
  set text(text) {
    this._textElement.textContent = text;
  }
}
