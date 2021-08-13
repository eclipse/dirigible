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
import * as Platform from '../platform/platform.js';
import * as TextUtils from '../text_utils/text_utils.js';
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class FontView extends UI.View.SimpleView {
  /**
   * @param {string} mimeType
   * @param {!TextUtils.ContentProvider.ContentProvider} contentProvider
   */
  constructor(mimeType, contentProvider) {
    super(Common.UIString.UIString('Font'));
    this.registerRequiredCSS('source_frame/fontView.css');
    this.element.classList.add('font-view');
    this._url = contentProvider.contentURL();
    UI.ARIAUtils.setAccessibleName(this.element, ls`Preview of font from ${this._url}`);
    this._mimeType = mimeType;
    this._contentProvider = contentProvider;
    this._mimeTypeLabel = new UI.Toolbar.ToolbarText(mimeType);
  }

  /**
   * @override
   * @return {!Promise<!Array<!UI.Toolbar.ToolbarItem>>}
   */
  async toolbarItems() {
    return [this._mimeTypeLabel];
  }

  /**
   * @param {string} uniqueFontName
   * @param {!TextUtils.ContentProvider.DeferredContent} deferredContent
   */
  _onFontContentLoaded(uniqueFontName, deferredContent) {
    const {content} = deferredContent;
    const url = content ? TextUtils.ContentProvider.contentAsDataURL(content, this._mimeType, true) : this._url;
    this.fontStyleElement.textContent =
        Platform.StringUtilities.sprintf('@font-face { font-family: "%s"; src: url(%s); }', uniqueFontName, url);
  }

  _createContentIfNeeded() {
    if (this.fontPreviewElement) {
      return;
    }

    const uniqueFontName = 'WebInspectorFontPreview' + (++_fontId);

    this.fontStyleElement = createElement('style');
    this._contentProvider.requestContent().then(deferredContent => {
      this._onFontContentLoaded(uniqueFontName, deferredContent);
    });
    this.element.appendChild(this.fontStyleElement);

    const fontPreview = createElement('div');
    for (let i = 0; i < _fontPreviewLines.length; ++i) {
      if (i > 0) {
        fontPreview.createChild('br');
      }
      fontPreview.createTextChild(_fontPreviewLines[i]);
    }
    this.fontPreviewElement = fontPreview.cloneNode(true);
    UI.ARIAUtils.markAsHidden(this.fontPreviewElement);
    this.fontPreviewElement.style.overflow = 'hidden';
    this.fontPreviewElement.style.setProperty('font-family', uniqueFontName);
    this.fontPreviewElement.style.setProperty('visibility', 'hidden');

    this._dummyElement = fontPreview;
    this._dummyElement.style.visibility = 'hidden';
    this._dummyElement.style.zIndex = '-1';
    this._dummyElement.style.display = 'inline';
    this._dummyElement.style.position = 'absolute';
    this._dummyElement.style.setProperty('font-family', uniqueFontName);
    this._dummyElement.style.setProperty('font-size', _measureFontSize + 'px');

    this.element.appendChild(this.fontPreviewElement);
  }

  /**
   * @override
   */
  wasShown() {
    this._createContentIfNeeded();

    this.updateFontPreviewSize();
  }

  /**
   * @override
   */
  onResize() {
    if (this._inResize) {
      return;
    }

    this._inResize = true;
    try {
      this.updateFontPreviewSize();
    } finally {
      delete this._inResize;
    }
  }

  _measureElement() {
    this.element.appendChild(this._dummyElement);
    const result = {width: this._dummyElement.offsetWidth, height: this._dummyElement.offsetHeight};
    this.element.removeChild(this._dummyElement);

    return result;
  }

  updateFontPreviewSize() {
    if (!this.fontPreviewElement || !this.isShowing()) {
      return;
    }

    this.fontPreviewElement.style.removeProperty('visibility');
    const dimension = this._measureElement();

    const height = dimension.height;
    const width = dimension.width;

    // Subtract some padding. This should match the paddings in the CSS plus room for the scrollbar.
    const containerWidth = this.element.offsetWidth - 50;
    const containerHeight = this.element.offsetHeight - 30;

    if (!height || !width || !containerWidth || !containerHeight) {
      this.fontPreviewElement.style.removeProperty('font-size');
      return;
    }

    const widthRatio = containerWidth / width;
    const heightRatio = containerHeight / height;
    const finalFontSize = Math.floor(_measureFontSize * Math.min(widthRatio, heightRatio)) - 2;

    this.fontPreviewElement.style.setProperty('font-size', finalFontSize + 'px', null);
  }
}

let _fontId = 0;

const _fontPreviewLines = ['ABCDEFGHIJKLM', 'NOPQRSTUVWXYZ', 'abcdefghijklm', 'nopqrstuvwxyz', '1234567890'];
const _measureFontSize = 50;
