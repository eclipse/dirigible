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
import {createShadowRootWithCoreStyles} from './utils/create-shadow-root-with-core-styles.js';

/**
 * @implements {Common.Progress.Progress}
 * @unrestricted
 */
export class ProgressIndicator {
  constructor() {
    this.element = createElementWithClass('div', 'progress-indicator');
    this._shadowRoot = createShadowRootWithCoreStyles(this.element, 'ui/progressIndicator.css');
    this._contentElement = this._shadowRoot.createChild('div', 'progress-indicator-shadow-container');

    this._labelElement = this._contentElement.createChild('div', 'title');
    this._progressElement = this._contentElement.createChild('progress');
    this._progressElement.value = 0;
    this._stopButton = this._contentElement.createChild('button', 'progress-indicator-shadow-stop-button');
    this._stopButton.addEventListener('click', this.cancel.bind(this));

    this._isCanceled = false;
    this._worked = 0;
  }

  /**
   * @param {!Element} parent
   */
  show(parent) {
    parent.appendChild(this.element);
  }

  /**
   * @override
   */
  done() {
    if (this._isDone) {
      return;
    }
    this._isDone = true;
    this.element.remove();
  }

  cancel() {
    this._isCanceled = true;
  }

  /**
   * @override
   * @return {boolean}
   */
  isCanceled() {
    return this._isCanceled;
  }

  /**
   * @override
   * @param {string} title
   */
  setTitle(title) {
    this._labelElement.textContent = title;
  }

  /**
   * @override
   * @param {number} totalWork
   */
  setTotalWork(totalWork) {
    this._progressElement.max = totalWork;
  }

  /**
   * @override
   * @param {number} worked
   * @param {string=} title
   */
  setWorked(worked, title) {
    this._worked = worked;
    this._progressElement.value = worked;
    if (title) {
      this.setTitle(title);
    }
  }

  /**
   * @override
   * @param {number=} worked
   */
  worked(worked) {
    this.setWorked(this._worked + (worked || 1));
  }
}
