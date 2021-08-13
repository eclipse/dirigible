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
import * as Components from '../components/components.js';
import * as SDK from '../sdk/sdk.js';
import * as UI from '../ui/ui.js';

import {CustomPreviewComponent} from './CustomPreviewComponent.js';
import {ObjectPropertiesSection} from './ObjectPropertiesSection.js';

export class ObjectPopoverHelper {
  /**
   * @param {?Components.Linkifier.Linkifier} linkifier
   * @param {boolean} resultHighlightedAsDOM
   */
  constructor(linkifier, resultHighlightedAsDOM) {
    this._linkifier = linkifier;
    this._resultHighlightedAsDOM = resultHighlightedAsDOM;
  }

  dispose() {
    if (this._resultHighlightedAsDOM) {
      SDK.OverlayModel.OverlayModel.hideDOMNodeHighlight();
    }
    if (this._linkifier) {
      this._linkifier.dispose();
    }
  }

  /**
   * @param {!SDK.RemoteObject.RemoteObject} result
   * @param {!UI.GlassPane.GlassPane} popover
   * @return {!Promise<?ObjectPopoverHelper>}
   */
  static async buildObjectPopover(result, popover) {
    const description = result.description.trimEndWithMaxLength(MaxPopoverTextLength);
    let popoverContentElement = null;
    if (result.type === 'object') {
      let linkifier = null;
      let resultHighlightedAsDOM = false;
      if (result.subtype === 'node') {
        SDK.OverlayModel.OverlayModel.highlightObjectAsDOMNode(result);
        resultHighlightedAsDOM = true;
      }

      if (result.customPreview()) {
        const customPreviewComponent = new CustomPreviewComponent(result);
        customPreviewComponent.expandIfPossible();
        popoverContentElement = customPreviewComponent.element;
      } else {
        popoverContentElement = createElementWithClass('div', 'object-popover-content');
        UI.Utils.appendStyle(popoverContentElement, 'object_ui/objectPopover.css');
        const titleElement = popoverContentElement.createChild('div', 'monospace object-popover-title');
        titleElement.createChild('span').textContent = description;
        linkifier = new Components.Linkifier.Linkifier();
        const section = new ObjectPropertiesSection(
            result, '', linkifier, undefined, undefined, undefined, true /* showOverflow */);
        section.element.classList.add('object-popover-tree');
        section.titleLessMode();
        popoverContentElement.appendChild(section.element);
      }
      popover.setMaxContentSize(new UI.Geometry.Size(300, 250));
      popover.setSizeBehavior(UI.GlassPane.SizeBehavior.SetExactSize);
      popover.contentElement.appendChild(popoverContentElement);
      return new ObjectPopoverHelper(linkifier, resultHighlightedAsDOM);
    }

    popoverContentElement = createElement('span');
    UI.Utils.appendStyle(popoverContentElement, 'object_ui/objectValue.css');
    UI.Utils.appendStyle(popoverContentElement, 'object_ui/objectPopover.css');
    const valueElement = popoverContentElement.createChild('span', 'monospace object-value-' + result.type);
    valueElement.style.whiteSpace = 'pre';

    if (result.type === 'string') {
      valueElement.createTextChildren(`"${description}"`);
    } else if (result.type !== 'function') {
      valueElement.textContent = description;
    }

    if (result.type !== 'function') {
      popover.contentElement.appendChild(popoverContentElement);
      return new ObjectPopoverHelper(null, false);
    }

    ObjectPropertiesSection.formatObjectAsFunction(result, valueElement, true);
    const response = await result.debuggerModel().functionDetailsPromise(result);
    if (!response) {
      return null;
    }

    const container = createElementWithClass('div', 'object-popover-container');
    const title = container.createChild('div', 'function-popover-title source-code');
    const functionName = title.createChild('span', 'function-name');
    functionName.textContent = UI.UIUtils.beautifyFunctionName(response.functionName);

    const rawLocation = response.location;
    const linkContainer = title.createChild('div', 'function-title-link-container');
    const sourceURL = rawLocation && rawLocation.script() && rawLocation.script().sourceURL;
    let linkifier = null;
    if (sourceURL) {
      linkifier = new Components.Linkifier.Linkifier();
      linkContainer.appendChild(
          linkifier.linkifyRawLocation(/** @type {!SDK.DebuggerModel.Location} */ (rawLocation), sourceURL));
    }
    container.appendChild(popoverContentElement);
    popover.contentElement.appendChild(container);
    return new ObjectPopoverHelper(linkifier, false);
  }
}

const MaxPopoverTextLength = 10000;
