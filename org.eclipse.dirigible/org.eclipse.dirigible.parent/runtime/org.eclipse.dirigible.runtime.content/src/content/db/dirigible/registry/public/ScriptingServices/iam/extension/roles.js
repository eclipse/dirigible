/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/roles";
const HTML_LINK = "roles.html";

exports.getHomeItem = function() {
	return {
      "image": "key",
      "color": "green",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Roles",
      "description": "Roles Definition"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Roles",
      "path": PATH,
      "link": HTML_LINK
   };
};
