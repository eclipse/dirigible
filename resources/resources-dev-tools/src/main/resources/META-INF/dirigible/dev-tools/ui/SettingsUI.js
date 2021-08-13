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

import * as ARIAUtils from './ARIAUtils.js';
import {CheckboxLabel} from './UIUtils.js';

/**
 * @param {string} name
 * @param {!Common.Settings.Setting} setting
 * @param {boolean=} omitParagraphElement
 * @param {string=} tooltip
 * @return {!Element}
 */
export const createSettingCheckbox = function(name, setting, omitParagraphElement, tooltip) {
  const label = CheckboxLabel.create(name);
  if (tooltip) {
    label.title = tooltip;
  }

  const input = label.checkboxElement;
  input.name = name;
  bindCheckbox(input, setting);

  if (omitParagraphElement) {
    return label;
  }

  const p = createElement('p');
  p.appendChild(label);
  return p;
};

/**
 * @param {string} name
 * @param {!Array<!{text: string, value: *, raw: (boolean|undefined)}>} options
 * @param {boolean} reloadRequired
 * @param {!Common.Settings.Setting} setting
 * @param {string=} subtitle
 * @return {!Element}
 */
const createSettingSelect = function(name, options, reloadRequired, setting, subtitle) {
  const settingSelectElement = createElement('p');
  const label = settingSelectElement.createChild('label');
  const select = settingSelectElement.createChild('select', 'chrome-select');
  label.textContent = name;
  if (subtitle) {
    settingSelectElement.classList.add('chrome-select-label');
    label.createChild('p').textContent = subtitle;
  }
  ARIAUtils.bindLabelToControl(label, select);

  for (let i = 0; i < options.length; ++i) {
    // The "raw" flag indicates text is non-i18n-izable.
    const option = options[i];
    const optionName = option.raw ? option.text : Common.UIString.UIString(option.text);
    select.add(new Option(optionName, option.value));
  }

  const reloadWarning = reloadRequired ? settingSelectElement.createChild('span', 'reload-warning hidden') : null;
  if (reloadWarning) {
    reloadWarning.textContent = ls`*Requires reload`;
    ARIAUtils.markAsAlert(reloadWarning);
  }

  setting.addChangeListener(settingChanged);
  settingChanged();
  select.addEventListener('change', selectChanged, false);
  return settingSelectElement;

  function settingChanged() {
    const newValue = setting.get();
    for (let i = 0; i < options.length; i++) {
      if (options[i].value === newValue) {
        select.selectedIndex = i;
      }
    }
  }

  function selectChanged() {
    // Don't use event.target.value to avoid conversion of the value to string.
    setting.set(options[select.selectedIndex].value);
    if (reloadWarning) {
      reloadWarning.classList.remove('hidden');
    }
  }
};

/**
 * @param {!Element} input
 * @param {!Common.Settings.Setting} setting
 */
export const bindCheckbox = function(input, setting) {
  function settingChanged() {
    if (input.checked !== setting.get()) {
      input.checked = setting.get();
    }
  }
  setting.addChangeListener(settingChanged);
  settingChanged();

  function inputChanged() {
    if (setting.get() !== input.checked) {
      setting.set(input.checked);
    }
  }
  input.addEventListener('change', inputChanged, false);
};

/**
 * @param {string} name
 * @param {!Element} element
 * @return {!Element}
 */
export const createCustomSetting = function(name, element) {
  const p = createElement('p');
  const fieldsetElement = p.createChild('fieldset');
  const label = fieldsetElement.createChild('label');
  label.textContent = name;
  ARIAUtils.bindLabelToControl(label, element);
  fieldsetElement.appendChild(element);
  return p;
};

/**
 * @param {!Common.Settings.Setting} setting
 * @param {string=} subtitle
 * @return {?Element}
 */
export const createControlForSetting = function(setting, subtitle) {
  if (!setting.extension()) {
    return null;
  }
  const descriptor = setting.extension().descriptor();
  const uiTitle = Common.UIString.UIString(setting.title() || '');
  switch (descriptor['settingType']) {
    case 'boolean':
      return createSettingCheckbox(uiTitle, setting);
    case 'enum':
      if (Array.isArray(descriptor['options'])) {
        return createSettingSelect(uiTitle, descriptor['options'], descriptor['reloadRequired'], setting, subtitle);
      }
      console.error('Enum setting defined without options');
      return null;
    default:
      console.error('Invalid setting type: ' + descriptor['settingType']);
      return null;
  }
};

/**
 * @interface
 */
export class SettingUI {
  /**
   * @return {?Element}
   */
  settingElement() {}
}
