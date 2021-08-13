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
import * as DataGrid from '../data_grid/data_grid.js';
import * as UI from '../ui/ui.js';

/**
 * @unrestricted
 */
export class DatabaseTableView extends UI.View.SimpleView {
  constructor(database, tableName) {
    super(Common.UIString.UIString('Database'));

    this.database = database;
    this.tableName = tableName;

    this.element.classList.add('storage-view', 'table');

    this._visibleColumnsSetting =
        Common.Settings.Settings.instance().createSetting('databaseTableViewVisibleColumns', {});

    this.refreshButton = new UI.Toolbar.ToolbarButton(Common.UIString.UIString('Refresh'), 'largeicon-refresh');
    this.refreshButton.addEventListener(UI.Toolbar.ToolbarButton.Events.Click, this._refreshButtonClicked, this);
    this._visibleColumnsInput = new UI.Toolbar.ToolbarInput(Common.UIString.UIString('Visible columns'), '', 1);
    this._visibleColumnsInput.addEventListener(
        UI.Toolbar.ToolbarInput.Event.TextChanged, this._onVisibleColumnsChanged, this);

    /** @type {?DataGrid.DataGrid.DataGridImpl} */
    this._dataGrid;
  }

  /**
   * @override
   */
  wasShown() {
    this.update();
  }

  /**
   * @override
   * @return {!Promise<!Array<!UI.Toolbar.ToolbarItem>>}
   */
  async toolbarItems() {
    return [this.refreshButton, this._visibleColumnsInput];
  }

  /**
   * @param {string} tableName
   * @return {string}
   */
  _escapeTableName(tableName) {
    return tableName.replace(/\"/g, '""');
  }

  update() {
    this.database.executeSql(
        'SELECT rowid, * FROM "' + this._escapeTableName(this.tableName) + '"', this._queryFinished.bind(this),
        this._queryError.bind(this));
  }

  _queryFinished(columnNames, values) {
    this.detachChildWidgets();
    this.element.removeChildren();

    this._dataGrid = DataGrid.SortableDataGrid.SortableDataGrid.create(columnNames, values, ls`Database`);
    this._visibleColumnsInput.setVisible(!!this._dataGrid);
    if (!this._dataGrid) {
      this._emptyWidget = new UI.EmptyWidget.EmptyWidget(ls`The "${this.tableName}"\ntable is empty.`);
      this._emptyWidget.show(this.element);
      return;
    }
    this._dataGrid.setStriped(true);
    this._dataGrid.asWidget().show(this.element);
    this._dataGrid.autoSizeColumns(5);

    this._columnsMap = new Map();
    for (let i = 1; i < columnNames.length; ++i) {
      this._columnsMap.set(columnNames[i], String(i));
    }
    this._lastVisibleColumns = '';
    const visibleColumnsText = this._visibleColumnsSetting.get()[this.tableName] || '';
    this._visibleColumnsInput.setValue(visibleColumnsText);
    this._onVisibleColumnsChanged();
  }

  _onVisibleColumnsChanged() {
    if (!this._dataGrid) {
      return;
    }
    const text = this._visibleColumnsInput.value();
    const parts = text.split(/[\s,]+/);
    const matches = new Set();
    const columnsVisibility = {};
    columnsVisibility['0'] = true;
    for (let i = 0; i < parts.length; ++i) {
      const part = parts[i];
      if (this._columnsMap.has(part)) {
        matches.add(part);
        columnsVisibility[this._columnsMap.get(part)] = true;
      }
    }
    const newVisibleColumns = [...matches].sort().join(', ');
    if (newVisibleColumns.length === 0) {
      for (const v of this._columnsMap.values()) {
        columnsVisibility[v] = true;
      }
    }
    if (newVisibleColumns === this._lastVisibleColumns) {
      return;
    }
    const visibleColumnsRegistry = this._visibleColumnsSetting.get();
    visibleColumnsRegistry[this.tableName] = text;
    this._visibleColumnsSetting.set(visibleColumnsRegistry);
    this._dataGrid.setColumnsVisiblity(columnsVisibility);
    this._lastVisibleColumns = newVisibleColumns;
  }

  _queryError(error) {
    this.detachChildWidgets();
    this.element.removeChildren();

    const errorMsgElement = createElement('div');
    errorMsgElement.className = 'storage-table-error';
    errorMsgElement.textContent = ls`An error occurred trying to\nread the "${this.tableName}" table.`;
    this.element.appendChild(errorMsgElement);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _refreshButtonClicked(event) {
    this.update();
  }
}
