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

import * as TextEditor from '../text_editor/text_editor.js';
import * as UI from '../ui/ui.js';  // eslint-disable-line no-unused-vars

import {Row, RowType} from './ChangesView.js';  // eslint-disable-line no-unused-vars

/**
 * @extends {TextEditor.CodeMirrorTextEditor.CodeMirrorTextEditor}
 */
export class ChangesTextEditor extends TextEditor.CodeMirrorTextEditor.CodeMirrorTextEditor {
  /**
   * @param {!UI.TextEditor.Options} options
   */
  constructor(options) {
    options.inputStyle = 'devToolsAccessibleDiffTextArea';
    super(options);
    this.codeMirror().setOption('gutters', ['CodeMirror-linenumbers', 'changes-diff-gutter']);
    this.codeMirror().setOption('extraKeys', {
      Enter: false,
      Space: false,
      Left: function(cm) {
        const scrollInfo = cm.getScrollInfo();
        // Left edge check required due to bug where line numbers would disappear when attempting to scroll left when the scrollbar is at the leftmost point.
        // CodeMirror Issue: https://github.com/codemirror/CodeMirror/issues/6139
        if (scrollInfo.left > 0) {
          cm.scrollTo(scrollInfo.left - Math.round(scrollInfo.clientWidth / 6), null);
        }
      },
      Right: function(cm) {
        const scrollInfo = cm.getScrollInfo();
        cm.scrollTo(scrollInfo.left + Math.round(scrollInfo.clientWidth / 6), null);
      }
    });
  }

  /**
   * @param {!Array<!Row>} diffRows
   */
  updateDiffGutter(diffRows) {
    this.codeMirror().eachLine(line => {
      const lineNumber = this.codeMirror().getLineNumber(line);
      const row = diffRows[lineNumber];
      let gutterMarker;
      if (row.type === RowType.Deletion) {
        gutterMarker = createElementWithClass('div', 'deletion changes-diff-gutter-marker');
        gutterMarker.textContent = '-';
      } else if (row.type === RowType.Addition) {
        gutterMarker = createElementWithClass('div', 'addition changes-diff-gutter-marker');
        gutterMarker.textContent = '+';
      }
      if (gutterMarker) {
        this.codeMirror().setGutterMarker(line, 'changes-diff-gutter', gutterMarker);
      }
    });
  }
}

export class DevToolsAccessibleDiffTextArea extends CodeMirror.inputStyles.devToolsAccessibleTextArea {
  /**
  * @override
  * @param {boolean=} typing - whether the user is currently typing
  */
  reset(typing) {
    super.reset(typing);
    if (this.textAreaBusy(!!typing) || !(typeof this.cm.doc.modeOption === 'object')) {
      return;
    }

    const diffRows = this.cm.doc.modeOption.diffRows;
    const lineNumber = this.cm.getCursor().line;
    const rowType = diffRows[lineNumber].type;

    if (rowType === RowType.Deletion) {
      this.textarea.value = ls`Deletion:${this.textarea.value}`;
    }
    if (rowType === RowType.Addition) {
      this.textarea.value = ls`Addition:${this.textarea.value}`;
    }
    this.prevInput = this.textarea.value;
  }
}


/**
 * @constructor
 */
CodeMirror.inputStyles.devToolsAccessibleDiffTextArea = DevToolsAccessibleDiffTextArea;
