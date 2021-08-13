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
import * as SDK from '../sdk/sdk.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';

import {ComputedStyleModel, Events} from './ComputedStyleModel.js';  // eslint-disable-line no-unused-vars

/**
 * @unrestricted
 */
export class PlatformFontsWidget extends UI.ThrottledWidget.ThrottledWidget {
  /**
   * @param {!ComputedStyleModel} sharedModel
   */
  constructor(sharedModel) {
    super(true);
    this.registerRequiredCSS('elements/platformFontsWidget.css');

    this._sharedModel = sharedModel;
    this._sharedModel.addEventListener(Events.ComputedStyleChanged, this.update, this);

    this._sectionTitle = createElementWithClass('div', 'title');
    this.contentElement.classList.add('platform-fonts');
    this.contentElement.appendChild(this._sectionTitle);
    this._sectionTitle.textContent = Common.UIString.UIString('Rendered Fonts');
    this._fontStatsSection = this.contentElement.createChild('div', 'stats-section');
  }

  /**
   * @override
   * @protected
   * @return {!Promise.<?>}
   */
  doUpdate() {
    const cssModel = this._sharedModel.cssModel();
    const node = this._sharedModel.node();
    if (!node || !cssModel) {
      return Promise.resolve();
    }

    return cssModel.platformFontsPromise(node.id).then(this._refreshUI.bind(this, node));
  }

  /**
   * @param {!SDK.DOMModel.DOMNode} node
   * @param {?Array.<!Protocol.CSS.PlatformFontUsage>} platformFonts
   */
  _refreshUI(node, platformFonts) {
    if (this._sharedModel.node() !== node) {
      return;
    }

    this._fontStatsSection.removeChildren();

    const isEmptySection = !platformFonts || !platformFonts.length;
    this._sectionTitle.classList.toggle('hidden', isEmptySection);
    if (isEmptySection) {
      return;
    }

    platformFonts.sort(function(a, b) {
      return b.glyphCount - a.glyphCount;
    });
    for (let i = 0; i < platformFonts.length; ++i) {
      const fontStatElement = this._fontStatsSection.createChild('div', 'font-stats-item');

      const fontNameElement = fontStatElement.createChild('span', 'font-name');
      fontNameElement.textContent = platformFonts[i].familyName;

      const fontDelimeterElement = fontStatElement.createChild('span', 'font-delimeter');
      fontDelimeterElement.textContent = '\u2014';

      const fontOrigin = fontStatElement.createChild('span');
      fontOrigin.textContent = platformFonts[i].isCustomFont ? Common.UIString.UIString('Network resource') :
                                                               Common.UIString.UIString('Local file');

      const fontUsageElement = fontStatElement.createChild('span', 'font-usage');
      const usage = platformFonts[i].glyphCount;
      fontUsageElement.textContent =
          usage === 1 ? Common.UIString.UIString('(%d glyph)', usage) : Common.UIString.UIString('(%d glyphs)', usage);
    }
  }
}
