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
var mxStyleRegistry =
{
	/**
	 * Class: mxStyleRegistry
	 *
	 * Singleton class that acts as a global converter from string to object values
	 * in a style. This is currently only used to perimeters and edge styles.
	 * 
	 * Variable: values
	 *
	 * Maps from strings to objects.
	 */
	values: [],

	/**
	 * Function: putValue
	 *
	 * Puts the given object into the registry under the given name.
	 */
	putValue: function(name, obj)
	{
		mxStyleRegistry.values[name] = obj;
	},

	/**
	 * Function: getValue
	 *
	 * Returns the value associated with the given name.
	 */
	getValue: function(name)
	{
		return mxStyleRegistry.values[name];
	},
	
	/**
	 * Function: getName
	 * 
	 * Returns the name for the given value.
	 */
	getName: function(value)
	{
		for (var key in mxStyleRegistry.values)
		{
			if (mxStyleRegistry.values[key] == value)
			{
				return key;
			}
		}
		
		return null;
	}

};

mxStyleRegistry.putValue(mxConstants.EDGESTYLE_ELBOW, mxEdgeStyle.ElbowConnector);
mxStyleRegistry.putValue(mxConstants.EDGESTYLE_ENTITY_RELATION, mxEdgeStyle.EntityRelation);
mxStyleRegistry.putValue(mxConstants.EDGESTYLE_LOOP, mxEdgeStyle.Loop);
mxStyleRegistry.putValue(mxConstants.EDGESTYLE_SIDETOSIDE, mxEdgeStyle.SideToSide);
mxStyleRegistry.putValue(mxConstants.EDGESTYLE_TOPTOBOTTOM, mxEdgeStyle.TopToBottom);
mxStyleRegistry.putValue(mxConstants.EDGESTYLE_ORTHOGONAL, mxEdgeStyle.OrthConnector);
mxStyleRegistry.putValue(mxConstants.EDGESTYLE_SEGMENT, mxEdgeStyle.SegmentConnector);

mxStyleRegistry.putValue(mxConstants.PERIMETER_ELLIPSE, mxPerimeter.EllipsePerimeter);
mxStyleRegistry.putValue(mxConstants.PERIMETER_RECTANGLE, mxPerimeter.RectanglePerimeter);
mxStyleRegistry.putValue(mxConstants.PERIMETER_RHOMBUS, mxPerimeter.RhombusPerimeter);
mxStyleRegistry.putValue(mxConstants.PERIMETER_TRIANGLE, mxPerimeter.TrianglePerimeter);
mxStyleRegistry.putValue(mxConstants.PERIMETER_HEXAGON, mxPerimeter.HexagonPerimeter);
