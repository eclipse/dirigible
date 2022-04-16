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
 * Class: mxImage
 *
 * Encapsulates the URL, width and height of an image.
 * 
 * Constructor: mxImage
 * 
 * Constructs a new image.
 */
function mxImage(src, width, height)
{
	this.src = src;
	this.width = width;
	this.height = height;
};

/**
 * Variable: src
 *
 * String that specifies the URL of the image.
 */
mxImage.prototype.src = null;

/**
 * Variable: width
 *
 * Integer that specifies the width of the image.
 */
mxImage.prototype.width = null;

/**
 * Variable: height
 *
 * Integer that specifies the height of the image.
 */
mxImage.prototype.height = null;
