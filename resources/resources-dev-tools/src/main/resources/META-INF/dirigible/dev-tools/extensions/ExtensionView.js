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

import {ExtensionServer} from './ExtensionServer.js';  // eslint-disable-line no-unused-vars

/**
 * @unrestricted
*/
export class ExtensionView extends UI.Widget.Widget {
  /**
   * @param {!ExtensionServer} server
   * @param {string} id
   * @param {string} src
   * @param {string} className
   */
  constructor(server, id, src, className) {
    super();
    this.setHideOnDetach();
    this.element.className = 'vbox flex-auto';  // Override

    // TODO(crbug.com/872438): remove once we can use this._iframe instead
    this.element.tabIndex = -1;

    this._server = server;
    this._id = id;
    this._iframe = createElement('iframe');
    this._iframe.addEventListener('load', this._onLoad.bind(this), false);
    this._iframe.src = src;
    this._iframe.className = className;

    // TODO(crbug.com/872438): make this._iframe the default focused element
    this.setDefaultFocusedElement(this.element);

    this.element.appendChild(this._iframe);
  }

  /**
   * @override
   */
  wasShown() {
    if (typeof this._frameIndex === 'number') {
      this._server.notifyViewShown(this._id, this._frameIndex);
    }
  }

  /**
   * @override
   */
  willHide() {
    if (typeof this._frameIndex === 'number') {
      this._server.notifyViewHidden(this._id);
    }
  }

  _onLoad() {
    const frames = window.frames;
    this._frameIndex = Array.prototype.indexOf.call(frames, this._iframe.contentWindow);
    if (this.isShowing()) {
      this._server.notifyViewShown(this._id, this._frameIndex);
    }
  }
}

/**
 * @unrestricted
 */
export class ExtensionNotifierView extends UI.Widget.VBox {
  /**
   * @param {!ExtensionServer} server
   * @param {string} id
   */
  constructor(server, id) {
    super();

    this._server = server;
    this._id = id;
  }

  /**
   * @override
   */
  wasShown() {
    this._server.notifyViewShown(this._id);
  }

  /**
   * @override
   */
  willHide() {
    this._server.notifyViewHidden(this._id);
  }
}
