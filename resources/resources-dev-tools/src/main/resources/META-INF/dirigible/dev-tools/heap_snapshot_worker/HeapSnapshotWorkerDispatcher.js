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
import * as HeapSnapshotModel from '../heap_snapshot_model/heap_snapshot_model.js';  // eslint-disable-line no-unused-vars

/**
 * @unrestricted
 */
export class HeapSnapshotWorkerDispatcher {
  constructor(globalObject, postMessage) {
    this._objects = [];
    this._global = globalObject;
    this._postMessage = postMessage;
  }

  _findFunction(name) {
    const path = name.split('.');
    let result = this._global;
    for (let i = 0; i < path.length; ++i) {
      result = result[path[i]];
    }
    return result;
  }

  /**
   * @param {string} name
   * @param {*} data
   */
  sendEvent(name, data) {
    this._postMessage({eventName: name, data: data});
  }

  dispatchMessage(event) {
    const data = /** @type {!HeapSnapshotModel.HeapSnapshotModel.WorkerCommand } */ (event.data);
    const response = {callId: data.callId};
    try {
      switch (data.disposition) {
        case 'create': {
          const constructorFunction = this._findFunction(data.methodName);
          this._objects[data.objectId] = new constructorFunction(this);
          break;
        }
        case 'dispose': {
          delete this._objects[data.objectId];
          break;
        }
        case 'getter': {
          const object = this._objects[data.objectId];
          const result = object[data.methodName];
          response.result = result;
          break;
        }
        case 'factory': {
          const object = this._objects[data.objectId];
          const result = object[data.methodName].apply(object, data.methodArguments);
          if (result) {
            this._objects[data.newObjectId] = result;
          }
          response.result = !!result;
          break;
        }
        case 'method': {
          const object = this._objects[data.objectId];
          response.result = object[data.methodName].apply(object, data.methodArguments);
          break;
        }
        case 'evaluateForTest': {
          try {
            response.result = self.eval(data.source);
          } catch (error) {
            response.result = error.toString();
          }
          break;
        }
      }
    } catch (error) {
      response.error = error.toString();
      response.errorCallStack = error.stack;
      if (data.methodName) {
        response.errorMethodName = data.methodName;
      }
    }
    this._postMessage(response);
  }
}
