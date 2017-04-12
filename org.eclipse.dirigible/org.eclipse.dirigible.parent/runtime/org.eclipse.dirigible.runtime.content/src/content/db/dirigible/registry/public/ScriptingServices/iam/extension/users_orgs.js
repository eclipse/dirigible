/* globals $ */
/* eslint-env node, dirigible */

const PATH = "/users_orgs";
const HTML_LINK = "users_orgs.html";

exports.getHomeItem = function() {
	return {
      "image": "users_orgs",
      "color": "purple",
      "path": PATH,
      "link": HTML_LINK,
      "title": "Users in Orgs",
      "description": "Users in Organizations"
   };
};

exports.getMenuItem = function() {
	return {  
      "name": "Users in Orgs",
      "path": PATH,
      "link": HTML_LINK
   };
};
