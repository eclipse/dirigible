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
import * as SDK from '../sdk/sdk.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';     // eslint-disable-line no-unused-vars

import {Formatter, ProfileDataGridNode, ProfileDataGridTree} from './ProfileDataGrid.js';  // eslint-disable-line no-unused-vars

/**
 * @unrestricted
 */
export class TopDownProfileDataGridNode extends ProfileDataGridNode {
  /**
   * @param {!SDK.ProfileTreeModel.ProfileNode} profileNode
   * @param {!TopDownProfileDataGridTree} owningTree
   */
  constructor(profileNode, owningTree) {
    const hasChildren = !!(profileNode.children && profileNode.children.length);

    super(profileNode, owningTree, hasChildren);

    this._remainingChildren = profileNode.children;
  }

  /**
   * @param {!TopDownProfileDataGridNode|!TopDownProfileDataGridTree} container
   */
  static _sharedPopulate(container) {
    const children = container._remainingChildren;
    const childrenLength = children.length;

    for (let i = 0; i < childrenLength; ++i) {
      container.appendChild(
          new TopDownProfileDataGridNode(children[i], /** @type {!TopDownProfileDataGridTree} */ (container.tree)));
    }

    container._remainingChildren = null;
  }

  /**
   * @param {!TopDownProfileDataGridNode|!TopDownProfileDataGridTree} container
   * @param {string} aCallUID
   */
  static _excludeRecursively(container, aCallUID) {
    if (container._remainingChildren) {
      container.populate();
    }

    container.save();

    const children = container.children;
    let index = container.children.length;

    while (index--) {
      TopDownProfileDataGridNode._excludeRecursively(children[index], aCallUID);
    }

    const child = container.childrenByCallUID.get(aCallUID);

    if (child) {
      ProfileDataGridNode.merge(container, child, true);
    }
  }

  /**
   * @override
   */
  populateChildren() {
    TopDownProfileDataGridNode._sharedPopulate(this);
  }
}

/**
 * @unrestricted
 */
export class TopDownProfileDataGridTree extends ProfileDataGridTree {
  /**
   * @param {!Formatter} formatter
   * @param {!UI.SearchableView.SearchableView} searchableView
   * @param {!SDK.ProfileTreeModel.ProfileNode} rootProfileNode
   * @param {number} total
   */
  constructor(formatter, searchableView, rootProfileNode, total) {
    super(formatter, searchableView, total);
    this._remainingChildren = rootProfileNode.children;
    ProfileDataGridNode.populate(this);
  }

  /**
   * @param {!ProfileDataGridNode} profileDataGridNode
   */
  focus(profileDataGridNode) {
    if (!profileDataGridNode) {
      return;
    }

    this.save();
    profileDataGridNode.savePosition();

    this.children = [profileDataGridNode];
    this.total = profileDataGridNode.total;
  }

  /**
   * @param {!ProfileDataGridNode} profileDataGridNode
   */
  exclude(profileDataGridNode) {
    if (!profileDataGridNode) {
      return;
    }

    this.save();

    TopDownProfileDataGridNode._excludeRecursively(this, profileDataGridNode.callUID);

    if (this.lastComparator) {
      this.sort(this.lastComparator, true);
    }
  }

  /**
   * @override
   */
  restore() {
    if (!this._savedChildren) {
      return;
    }

    this.children[0].restorePosition();

    super.restore();
  }

  /**
   * @override
   */
  populateChildren() {
    TopDownProfileDataGridNode._sharedPopulate(this);
  }
}
