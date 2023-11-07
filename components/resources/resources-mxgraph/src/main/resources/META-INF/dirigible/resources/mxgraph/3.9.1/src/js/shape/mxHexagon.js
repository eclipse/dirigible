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
 * Class: mxHexagon
 * 
 * Implementation of the hexagon shape.
 * 
 * Constructor: mxHexagon
 *
 * Constructs a new hexagon shape.
 */
function mxHexagon()
{
	mxActor.call(this);
};

/**
 * Extends mxActor.
 */
mxUtils.extend(mxHexagon, mxActor);

/**
 * Function: redrawPath
 *
 * Draws the path for this shape.
 */
mxHexagon.prototype.redrawPath = function(c, x, y, w, h)
{
	var arcSize = mxUtils.getValue(this.style, mxConstants.STYLE_ARCSIZE, mxConstants.LINE_ARCSIZE) / 2;
	this.addPoints(c, [new mxPoint(0.25 * w, 0), new mxPoint(0.75 * w, 0), new mxPoint(w, 0.5 * h), new mxPoint(0.75 * w, h),
	                   new mxPoint(0.25 * w, h), new mxPoint(0, 0.5 * h)], this.isRounded, arcSize, true);
};
