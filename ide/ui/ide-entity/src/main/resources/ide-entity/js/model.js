// Defines the property user object
function Property(name) {
	this.name = name;
}

Property.prototype.type = 'VARCHAR';
Property.prototype.columnLength = '20';
Property.prototype.defaultValue = null;
Property.prototype.primaryKey = false;
Property.prototype.autoIncrement = false;
Property.prototype.notNull = false;
Property.prototype.unique = false;
Property.prototype.precision = '';
Property.prototype.scale = '';
Property.prototype.relationshipType = 'NONE';
Property.prototype.relationshipRatio = '0';
Property.prototype.relationshipName = '';

Property.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the entity user object
function Entity(name) {
	this.name = name;
}

Entity.prototype.isPrimary = true;
Entity.prototype.menuLabel = '';
Entity.prototype.templateType = 'MASTER';

Entity.prototype.clone = function() {
	return mxUtils.clone(this);
};

// Defines the connector user object
function Connector(name) {
	this.name = name;
}
