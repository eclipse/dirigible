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
var mxStencilRegistry =
{
	/**
	 * Class: mxStencilRegistry
	 * 
	 * A singleton class that provides a registry for stencils and the methods
	 * for painting those stencils onto a canvas or into a DOM.
	 */
	stencils: {},
	
	/**
	 * Function: addStencil
	 * 
	 * Adds the given <mxStencil>.
	 */
	addStencil: function(name, stencil)
	{
		mxStencilRegistry.stencils[name] = stencil;
	},
	
	/**
	 * Function: getStencil
	 * 
	 * Returns the <mxStencil> for the given name.
	 */
	getStencil: function(name)
	{
		return mxStencilRegistry.stencils[name];
	}

};
