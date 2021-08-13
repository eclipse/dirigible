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
import * as SourceFrame from '../source_frame/source_frame.js';
import * as TextUtils from '../text_utils/text_utils.js';
import * as UI from '../ui/ui.js';

import {RequestHTMLView} from './RequestHTMLView.js';
import {RequestResponseView} from './RequestResponseView.js';
import {SignedExchangeInfoView} from './SignedExchangeInfoView.js';

export class RequestPreviewView extends RequestResponseView {
  /**
   * @param {!SDK.NetworkRequest.NetworkRequest} request
   */
  constructor(request) {
    super(request);
  }

  /**
   * @override
   * @protected
   * @return {!Promise<!UI.Widget.Widget>}
   */
  async showPreview() {
    const view = await super.showPreview();
    if (!(view instanceof UI.View.SimpleView)) {
      return view;
    }
    const toolbar = new UI.Toolbar.Toolbar('network-item-preview-toolbar', this.element);
    view.toolbarItems().then(items => {
      items.map(item => toolbar.appendToolbarItem(item));
    });
    return view;
  }

  /**
   * @return {!Promise<?UI.Widget.Widget>}
   */
  async _htmlPreview() {
    const contentData = await this.request.contentData();
    if (contentData.error) {
      return new UI.EmptyWidget.EmptyWidget(Common.UIString.UIString('Failed to load response data'));
    }

    const whitelist = new Set(['text/html', 'text/plain', 'application/xhtml+xml']);
    if (!whitelist.has(this.request.mimeType)) {
      return null;
    }

    const content = contentData.encoded ? window.atob(/** @type {string} */ (contentData.content)) :
                                          /** @type {string} */ (contentData.content);

    // http://crbug.com/767393 - DevTools should recognize JSON regardless of the content type
    const jsonView = await SourceFrame.JSONView.JSONView.createView(content);
    if (jsonView) {
      return jsonView;
    }

    const dataURL = TextUtils.ContentProvider.contentAsDataURL(
        contentData.content, this.request.mimeType, contentData.encoded, this.request.charset());
    return dataURL ? new RequestHTMLView(dataURL) : null;
  }

  /**
   * @override
   * @protected
   * @return {!Promise<!UI.Widget.Widget>}
   */
  async createPreview() {
    if (this.request.signedExchangeInfo()) {
      return new SignedExchangeInfoView(this.request);
    }

    const htmlErrorPreview = await this._htmlPreview();
    if (htmlErrorPreview) {
      return htmlErrorPreview;
    }

    const provided = await SourceFrame.PreviewFactory.PreviewFactory.createPreview(this.request, this.request.mimeType);
    if (provided) {
      return provided;
    }

    return new UI.EmptyWidget.EmptyWidget(Common.UIString.UIString('Preview not available'));
  }
}
