/*eslint-env node */

exports.getItem = function() {
	var item = {
		"name": "Monitoring",
	    "link": "#/monitoring"
	};
	return item;
};

exports.getOrder = function() {
	return 3;
};