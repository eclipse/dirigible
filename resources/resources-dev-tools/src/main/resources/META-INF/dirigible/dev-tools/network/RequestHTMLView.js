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
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class RequestHTMLView extends UI.Widget.VBox {
  /**
   * @param {string} dataURL
   */
  constructor(dataURL) {
    super(true);
    this.registerRequiredCSS('network/requestHTMLView.css');
    this._dataURL = encodeURI(dataURL).replace(/#/g, '%23');
    this.contentElement.classList.add('html', 'request-view');
  }

  /**
   * @override
   */
  wasShown() {
    this._createIFrame();
  }

  /**
   * @override
   */
  willHide() {
    this.contentElement.removeChildren();
  }

  _createIFrame() {
    // We need to create iframe again each time because contentDocument
    // is deleted when iframe is removed from its parent.
    this.contentElement.removeChildren();
    const iframe = createElement('iframe');
    iframe.className = 'html-preview-frame';
    iframe.setAttribute('sandbox', '');  // Forbid to run JavaScript and set unique origin.
    iframe.setAttribute('src', this._dataURL);
    iframe.setAttribute('tabIndex', -1);
    UI.ARIAUtils.markAsPresentation(iframe);
    this.contentElement.appendChild(iframe);
  }
}
