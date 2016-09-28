/*eslint-env node */

exports.getItem = function() {
	var item = {
		'icon': 'fa-desktop' ,
		'title': 'Desktop Eclipse' ,
		'content': 'There is an update site with Eclipse based plugins convenient especially for \'Java-saurus\' developers, who will never go to a Web IDE for theirs daily tasks.'
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
