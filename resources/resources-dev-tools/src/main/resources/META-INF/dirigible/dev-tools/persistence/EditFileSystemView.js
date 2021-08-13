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
import * as UI from '../ui/ui.js';

import {Events, IsolatedFileSystemManager} from './IsolatedFileSystemManager.js';

/**
 * @implements {UI.ListWidget.Delegate}
 * @unrestricted
 */
export class EditFileSystemView extends UI.Widget.VBox {
  /**
   * @param {string} fileSystemPath
   */
  constructor(fileSystemPath) {
    super(true);
    this.registerRequiredCSS('persistence/editFileSystemView.css');
    this._fileSystemPath = fileSystemPath;

    this._eventListeners = [
      IsolatedFileSystemManager.instance().addEventListener(Events.ExcludedFolderAdded, this._update, this),
      IsolatedFileSystemManager.instance().addEventListener(Events.ExcludedFolderRemoved, this._update, this)
    ];

    const excludedFoldersHeader = this.contentElement.createChild('div', 'file-system-header');
    excludedFoldersHeader.createChild('div', 'file-system-header-text').textContent =
        Common.UIString.UIString('Excluded folders');
    excludedFoldersHeader.appendChild(UI.UIUtils.createTextButton(
        Common.UIString.UIString('Add'), this._addExcludedFolderButtonClicked.bind(this), 'add-button'));
    this._excludedFoldersList = new UI.ListWidget.ListWidget(this);
    this._excludedFoldersList.element.classList.add('file-system-list');
    this._excludedFoldersList.registerRequiredCSS('persistence/editFileSystemView.css');
    const excludedFoldersPlaceholder = createElementWithClass('div', 'file-system-list-empty');
    excludedFoldersPlaceholder.textContent = Common.UIString.UIString('None');
    this._excludedFoldersList.setEmptyPlaceholder(excludedFoldersPlaceholder);
    this._excludedFoldersList.show(this.contentElement);

    this._update();
  }

  dispose() {
    Common.EventTarget.EventTarget.removeEventListeners(this._eventListeners);
  }

  _update() {
    if (this._muteUpdate) {
      return;
    }

    this._excludedFoldersList.clear();
    this._excludedFolders = [];
    for (const folder of IsolatedFileSystemManager.instance()
             .fileSystem(this._fileSystemPath)
             .excludedFolders()
             .values()) {
      this._excludedFolders.push(folder);
      this._excludedFoldersList.appendItem(folder, true);
    }
  }

  _addExcludedFolderButtonClicked() {
    this._excludedFoldersList.addNewItem(0, '');
  }

  /**
   * @override
   * @param {*} item
   * @param {boolean} editable
   * @return {!Element}
   */
  renderItem(item, editable) {
    const element = createElementWithClass('div', 'file-system-list-item');
    const pathPrefix = /** @type {string} */ (editable ? item : Common.UIString.UIString('%s (via .devtools)', item));
    const pathPrefixElement = element.createChild('div', 'file-system-value');
    pathPrefixElement.textContent = pathPrefix;
    pathPrefixElement.title = pathPrefix;
    return element;
  }

  /**
   * @override
   * @param {*} item
   * @param {number} index
   */
  removeItemRequested(item, index) {
    IsolatedFileSystemManager.instance()
        .fileSystem(this._fileSystemPath)
        .removeExcludedFolder(this._excludedFolders[index]);
  }

  /**
   * @override
   * @param {*} item
   * @param {!UI.ListWidget.Editor} editor
   * @param {boolean} isNew
   */
  commitEdit(item, editor, isNew) {
    this._muteUpdate = true;
    if (!isNew) {
      IsolatedFileSystemManager.instance()
          .fileSystem(this._fileSystemPath)
          .removeExcludedFolder(/** @type {string} */ (item));
    }
    IsolatedFileSystemManager.instance()
        .fileSystem(this._fileSystemPath)
        .addExcludedFolder(this._normalizePrefix(editor.control('pathPrefix').value));
    this._muteUpdate = false;
    this._update();
  }

  /**
   * @override
   * @param {*} item
   * @return {!UI.ListWidget.Editor}
   */
  beginEdit(item) {
    const editor = this._createExcludedFolderEditor();
    editor.control('pathPrefix').value = item;
    return editor;
  }

  /**
   * @return {!UI.ListWidget.Editor}
   */
  _createExcludedFolderEditor() {
    if (this._excludedFolderEditor) {
      return this._excludedFolderEditor;
    }

    const editor = new UI.ListWidget.Editor();
    this._excludedFolderEditor = editor;
    const content = editor.contentElement();

    const titles = content.createChild('div', 'file-system-edit-row');
    titles.createChild('div', 'file-system-value').textContent = Common.UIString.UIString('Folder path');

    const fields = content.createChild('div', 'file-system-edit-row');
    fields.createChild('div', 'file-system-value')
        .appendChild(editor.createInput('pathPrefix', 'text', '/path/to/folder/', pathPrefixValidator.bind(this)));

    return editor;

    /**
     * @param {*} item
     * @param {number} index
     * @param {!HTMLInputElement|!HTMLSelectElement} input
     * @return {!UI.ListWidget.ValidatorResult}
     * @this {EditFileSystemView}
     */
    function pathPrefixValidator(item, index, input) {
      const prefix = this._normalizePrefix(input.value.trim());

      if (!prefix) {
        return {valid: false, errorMessage: ls`Enter a path`};
      }

      const configurableCount =
          IsolatedFileSystemManager.instance().fileSystem(this._fileSystemPath).excludedFolders().size;
      for (let i = 0; i < configurableCount; ++i) {
        if (i !== index && this._excludedFolders[i] === prefix) {
          return {valid: false, errorMessage: ls`Enter a unique path`};
        }
      }
      return {valid: true};
    }
  }

  /**
   * @param {string} prefix
   * @return {string}
   */
  _normalizePrefix(prefix) {
    if (!prefix) {
      return '';
    }
    return prefix + (prefix[prefix.length - 1] === '/' ? '' : '/');
  }
}
