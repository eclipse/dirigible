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
import * as Platform from '../platform/platform.js';
import * as SDK from '../sdk/sdk.js';
import * as Workspace from '../workspace/workspace.js';

/**
 * @param {string} url
 * @return {?SDK.Resource.Resource}
 */
export function resourceForURL(url) {
  for (const resourceTreeModel of SDK.SDKModel.TargetManager.instance().models(
           SDK.ResourceTreeModel.ResourceTreeModel)) {
    const resource = resourceTreeModel.resourceForURL(url);
    if (resource) {
      return resource;
    }
  }
  return null;
}

/**
 * @param {string} url
 * @return {string}
 */
export function displayNameForURL(url) {
  if (!url) {
    return '';
  }

  const resource = resourceForURL(url);
  if (resource) {
    return resource.displayName;
  }

  const uiSourceCode = Workspace.Workspace.WorkspaceImpl.instance().uiSourceCodeForURL(url);
  if (uiSourceCode) {
    return uiSourceCode.displayName();
  }

  const mainTarget = SDK.SDKModel.TargetManager.instance().mainTarget();
  const inspectedURL = mainTarget && mainTarget.inspectedURL();
  if (!inspectedURL) {
    return Platform.StringUtilities.trimURL(url, '');
  }

  const parsedURL = Common.ParsedURL.ParsedURL.fromString(inspectedURL);
  const lastPathComponent = parsedURL ? parsedURL.lastPathComponent : parsedURL;
  const index = inspectedURL.indexOf(lastPathComponent);
  if (index !== -1 && index + lastPathComponent.length === inspectedURL.length) {
    const baseURL = inspectedURL.substring(0, index);
    if (url.startsWith(baseURL)) {
      return url.substring(index);
    }
  }

  if (!parsedURL) {
    return url;
  }

  const displayName = Platform.StringUtilities.trimURL(url, parsedURL.host);
  return displayName === '/' ? parsedURL.host + '/' : displayName;
}

/**
 * @param {!SDK.SDKModel.Target} target
 * @param {string} frameId
 * @param {string} url
 * @return {?Workspace.UISourceCode.UISourceCodeMetadata}
 */
export function metadataForURL(target, frameId, url) {
  const resourceTreeModel = target.model(SDK.ResourceTreeModel.ResourceTreeModel);
  if (!resourceTreeModel) {
    return null;
  }
  const frame = resourceTreeModel.frameForId(frameId);
  if (!frame) {
    return null;
  }
  return resourceMetadata(frame.resourceForURL(url));
}

/**
 * @param {?SDK.Resource.Resource} resource
 * @return {?Workspace.UISourceCode.UISourceCodeMetadata}
 */
export function resourceMetadata(resource) {
  if (!resource || (typeof resource.contentSize() !== 'number' && !resource.lastModified())) {
    return null;
  }
  return new Workspace.UISourceCode.UISourceCodeMetadata(resource.lastModified(), resource.contentSize());
}
