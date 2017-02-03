/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/users";
const HTML_LINK = "users.html";

exports.getHomeItem = function() {
	return {
      "image": "users",
      "color": "blue",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Users",
      "description": "Registered Users"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Users",
      "path": PATH,
      "link": HTML_LINK
   };
};
