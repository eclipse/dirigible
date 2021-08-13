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
import * as UI from '../ui/ui.js';

import {ConsoleView} from './ConsoleView.js';

/**
 * @unrestricted
 */
export class ConsolePanel extends UI.Panel.Panel {
  constructor() {
    super('console');
    this._view = ConsoleView.instance();
  }

  /**
   * @return {!ConsolePanel}
   */
  static instance() {
    return /** @type {!ConsolePanel} */ (self.runtime.sharedInstance(ConsolePanel));
  }

  static _updateContextFlavor() {
    const consoleView = ConsolePanel.instance()._view;
    self.UI.context.setFlavor(ConsoleView, consoleView.isShowing() ? consoleView : null);
  }

  /**
   * @override
   */
  wasShown() {
    super.wasShown();
    const wrapper = WrapperView._instance;
    if (wrapper && wrapper.isShowing()) {
      self.UI.inspectorView.setDrawerMinimized(true);
    }
    this._view.show(this.element);
    ConsolePanel._updateContextFlavor();
  }

  /**
   * @override
   */
  willHide() {
    super.willHide();
    // The minimized drawer has 0 height, and showing Console inside may set
    // Console's scrollTop to 0. Unminimize before calling show to avoid this.
    self.UI.inspectorView.setDrawerMinimized(false);
    if (WrapperView._instance) {
      WrapperView._instance._showViewInWrapper();
    }
    ConsolePanel._updateContextFlavor();
  }

  /**
   * @override
   * @return {?UI.SearchableView.SearchableView}
   */
  searchableView() {
    return ConsoleView.instance().searchableView();
  }
}

/**
 * @unrestricted
 */
export class WrapperView extends UI.Widget.VBox {
  constructor() {
    super();
    this.element.classList.add('console-view-wrapper');

    WrapperView._instance = this;

    this._view = ConsoleView.instance();
  }

  /**
   * @override
   */
  wasShown() {
    if (!ConsolePanel.instance().isShowing()) {
      this._showViewInWrapper();
    } else {
      self.UI.inspectorView.setDrawerMinimized(true);
    }
    ConsolePanel._updateContextFlavor();
  }

  /**
   * @override
   */
  willHide() {
    self.UI.inspectorView.setDrawerMinimized(false);
    ConsolePanel._updateContextFlavor();
  }

  _showViewInWrapper() {
    this._view.show(this.element);
  }
}

/**
 * @implements {Common.Revealer.Revealer}
 * @unrestricted
 */
export class ConsoleRevealer {
  /**
   * @override
   * @param {!Object} object
   * @return {!Promise}
   */
  reveal(object) {
    const consoleView = ConsoleView.instance();
    if (consoleView.isShowing()) {
      consoleView.focus();
      return Promise.resolve();
    }
    UI.ViewManager.ViewManager.instance().showView('console-view');
    return Promise.resolve();
  }
}
