/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/profile";
const HTML_LINK = "../profile/update.html";

exports.getHomeItem = function() {
	return {
      "image": "id-card-o",
      "color": "yellow",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Profile",
      "description": "Edit Profile"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Profile",
      "path": PATH,
      "link": HTML_LINK
   };
};
