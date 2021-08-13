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

import * as FormatterWorkerModule from './formatter_worker.js';

self.FormatterWorker = self.FormatterWorker || {};
FormatterWorker = FormatterWorker || {};

/** @constructor */
FormatterWorker.AcornTokenizer = FormatterWorkerModule.AcornTokenizer.AcornTokenizer;

/** @constructor */
FormatterWorker.CSSFormatter = FormatterWorkerModule.CSSFormatter.CSSFormatter;

FormatterWorker.CSSParserStates = FormatterWorkerModule.CSSRuleParser.CSSParserStates;
FormatterWorker.parseCSS = FormatterWorkerModule.CSSRuleParser.parseCSS;

/** @constructor */
FormatterWorker.ESTreeWalker = FormatterWorkerModule.ESTreeWalker.ESTreeWalker;

/** @constructor */
FormatterWorker.FormattedContentBuilder = FormatterWorkerModule.FormattedContentBuilder.FormattedContentBuilder;

FormatterWorker.AbortTokenization = FormatterWorkerModule.FormatterWorker.AbortTokenization;
FormatterWorker.createTokenizer = FormatterWorkerModule.FormatterWorker.createTokenizer;
FormatterWorker.parseJSONRelaxed = FormatterWorkerModule.FormatterWorker.parseJSONRelaxed;
FormatterWorker.evaluatableJavaScriptSubstring = FormatterWorkerModule.FormatterWorker.evaluatableJavaScriptSubstring;
FormatterWorker.javaScriptIdentifiers = FormatterWorkerModule.FormatterWorker.javaScriptIdentifiers;
FormatterWorker.format = FormatterWorkerModule.FormatterWorker.format;
FormatterWorker.findLastFunctionCall = FormatterWorkerModule.FormatterWorker.findLastFunctionCall;
FormatterWorker.argumentsList = FormatterWorkerModule.FormatterWorker.argumentsList;
FormatterWorker.findLastExpression = FormatterWorkerModule.FormatterWorker.findLastExpression;
FormatterWorker.FormatterWorkerContentParser = FormatterWorkerModule.FormatterWorker.FormatterWorkerContentParser;

/** @constructor */
FormatterWorker.HTMLFormatter = FormatterWorkerModule.HTMLFormatter.HTMLFormatter;

/** @constructor */
FormatterWorker.HTMLModel = FormatterWorkerModule.HTMLFormatter.HTMLModel;

/** @constructor */
FormatterWorker.IdentityFormatter = FormatterWorkerModule.IdentityFormatter.IdentityFormatter;

/** @constructor */
FormatterWorker.JavaScriptFormatter = FormatterWorkerModule.JavaScriptFormatter.JavaScriptFormatter;

FormatterWorker.javaScriptOutline = FormatterWorkerModule.JavaScriptOutline.javaScriptOutline;

FormatterWorker.RelaxedJSONParser = FormatterWorkerModule.RelaxedJSONParser.RelaxedJSONParser;
