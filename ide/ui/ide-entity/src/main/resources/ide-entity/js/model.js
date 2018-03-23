// Defines the entity user object
function Entity(name) {
	this.name = name;
}

Entity.prototype.dataName = null;
Entity.prototype.isPrimary = true;
Entity.prototype.menuKey = '';
Entity.prototype.menuLabel = '';
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
Property.prototype.widgetPattern = null;
Property.prototype.widgetService = null;

Property.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the connector user object
function Connector(name) {
	this.name = name;
}
