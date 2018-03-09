// Defines the column user object
function Column(name) {
	this.name = name;
}

Column.prototype.type = 'VARCHAR';
Column.prototype.columnLength = '20';
Column.prototype.defaultValue = null;
Column.prototype.primaryKey = false;
Column.prototype.autoIncrement = false;
Column.prototype.notNull = false;
Column.prototype.unique = false;
Column.prototype.precision = '';
Column.prototype.scale = '';

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