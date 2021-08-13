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
import * as ARIAUtils from './ARIAUtils.js';
import {GlassPane, PointerEventsBehavior} from './GlassPane.js';
import {KeyboardShortcut, Keys} from './KeyboardShortcut.js';
import {SplitWidget} from './SplitWidget.js';  // eslint-disable-line no-unused-vars
import {WidgetFocusRestorer} from './Widget.js';

export class Dialog extends GlassPane {
  constructor() {
    super();
    this.registerRequiredCSS('ui/dialog.css');
    this.contentElement.tabIndex = 0;
    this.contentElement.addEventListener('focus', () => this.widget().focus(), false);
    this.widget().setDefaultFocusedElement(this.contentElement);
    this.setPointerEventsBehavior(PointerEventsBehavior.BlockedByGlassPane);
    this.setOutsideClickCallback(event => {
      this.hide();
      event.consume(true);
    });
    ARIAUtils.markAsModalDialog(this.contentElement);
    /** @type {!OutsideTabIndexBehavior} */
    this._tabIndexBehavior = OutsideTabIndexBehavior.DisableAllOutsideTabIndex;
    /** @type {!Map<!HTMLElement, number>} */
    this._tabIndexMap = new Map();
    /** @type {?WidgetFocusRestorer} */
    this._focusRestorer = null;
    this._closeOnEscape = true;
    /** @type {?Document} */
    this._targetDocument;
    this._targetDocumentKeyDownHandler = this._onKeyDown.bind(this);
  }

  /**
   * @return {boolean}
   */
  static hasInstance() {
    return !!Dialog._instance;
  }

  /**
   * @override
   * @param {!Document|!Element=} where
   */
  show(where) {
    const document = /** @type {!Document} */ (
        where instanceof Document ? where : (where || self.UI.inspectorView.element).ownerDocument);
    this._targetDocument = document;
    this._targetDocument.addEventListener('keydown', this._targetDocumentKeyDownHandler, true);

    if (Dialog._instance) {
      Dialog._instance.hide();
    }
    Dialog._instance = this;
    this._disableTabIndexOnElements(document);
    super.show(document);
    this._focusRestorer = new WidgetFocusRestorer(this.widget());
  }

  /**
   * @override
   */
  hide() {
    this._focusRestorer.restore();
    super.hide();

    if (this._targetDocument) {
      this._targetDocument.removeEventListener('keydown', this._targetDocumentKeyDownHandler, true);
    }
    this._restoreTabIndexOnElements();
    delete Dialog._instance;
  }

  /**
   * @param {boolean} close
   */
  setCloseOnEscape(close) {
    this._closeOnEscape = close;
  }

  addCloseButton() {
    const closeButton = this.contentElement.createChild('div', 'dialog-close-button', 'dt-close-button');
    closeButton.gray = true;
    closeButton.addEventListener('click', () => this.hide(), false);
  }

  /**
   * @param {!OutsideTabIndexBehavior} tabIndexBehavior
   */
  setOutsideTabIndexBehavior(tabIndexBehavior) {
    this._tabIndexBehavior = tabIndexBehavior;
  }

  /**
   * @param {!Document} document
   */
  _disableTabIndexOnElements(document) {
    if (this._tabIndexBehavior === OutsideTabIndexBehavior.PreserveTabIndex) {
      return;
    }

    let exclusionSet = /** @type {?Set.<!HTMLElement>} */ (null);
    if (this._tabIndexBehavior === OutsideTabIndexBehavior.PreserveMainViewTabIndex) {
      exclusionSet = this._getMainWidgetTabIndexElements(self.UI.inspectorView.ownerSplit());
    }

    this._tabIndexMap.clear();
    for (let node = document; node; node = node.traverseNextNode(document)) {
      if (node instanceof HTMLElement) {
        const element = /** @type {!HTMLElement} */ (node);
        const tabIndex = element.tabIndex;
        if (tabIndex >= 0 && (!exclusionSet || !exclusionSet.has(element))) {
          this._tabIndexMap.set(element, tabIndex);
          element.tabIndex = -1;
        }
      }
    }
  }

  /**
   * @param {?SplitWidget} splitWidget
   * @return {!Set.<!HTMLElement>}
   */
  _getMainWidgetTabIndexElements(splitWidget) {
    const elementSet = /** @type {!Set.<!HTMLElement>} */ (new Set());
    if (!splitWidget) {
      return elementSet;
    }

    const mainWidget = splitWidget.mainWidget();
    if (!mainWidget || !mainWidget.element) {
      return elementSet;
    }

    for (let node = mainWidget.element; node; node = node.traverseNextNode(mainWidget.element)) {
      if (!(node instanceof HTMLElement)) {
        continue;
      }

      const element = /** @type {!HTMLElement} */ (node);
      const tabIndex = element.tabIndex;
      if (tabIndex < 0) {
        continue;
      }

      elementSet.add(element);
    }

    return elementSet;
  }

  _restoreTabIndexOnElements() {
    for (const element of this._tabIndexMap.keys()) {
      element.tabIndex = /** @type {number} */ (this._tabIndexMap.get(element));
    }
    this._tabIndexMap.clear();
  }

  /**
   * @param {!Event} event
   */
  _onKeyDown(event) {
    if (this._closeOnEscape && event.keyCode === Keys.Esc.code && KeyboardShortcut.hasNoModifiers(event)) {
      event.consume(true);
      this.hide();
    }
  }
}

/** @enum {symbol} */
export const OutsideTabIndexBehavior = {
  DisableAllOutsideTabIndex: Symbol('DisableAllTabIndex'),
  PreserveMainViewTabIndex: Symbol('PreserveMainViewTabIndex'),
  PreserveTabIndex: Symbol('PreserveTabIndex')
};
