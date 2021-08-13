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
import * as SDK from '../sdk/sdk.js';
import * as UI from '../ui/ui.js';

import {EventSourceMessagesView} from './EventSourceMessagesView.js';
import {NetworkTimeCalculator} from './NetworkTimeCalculator.js';  // eslint-disable-line no-unused-vars
import {RequestCookiesView} from './RequestCookiesView.js';
import {RequestHeadersView} from './RequestHeadersView.js';
import {RequestInitiatorView} from './RequestInitiatorView.js';
import {RequestPreviewView} from './RequestPreviewView.js';
import {RequestResponseView} from './RequestResponseView.js';
import {RequestTimingView} from './RequestTimingView.js';
import {ResourceWebSocketFrameView} from './ResourceWebSocketFrameView.js';

export class NetworkItemView extends UI.TabbedPane.TabbedPane {
  /**
   * @param {!SDK.NetworkRequest.NetworkRequest} request
   * @param {!NetworkTimeCalculator} calculator
   * @param {!Tabs=} initialTab If specified, will open `initalTab` when the view shows. Otherwise the tab that
   *                            was last shown is opened. Note that specifying `initalTab` won't override the
   *                            setting that stores the 'last opened tab' (similar to how revealers work).
   */
  constructor(request, calculator, initialTab) {
    super();
    this._request = request;
    this.element.classList.add('network-item-view');

    this._resourceViewTabSetting = Common.Settings.Settings.instance().createSetting('resourceViewTab', 'preview');

    this._headersView = new RequestHeadersView(request);
    this.appendTab(
        Tabs.Headers, Common.UIString.UIString('Headers'), this._headersView,
        Common.UIString.UIString('Headers and request body'));

    this.addEventListener(UI.TabbedPane.Events.TabSelected, this._tabSelected, this);

    if (request.resourceType() === Common.ResourceType.resourceTypes.WebSocket) {
      const frameView = new ResourceWebSocketFrameView(request);
      this.appendTab(
          Tabs.WsFrames, Common.UIString.UIString('Messages'), frameView,
          Common.UIString.UIString('WebSocket messages'));
    } else if (request.mimeType === 'text/event-stream') {
      this.appendTab(Tabs.EventSource, Common.UIString.UIString('EventStream'), new EventSourceMessagesView(request));
    } else {
      this._responseView = new RequestResponseView(request);
      const previewView = new RequestPreviewView(request);
      this.appendTab(
          Tabs.Preview, Common.UIString.UIString('Preview'), previewView, Common.UIString.UIString('Response preview'));
      if (request.signedExchangeInfo() && request.signedExchangeInfo().errors &&
          request.signedExchangeInfo().errors.length) {
        const icon = UI.Icon.Icon.create('smallicon-error');
        icon.title = Common.UIString.UIString('SignedExchange error');
        this.setTabIcon(Tabs.Preview, icon);
      }
      this.appendTab(
          Tabs.Response, Common.UIString.UIString('Response'), this._responseView,
          Common.UIString.UIString('Raw response data'));
    }

    this.appendTab(Tabs.Initiator, ls`Initiator`, new RequestInitiatorView(request), ls`Request initiator call stack`);

    this.appendTab(
        Tabs.Timing, Common.UIString.UIString('Timing'), new RequestTimingView(request, calculator),
        Common.UIString.UIString('Request and response timeline'));

    /** @type {?RequestCookiesView} */
    this._cookiesView = null;

    /** @type {!Tabs} */
    this._initialTab = initialTab || this._resourceViewTabSetting.get();
  }

  /**
   * @override
   */
  wasShown() {
    super.wasShown();
    this._request.addEventListener(
        SDK.NetworkRequest.Events.RequestHeadersChanged, this._maybeAppendCookiesPanel, this);
    this._request.addEventListener(
        SDK.NetworkRequest.Events.ResponseHeadersChanged, this._maybeAppendCookiesPanel, this);
    this._maybeAppendCookiesPanel();
    this._selectTab(this._initialTab);
  }

  /**
   * @override
   */
  willHide() {
    this._request.removeEventListener(
        SDK.NetworkRequest.Events.RequestHeadersChanged, this._maybeAppendCookiesPanel, this);
    this._request.removeEventListener(
        SDK.NetworkRequest.Events.ResponseHeadersChanged, this._maybeAppendCookiesPanel, this);
  }

  _maybeAppendCookiesPanel() {
    const cookiesPresent = this._request.requestCookies.length || this._request.responseCookies.length;
    console.assert(cookiesPresent || !this._cookiesView, 'Cookies were introduced in headers and then removed!');
    if (cookiesPresent && !this._cookiesView) {
      this._cookiesView = new RequestCookiesView(this._request);
      this.appendTab(
          Tabs.Cookies, Common.UIString.UIString('Cookies'), this._cookiesView,
          Common.UIString.UIString('Request and response cookies'));
    }
  }

  /**
   * @param {string} tabId
   */
  _selectTab(tabId) {
    if (!this.selectTab(tabId)) {
      this.selectTab('headers');
    }
  }

  _tabSelected(event) {
    if (!event.data.isUserGesture) {
      return;
    }
    this._resourceViewTabSetting.set(event.data.tabId);
  }

  /**
   * @return {!SDK.NetworkRequest.NetworkRequest}
   */
  request() {
    return this._request;
  }

  /**
   * @param {number=} line
   * @return {!Promise}
   */
  async revealResponseBody(line) {
    this._selectTab(Tabs.Response);
    if (this._responseView && typeof line === 'number') {
      await this._responseView.revealLine(/** @type {number} */ (line));
    }
  }

  /**
   * @param {string} header
   */
  revealRequestHeader(header) {
    this._selectTab(Tabs.Headers);
    this._headersView.revealRequestHeader(header);
  }

  /**
   * @param {string} header
   */
  revealResponseHeader(header) {
    this._selectTab(Tabs.Headers);
    this._headersView.revealResponseHeader(header);
  }
}

/**
 * @enum {string}
 */
export const Tabs = {
  Cookies: 'cookies',
  EventSource: 'eventSource',
  Headers: 'headers',
  Initiator: 'initiator',
  Preview: 'preview',
  Response: 'response',
  Timing: 'timing',
  WsFrames: 'webSocketFrames'
};
