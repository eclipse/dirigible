/*eslint-env node */

exports.getItem = function() {
	var item = {
		'icon': 'fa-mobile' ,
		'title': 'Light IDE' ,
		'content': 'For quick fixes with simple source code editing and publishing capabilities, there is a lightweight development environment convenient even from mobile devices.'	
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
