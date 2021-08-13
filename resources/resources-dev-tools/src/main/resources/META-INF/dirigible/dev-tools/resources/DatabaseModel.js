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
import * as ProtocolClient from '../protocol_client/protocol_client.js';
import * as SDK from '../sdk/sdk.js';

/**
 * @unrestricted
 */
export class Database {
  /**
   * @param {!DatabaseModel} model
   * @param {string} id
   * @param {string} domain
   * @param {string} name
   * @param {string} version
   */
  constructor(model, id, domain, name, version) {
    this._model = model;
    this._id = id;
    this._domain = domain;
    this._name = name;
    this._version = version;
  }

  /** @return {string} */
  get id() {
    return this._id;
  }

  /** @return {string} */
  get name() {
    return this._name;
  }

  /** @param {string} x */
  set name(x) {
    this._name = x;
  }

  /** @return {string} */
  get version() {
    return this._version;
  }

  /** @param {string} x */
  set version(x) {
    this._version = x;
  }

  /** @return {string} */
  get domain() {
    return this._domain;
  }

  /** @param {string} x */
  set domain(x) {
    this._domain = x;
  }

  /**
   * @return {!Promise<!Array<string>>}
   */
  async tableNames() {
    const names = await this._model._agent.getDatabaseTableNames(this._id) || [];
    return names.sort();
  }

  /**
   * @param {string} query
   * @param {function(!Array.<string>=, !Array.<*>=)} onSuccess
   * @param {function(string)} onError
   */
  async executeSql(query, onSuccess, onError) {
    const response = await this._model._agent.invoke_executeSQL({'databaseId': this._id, 'query': query});
    const error = response[ProtocolClient.InspectorBackend.ProtocolError];
    if (error) {
      onError(error);
      return;
    }
    const sqlError = response.sqlError;
    if (!sqlError) {
      onSuccess(response.columnNames, response.values);
      return;
    }
    let message;
    if (sqlError.message) {
      message = sqlError.message;
    } else if (sqlError.code === 2) {
      message = Common.UIString.UIString('Database no longer has expected version.');
    } else {
      message = Common.UIString.UIString('An unexpected error %s occurred.', sqlError.code);
    }
    onError(message);
  }
}

/**
 * @unrestricted
 */
export class DatabaseModel extends SDK.SDKModel.SDKModel {
  /**
   * @param {!SDK.SDKModel.Target} target
   */
  constructor(target) {
    super(target);

    this._databases = [];
    this._agent = target.databaseAgent();
    this.target().registerDatabaseDispatcher(new DatabaseDispatcher(this));
  }

  enable() {
    if (this._enabled) {
      return;
    }
    this._agent.enable();
    this._enabled = true;
  }

  disable() {
    if (!this._enabled) {
      return;
    }
    this._enabled = false;
    this._databases = [];
    this._agent.disable();
    this.dispatchEventToListeners(Events.DatabasesRemoved);
  }

  /**
   * @return {!Array.<!Database>}
   */
  databases() {
    const result = [];
    for (const database of this._databases) {
      result.push(database);
    }
    return result;
  }

  /**
   * @param {!Database} database
   */
  _addDatabase(database) {
    this._databases.push(database);
    this.dispatchEventToListeners(Events.DatabaseAdded, database);
  }
}

SDK.SDKModel.SDKModel.register(DatabaseModel, SDK.SDKModel.Capability.DOM, false);

/** @enum {symbol} */
export const Events = {
  DatabaseAdded: Symbol('DatabaseAdded'),
  DatabasesRemoved: Symbol('DatabasesRemoved'),
};

/**
 * @implements {Protocol.DatabaseDispatcher}
 * @unrestricted
 */
export class DatabaseDispatcher {
  /**
   * @param {!DatabaseModel} model
   */
  constructor(model) {
    this._model = model;
  }

  /**
   * @override
   * @param {!Protocol.Database.Database} payload
   */
  addDatabase(payload) {
    this._model._addDatabase(new Database(this._model, payload.id, payload.domain, payload.name, payload.version));
  }
}
