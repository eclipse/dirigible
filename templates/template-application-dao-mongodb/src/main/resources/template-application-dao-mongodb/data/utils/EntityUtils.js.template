exports.setLocalDate = function(object, property) {
	if (object[property]) {
		object[property] = new Date(new Date(object[property]).setHours(-(new Date().getTimezoneOffset()/60), 0, 0, 0)).toISOString();
	}
};

exports.setBoolean = function(object, property) {
	if (object[property] !== undefined) {
		object[property] = object[property] ? true : false;
	}
};