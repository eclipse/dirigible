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
import * as Components from '../components/components.js';
import * as ObjectUI from '../object_ui/object_ui.js';
import * as SDK from '../sdk/sdk.js';
import * as UI from '../ui/ui.js';

import {resolveScopeInObject, resolveThisObject} from './SourceMapNamesResolver.js';

/**
 * @implements {UI.ContextFlavorListener.ContextFlavorListener}
 * @unrestricted
 */
export class ScopeChainSidebarPaneBase extends UI.Widget.VBox {
  constructor() {
    super(true);
    this.registerRequiredCSS('sources/scopeChainSidebarPane.css');
    this._treeOutline = new ObjectUI.ObjectPropertiesSection.ObjectPropertiesSectionsTreeOutline();
    this._treeOutline.registerRequiredCSS('sources/scopeChainSidebarPane.css');
    this._treeOutline.setShowSelectionOnKeyboardFocus(/* show */ true);
    this._expandController =
        new ObjectUI.ObjectPropertiesSection.ObjectPropertiesSectionsTreeExpandController(this._treeOutline);
    this._linkifier = new Components.Linkifier.Linkifier();
    this._infoElement = createElement('div');
    this._infoElement.className = 'gray-info-message';
    this._infoElement.textContent = ls`Not paused`;
    this._infoElement.tabIndex = -1;
    this._update();
  }

  /**
   * @override
   * @param {?Object} object
   */
  flavorChanged(object) {
    this._update();
  }

  /**
   * @override
   */
  focus() {
    if (this.hasFocus()) {
      return;
    }

    if (self.UI.context.flavor(SDK.DebuggerModel.DebuggerPausedDetails)) {
      this._treeOutline.forceSelect();
    }
  }

  _getScopeChain(callFrame) {
    return [];
  }

  _update() {
    const callFrame = self.UI.context.flavor(SDK.DebuggerModel.CallFrame);
    const details = self.UI.context.flavor(SDK.DebuggerModel.DebuggerPausedDetails);
    this._linkifier.reset();
    resolveThisObject(callFrame).then(this._innerUpdate.bind(this, details, callFrame));
  }

  /**
   * @param {?SDK.DebuggerModel.DebuggerPausedDetails} details
   * @param {?SDK.DebuggerModel.CallFrame} callFrame
   * @param {?SDK.RemoteObject.RemoteObject} thisObject
   */
  _innerUpdate(details, callFrame, thisObject) {
    this._treeOutline.removeChildren();
    this.contentElement.removeChildren();

    if (!details || !callFrame) {
      this.contentElement.appendChild(this._infoElement);
      return;
    }

    this.contentElement.appendChild(this._treeOutline.element);
    let foundLocalScope = false;
    const scopeChain = this._getScopeChain(callFrame);
    if (scopeChain) {
      for (let i = 0; i < scopeChain.length; ++i) {
        const scope = scopeChain[i];
        const extraProperties = this._extraPropertiesForScope(scope, details, callFrame, thisObject, i === 0);

        if (scope.type() === Protocol.Debugger.ScopeType.Local) {
          foundLocalScope = true;
        }

        const section = this._createScopeSectionTreeElement(scope, extraProperties);
        if (scope.type() === Protocol.Debugger.ScopeType.Global) {
          section.collapse();
        } else if (!foundLocalScope || scope.type() === Protocol.Debugger.ScopeType.Local) {
          section.expand();
        }

        this._treeOutline.appendChild(section);
        if (i === 0) {
          section.select(/* omitFocus */ true);
        }
      }
    }
    this._sidebarPaneUpdatedForTest();
  }

  /**
   * @param {!SDK.DebuggerModel.Scope} scope
   * @param {!Array.<!SDK.RemoteObject.RemoteObjectProperty>} extraProperties
   * @return {!ObjectUI.ObjectPropertiesSection.RootElement}
   */
  _createScopeSectionTreeElement(scope, extraProperties) {
    let emptyPlaceholder = null;
    if (scope.type() === Protocol.Debugger.ScopeType.Local || Protocol.Debugger.ScopeType.Closure) {
      emptyPlaceholder = ls`No variables`;
    }

    let title = scope.typeName();
    if (scope.type() === Protocol.Debugger.ScopeType.Closure) {
      const scopeName = scope.name();
      if (scopeName) {
        title = ls`Closure (${UI.UIUtils.beautifyFunctionName(scopeName)})`;
      } else {
        title = ls`Closure`;
      }
    }
    let subtitle = scope.description();
    if (!title || title === subtitle) {
      subtitle = undefined;
    }

    const titleElement = createElementWithClass('div', 'scope-chain-sidebar-pane-section-header tree-element-title');
    titleElement.createChild('div', 'scope-chain-sidebar-pane-section-subtitle').textContent = subtitle;
    titleElement.createChild('div', 'scope-chain-sidebar-pane-section-title').textContent = title;

    const section = new ObjectUI.ObjectPropertiesSection.RootElement(
        resolveScopeInObject(scope), this._linkifier, emptyPlaceholder,
        /* ignoreHasOwnProperty */ true, extraProperties);
    section.title = titleElement;
    section.listItemElement.classList.add('scope-chain-sidebar-pane-section');
    this._expandController.watchSection(title + (subtitle ? ':' + subtitle : ''), section);

    return section;
  }

  /**
   * @param {!SDK.DebuggerModel.Scope} scope
   * @param {?SDK.DebuggerModel.DebuggerPausedDetails} details
   * @param {?SDK.DebuggerModel.CallFrame} callFrame
   * @param {?SDK.RemoteObject.RemoteObject} thisObject
   * @param {boolean} isFirstScope
   * @return {!Array.<!SDK.RemoteObject.RemoteObjectProperty>}
   */
  _extraPropertiesForScope(scope, details, callFrame, thisObject, isFirstScope) {
    if (scope.type() !== Protocol.Debugger.ScopeType.Local) {
      return [];
    }

    const extraProperties = [];
    if (thisObject) {
      extraProperties.push(new SDK.RemoteObject.RemoteObjectProperty('this', thisObject));
    }
    if (isFirstScope) {
      const exception = details.exception();
      if (exception) {
        extraProperties.push(new SDK.RemoteObject.RemoteObjectProperty(
            Common.UIString.UIString('Exception'), exception, undefined, undefined, undefined, undefined, undefined,
            /* synthetic */ true));
      }
      const returnValue = callFrame.returnValue();
      if (returnValue) {
        extraProperties.push(new SDK.RemoteObject.RemoteObjectProperty(
            Common.UIString.UIString('Return value'), returnValue, undefined, undefined, undefined, undefined,
            undefined,
            /* synthetic */ true, callFrame.setReturnValue.bind(callFrame)));
      }
    }

    return extraProperties;
  }

  _sidebarPaneUpdatedForTest() {
  }
}

/**
 * @unrestricted
 */
export class SourceScopeChainSidebarPane extends ScopeChainSidebarPaneBase {
  constructor() {
    super();
  }
  /**
   * @override
   */
  _getScopeChain(callFrame) {
    return callFrame.sourceScopeChain;
  }
}

/**
 * @unrestricted
 */
export class ScopeChainSidebarPane extends ScopeChainSidebarPaneBase {
  /**
   * @override
   */
  _getScopeChain(callFrame) {
    return callFrame.scopeChain();
  }
}


export const pathSymbol = Symbol('path');
