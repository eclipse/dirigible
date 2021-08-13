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
import * as Bindings from '../bindings/bindings.js';
import * as UI from '../ui/ui.js';

import {Linkifier} from './Linkifier.js';

/**
 * @param {?SDK.Target} target
 * @param {!Linkifier} linkifier
 * @param {!Options=} options
 * @return {{element: !Element, links: !Array<!Element>}}
 */
export function buildStackTracePreviewContents(target, linkifier, options = {}) {
  const {stackTrace, contentUpdated, tabStops} = options;
  const element = createElementWithClass('span', 'monospace');
  element.style.display = 'inline-block';
  const shadowRoot = UI.Utils.createShadowRootWithCoreStyles(element, 'components/jsUtils.css');
  const contentElement = shadowRoot.createChild('table', 'stack-preview-container');
  let totalHiddenCallFramesCount = 0;
  let totalCallFramesCount = 0;
  /** @type {!Array<!Element>} */
  const links = [];

  /**
   * @param {!Protocol.Runtime.StackTrace} stackTrace
   * @return {boolean}
   */
  function appendStackTrace(stackTrace) {
    let hiddenCallFrames = 0;
    for (const stackFrame of stackTrace.callFrames) {
      totalCallFramesCount++;
      let shouldHide = totalCallFramesCount > 30 && stackTrace.callFrames.length > 31;
      const row = createElement('tr');
      row.createChild('td').textContent = '\n';
      row.createChild('td', 'function-name').textContent = UI.UIUtils.beautifyFunctionName(stackFrame.functionName);
      const link = linkifier.maybeLinkifyConsoleCallFrame(target, stackFrame, {tabStop: !!tabStops});
      if (link) {
        link.addEventListener('contextmenu', populateContextMenu.bind(null, link));
        const uiLocation = Linkifier.uiLocation(link);
        if (uiLocation &&
            Bindings.BlackboxManager.BlackboxManager.instance().isBlackboxedUISourceCode(uiLocation.uiSourceCode)) {
          shouldHide = true;
        }
        row.createChild('td').textContent = ' @ ';
        row.createChild('td').appendChild(link);
        links.push(link);
      }
      if (shouldHide) {
        row.classList.add('blackboxed');
        ++hiddenCallFrames;
      }
      contentElement.appendChild(row);
    }
    totalHiddenCallFramesCount += hiddenCallFrames;
    return stackTrace.callFrames.length === hiddenCallFrames;
  }

  /**
   * @param {!Element} link
   * @param {!Event} event
   */
  function populateContextMenu(link, event) {
    const contextMenu = new UI.ContextMenu.ContextMenu(event);
    event.consume(true);
    const uiLocation = Linkifier.uiLocation(link);
    if (uiLocation &&
        Bindings.BlackboxManager.BlackboxManager.instance().canBlackboxUISourceCode(uiLocation.uiSourceCode)) {
      if (Bindings.BlackboxManager.BlackboxManager.instance().isBlackboxedUISourceCode(uiLocation.uiSourceCode)) {
        contextMenu.debugSection().appendItem(
            ls`Stop blackboxing`,
            () => Bindings.BlackboxManager.BlackboxManager.instance().unblackboxUISourceCode(uiLocation.uiSourceCode));
      } else {
        contextMenu.debugSection().appendItem(
            ls`Blackbox script`,
            () => Bindings.BlackboxManager.BlackboxManager.instance().blackboxUISourceCode(uiLocation.uiSourceCode));
      }
    }
    contextMenu.appendApplicableItems(event);
    contextMenu.show();
  }

  if (!stackTrace) {
    return {element, links};
  }

  appendStackTrace(stackTrace);

  let asyncStackTrace = stackTrace.parent;
  while (asyncStackTrace) {
    if (!asyncStackTrace.callFrames.length) {
      asyncStackTrace = asyncStackTrace.parent;
      continue;
    }
    const row = contentElement.createChild('tr');
    row.createChild('td').textContent = '\n';
    row.createChild('td', 'stack-preview-async-description').textContent =
        UI.UIUtils.asyncStackTraceLabel(asyncStackTrace.description);
    row.createChild('td');
    row.createChild('td');
    if (appendStackTrace(asyncStackTrace)) {
      row.classList.add('blackboxed');
    }
    asyncStackTrace = asyncStackTrace.parent;
  }

  if (totalHiddenCallFramesCount) {
    const row = contentElement.createChild('tr', 'show-blackboxed-link');
    row.createChild('td').textContent = '\n';
    const cell = row.createChild('td');
    cell.colSpan = 4;
    const showAllLink = cell.createChild('span', 'link');
    if (totalHiddenCallFramesCount === 1) {
      showAllLink.textContent = ls`Show 1 more frame`;
    } else {
      showAllLink.textContent = ls`Show ${totalHiddenCallFramesCount} more frames`;
    }
    showAllLink.addEventListener('click', () => {
      contentElement.classList.add('show-blackboxed');
      if (contentUpdated) {
        contentUpdated();
      }
    }, false);
  }

  return {element, links};
}

/**
 * @typedef {{
 *   stackTrace: (!Protocol.Runtime.StackTrace|undefined),
 *   contentUpdated: (function()|undefined),
 *   tabStops: (boolean|undefined)
 * }}
 */
export let Options;
