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
exports.getEditor = function() {
	var editor = {
			"id":"flowable",
			"name":"Flowable",
			"factory":"frame",
			"region":"center-top",
			"label":"Flowable",
			"link":"../ide-bpm/index.html#/editor",
			"contentTypes":["application/bpmn+xml"]
	};
	return editor;
};
