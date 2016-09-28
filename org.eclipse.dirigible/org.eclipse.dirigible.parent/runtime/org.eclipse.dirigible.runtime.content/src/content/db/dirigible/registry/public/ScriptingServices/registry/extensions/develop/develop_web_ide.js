/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: 'laptop',
		color: 'blue',
		path: '../../index.html',
		title: 'Web IDE',
		description: "Development Toolkit",
		newTab: true
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};
