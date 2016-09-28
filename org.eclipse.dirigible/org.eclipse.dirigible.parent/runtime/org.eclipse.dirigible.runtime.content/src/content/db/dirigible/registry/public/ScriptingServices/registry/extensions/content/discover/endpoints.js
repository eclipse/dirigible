/*eslint-env node */

exports.getItem = function() {
	var item = {
		'icon': 'fa-server',
		'title': 'Find Endpoints',
		'content': 'Navigate throughout all the registered service endpoints and perform test calls. Web content ususally is served as is, while Wiki pages first go thru transformation. The services are executed by the corresponding scripting engine for the given language. The same applies for the Flows and Jobs definitions.'
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
