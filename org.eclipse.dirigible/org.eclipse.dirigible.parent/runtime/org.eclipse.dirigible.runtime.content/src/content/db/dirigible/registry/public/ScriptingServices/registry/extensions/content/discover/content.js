/*eslint-env node */

exports.getItem = function() {
	var item = {
		'icon': 'fa-search',
		'title': 'Browse Content',
		'content': 'Browse the raw content of the Registry containing all the published artifacts. Inspect the source of the HTML or Wiki pages as well as the code for the scripting services in JavaScript, Java, SQL and Shell Commands'
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};
