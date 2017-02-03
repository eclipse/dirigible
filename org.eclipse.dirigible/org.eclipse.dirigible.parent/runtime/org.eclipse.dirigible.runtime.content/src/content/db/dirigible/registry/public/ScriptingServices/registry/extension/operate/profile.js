/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Operate';
};

exports.getHomeItem = function() {
	return {
		image: "id-card-o",
		color: "orange",
		path: "../profile/update.html",
		title: "Profile",
		description: "Update User Profile",
		newTab:true
	};
};

//exports.getDescription = function() {
//	return {
//		"icon": "fa-id-card-o",
//		"title": "Update User Profile",
//		"content": "Every logged-in User can change his or her own attributes of the Profile as well as the Avatar picture."
//	};
//};

exports.getOrder = function() {
	return 5;
};
