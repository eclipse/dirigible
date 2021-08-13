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

export class RenderingOptionsView extends UI.Widget.VBox {
  constructor() {
    super(true);
    this.registerRequiredCSS('inspector_main/renderingOptions.css');

    this._appendCheckbox(
        ls`Paint flashing`,
        ls
        `Highlights areas of the page (green) that need to be repainted. May not be suitable for people prone to photosensitive epilepsy.`,
        Common.Settings.Settings.instance().moduleSetting('showPaintRects'));
    this._appendCheckbox(
        ls`Layout Shift Regions`,
        ls
        `Highlights areas of the page (blue) that were shifted. May not be suitable for people prone to photosensitive epilepsy.`,
        Common.Settings.Settings.instance().moduleSetting('showLayoutShiftRegions'));
        this._appendCheckbox(
            ls`Layer borders`, ls`Shows layer borders (orange/olive) and tiles (cyan).`,
            Common.Settings.Settings.instance().moduleSetting('showDebugBorders'));
        this._appendCheckbox(
            ls`FPS meter`, ls`Plots frames per second, frame rate distribution, and GPU memory.`,
            Common.Settings.Settings.instance().moduleSetting('showFPSCounter'));
    this._appendCheckbox(
        ls`Scrolling performance issues`,
        ls
        `Highlights elements (teal) that can slow down scrolling, including touch & wheel event handlers and other main-thread scrolling situations.`,
        Common.Settings.Settings.instance().moduleSetting('showScrollBottleneckRects'));
        this._appendCheckbox(
            ls`Highlight ad frames`, ls`Highlights frames (red) detected to be ads.`,
            Common.Settings.Settings.instance().moduleSetting('showAdHighlights'));
        this._appendCheckbox(
            ls`Hit-test borders`, ls`Shows borders around hit-test regions.`,
            Common.Settings.Settings.instance().moduleSetting('showHitTestBorders'));
        this.contentElement.createChild('div').classList.add('panel-section-separator');

        this._appendSelect(
            ls`Forces media type for testing print and screen styles`,
            Common.Settings.Settings.instance().moduleSetting('emulatedCSSMedia'));
        this._appendSelect(
            ls`Forces CSS prefers-color-scheme media feature`,
            Common.Settings.Settings.instance().moduleSetting('emulatedCSSMediaFeaturePrefersColorScheme'));
        this._appendSelect(
            ls`Forces CSS prefers-reduced-motion media feature`,
            Common.Settings.Settings.instance().moduleSetting('emulatedCSSMediaFeaturePrefersReducedMotion'));
        this.contentElement.createChild('div').classList.add('panel-section-separator');

        this._appendSelect(
            ls`Forces vision deficiency emulation`,
            Common.Settings.Settings.instance().moduleSetting('emulatedVisionDeficiency'));
  }

  /**
   * @param {string} label
   * @param {string} subtitle
   * @param {!Common.Settings.Setting} setting
   */
  _appendCheckbox(label, subtitle, setting) {
    const checkboxLabel = UI.UIUtils.CheckboxLabel.create(label, false, subtitle);
    UI.SettingsUI.bindCheckbox(checkboxLabel.checkboxElement, setting);
    this.contentElement.appendChild(checkboxLabel);
  }

  /**
   * @param {string} label
   * @param {!Common.Settings.Setting} setting
   */
  _appendSelect(label, setting) {
    const control = UI.SettingsUI.createControlForSetting(setting, label);
    if (control) {
      this.contentElement.appendChild(control);
    }
  }
}
