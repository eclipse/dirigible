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
/**
 * Class: mxTriangle
 * 
 * Implementation of the triangle shape.
 * 
 * Constructor: mxTriangle
 *
 * Constructs a new triangle shape.
 */
function mxTriangle()
{
	mxActor.call(this);
};

/**
 * Extends mxActor.
 */
mxUtils.extend(mxTriangle, mxActor);

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 */
mxTriangle.prototype.redrawPath = function(c, x, y, w, h)
{
	var arcSize = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2;
	this.addPoints(c, [new mxPoint(0, 0), new mxPoint(w, 0.5 * h), new mxPoint(0, h)], this.isRounded, arcSize, true);
};
