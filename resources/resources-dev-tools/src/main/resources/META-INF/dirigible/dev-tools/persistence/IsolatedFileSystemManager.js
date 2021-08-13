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
import * as Host from '../host/host.js';

import {IsolatedFileSystem} from './IsolatedFileSystem.js';
import {PlatformFileSystem} from './PlatformFileSystem.js';  // eslint-disable-line no-unused-vars

/**
 * @type {!IsolatedFileSystemManager}
 */
let isolatedFileSystemManagerInstance;

/**
 * @unrestricted
 */
export class IsolatedFileSystemManager extends Common.ObjectWrapper.ObjectWrapper {
  /**
   * @private
   */
  constructor() {
    super();

    /** @type {!Map<string, !PlatformFileSystem>} */
    this._fileSystems = new Map();
    /** @type {!Map<number, function(!Array.<string>)>} */
    this._callbacks = new Map();
    /** @type {!Map<number, !Common.Progress.Progress>} */
    this._progresses = new Map();

    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.FileSystemRemoved, this._onFileSystemRemoved, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.FileSystemAdded, event => {
          this._onFileSystemAdded(event);
        }, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.FileSystemFilesChangedAddedRemoved, this._onFileSystemFilesChanged, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.IndexingTotalWorkCalculated, this._onIndexingTotalWorkCalculated, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.IndexingWorked, this._onIndexingWorked, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.IndexingDone, this._onIndexingDone, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.SearchCompleted, this._onSearchCompleted, this);

    this._initExcludePatterSetting();

    /** @type {?function(?IsolatedFileSystem)} */
    this._fileSystemRequestResolve = null;
    this._fileSystemsLoadedPromise = this._requestFileSystems();
  }

  /**
   * @param {{forceNew: ?boolean}} opts
   */
  static instance(opts = {forceNew: null}) {
    const {forceNew} = opts;
    if (!isolatedFileSystemManagerInstance || forceNew) {
      isolatedFileSystemManagerInstance = new IsolatedFileSystemManager();
    }

    return isolatedFileSystemManagerInstance;
  }

  /**
   * @return {!Promise<!Array<!IsolatedFileSystem>>}
   */
  _requestFileSystems() {
    let fulfill;
    const promise = new Promise(f => fulfill = f);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.FileSystemsLoaded, onFileSystemsLoaded, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.requestFileSystems();
    return promise;

    /**
     * @param {!Common.EventTarget.EventTargetEvent} event
     * @this {IsolatedFileSystemManager}
     */
    function onFileSystemsLoaded(event) {
      const fileSystems = /** @type {!Array.<!FileSystem>} */ (event.data);
      const promises = [];
      for (let i = 0; i < fileSystems.length; ++i) {
        promises.push(this._innerAddFileSystem(fileSystems[i], false));
      }
      Promise.all(promises).then(onFileSystemsAdded);
    }

    /**
     * @param {!Array<?IsolatedFileSystem>} fileSystems
     */
    function onFileSystemsAdded(fileSystems) {
      fulfill(fileSystems.filter(fs => !!fs));
    }
  }

  /**
   * @param {string=} type
   * @return {!Promise<?IsolatedFileSystem>}
   */
  addFileSystem(type) {
    return new Promise(resolve => {
      this._fileSystemRequestResolve = resolve;
      Host.InspectorFrontendHost.InspectorFrontendHostInstance.addFileSystem(type || '');
    });
  }

  /**
   * @param {!PlatformFileSystem} fileSystem
   */
  removeFileSystem(fileSystem) {
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.removeFileSystem(fileSystem.embedderPath());
  }

  /**
   * @return {!Promise<!Array<!IsolatedFileSystem>>}
   */
  waitForFileSystems() {
    return this._fileSystemsLoadedPromise;
  }

  /**
   * @param {!FileSystem} fileSystem
   * @param {boolean} dispatchEvent
   * @return {!Promise<?IsolatedFileSystem>}
   */
  _innerAddFileSystem(fileSystem, dispatchEvent) {
    const embedderPath = fileSystem.fileSystemPath;
    const fileSystemURL = Common.ParsedURL.ParsedURL.platformPathToURL(fileSystem.fileSystemPath);
    const promise = IsolatedFileSystem.create(
        this, fileSystemURL, embedderPath, fileSystem.type, fileSystem.fileSystemName, fileSystem.rootURL);
    return promise.then(storeFileSystem.bind(this));

    /**
     * @param {?PlatformFileSystem} fileSystem
     * @this {IsolatedFileSystemManager}
     */
    function storeFileSystem(fileSystem) {
      if (!fileSystem) {
        return null;
      }
      this._fileSystems.set(fileSystemURL, fileSystem);
      if (dispatchEvent) {
        this.dispatchEventToListeners(Events.FileSystemAdded, fileSystem);
      }
      return fileSystem;
    }
  }

  /**
   * @param {string} fileSystemURL
   * @param {!PlatformFileSystem} fileSystem
   */
  addPlatformFileSystem(fileSystemURL, fileSystem) {
    this._fileSystems.set(fileSystemURL, fileSystem);
    this.dispatchEventToListeners(Events.FileSystemAdded, fileSystem);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onFileSystemAdded(event) {
    const errorMessage = /** @type {string} */ (event.data['errorMessage']);
    const fileSystem = /** @type {?FileSystem} */ (event.data['fileSystem']);
    if (errorMessage) {
      if (errorMessage !== '<selection cancelled>') {
        Common.Console.Console.instance().error(Common.UIString.UIString('Unable to add filesystem: %s', errorMessage));
      }
      if (!this._fileSystemRequestResolve) {
        return;
      }
      this._fileSystemRequestResolve.call(null, null);
      this._fileSystemRequestResolve = null;
    } else if (fileSystem) {
      this._innerAddFileSystem(fileSystem, true).then(fileSystem => {
        if (this._fileSystemRequestResolve) {
          this._fileSystemRequestResolve.call(null, fileSystem);
          this._fileSystemRequestResolve = null;
        }
      });
    }
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onFileSystemRemoved(event) {
    const embedderPath = /** @type {string} */ (event.data);
    const fileSystemPath = Common.ParsedURL.ParsedURL.platformPathToURL(embedderPath);
    const isolatedFileSystem = this._fileSystems.get(fileSystemPath);
    if (!isolatedFileSystem) {
      return;
    }
    this._fileSystems.delete(fileSystemPath);
    isolatedFileSystem.fileSystemRemoved();
    this.dispatchEventToListeners(Events.FileSystemRemoved, isolatedFileSystem);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onFileSystemFilesChanged(event) {
    const urlPaths = {
      changed: groupFilePathsIntoFileSystemPaths.call(this, event.data.changed),
      added: groupFilePathsIntoFileSystemPaths.call(this, event.data.added),
      removed: groupFilePathsIntoFileSystemPaths.call(this, event.data.removed)
    };

    this.dispatchEventToListeners(Events.FileSystemFilesChanged, urlPaths);

    /**
     * @param {!Array<string>} embedderPaths
     * @return {!Platform.Multimap<string, string>}
     * @this {IsolatedFileSystemManager}
     */
    function groupFilePathsIntoFileSystemPaths(embedderPaths) {
      const paths = new Platform.Multimap();
      for (const embedderPath of embedderPaths) {
        const filePath = Common.ParsedURL.ParsedURL.platformPathToURL(embedderPath);
        for (const fileSystemPath of this._fileSystems.keys()) {
          if (this._fileSystems.get(fileSystemPath).isFileExcluded(embedderPath)) {
            continue;
          }
          const pathPrefix = fileSystemPath.endsWith('/') ? fileSystemPath : fileSystemPath + '/';
          if (!filePath.startsWith(pathPrefix)) {
            continue;
          }
          paths.set(fileSystemPath, filePath);
        }
      }
      return paths;
    }
  }

  /**
   * @return {!Array<!IsolatedFileSystem>}
   */
  fileSystems() {
    return [...this._fileSystems.values()];
  }

  /**
   * @param {string} fileSystemPath
   * @return {?PlatformFileSystem}
   */
  fileSystem(fileSystemPath) {
    return this._fileSystems.get(fileSystemPath) || null;
  }

  _initExcludePatterSetting() {
    const defaultCommonExcludedFolders = [
      '/node_modules/', '/bower_components/', '/\\.devtools', '/\\.git/', '/\\.sass-cache/', '/\\.hg/', '/\\.idea/',
      '/\\.svn/', '/\\.cache/', '/\\.project/'
    ];
    const defaultWinExcludedFolders = ['/Thumbs.db$', '/ehthumbs.db$', '/Desktop.ini$', '/\\$RECYCLE.BIN/'];
    const defaultMacExcludedFolders = [
      '/\\.DS_Store$', '/\\.Trashes$', '/\\.Spotlight-V100$', '/\\.AppleDouble$', '/\\.LSOverride$', '/Icon$',
      '/\\._.*$'
    ];
    const defaultLinuxExcludedFolders = ['/.*~$'];
    let defaultExcludedFolders = defaultCommonExcludedFolders;
    if (Host.Platform.isWin()) {
      defaultExcludedFolders = defaultExcludedFolders.concat(defaultWinExcludedFolders);
    } else if (Host.Platform.isMac()) {
      defaultExcludedFolders = defaultExcludedFolders.concat(defaultMacExcludedFolders);
    } else {
      defaultExcludedFolders = defaultExcludedFolders.concat(defaultLinuxExcludedFolders);
    }
    const defaultExcludedFoldersPattern = defaultExcludedFolders.join('|');
    this._workspaceFolderExcludePatternSetting = Common.Settings.Settings.instance().createRegExpSetting(
        'workspaceFolderExcludePattern', defaultExcludedFoldersPattern, Host.Platform.isWin() ? 'i' : '');
  }

  /**
   * @return {!Common.Settings.Setting}
   */
  workspaceFolderExcludePatternSetting() {
    return this._workspaceFolderExcludePatternSetting;
  }

  /**
   * @param {function(!Array.<string>)} callback
   * @return {number}
   */
  registerCallback(callback) {
    const requestId = ++_lastRequestId;
    this._callbacks.set(requestId, callback);
    return requestId;
  }

  /**
   * @param {!Common.Progress.Progress} progress
   * @return {number}
   */
  registerProgress(progress) {
    const requestId = ++_lastRequestId;
    this._progresses.set(requestId, progress);
    return requestId;
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onIndexingTotalWorkCalculated(event) {
    const requestId = /** @type {number} */ (event.data['requestId']);
    const totalWork = /** @type {number} */ (event.data['totalWork']);

    const progress = this._progresses.get(requestId);
    if (!progress) {
      return;
    }
    progress.setTotalWork(totalWork);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onIndexingWorked(event) {
    const requestId = /** @type {number} */ (event.data['requestId']);
    const worked = /** @type {number} */ (event.data['worked']);

    const progress = this._progresses.get(requestId);
    if (!progress) {
      return;
    }
    progress.worked(worked);
    if (progress.isCanceled()) {
      Host.InspectorFrontendHost.InspectorFrontendHostInstance.stopIndexing(requestId);
      this._onIndexingDone(event);
    }
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onIndexingDone(event) {
    const requestId = /** @type {number} */ (event.data['requestId']);

    const progress = this._progresses.get(requestId);
    if (!progress) {
      return;
    }
    progress.done();
    this._progresses.delete(requestId);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onSearchCompleted(event) {
    const requestId = /** @type {number} */ (event.data['requestId']);
    const files = /** @type {!Array.<string>} */ (event.data['files']);

    const callback = this._callbacks.get(requestId);
    if (!callback) {
      return;
    }
    callback.call(null, files);
    this._callbacks.delete(requestId);
  }
}

/** @enum {symbol} */
export const Events = {
  FileSystemAdded: Symbol('FileSystemAdded'),
  FileSystemRemoved: Symbol('FileSystemRemoved'),
  FileSystemFilesChanged: Symbol('FileSystemFilesChanged'),
  ExcludedFolderAdded: Symbol('ExcludedFolderAdded'),
  ExcludedFolderRemoved: Symbol('ExcludedFolderRemoved')
};

let _lastRequestId = 0;

/** @typedef {!{type: string, fileSystemName: string, rootURL: string, fileSystemPath: string}} */
export let FileSystem;
