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
// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * @fileoverview using private properties isn't a Closure violation in tests.
 * @suppress {accessControls}
 */

ElementsTestRunner.doAddAttribute = function(testName, dataNodeId, attributeText, next) {
  ElementsTestRunner.domActionTestForNodeId(testName, dataNodeId, testBody, next);

  function testBody(node, done) {
    ElementsTestRunner.editNodePart(node, 'webkit-html-attribute');
    eventSender.keyDown('Tab');
    TestRunner.deprecatedRunAfterPendingDispatches(testContinuation);

    function testContinuation() {
      const editorElement = ElementsTestRunner.firstElementsTreeOutline()._shadowRoot.getSelection().anchorNode.parentElement;
      editorElement.textContent = attributeText;
      editorElement.dispatchEvent(TestRunner.createKeyEvent('Enter'));
      TestRunner.addSniffer(Elements.ElementsTreeOutline.prototype, '_updateModifiedNodes', done);
    }
  }
};

ElementsTestRunner.domActionTestForNodeId = function(testName, dataNodeId, testBody, next) {
  function callback(testNode, continuation) {
    ElementsTestRunner.selectNodeWithId(dataNodeId, continuation);
  }

  ElementsTestRunner.domActionTest(testName, callback, testBody, next);
};

ElementsTestRunner.domActionTest = function(testName, dataNodeSelectionCallback, testBody, next) {
  const testNode = ElementsTestRunner.expandedNodeWithId(testName);
  TestRunner.addResult('==== before ====');
  ElementsTestRunner.dumpElementsTree(testNode);
  dataNodeSelectionCallback(testNode, step0);

  function step0(node) {
    TestRunner.deprecatedRunAfterPendingDispatches(step1.bind(null, node));
  }

  function step1(node) {
    testBody(node, step2);
  }

  function step2() {
    TestRunner.addResult('==== after ====');
    ElementsTestRunner.dumpElementsTree(testNode);
    next();
  }
};

ElementsTestRunner.editNodePart = function(node, className) {
  const treeElement = ElementsTestRunner.firstElementsTreeOutline().findTreeElement(node);
  let textElement = treeElement.listItemElement.getElementsByClassName(className)[0];

  if (!textElement && treeElement.childrenListElement) {
    textElement = treeElement.childrenListElement.getElementsByClassName(className)[0];
  }

  treeElement._startEditingTarget(textElement);
  return textElement;
};

ElementsTestRunner.editNodePartAndRun = function(node, className, newValue, step2, useSniffer) {
  const editorElement = ElementsTestRunner.editNodePart(node, className);
  editorElement.textContent = newValue;
  editorElement.dispatchEvent(TestRunner.createKeyEvent('Enter'));

  if (useSniffer) {
    TestRunner.addSniffer(Elements.ElementsTreeOutline.prototype, '_updateModifiedNodes', step2);
  } else {
    TestRunner.deprecatedRunAfterPendingDispatches(step2);
  }
};
