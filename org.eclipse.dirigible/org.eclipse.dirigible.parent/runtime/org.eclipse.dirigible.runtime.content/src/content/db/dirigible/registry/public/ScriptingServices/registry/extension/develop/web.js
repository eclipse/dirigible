/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Develop';
};

exports.getHomeItem = function() {
	return {
		image: 'laptop',
		color:'blue',
		path:'../../index.html',
		title:'Web IDE',
		description:'Development Toolkit',
		newTab:true
	};
};

exports.getDescription = function() {
	return {
		'icon': 'fa-laptop',
		'title': 'Web IDE',
		'content': 'The environment itself runs directly in a browser, therefore does not require additional downloads and installations. It has a rich set of editors, wizards and viewers, and also supports debugging, operations and monitoring.'
	};
};

exports.getOrder = function() {
	return 1;
};
