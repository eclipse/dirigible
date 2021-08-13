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
import * as DataGrid from '../data_grid/data_grid.js';
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class DatabaseQueryView extends UI.Widget.VBox {
  constructor(database) {
    super();

    this.database = database;

    this.element.classList.add('storage-view', 'query', 'monospace');
    this.element.addEventListener('selectstart', this._selectStart.bind(this), false);

    this._queryWrapper = this.element.createChild('div', 'database-query-group-messages');
    this._queryWrapper.addEventListener('focusin', this._onFocusIn.bind(this));
    this._queryWrapper.addEventListener('focusout', this._onFocusOut.bind(this));
    this._queryWrapper.addEventListener('keydown', this._onKeyDown.bind(this));
    this._queryWrapper.tabIndex = -1;

    this._promptContainer = this.element.createChild('div', 'database-query-prompt-container');
    this._promptContainer.appendChild(UI.Icon.Icon.create('smallicon-text-prompt', 'prompt-icon'));
    this._promptElement = this._promptContainer.createChild('div');
    this._promptElement.className = 'database-query-prompt';
    this._promptElement.addEventListener('keydown', this._promptKeyDown.bind(this));

    this._prompt = new UI.TextPrompt.TextPrompt();
    this._prompt.initialize(this.completions.bind(this), ' ');
    this._proxyElement = this._prompt.attach(this._promptElement);

    this.element.addEventListener('click', this._messagesClicked.bind(this), true);

    /** @type {!Array<!Element>} */
    this._queryResults = [];
    this._virtualSelectedIndex = -1;
    /** @type {?Element} */
    this._lastSelectedElement;
    /** @type {number} */
    this._selectionTimeout = 0;
  }

  _messagesClicked() {
    this._prompt.focus();
    if (!this._prompt.isCaretInsidePrompt() && !this.element.hasSelection()) {
      this._prompt.moveCaretToEndOfPrompt();
    }
  }

  /**
   * @param {!Event} event
   */
  _onKeyDown(event) {
    if (UI.UIUtils.isEditing() || !this._queryResults.length || event.shiftKey) {
      return;
    }
    switch (event.key) {
      case 'ArrowUp':
        if (this._virtualSelectedIndex > 0) {
          this._virtualSelectedIndex--;
        } else {
          return;
        }
        break;
      case 'ArrowDown':
        if (this._virtualSelectedIndex < this._queryResults.length - 1) {
          this._virtualSelectedIndex++;
        } else {
          return;
        }
        break;
      case 'Home':
        this._virtualSelectedIndex = 0;
        break;
      case 'End':
        this._virtualSelectedIndex = this._queryResults.length - 1;
        break;
      default:
        return;
    }
    event.consume(true);
    this._updateFocusedItem();
  }

  /**
   * @param {!Event} event
   */
  _onFocusIn(event) {
    // Make default selection when moving from external (e.g. prompt) to the container.
    if (this._virtualSelectedIndex === -1 && this._isOutsideViewport(/** @type {?Element} */ (event.relatedTarget)) &&
        event.target === this._queryWrapper && this._queryResults.length) {
      this._virtualSelectedIndex = this._queryResults.length - 1;
    }
    this._updateFocusedItem();
  }

  /**
   * @param {!Event} event
   */
  _onFocusOut(event) {
    // Remove selection when focus moves to external location (e.g. prompt).
    if (this._isOutsideViewport(/** @type {?Element} */ (event.relatedTarget))) {
      this._virtualSelectedIndex = -1;
    }
    this._updateFocusedItem();

    this._queryWrapper.scrollTop = 10000000;
  }

  /**
   * @param {?Element} element
   * @return {boolean}
   */
  _isOutsideViewport(element) {
    return !!element && !element.isSelfOrDescendant(this._queryWrapper);
  }

  _updateFocusedItem() {
    let index = this._virtualSelectedIndex;
    if (this._queryResults.length && this._virtualSelectedIndex < 0) {
      index = this._queryResults.length - 1;
    }

    const selectedElement = index >= 0 ? this._queryResults[index] : null;
    const changed = this._lastSelectedElement !== selectedElement;
    const containerHasFocus = this._queryWrapper === this.element.ownerDocument.deepActiveElement();

    if (selectedElement && (changed || containerHasFocus) && this.element.hasFocus()) {
      if (!selectedElement.hasFocus()) {
        selectedElement.focus();
      }
    }

    if (this._queryResults.length && !this._queryWrapper.hasFocus()) {
      this._queryWrapper.tabIndex = 0;
    } else {
      this._queryWrapper.tabIndex = -1;
    }
    this._lastSelectedElement = selectedElement;
  }

  /**
   * @param {string} expression
   * @param {string} prefix
   * @param {boolean=} force
   * @return {!Promise<!UI.SuggestBox.Suggestions>}
   */
  async completions(expression, prefix, force) {
    if (!prefix) {
      return [];
    }

    prefix = prefix.toLowerCase();
    const tableNames = await this.database.tableNames();
    return tableNames.map(name => name + ' ')
        .concat(SQL_BUILT_INS)
        .filter(proposal => proposal.toLowerCase().startsWith(prefix))
        .map(completion => ({text: completion}));
  }

  _selectStart(event) {
    if (this._selectionTimeout) {
      clearTimeout(this._selectionTimeout);
    }

    this._prompt.clearAutocomplete();

    /**
     * @this {DatabaseQueryView}
     */
    function moveBackIfOutside() {
      delete this._selectionTimeout;
      if (!this._prompt.isCaretInsidePrompt() && !this.element.hasSelection()) {
        this._prompt.moveCaretToEndOfPrompt();
      }
      this._prompt.autoCompleteSoon();
    }

    this._selectionTimeout = setTimeout(moveBackIfOutside.bind(this), 100);
  }

  _promptKeyDown(event) {
    if (isEnterKey(event)) {
      this._enterKeyPressed(event);
      return;
    }
  }

  async _enterKeyPressed(event) {
    event.consume(true);

    const query = this._prompt.textWithCurrentSuggestion();
    this._prompt.clearAutocomplete();

    if (!query.length) {
      return;
    }

    this._prompt.setEnabled(false);
    try {
      const result = await new Promise((resolve, reject) => {
        this.database.executeSql(
            query, (columnNames, values) => resolve({columnNames, values}), errorText => reject(errorText));
      });
      this._queryFinished(query, result.columnNames, result.values);
    } catch (e) {
      this._appendErrorQueryResult(query, e);
    }
    this._prompt.setEnabled(true);
    this._prompt.setText('');
    this._prompt.focus();
  }

  _queryFinished(query, columnNames, values) {
    const dataGrid = DataGrid.SortableDataGrid.SortableDataGrid.create(columnNames, values, ls`Database Query`);
    const trimmedQuery = query.trim();

    let view = null;
    if (dataGrid) {
      dataGrid.setStriped(true);
      dataGrid.renderInline();
      dataGrid.autoSizeColumns(5);
      view = dataGrid.asWidget();
      dataGrid.setFocusable(false);
    }
    this._appendViewQueryResult(trimmedQuery, view);

    if (trimmedQuery.match(/^create /i) || trimmedQuery.match(/^drop table /i)) {
      this.dispatchEventToListeners(Events.SchemaUpdated, this.database);
    }
  }

  /**
   * @param {string} query
   * @param {?UI.Widget.Widget} view
   */
  _appendViewQueryResult(query, view) {
    const resultElement = this._appendQueryResult(query);
    if (view) {
      view.show(resultElement);
    } else {
      resultElement.remove();
    }

    this._scrollResultIntoView();
  }

  /**
   * @param {string} query
   * @param {string} errorText
   */
  _appendErrorQueryResult(query, errorText) {
    const resultElement = this._appendQueryResult(query);
    resultElement.classList.add('error');
    resultElement.appendChild(UI.Icon.Icon.create('smallicon-error', 'prompt-icon'));
    resultElement.createTextChild(errorText);

    this._scrollResultIntoView();
  }

  _scrollResultIntoView() {
    this._queryResults[this._queryResults.length - 1].scrollIntoView(false);
    this._promptElement.scrollIntoView(false);
  }

  /**
   * @param {string} query
   */
  _appendQueryResult(query) {
    const element = createElement('div');
    element.className = 'database-user-query';
    element.tabIndex = -1;

    UI.ARIAUtils.setAccessibleName(element, ls`Query: ${query}`);
    this._queryResults.push(element);
    this._updateFocusedItem();

    element.appendChild(UI.Icon.Icon.create('smallicon-user-command', 'prompt-icon'));

    const commandTextElement = createElement('span');
    commandTextElement.className = 'database-query-text';
    commandTextElement.textContent = query;
    element.appendChild(commandTextElement);

    const resultElement = createElement('div');
    resultElement.className = 'database-query-result';
    element.appendChild(resultElement);

    this._queryWrapper.appendChild(element);
    return resultElement;
  }
}

/** @enum {symbol} */
export const Events = {
  SchemaUpdated: Symbol('SchemaUpdated')
};

export const SQL_BUILT_INS = [
  'SELECT ', 'FROM ', 'WHERE ', 'LIMIT ', 'DELETE FROM ', 'CREATE ', 'DROP ', 'TABLE ', 'INDEX ', 'UPDATE ',
  'INSERT INTO ', 'VALUES ('
];
