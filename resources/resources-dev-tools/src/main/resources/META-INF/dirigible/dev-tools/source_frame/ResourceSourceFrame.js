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
import * as TextUtils from '../text_utils/text_utils.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';

import {SourceFrameImpl} from './SourceFrame.js';

/**
 * @unrestricted
 */
export class ResourceSourceFrame extends SourceFrameImpl {
  /**
   * @param {!TextUtils.ContentProvider.ContentProvider} resource
   * @param {boolean=} autoPrettyPrint
   * @param {!UI.TextEditor.Options=} codeMirrorOptions
   */
  constructor(resource, autoPrettyPrint, codeMirrorOptions) {
    super(async () => {
      let content = (await resource.requestContent()).content || '';
      if (await resource.contentEncoded()) {
        content = window.atob(content);
      }
      return {content, isEncoded: false};
    }, codeMirrorOptions);
    this._resource = resource;
    this.setCanPrettyPrint(this._resource.contentType().isDocumentOrScriptOrStyleSheet(), autoPrettyPrint);
  }

  /**
   * @param {!TextUtils.ContentProvider.ContentProvider} resource
   * @param {string} highlighterType
   * @param {boolean=} autoPrettyPrint
   * @return {!UI.Widget.Widget}
   */
  static createSearchableView(resource, highlighterType, autoPrettyPrint) {
    return new SearchableContainer(resource, highlighterType, autoPrettyPrint);
  }

  get resource() {
    return this._resource;
  }

  /**
   * @override
   * @param {!UI.ContextMenu.ContextMenu} contextMenu
   * @param {number} lineNumber
   * @param {number} columnNumber
   * @return {!Promise}
   */
  populateTextAreaContextMenu(contextMenu, lineNumber, columnNumber) {
    contextMenu.appendApplicableItems(this._resource);
    return Promise.resolve();
  }
}

export class SearchableContainer extends UI.Widget.VBox {
  /**
   * @param {!TextUtils.ContentProvider.ContentProvider} resource
   * @param {string} highlighterType
   * @param {boolean=} autoPrettyPrint
   * @return {!UI.Widget.Widget}
   */
  constructor(resource, highlighterType, autoPrettyPrint) {
    super(true);
    this.registerRequiredCSS('source_frame/resourceSourceFrame.css');
    const sourceFrame = new ResourceSourceFrame(resource, autoPrettyPrint);
    this._sourceFrame = sourceFrame;
    sourceFrame.setHighlighterType(highlighterType);
    const searchableView = new UI.SearchableView.SearchableView(sourceFrame);
    searchableView.element.classList.add('searchable-view');
    searchableView.setPlaceholder(ls`Find`);
    sourceFrame.show(searchableView.element);
    sourceFrame.setSearchableView(searchableView);
    searchableView.show(this.contentElement);

    const toolbar = new UI.Toolbar.Toolbar('toolbar', this.contentElement);
    sourceFrame.toolbarItems().then(items => {
      items.map(item => toolbar.appendToolbarItem(item));
    });
  }

  /**
   * @param {number} lineNumber
   * @param {number=} columnNumber
   */
  async revealPosition(lineNumber, columnNumber) {
    this._sourceFrame.revealPosition(lineNumber, columnNumber, true);
  }
}
