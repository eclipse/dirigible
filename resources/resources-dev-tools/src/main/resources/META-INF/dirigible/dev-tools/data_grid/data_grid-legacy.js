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
// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as DataGridModule from './data_grid.js';

self.DataGrid = self.DataGrid || {};
DataGrid = DataGrid || {};

DataGrid._preferredWidthSymbol = Symbol('preferredWidth');
DataGrid._columnIdSymbol = Symbol('columnId');
DataGrid._sortIconSymbol = Symbol('sortIcon');
DataGrid._longTextSymbol = Symbol('longText');

/**
 * @unrestricted
 * @constructor
 */
DataGrid.DataGrid = DataGridModule.DataGrid.DataGridImpl;

/**
 * @unrestricted
 * @constructor
 */
DataGrid.CreationDataGridNode = DataGridModule.DataGrid.CreationDataGridNode;

/**
 * @unrestricted
 * @constructor
 */
DataGrid.DataGridNode = DataGridModule.DataGrid.DataGridNode;
DataGrid.DataGridWidget = DataGridModule.DataGrid.DataGridWidget;

/** @enum {symbol} */
DataGrid.DataGrid.Events = DataGridModule.DataGrid.Events;

/** @enum {string} */
DataGrid.DataGrid.Order = DataGridModule.DataGrid.Order;

/** @enum {string} */
DataGrid.DataGrid.Align = DataGridModule.DataGrid.Align;

/** @enum {symbol} */
DataGrid.DataGrid.DataType = DataGridModule.DataGrid.DataType;

/** @enum {string} */
DataGrid.DataGrid.ResizeMethod = DataGridModule.DataGrid.ResizeMethod;

/**
 * @constructor
 */
DataGrid.ShowMoreDataGridNode = DataGridModule.ShowMoreDataGridNode.ShowMoreDataGridNode;

/**
 * @unrestricted
 * @constructor
 */
DataGrid.SortableDataGrid = DataGridModule.SortableDataGrid.SortableDataGrid;

/**
 * @unrestricted
 * @constructor
 * @extends {DataGrid.ViewportDataGridNode<!NODE_TYPE>}
 */
DataGrid.SortableDataGridNode = DataGridModule.SortableDataGrid.SortableDataGridNode;

/**
 * @unrestricted
 * @extends {DataGrid.DataGrid<!NODE_TYPE>}
 * @constructor
 */
DataGrid.ViewportDataGrid = DataGridModule.ViewportDataGrid.ViewportDataGrid;

/**
 * @override @suppress {checkPrototypalTypes} @enum {symbol}
 */
DataGrid.ViewportDataGrid.Events = DataGridModule.ViewportDataGrid.Events;

/**
 * @unrestricted
 * @extends {DataGrid.DataGridNode<!NODE_TYPE>}
 * @constructor
 */
DataGrid.ViewportDataGridNode = DataGridModule.ViewportDataGrid.ViewportDataGridNode;
