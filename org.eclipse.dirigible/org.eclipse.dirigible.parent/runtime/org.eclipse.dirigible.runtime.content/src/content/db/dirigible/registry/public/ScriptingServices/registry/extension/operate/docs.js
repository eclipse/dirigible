/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "book",
		color: "yellow",
		path: "../docs_explorer/web/index.html",
		title: "Documents",
		description: "Documents Explorer",
		newTab:true
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-book",
		"title": "Manage Documents",
		"content": "Documents Explorer gives direct access to the underlying CMIS Repository for storeing the unstructured content. You can upload single documents or import zip files, which will be inflated and stored in the corresponding folders"
	};
};

exports.getOrder = function() {
	return 3;
};
