/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
// Defines the column user object
function Column(name) {
	this.name = name;
}

Column.prototype.type = 'VARCHAR';
Column.prototype.columnLength = '20';
Column.prototype.defaultValue = null;
Column.prototype.primaryKey = 'false';
Column.prototype.autoIncrement = 'false';
Column.prototype.notNull = 'false';
Column.prototype.unique = 'false';
Column.prototype.precision = null;
Column.prototype.scale = null;

Column.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the table user object
function Table(name) {
	this.name = name;
};

Table.prototype.clone = function() {
	return mxUtils.clone(this);
};
// Defines the view user object
function View(name) {
	this.name = name;
};

View.prototype.clone = function() {
	return mxUtils.clone(this);
};