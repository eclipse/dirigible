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
/**
 * @param {string} mimeType
 * @return {function(string, function(string, ?string, number, number):(!Object|undefined))}
 */

import * as WasmDis from '../third_party/wasmparser/WasmDis.js';
import * as WasmParser from '../third_party/wasmparser/WasmParser.js';

self.onmessage = async function(event) {
  const method = /** @type {string} */ (event.data.method);
  const params = /** @type !{content: string} */ (event.data.params);
  if (!method || method !== 'disassemble') {
    return;
  }

  const response = await fetch(`data:application/wasm;base64,${params.content}`);
  const buffer = await response.arrayBuffer();
  const data = new Uint8Array(buffer);

  const parser = new WasmParser.BinaryReader();
  parser.setData(data, 0, data.length);
  const nameGenerator = new WasmDis.DevToolsNameGenerator();
  nameGenerator.read(parser);
  const dis = new WasmDis.WasmDisassembler();
  dis.nameResolver = nameGenerator.getNameResolver();
  dis.addOffsets = true;
  dis.maxLines = 1000000;
  parser.setData(data, 0, data.length);
  dis.disassembleChunk(parser);
  const result = dis.getResult();
  this.postMessage({source: result.lines.join('\n'), offsets: result.offsets});
};

/* Legacy exported object */
self.WasmParserWorker = self.WasmParserWorker || {};

/* Legacy exported object */
WasmParserWorker = WasmParserWorker || {};
