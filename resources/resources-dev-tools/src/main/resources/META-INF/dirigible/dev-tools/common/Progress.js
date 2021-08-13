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
/**
 * @interface
 */
export class Progress {
  /**
   * @param {number} totalWork
   */
  setTotalWork(totalWork) {
  }

  /**
   * @param {string} title
   */
  setTitle(title) {
  }

  /**
   * @param {number} worked
   * @param {string=} title
   */
  setWorked(worked, title) {
  }

  /**
   * @param {number=} worked
   */
  worked(worked) {
  }

  done() {
  }

  /**
   * @return {boolean}
   */
  isCanceled() {
    return false;
  }
}

/**
 * @unrestricted
 */
export class CompositeProgress {
  /**
   * @param {!Progress} parent
   */
  constructor(parent) {
    this._parent = parent;
    /** @type {!Array.<!SubProgress>} */
    this._children = [];
    this._childrenDone = 0;
    this._parent.setTotalWork(1);
    this._parent.setWorked(0);
  }

  _childDone() {
    if (++this._childrenDone !== this._children.length) {
      return;
    }
    this._parent.done();
  }

  /**
   * @param {number=} weight
   * @return {!SubProgress}
   */
  createSubProgress(weight) {
    const child = new SubProgress(this, weight);
    this._children.push(child);
    return child;
  }

  _update() {
    let totalWeights = 0;
    let done = 0;

    for (let i = 0; i < this._children.length; ++i) {
      const child = this._children[i];
      if (child._totalWork) {
        done += child._weight * child._worked / child._totalWork;
      }
      totalWeights += child._weight;
    }
    this._parent.setWorked(done / totalWeights);
  }
}

/**
 * @implements {Progress}
 * @unrestricted
 */
export class SubProgress {
  /**
   * @param {!CompositeProgress} composite
   * @param {number=} weight
   */
  constructor(composite, weight) {
    this._composite = composite;
    this._weight = weight || 1;
    this._worked = 0;

    /** @type {number} */
    this._totalWork = 0;
  }

  /**
   * @override
   * @return {boolean}
   */
  isCanceled() {
    return this._composite._parent.isCanceled();
  }

  /**
   * @override
   * @param {string} title
   */
  setTitle(title) {
    this._composite._parent.setTitle(title);
  }

  /**
   * @override
   */
  done() {
    this.setWorked(this._totalWork);
    this._composite._childDone();
  }

  /**
   * @override
   * @param {number} totalWork
   */
  setTotalWork(totalWork) {
    this._totalWork = totalWork;
    this._composite._update();
  }

  /**
   * @override
   * @param {number} worked
   * @param {string=} title
   */
  setWorked(worked, title) {
    this._worked = worked;
    if (typeof title !== 'undefined') {
      this.setTitle(title);
    }
    this._composite._update();
  }

  /**
   * @override
   * @param {number=} worked
   */
  worked(worked) {
    this.setWorked(this._worked + (worked || 1));
  }
}

/**
 * @implements {Progress}
 * @unrestricted
 */
export class ProgressProxy {
  /**
   * @param {?Progress=} delegate
   * @param {function()=} doneCallback
   */
  constructor(delegate, doneCallback) {
    this._delegate = delegate;
    this._doneCallback = doneCallback;
  }

  /**
   * @override
   * @return {boolean}
   */
  isCanceled() {
    return this._delegate ? this._delegate.isCanceled() : false;
  }

  /**
   * @override
   * @param {string} title
   */
  setTitle(title) {
    if (this._delegate) {
      this._delegate.setTitle(title);
    }
  }

  /**
   * @override
   */
  done() {
    if (this._delegate) {
      this._delegate.done();
    }
    if (this._doneCallback) {
      this._doneCallback();
    }
  }

  /**
   * @override
   * @param {number} totalWork
   */
  setTotalWork(totalWork) {
    if (this._delegate) {
      this._delegate.setTotalWork(totalWork);
    }
  }

  /**
   * @override
   * @param {number} worked
   * @param {string=} title
   */
  setWorked(worked, title) {
    if (this._delegate) {
      this._delegate.setWorked(worked, title);
    }
  }

  /**
   * @override
   * @param {number=} worked
   */
  worked(worked) {
    if (this._delegate) {
      this._delegate.worked(worked);
    }
  }
}
