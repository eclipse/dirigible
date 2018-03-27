// Defines the entity user object
function Entity(name) {
	this.name = name;
}

Entity.prototype.dataName = null;
Entity.prototype.isPrimary = true; // whether the entity is a major one to be shown in the e.g. main menu
Entity.prototype.menuKey = ''; // the hidden key for the menu
Entity.prototype.menuLabel = ''; // the visible name of the menu
Entity.prototype.menuIndex = 100;
Entity.prototype.layoutType = 'MANAGE';

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
Property.prototype.dataPrimaryKey = false;
Property.prototype.dataAutoIncrement = false;
Property.prototype.dataNotNull = false;
Property.prototype.dataUnique = false;
Property.prototype.dataPrecision = null;
Property.prototype.dataScale = null;
Property.prototype.relationshipType = null;
Property.prototype.relationshipCardinality = null;
Property.prototype.relationshipName = null;
Property.prototype.widgetType = 'TEXTBOX';
Property.prototype.widgetLength = '20';
Property.prototype.widgetPattern = null; // the input validation patern
Property.prototype.widgetService = null; // the service used to fill in the widget if any
Property.prototype.widgetIsMajor = true; // whether this property will be shown in e.g. a list of entities table
Property.prototype.widgetSection = null; // the name of the grouping section
Property.prototype.widgetLabel = null; // the regular form label
Property.prototype.widgetShortLabel = null; // a short label for limited character places e.g. table headers
Property.prototype.widgetFormat = null; // the format for rendering

Property.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the connector user object
function Connector(name) {
	this.name = name;
}
