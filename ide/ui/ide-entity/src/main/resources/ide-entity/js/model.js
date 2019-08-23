/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
// Defines the entity user object
function Entity(name) {
	this.name = name;
}

Entity.prototype.entityType = 'PRIMARY'; // the type of the entity - PRIMARY, DEPENDENT, REPORT
Entity.prototype.dataName = null;
Entity.prototype.dataQuery = null; // database query in case of a report type of the entity
Entity.prototype.dataCount = null; // database query for the count of the entities
Entity.prototype.title = '';
Entity.prototype.tooltip = '';
Entity.prototype.icon = 'file-o';
Entity.prototype.menuLabel = ''; // the visible name of the menu
Entity.prototype.menuIndex = 100;
Entity.prototype.layoutType = 'MANAGE';
Entity.prototype.perspectiveName = 'Entities';
Entity.prototype.perspectiveIcon = 'files-o';
Entity.prototype.perspectiveOrder = 100;

Entity.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the property user object
function Property(name) {
	this.name = name;
}

Property.prototype.dataName = null;
Property.prototype.dataType = 'VARCHAR';
Property.prototype.dataLength = '20';
Property.prototype.dataDefaultValue = null;
Property.prototype.dataPrimaryKey = 'false';
Property.prototype.dataAutoIncrement = 'false';
Property.prototype.dataNotNull = 'false';
Property.prototype.dataUnique = 'false';
Property.prototype.dataPrecision = null;
Property.prototype.dataScale = null;
Property.prototype.relationshipType = null;
Property.prototype.relationshipCardinality = null;
Property.prototype.relationshipName = null;
Property.prototype.relationshipEntityName = null;
Property.prototype.relationshipEntityPerspectiveName = null;
Property.prototype.widgetType = 'TEXTBOX';
Property.prototype.widgetLength = '20';
Property.prototype.widgetPattern = null; // the input validation patern
Property.prototype.widgetService = null; // the service used to fill in the widget if any
Property.prototype.widgetIsMajor = 'true'; // whether this property will be shown in e.g. a list of entities table
Property.prototype.widgetSection = null; // the name of the grouping section
Property.prototype.widgetLabel = null; // the regular form label
Property.prototype.widgetShortLabel = null; // a short label for limited character places e.g. table headers
Property.prototype.widgetFormat = null; // the format for rendering
Property.prototype.widgetDropDownKey = null; // the key property in drop down case
Property.prototype.widgetDropDownValue = null; // the value property in drop down case

Property.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the connector user object
function Connector(name) {
	this.name = name;
}
