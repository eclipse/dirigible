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
import * as TextUtils from '../text_utils/text_utils.js';
import * as UI from '../ui/ui.js';
import * as Workspace from '../workspace/workspace.js';

/**
 * @unrestricted
 */
export class ImageView extends UI.View.SimpleView {
  /**
   * @param {string} mimeType
   * @param {!TextUtils.ContentProvider.ContentProvider} contentProvider
   */
  constructor(mimeType, contentProvider) {
    super(Common.UIString.UIString('Image'));
    this.registerRequiredCSS('source_frame/imageView.css');
    this.element.tabIndex = 0;
    this.element.classList.add('image-view');
    this._url = contentProvider.contentURL();
    this._parsedURL = new Common.ParsedURL.ParsedURL(this._url);
    this._mimeType = mimeType;
    this._contentProvider = contentProvider;
    this._uiSourceCode = contentProvider instanceof Workspace.UISourceCode.UISourceCode ?
        /** @type {!Workspace.UISourceCode.UISourceCode} */ (contentProvider) :
        null;
    if (this._uiSourceCode) {
      this._uiSourceCode.addEventListener(
          Workspace.UISourceCode.Events.WorkingCopyCommitted, this._workingCopyCommitted, this);
      new UI.DropTarget.DropTarget(
          this.element, [UI.DropTarget.Type.ImageFile, UI.DropTarget.Type.URI],
          Common.UIString.UIString('Drop image file here'), this._handleDrop.bind(this));
    }
    this._sizeLabel = new UI.Toolbar.ToolbarText();
    this._dimensionsLabel = new UI.Toolbar.ToolbarText();
    this._mimeTypeLabel = new UI.Toolbar.ToolbarText(mimeType);
    this._container = this.element.createChild('div', 'image');
    this._imagePreviewElement = this._container.createChild('img', 'resource-image-view');
    this._imagePreviewElement.addEventListener('contextmenu', this._contextMenu.bind(this), true);
    this._imagePreviewElement.alt = ls`Image from ${this._url}`;
  }

  /**
   * @override
   * @return {!Promise<!Array<!UI.Toolbar.ToolbarItem>>}
   */
  async toolbarItems() {
    return [
      this._sizeLabel, new UI.Toolbar.ToolbarSeparator(), this._dimensionsLabel, new UI.Toolbar.ToolbarSeparator(),
      this._mimeTypeLabel
    ];
  }

  /**
   * @override
   */
  wasShown() {
    this._updateContentIfNeeded();
  }

  /**
   * @override
   */
  disposeView() {
    if (this._uiSourceCode) {
      this._uiSourceCode.removeEventListener(
          Workspace.UISourceCode.Events.WorkingCopyCommitted, this._workingCopyCommitted, this);
    }
  }

  _workingCopyCommitted() {
    this._updateContentIfNeeded();
  }

  async _updateContentIfNeeded() {
    const {content} = await this._contentProvider.requestContent();
    if (this._cachedContent === content) {
      return;
    }

    const contentEncoded = await this._contentProvider.contentEncoded();
    this._cachedContent = content;
    let imageSrc = TextUtils.ContentProvider.contentAsDataURL(content, this._mimeType, contentEncoded);
    if (content === null) {
      imageSrc = this._url;
    }
    const loadPromise = new Promise(x => this._imagePreviewElement.onload = x);
    this._imagePreviewElement.src = imageSrc;
    const size = content && !contentEncoded ? content.length : base64ToSize(content);
    this._sizeLabel.setText(Number.bytesToString(size));
    await loadPromise;
    this._dimensionsLabel.setText(Common.UIString.UIString(
        '%d × %d', this._imagePreviewElement.naturalWidth, this._imagePreviewElement.naturalHeight));
  }

  _contextMenu(event) {
    const contextMenu = new UI.ContextMenu.ContextMenu(event);
    if (!this._parsedURL.isDataURL()) {
      contextMenu.clipboardSection().appendItem(
          Common.UIString.UIString('Copy image URL'), this._copyImageURL.bind(this));
    }
    if (this._imagePreviewElement.src) {
      contextMenu.clipboardSection().appendItem(
          Common.UIString.UIString('Copy image as data URI'), this._copyImageAsDataURL.bind(this));
    }

    contextMenu.clipboardSection().appendItem(
        Common.UIString.UIString('Open image in new tab'), this._openInNewTab.bind(this));
    contextMenu.clipboardSection().appendItem(Common.UIString.UIString('Save…'), this._saveImage.bind(this));
    contextMenu.show();
  }

  _copyImageAsDataURL() {
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.copyText(this._imagePreviewElement.src);
  }

  _copyImageURL() {
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.copyText(this._url);
  }

  _saveImage() {
    const link = createElement('a');
    link.download = this._parsedURL.displayName;
    link.href = this._url;
    link.click();
  }

  _openInNewTab() {
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.openInNewTab(this._url);
  }

  /**
   * @param {!DataTransfer} dataTransfer
   */
  async _handleDrop(dataTransfer) {
    const items = dataTransfer.items;
    if (!items.length || items[0].kind !== 'file') {
      return;
    }

    const entry = items[0].webkitGetAsEntry();
    const encoded = !entry.name.endsWith('.svg');
    entry.file(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        let result;
        try {
          result = /** @type {?string} */ (reader.result);
        } catch (e) {
          result = null;
          console.error('Can\'t read file: ' + e);
        }
        if (typeof result !== 'string') {
          return;
        }
        this._uiSourceCode.setContent(encoded ? btoa(result) : result, encoded);
      };
      if (encoded) {
        reader.readAsBinaryString(file);
      } else {
        reader.readAsText(file);
      }
    });
  }
}
