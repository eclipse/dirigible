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
 * Class: mxLine
 *
 * Extends <mxShape> to implement a horizontal line shape.
 * This shape is registered under <mxConstants.SHAPE_LINE> in
 * <mxCellRenderer>.
 * 
 * Constructor: mxLine
 *
 * Constructs a new line shape.
 * 
 * Parameters:
 * 
 * bounds - <mxRectangle> that defines the bounds. This is stored in
 * <mxShape.bounds>.
 * stroke - String that defines the stroke color. Default is 'black'. This is
 * stored in <stroke>.
 * strokewidth - Optional integer that defines the stroke width. Default is
 * 1. This is stored in <strokewidth>.
 */
function mxLine(bounds, stroke, strokewidth)
{
	mxShape.call(this);
	this.bounds = bounds;
	this.stroke = stroke;
	this.strokewidth = (strokewidth != null) ? strokewidth : 1;
};

/**
 * Extends mxShape.
 */
mxUtils.extend(mxLine, mxShape);

/**
 * Function: paintVertexShape
 * 
 * Redirects to redrawPath for subclasses to work.
 */
mxLine.prototype.paintVertexShape = function(c, x, y, w, h)
{
	var mid = y + h / 2;

	c.begin();
	c.moveTo(x, mid);
	c.lineTo(x + w, mid);
	c.stroke();
};
