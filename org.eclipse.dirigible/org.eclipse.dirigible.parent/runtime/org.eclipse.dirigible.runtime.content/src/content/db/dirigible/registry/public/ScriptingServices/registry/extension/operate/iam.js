/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "users",
		color: "red",
		path: "../iam/index.html",
		title: "IAM",
		description: "Identity and Access",
		newTab:true
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-users",
		"title": "Identity and Access Management",
		"content": "Identity and Access Management (IAM) is a tool for registering new Users and Roles as well as their Assignments."
	};
};

exports.getOrder = function() {
	return 4;
};
