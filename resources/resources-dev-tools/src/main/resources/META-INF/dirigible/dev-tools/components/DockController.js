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
import * as Common from '../common/common.js';
import * as Host from '../host/host.js';
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class DockController extends Common.ObjectWrapper.ObjectWrapper {
  /**
   * @param {boolean} canDock
   */
  constructor(canDock) {
    super();
    this._canDock = canDock;

    this._closeButton = new UI.Toolbar.ToolbarButton(Common.UIString.UIString('Close'), 'largeicon-delete');
    this._closeButton.addEventListener(
        UI.Toolbar.ToolbarButton.Events.Click,
        Host.InspectorFrontendHost.InspectorFrontendHostInstance.closeWindow.bind(
            Host.InspectorFrontendHost.InspectorFrontendHostInstance));

    if (!canDock) {
      this._dockSide = State.Undocked;
      this._closeButton.setVisible(false);
      return;
    }

    this._states = [State.DockedToRight, State.DockedToBottom, State.DockedToLeft, State.Undocked];
    this._currentDockStateSetting = Common.Settings.Settings.instance().moduleSetting('currentDockState');
    this._currentDockStateSetting.addChangeListener(this._dockSideChanged, this);
    this._lastDockStateSetting = Common.Settings.Settings.instance().createSetting('lastDockState', 'bottom');
    if (this._states.indexOf(this._currentDockStateSetting.get()) === -1) {
      this._currentDockStateSetting.set('right');
    }
    if (this._states.indexOf(this._lastDockStateSetting.get()) === -1) {
      this._currentDockStateSetting.set('bottom');
    }
  }

  initialize() {
    if (!this._canDock) {
      return;
    }

    this._titles = [
      Common.UIString.UIString('Dock to right'), Common.UIString.UIString('Dock to bottom'),
      Common.UIString.UIString('Dock to left'), Common.UIString.UIString('Undock into separate window')
    ];
    this._dockSideChanged();
  }

  _dockSideChanged() {
    this.setDockSide(this._currentDockStateSetting.get());
  }

  /**
   * @return {string}
   */
  dockSide() {
    return this._dockSide;
  }

  /**
   * @return {boolean}
   */
  canDock() {
    return this._canDock;
  }

  /**
   * @return {boolean}
   */
  isVertical() {
    return this._dockSide === State.DockedToRight || this._dockSide === State.DockedToLeft;
  }

  /**
   * @param {string} dockSide
   * @suppressGlobalPropertiesCheck
   */
  setDockSide(dockSide) {
    if (this._states.indexOf(dockSide) === -1) {
      dockSide = this._states[0];
    }

    if (this._dockSide === dockSide) {
      return;
    }

    if (this._dockSide) {
      this._lastDockStateSetting.set(this._dockSide);
    }

    this._savedFocus = document.deepActiveElement();
    const eventData = {from: this._dockSide, to: dockSide};
    this.dispatchEventToListeners(Events.BeforeDockSideChanged, eventData);
    console.timeStamp('DockController.setIsDocked');
    this._dockSide = dockSide;
    this._currentDockStateSetting.set(dockSide);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.setIsDocked(
        dockSide !== State.Undocked, this._setIsDockedResponse.bind(this, eventData));
    this._closeButton.setVisible(this._dockSide !== State.Undocked);
    this.dispatchEventToListeners(Events.DockSideChanged, eventData);
  }

  /**
   * @param {{from: string, to: string}} eventData
   */
  _setIsDockedResponse(eventData) {
    this.dispatchEventToListeners(Events.AfterDockSideChanged, eventData);
    if (this._savedFocus) {
      this._savedFocus.focus();
      this._savedFocus = null;
    }
  }

  _toggleDockSide() {
    if (this._lastDockStateSetting.get() === this._currentDockStateSetting.get()) {
      const index = this._states.indexOf(this._currentDockStateSetting.get()) || 0;
      this._lastDockStateSetting.set(this._states[(index + 1) % this._states.length]);
    }
    this.setDockSide(this._lastDockStateSetting.get());
  }
}

export const State = {
  DockedToBottom: 'bottom',
  DockedToRight: 'right',
  DockedToLeft: 'left',
  Undocked: 'undocked'
};

// Use BeforeDockSideChanged to do something before all the UI bits are updated,
// DockSideChanged to update UI, and AfterDockSideChanged to perform actions
// after frontend is docked/undocked in the browser.

/** @enum {symbol} */
export const Events = {
  BeforeDockSideChanged: Symbol('BeforeDockSideChanged'),
  DockSideChanged: Symbol('DockSideChanged'),
  AfterDockSideChanged: Symbol('AfterDockSideChanged')
};

/**
 * @implements {UI.ActionDelegate.ActionDelegate}
 * @unrestricted
 */
export class ToggleDockActionDelegate {
  /**
   * @override
   * @param {!UI.Context.Context} context
   * @param {string} actionId
   * @return {boolean}
   */
  handleAction(context, actionId) {
    self.Components.dockController._toggleDockSide();
    return true;
  }
}

/**
 * @implements {UI.Toolbar.Provider}
 * @unrestricted
 */
export class CloseButtonProvider {
  /**
   * @override
   * @return {?UI.Toolbar.ToolbarItem}
   */
  item() {
    return self.Components.dockController._closeButton;
  }
}
