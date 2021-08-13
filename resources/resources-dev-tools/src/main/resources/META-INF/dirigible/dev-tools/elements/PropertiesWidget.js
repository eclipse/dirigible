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
import * as Host from '../host/host.js';
import * as ObjectUI from '../object_ui/object_ui.js';
import * as SDK from '../sdk/sdk.js';
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class PropertiesWidget extends UI.ThrottledWidget.ThrottledWidget {
  constructor() {
    super(true /* isWebComponent */);
    this.registerRequiredCSS('elements/propertiesWidget.css');

    SDK.SDKModel.TargetManager.instance().addModelListener(
        SDK.DOMModel.DOMModel, SDK.DOMModel.Events.AttrModified, this._onNodeChange, this);
    SDK.SDKModel.TargetManager.instance().addModelListener(
        SDK.DOMModel.DOMModel, SDK.DOMModel.Events.AttrRemoved, this._onNodeChange, this);
    SDK.SDKModel.TargetManager.instance().addModelListener(
        SDK.DOMModel.DOMModel, SDK.DOMModel.Events.CharacterDataModified, this._onNodeChange, this);
    SDK.SDKModel.TargetManager.instance().addModelListener(
        SDK.DOMModel.DOMModel, SDK.DOMModel.Events.ChildNodeCountUpdated, this._onNodeChange, this);
    self.UI.context.addFlavorChangeListener(SDK.DOMModel.DOMNode, this._setNode, this);
    this._node = self.UI.context.flavor(SDK.DOMModel.DOMNode);

    this._treeOutline = new ObjectUI.ObjectPropertiesSection.ObjectPropertiesSectionsTreeOutline({readOnly: true});
    this._treeOutline.setShowSelectionOnKeyboardFocus(/* show */ true, /* preventTabOrder */ false);
    this._expandController =
        new ObjectUI.ObjectPropertiesSection.ObjectPropertiesSectionsTreeExpandController(this._treeOutline);
    this.contentElement.appendChild(this._treeOutline.element);

    this._treeOutline.addEventListener(UI.TreeOutline.Events.ElementExpanded, () => {
      Host.userMetrics.actionTaken(Host.UserMetrics.Action.DOMPropertiesExpanded);
    });

    this.update();
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _setNode(event) {
    this._node = /** @type {?SDK.DOMModel.DOMNode} */ (event.data);
    this.update();
  }

  /**
   * @override
   * @protected
   * @return {!Promise<undefined>}
   */
  async doUpdate() {
    if (this._lastRequestedNode) {
      this._lastRequestedNode.domModel().runtimeModel().releaseObjectGroup(_objectGroupName);
      delete this._lastRequestedNode;
    }

    if (!this._node) {
      this._treeOutline.removeChildren();
      return;
    }

    this._lastRequestedNode = this._node;
    const object = await this._node.resolveToObject(_objectGroupName);
    if (!object) {
      return;
    }

    const result = await object.callFunction(protoList);
    object.release();

    if (!result.object || result.wasThrown) {
      return;
    }

    const propertiesResult = await result.object.getOwnProperties(false /* generatePreview */);
    result.object.release();

    if (!propertiesResult || !propertiesResult.properties) {
      return;
    }

    const properties = propertiesResult.properties;
    this._treeOutline.removeChildren();

    let selected = false;
    // Get array of property user-friendly names.
    for (let i = 0; i < properties.length; ++i) {
      if (!parseInt(properties[i].name, 10)) {
        continue;
      }
      const property = properties[i].value;
      let title = property.description;
      title = title.replace(/Prototype$/, '');

      const section = this._createSectionTreeElement(property, title);
      this._treeOutline.appendChild(section);
      if (!selected) {
        section.select(/* omitFocus= */ true, /* selectedByUser= */ false);
        selected = true;
      }
    }

    /**
     * @suppressReceiverCheck
     * @this {*}
     */
    function protoList() {
      let proto = this;
      const result = {__proto__: null};
      let counter = 1;
      while (proto) {
        result[counter++] = proto;
        proto = proto.__proto__;
      }
      return result;
    }
  }

  /**
   * @param {!SDK.RemoteObject.RemoteObject} property
   * @param {string} title
   * @returns {!ObjectUI.ObjectPropertiesSection.RootElement}
   */
  _createSectionTreeElement(property, title) {
    const titleElement = createElementWithClass('span', 'tree-element-title');
    titleElement.textContent = title;

    const section = new ObjectUI.ObjectPropertiesSection.RootElement(property);
    section.title = titleElement;
    this._expandController.watchSection(title, section);

    return section;
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onNodeChange(event) {
    if (!this._node) {
      return;
    }
    const data = event.data;
    const node = /** @type {!SDK.DOMModel.DOMNode} */ (data instanceof SDK.DOMModel.DOMNode ? data : data.node);
    if (this._node !== node) {
      return;
    }
    this.update();
  }
}

export const _objectGroupName = 'properties-sidebar-pane';
