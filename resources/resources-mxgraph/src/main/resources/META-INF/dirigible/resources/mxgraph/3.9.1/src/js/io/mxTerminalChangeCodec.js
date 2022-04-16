/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
mxCodecRegistry.register(function()
{
	/**
	 * Class: mxTerminalChangeCodec
	 *
	 * Codec for <mxTerminalChange>s. This class is created and registered
	 * dynamically at load time and used implicitely via <mxCodec> and
	 * the <mxCodecRegistry>.
	 *
	 * Transient Fields:
	 *
	 * - model
	 * - previous
	 *
	 * Reference Fields:
	 *
	 * - cell
	 * - terminal
	 */
	var codec = new mxObjectCodec(new mxTerminalChange(),
		['model', 'previous'], ['cell', 'terminal']);

	/**
	 * Function: afterDecode
	 *
	 * Restores the state by assigning the previous value.
	 */
	codec.afterDecode = function(dec, node, obj)
	{
		obj.previous = obj.terminal;
		
		return obj;
	};

	// Returns the codec into the registry
	return codec;

}());
