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
import * as UI from '../ui/ui.js';

import {IsolateSelector} from './IsolateSelector.js';
import {ProfileType} from './ProfileHeader.js';  // eslint-disable-line no-unused-vars

/**
 * @unrestricted
 */
export class ProfileLauncherView extends UI.Widget.VBox {
  /**
   * @param {!UI.Panel.PanelWithSidebar} profilesPanel
   */
  constructor(profilesPanel) {
    super();
    this.registerRequiredCSS('profiler/profileLauncherView.css');

    this._panel = profilesPanel;
    this.element.classList.add('profile-launcher-view');
    this._contentElement = this.element.createChild('div', 'profile-launcher-view-content vbox');

    const profileTypeSelectorElement = this._contentElement.createChild('div', 'vbox');
    this._selectedProfileTypeSetting = Common.Settings.Settings.instance().createSetting('selectedProfileType', 'CPU');
    this._profileTypeHeaderElement = profileTypeSelectorElement.createChild('h1');
    this._profileTypeSelectorForm = profileTypeSelectorElement.createChild('form');
    UI.ARIAUtils.markAsRadioGroup(this._profileTypeSelectorForm);

    const isolateSelectorElement = this._contentElement.createChild('div', 'vbox profile-isolate-selector-block');
    isolateSelectorElement.createChild('h1').textContent = ls`Select JavaScript VM instance`;
    const isolateSelector = new IsolateSelector();
    isolateSelector.show(isolateSelectorElement.createChild('div', 'vbox profile-launcher-target-list'));
    isolateSelectorElement.appendChild(isolateSelector.totalMemoryElement());

    const buttonsDiv = this._contentElement.createChild('div', 'hbox profile-launcher-buttons');
    this._controlButton =
        UI.UIUtils.createTextButton('', this._controlButtonClicked.bind(this), '', /* primary */ true);
    this._loadButton = UI.UIUtils.createTextButton(ls`Load`, this._loadButtonClicked.bind(this), '');
    buttonsDiv.appendChild(this._controlButton);
    buttonsDiv.appendChild(this._loadButton);
    this._recordButtonEnabled = true;

    /** @type {!Map<string, !HTMLOptionElement>} */
    this._typeIdToOptionElement = new Map();
  }

  _loadButtonClicked() {
    this._panel.showLoadFromFileDialog();
  }

  _updateControls() {
    if (this._isEnabled && this._recordButtonEnabled) {
      this._controlButton.removeAttribute('disabled');
    } else {
      this._controlButton.setAttribute('disabled', '');
    }
    this._controlButton.title = this._recordButtonEnabled ? '' : UI.UIUtils.anotherProfilerActiveLabel();
    if (this._isInstantProfile) {
      this._controlButton.classList.remove('running');
      this._controlButton.classList.add('primary-button');
      this._controlButton.textContent = Common.UIString.UIString('Take snapshot');
    } else if (this._isProfiling) {
      this._controlButton.classList.add('running');
      this._controlButton.classList.remove('primary-button');
      this._controlButton.textContent = Common.UIString.UIString('Stop');
    } else {
      this._controlButton.classList.remove('running');
      this._controlButton.classList.add('primary-button');
      this._controlButton.textContent = Common.UIString.UIString('Start');
    }
    for (const item of this._typeIdToOptionElement.values()) {
      item.disabled = !!this._isProfiling;
    }
  }

  profileStarted() {
    this._isProfiling = true;
    this._updateControls();
  }

  profileFinished() {
    this._isProfiling = false;
    this._updateControls();
  }

  /**
   * @param {!ProfileType} profileType
   * @param {boolean} recordButtonEnabled
   */
  updateProfileType(profileType, recordButtonEnabled) {
    this._isInstantProfile = profileType.isInstantProfile();
    this._recordButtonEnabled = recordButtonEnabled;
    this._isEnabled = profileType.isEnabled();
    this._updateControls();
  }

  /**
   * @param {!ProfileType} profileType
   */
  addProfileType(profileType) {
    const labelElement = UI.UIUtils.createRadioLabel('profile-type', profileType.name);
    this._profileTypeSelectorForm.appendChild(labelElement);
    const optionElement = labelElement.radioElement;
    this._typeIdToOptionElement.set(profileType.id, optionElement);
    optionElement._profileType = profileType;
    optionElement.style.hidden = true;
    optionElement.addEventListener('change', this._profileTypeChanged.bind(this, profileType), false);
    const descriptionElement = this._profileTypeSelectorForm.createChild('p');
    descriptionElement.textContent = profileType.description;
    UI.ARIAUtils.setDescription(optionElement, profileType.description);
    const customContent = profileType.customContent();
    if (customContent) {
      this._profileTypeSelectorForm.createChild('p').appendChild(customContent);
      profileType.setCustomContentEnabled(false);
    }
    const headerText = this._typeIdToOptionElement.size > 1 ? ls`Select profiling type` : profileType.name;
    this._profileTypeHeaderElement.textContent = headerText;
    UI.ARIAUtils.setAccessibleName(this._profileTypeSelectorForm, headerText);
  }

  restoreSelectedProfileType() {
    let typeId = this._selectedProfileTypeSetting.get();
    if (!this._typeIdToOptionElement.has(typeId)) {
      typeId = this._typeIdToOptionElement.keys().next().value;
      this._selectedProfileTypeSetting.set(typeId);
    }
    this._typeIdToOptionElement.get(typeId).checked = true;
    const type = this._typeIdToOptionElement.get(typeId)._profileType;
    for (const [id, element] of this._typeIdToOptionElement) {
      const enabled = (id === typeId);
      element._profileType.setCustomContentEnabled(enabled);
    }
    this.dispatchEventToListeners(Events.ProfileTypeSelected, type);
  }

  _controlButtonClicked() {
    this._panel.toggleRecord();
  }

  /**
   * @param {!ProfileType} profileType
   */
  _profileTypeChanged(profileType) {
    const typeId = this._selectedProfileTypeSetting.get();
    const type = this._typeIdToOptionElement.get(typeId)._profileType;
    type.setCustomContentEnabled(false);
    profileType.setCustomContentEnabled(true);
    this.dispatchEventToListeners(Events.ProfileTypeSelected, profileType);
    this._isInstantProfile = profileType.isInstantProfile();
    this._isEnabled = profileType.isEnabled();
    this._updateControls();
    this._selectedProfileTypeSetting.set(profileType.id);
  }
}

/** @enum {symbol} */
export const Events = {
  ProfileTypeSelected: Symbol('ProfileTypeSelected')
};
