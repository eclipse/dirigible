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

import * as HeapSnapshotModel from '../heap_snapshot_model/heap_snapshot_model.js';  // eslint-disable-line no-unused-vars

/**
 * @interface
 */
export class ChildrenProvider {
  dispose() {
  }

  /**
   * @param {number} snapshotObjectId
   * @return {!Promise<number>}
   */
  nodePosition(snapshotObjectId) {
  }

  /**
   * @return {!Promise<boolean>}
   */
  isEmpty() {
  }

  /**
   * @param {number} startPosition
   * @param {number} endPosition
   * @return {!Promise<!HeapSnapshotModel.HeapSnapshotModel.ItemsRange>}
   */
  serializeItemsRange(startPosition, endPosition) {
  }

  /**
   * @param {!HeapSnapshotModel.HeapSnapshotModel.ComparatorConfig} comparator
   * @return {!Promise<?>}
   */
  sortAndRewind(comparator) {
  }
}
