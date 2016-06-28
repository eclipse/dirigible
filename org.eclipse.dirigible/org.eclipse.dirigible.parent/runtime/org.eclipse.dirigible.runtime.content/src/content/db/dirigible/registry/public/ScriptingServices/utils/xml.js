/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

exports.fromJson = function(json) {
	return $.getXmlUtils().fromJson(JSON.stringify(json));
};

exports.toJson = function(xml) {
	return $.getXmlUtils().toJson(xml);
};
