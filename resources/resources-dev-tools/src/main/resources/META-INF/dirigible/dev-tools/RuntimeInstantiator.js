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
// Make sure that global references to Runtime still exist, which is
// necessary for entrypoints such as the workers.
import './root/root-legacy.js';

import * as RootModule from './root/root.js';

self.Runtime = self.Runtime || {};
Runtime = Runtime || {};

/**
 * @type {!Object.<string, string>}
 */
self.Runtime.cachedResources = {
  __proto__: null
};

self.Root = self.Root || {};
Root = Root || {};

// The following two variables are initialized in `build_release_applications`
Root.allDescriptors = Root.allDescriptors || [];
Root.applicationDescriptor = Root.applicationDescriptor || undefined;

/** @type {Function} */
let appStartedPromiseCallback;
Runtime.appStarted = new Promise(fulfil => appStartedPromiseCallback = fulfil);

/**
 * @param {string} appName
 * @return {!Promise.<void>}
 */
export async function startApplication(appName) {
  console.timeStamp('Root.Runtime.startApplication');

  /** @type {!Object<string, RootModule.Runtime.ModuleDescriptor>} */
  const allDescriptorsByName = {};
  for (let i = 0; i < Root.allDescriptors.length; ++i) {
    const d = Root.allDescriptors[i];
    allDescriptorsByName[d['name']] = d;
  }

  if (!Root.applicationDescriptor) {
    let data = await RootModule.Runtime.loadResourcePromise(appName + '.json');
    Root.applicationDescriptor = JSON.parse(data);
    let descriptor = Root.applicationDescriptor;
    while (descriptor.extends) {
      data = await RootModule.Runtime.loadResourcePromise(descriptor.extends + '.json');
      descriptor = JSON.parse(data);
      Root.applicationDescriptor.modules = descriptor.modules.concat(Root.applicationDescriptor.modules);
    }
  }

  const configuration = Root.applicationDescriptor.modules;
  const moduleJSONPromises = [];
  const coreModuleNames = [];
  for (let i = 0; i < configuration.length; ++i) {
    const descriptor = configuration[i];
    const name = descriptor['name'];
    const moduleJSON = allDescriptorsByName[name];
    if (moduleJSON) {
      moduleJSONPromises.push(Promise.resolve(moduleJSON));
    } else {
      moduleJSONPromises.push(
          RootModule.Runtime.loadResourcePromise(name + '/module.json').then(JSON.parse.bind(JSON)));
    }
    if (descriptor['type'] === 'autostart') {
      coreModuleNames.push(name);
    }
  }

  const moduleDescriptors = await Promise.all(moduleJSONPromises);

  for (let i = 0; i < moduleDescriptors.length; ++i) {
    moduleDescriptors[i].name = configuration[i]['name'];
    moduleDescriptors[i].condition = configuration[i]['condition'];
    moduleDescriptors[i].remote = configuration[i]['type'] === 'remote';
  }
  self.runtime = RootModule.Runtime.Runtime.instance({forceNew: true, moduleDescriptors});
  if (coreModuleNames) {
    await self.runtime.loadAutoStartModules(coreModuleNames);
  }
  appStartedPromiseCallback();
}

/**
 * @param {string} appName
 * @return {!Promise.<void>}
 */
export async function startWorker(appName) {
  return startApplication(appName).then(sendWorkerReady);

  function sendWorkerReady() {
    self.postMessage('workerReady');
  }
}
