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

import * as AccessibilityModule from './accessibility.js';

self.Accessibility = self.Accessibility || {};
Accessibility = Accessibility || {};

/**
 * @constructor
 */
Accessibility.ARIAAttributesPane = AccessibilityModule.ARIAAttributesView.ARIAAttributesPane;

/**
 * @constructor
 */
Accessibility.ARIAAttributesTreeElement = AccessibilityModule.ARIAAttributesView.ARIAAttributesTreeElement;

/**
 * @constructor
 */
Accessibility.ARIAAttributesPane.ARIAAttributePrompt = AccessibilityModule.ARIAAttributesView.ARIAAttributePrompt;

/**
 * @constructor
 */
Accessibility.ARIAMetadata = AccessibilityModule.ARIAMetadata.ARIAMetadata;

/**
 * @unrestricted
 * @constructor
 */
Accessibility.ARIAMetadata.Attribute = AccessibilityModule.ARIAMetadata.Attribute;

/**
 * @return {!Accessibility.ARIAMetadata}
 */
Accessibility.ariaMetadata = AccessibilityModule.ARIAMetadata.ariaMetadata;

/**
 * @constructor
 */
Accessibility.AXBreadcrumbsPane = AccessibilityModule.AXBreadcrumbsPane.AXBreadcrumbsPane;

/**
 * @constructor
 */
Accessibility.AXBreadcrumb = AccessibilityModule.AXBreadcrumbsPane.AXBreadcrumb;

/** @type {!Object<string, string>} */
Accessibility.AXBreadcrumb.RoleStyles = AccessibilityModule.AXBreadcrumbsPane.RoleStyles;

/**
 * @constructor
 */
Accessibility.AccessibilityNode = AccessibilityModule.AccessibilityModel.AccessibilityNode;

/**
 * @constructor
 */
Accessibility.AccessibilityModel = AccessibilityModule.AccessibilityModel.AccessibilityModel;

/**
 * @constructor
 */
Accessibility.AXNodeSubPane = AccessibilityModule.AccessibilityNodeView.AXNodeSubPane;

/**
 * @constructor
 */
Accessibility.AXNodePropertyTreeElement = AccessibilityModule.AccessibilityNodeView.AXNodePropertyTreeElement;

/** @type {!Object<string, string>} */
Accessibility.AXNodePropertyTreeElement.TypeStyles = AccessibilityModule.AccessibilityNodeView.TypeStyles;

/** @type {!Set.<!Protocol.Accessibility.AXValueType>} */
Accessibility.AXNodePropertyTreeElement.StringProperties = AccessibilityModule.AccessibilityNodeView.StringProperties;

/**
 * @constructor
 */
Accessibility.AXNodePropertyTreePropertyElement =
    AccessibilityModule.AccessibilityNodeView.AXNodePropertyTreePropertyElement;

/**
 * @constructor
 */
Accessibility.AXValueSourceTreeElement = AccessibilityModule.AccessibilityNodeView.AXValueSourceTreeElement;

/**
 * @constructor
 */
Accessibility.AXRelatedNodeSourceTreeElement = AccessibilityModule.AccessibilityNodeView.AXRelatedNodeSourceTreeElement;

/**
 * @constructor
 */
Accessibility.AXRelatedNodeElement = AccessibilityModule.AccessibilityNodeView.AXRelatedNodeElement;

/**
 * @constructor
 */
Accessibility.AXNodeIgnoredReasonTreeElement = AccessibilityModule.AccessibilityNodeView.AXNodeIgnoredReasonTreeElement;

/**
 * @constructor
 */
Accessibility.AccessibilitySidebarView = AccessibilityModule.AccessibilitySidebarView.AccessibilitySidebarView;

/**
 * @constructor
 */
Accessibility.AccessibilitySubPane = AccessibilityModule.AccessibilitySubPane.AccessibilitySubPane;

Accessibility.AccessibilityStrings = {};

Accessibility.AccessibilityStrings.AXAttributes = AccessibilityModule.AccessibilityStrings.AXAttributes;

Accessibility.AccessibilityStrings.AXSourceTypes = AccessibilityModule.AccessibilityStrings.AXSourceTypes;

Accessibility.AccessibilityStrings.AXNativeSourceTypes = AccessibilityModule.AccessibilityStrings.AXNativeSourceTypes;
