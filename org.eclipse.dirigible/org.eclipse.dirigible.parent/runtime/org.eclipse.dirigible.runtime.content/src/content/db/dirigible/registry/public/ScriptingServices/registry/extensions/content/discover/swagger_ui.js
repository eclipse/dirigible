/*eslint-env node */

exports.getItem = function() {
	var item = {
		'icon': 'fa-laptop',
		'title': 'Web IDE',
		'content': 'The environment itself runs directly in a browser, therefore does not require additional downloads and installations. It has a rich set of editors, wizards and viewers, and also supports debugging, operations and monitoring.'
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};
