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
 * Class: mxConnectionConstraint
 * 
 * Defines an object that contains the constraints about how to connect one
 * side of an edge to its terminal.
 * 
 * Constructor: mxConnectionConstraint
 * 
 * Constructs a new connection constraint for the given point and boolean
 * arguments.
 * 
 * Parameters:
 * 
 * point - Optional <mxPoint> that specifies the fixed location of the point
 * in relative coordinates. Default is null.
 * perimeter - Optional boolean that specifies if the fixed point should be
 * projected onto the perimeter of the terminal. Default is true.
 */
function mxConnectionConstraint(point, perimeter, name)
{
	this.point = point;
	this.perimeter = (perimeter != null) ? perimeter : true;
	this.name = name;
};

/**
 * Variable: point
 * 
 * <mxPoint> that specifies the fixed location of the connection point.
 */
mxConnectionConstraint.prototype.point = null;

/**
 * Variable: perimeter
 * 
 * Boolean that specifies if the point should be projected onto the perimeter
 * of the terminal.
 */
mxConnectionConstraint.prototype.perimeter = null;

/**
 * Variable: name
 * 
 * Optional string that specifies the name of the constraint.
 */
mxConnectionConstraint.prototype.name = null;
