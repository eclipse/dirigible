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
// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import {Action} from './Action.js';
import {Context} from './Context.js';  // eslint-disable-line no-unused-vars

/**
 * @unrestricted
 */
export class ActionRegistry {
  constructor() {
    /** @type {!Map.<string, !Action>} */
    this._actionsById = new Map();
    this._registerActions();
  }

  _registerActions() {
    self.runtime.extensions('action').forEach(registerExtension, this);

    /**
     * @param {!Root.Runtime.Extension} extension
     * @this {ActionRegistry}
     */
    function registerExtension(extension) {
      if (!extension.canInstantiate()) {
        return;
      }
      const actionId = extension.descriptor()['actionId'];
      console.assert(actionId);
      console.assert(!this._actionsById.get(actionId));

      const action = new Action(extension);
      if (!action.category() || action.title()) {
        this._actionsById.set(actionId, action);
      } else {
        console.error(`Category actions require a title for command menu: ${actionId}`);
      }
    }
  }

  /**
   * @return {!Array.<!Action>}
   */
  availableActions() {
    return this.applicableActions([...this._actionsById.keys()], self.UI.context);
  }

  /**
   * @return {!Array.<!Action>}
   */
  actions() {
    return [...this._actionsById.values()];
  }

  /**
   * @param {!Array.<string>} actionIds
   * @param {!Context} context
   * @return {!Array.<!Action>}
   */
  applicableActions(actionIds, context) {
    const extensions = [];
    actionIds.forEach(function(actionId) {
      const action = this._actionsById.get(actionId);
      if (action) {
        extensions.push(action.extension());
      }
    }, this);
    return [...context.applicableExtensions(extensions)].map(extensionToAction.bind(this));

    /**
     * @param {!Root.Runtime.Extension} extension
     * @return {!Action}
     * @this {ActionRegistry}
     */
    function extensionToAction(extension) {
      return (
          /** @type {!Action} */ (this.action(extension.descriptor()['actionId'])));
    }
  }

  /**
   * @param {string} actionId
   * @return {?Action}
   */
  action(actionId) {
    return this._actionsById.get(actionId) || null;
  }
}
